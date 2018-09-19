package app.Controllers;

import app.Config.Config;
import app.Helper.LogExceptions;
import app.Helper.SettingHelper;
import app.Interface.Logging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class ListeningController implements ConnectionEventListener, ChannelEventListener, Logging {
    private static final ScheduledExecutorService connectionAttemptsWorker = Executors.newSingleThreadScheduledExecutor();
    private static final String CHANNEL_NAME = "miner." + SettingHelper.apiToken;
    private Pusher pusher;

    public ListeningController() {
        PusherOptions options = new PusherOptions().setCluster(Config.CLUSTER);
        this.pusher = new Pusher(Config.APP_KEY, options);
        this.setChannels();
        this.openConnection();
    }

    private void setChannels() {
        Channel channel = this.pusher.subscribe(CHANNEL_NAME, this);
        for (String event : Config.EVENT_LIST) {
            channel.bind(event, this);
        }
    }

    private void openConnection() {
        Runnable task = () -> pusher.connect(this);
        connectionAttemptsWorker.schedule(task, 0, TimeUnit.SECONDS);
    }

    //#region ConnectionEventListener interface
    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {
        if (change.getPreviousState() == ConnectionState.CONNECTED && change.getCurrentState() == ConnectionState.DISCONNECTED) {
            logger.warn("Connection state changed to " + change.getCurrentState());
        } else {
            logger.info("Connection state changed to " + change.getCurrentState());
        }
    }

    @Override
    public void onError(String message, String code, Exception e) {
        logger.error(e.getMessage());
        LogExceptions.trace(e);
    }
    //#endregion

    //#region ChannelEventListener interface
    @Override
    public void onSubscriptionSucceeded(String channelName) {
        logger.info("Start receiving commands from server");
    }

    @Override
    public void onEvent(String channelName, String eventName, String data) {
        logger.info(eventName + " received");
        if (eventName.equalsIgnoreCase("StartMiner")) {
            JsonObject obj = new JsonParser().parse(data).getAsJsonObject();
            new RunMiningController(obj.get("software").getAsString(), obj.get("exe_name").getAsString(), obj.get("config").getAsString());
        } else if (eventName.equalsIgnoreCase("UpdateMiner")) {
            if (SettingHelper.pid != null) {
                PidProcess p = Processes.newPidProcess(Integer.parseInt(SettingHelper.pid));
                try {
                    if (p.isAlive()) {
                        p.destroy(true);
                    }
                } catch (IOException | InterruptedException e) {
                    LogExceptions.trace(e);
                }
            }

            logger.info("restarting...");
            try {
                Runtime.getRuntime().exec("cmd /c start \"NeoSonFMS Launcher\" java -jar \"neosonfms_launcher.jar\" actual_run");
            } catch (IOException e) {
                logger.error(e.getMessage());
                LogExceptions.trace(e);
            } finally {
                System.exit(0);
            }
        }
    }
    //#endregion
}
