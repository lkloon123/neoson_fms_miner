package app.Controllers;

import app.Config.Config;
import app.Helper.*;
import app.Interface.Logging;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class RunMiningController implements Logging {
    private static int RETRIES = 3;

    public RunMiningController(String software, String exeName, String config) {

        try {
            GetMiningInfoThread.stop();

            SettingHelper.read();
            if (SettingHelper.pid != null) {
                logger.info("Check if any miner running");
                PidProcess p = Processes.newPidProcess(Integer.parseInt(SettingHelper.pid));
                if (p.isAlive()) {
                    p.destroy(true);
                }
            }

            logger.info("Starting miner");
            int port = SocketHelper.getPort();
            String cmd = "cmd /c start \"\" \"" + System.getProperty("user.dir") + File.separator + Config.SOFTWARE_FOLDER + File.separator + software + File.separator + exeName + "\" " + config + " --api-bind=" + port;
            logger.debug(cmd);

            //start the miner
            new ProcessExecutor().commandSplit(cmd).start();

            //check for the started miner pid
            logger.info("Getting miner process ID");
            Thread.sleep(5000);
            String taskList = new ProcessExecutor().command("tasklist", "/fi", "IMAGENAME eq \"" + exeName + "\"").readOutput(true).execute().outputUTF8();
            String[] output = taskList.replaceAll("\\s", "").replaceAll("=", "").split(".exe")[1].split("Console");

            //save to setting
            SettingHelper.exeName = exeName;
            SettingHelper.software = software;
            SettingHelper.config = config;
            SettingHelper.pid = output[0];
            SettingHelper.write();

            GetMiningInfoThread.setThread(new GetMiningInfoController(port, () -> {
                try {
                    Runtime.getRuntime().exec("cmd /c start \"NeoSonFMS Launcher\" java -jar \"neosonfms_launcher.jar\" actual_run");
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    LogExceptions.trace(e);
                } finally {
                    System.exit(0);
                }
            }));
            GetMiningInfoThread.start();

        } catch (IOException | TimeoutException | InterruptedException | InvalidExitValueException e) {
            LogExceptions.trace(e);
            if (e instanceof InvalidExitValueException) {
                SettingHelper.pid = null;
                SettingHelper.write();
                logger.info("Retrying...");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            LogExceptions.trace(e);
            logger.error("Miner not started, retrying...");
            if (RETRIES > 0) {
                RETRIES--;
                new RunMiningController(software, exeName, config);
            } else {
                logger.error("Unable to start miner, please report the error with log file");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            LogExceptions.trace(e);
        }
    }
}
