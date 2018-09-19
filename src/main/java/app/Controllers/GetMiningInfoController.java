package app.Controllers;

import app.Helper.ApiClient;
import app.Helper.ApiWrapper;
import app.Helper.LogExceptions;
import app.Helper.SettingHelper;
import app.Interface.Logging;
import app.Interface.MinerFailedHandler;
import app.Models.MinerSummary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class GetMiningInfoController implements Logging, Runnable {

    static private final int MAXRECEIVESIZE = 65535;
    static private Socket socket = null;
    private volatile boolean running = true;
    private MinerFailedHandler failedHandler;

    private InetAddress ip;
    private int port;

    public GetMiningInfoController(int port, MinerFailedHandler failedHandler) throws Exception {
        this.ip = InetAddress.getByName("127.0.0.1");
        this.port = port;
        this.failedHandler = failedHandler;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(15000);
                logger.info("Checking miner status");
                process("summary", ip, port);
                logger.info("Miner is running, send hashrate to server");
            } catch (InterruptedException e) {
                LogExceptions.trace(e);
            } catch (IOException e) {
                //miner failed
                logger.warn("Miner not running, restarting...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                }
                LogExceptions.trace(e);
                failedHandler.handle();
            }
        }
    }

    public void terminate() {
        running = false;
    }

    public void display(String result) {
        String[] gpuList = result.split("\\|", 0);

        for (String n : gpuList) {
            if (n.trim().length() > 0) {
                MinerSummary minerSummaryTemp = new MinerSummary();
                String[] map = n.split(";", 0);

                for (String typeData : map) {
                    String[] keyValue = typeData.split("=", 0);
                    String key = keyValue[0];
                    String value = keyValue[1];

                    switch (key) {
                        case "ALGO":
                            minerSummaryTemp.algo = value;
                            break;
                        case "GPUS":
                            minerSummaryTemp.gpuCount = Integer.parseInt(value);
                            break;
                        case "KHS":
                            minerSummaryTemp.hashrate = Float.parseFloat(value) * 1000;
                            break;
                        case "ACC":
                            minerSummaryTemp.acceptedHash = Integer.parseInt(value);
                            break;
                        case "REJ":
                            minerSummaryTemp.rejectedHash = Integer.parseInt(value);
                            break;
                        case "UPTIME":
                            minerSummaryTemp.upTime = Integer.parseInt(value);
                            break;
                        case "TS":
                            minerSummaryTemp.timestamp = Integer.parseInt(value);
                            break;
                    }
                }

                sendSummaryDataToServer(minerSummaryTemp);
            }
        }
    }

    private void closeAll() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public void process(String cmd, InetAddress ip, int port) throws IOException {
        StringBuffer sb = new StringBuffer();
        char buf[] = new char[MAXRECEIVESIZE];

        try {
            socket = new Socket(ip, port);
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.print(cmd.toCharArray());
            ps.flush();

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            while (0x80085 > 0) {
                int len = isr.read(buf, 0, MAXRECEIVESIZE);
                if (len < 1)
                    break;
                sb.append(buf, 0, len);
                if (buf[len - 1] == '\0')
                    break;
            }

            closeAll();
        } catch (IOException ioe) {
            closeAll();
            throw new IOException(ioe);
        }

        String result = sb.toString();
        display(result);
    }

    private void sendSummaryDataToServer(MinerSummary minerSummary) {
        minerSummary.apiToken = SettingHelper.apiToken;
        new ApiWrapper<>().execute(ApiClient.getInterface().sendSummary(minerSummary),
                (data, error) -> {
                    if (error != null) {
                        logger.debug(error.errorData);
                    }
                });
    }
}
