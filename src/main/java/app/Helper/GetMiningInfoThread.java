package app.Helper;

import app.Controllers.GetMiningInfoController;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class GetMiningInfoThread {
    private final static GetMiningInfoThread INSTANCE = new GetMiningInfoThread();
    private static Thread getMiningInfoThread;
    private static GetMiningInfoController runnable;

    public static void setThread(GetMiningInfoController task) {
        runnable = task;
        getMiningInfoThread = new Thread(runnable);
    }

    public static boolean start() {
        if (getMiningInfoThread != null) {
            getMiningInfoThread.start();
            return true;
        }

        return false;
    }

    public static boolean stop() {
        if (getMiningInfoThread != null) {
            try {
                runnable.terminate();
                getMiningInfoThread.join();
            } catch (InterruptedException e) {
                //
            }
            return true;
        }

        return false;
    }
}
