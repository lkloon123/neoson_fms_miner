package app;

import app.Controllers.SetupController;
import app.Helper.LogExceptions;
import app.Helper.SettingHelper;
import app.Interface.Logging;

import java.io.IOException;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */

public class Main implements Logging {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                //not running from launcher
                System.exit(0);
            }

            if (new Application().isAppRunning()) {
                logger.error("Another Instance Is Running");
                System.exit(0);
            }

            SettingHelper.read();

            new SetupController();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            LogExceptions.trace(ex);
        }
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }
}

