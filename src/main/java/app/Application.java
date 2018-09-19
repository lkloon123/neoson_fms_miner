package app;

import app.Helper.SettingHelper;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class Application {
    private FileLock lock;
    private FileChannel channel;

    public static String version() {
        return Application.class.getPackage().getImplementationVersion();
    }

    public boolean isAppRunning() throws IOException {
        File file = new File(System.getProperty("user.home"), "neosonfms.lock");
        channel = new RandomAccessFile(file, "rw").getChannel();

        lock = channel.tryLock();
        if (lock == null) {
            lock.release();
            channel.close();
            return true;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                lock.release();
                channel.close();
                if (SettingHelper.pid != null) {
                    PidProcess p = Processes.newPidProcess(Integer.parseInt(SettingHelper.pid));
                    if (p.isAlive()) {
                        p.destroy(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        return false;
    }
}
