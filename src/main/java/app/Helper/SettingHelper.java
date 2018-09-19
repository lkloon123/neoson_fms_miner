package app.Helper;

import app.Interface.Logging;

import java.io.*;
import java.util.Properties;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class SettingHelper implements Logging {
    private static final String fileName = "setting.properties";
    public static String apiToken;
    public static String config;
    public static String software;
    public static String exeName;
    public static String pid;

    public static void write() {
        Properties props = new Properties();

        try (OutputStream output = new FileOutputStream(fileName)) {

            if (apiToken != null)
                props.setProperty("apiToken", apiToken);
            if (software != null)
                props.setProperty("software", software);
            if (exeName != null)
                props.setProperty("exe_name", exeName);
            if (config != null)
                props.setProperty("config", config);
            if (pid != null)
                props.setProperty("pid", pid);

            props.store(output, null);

            logger.info("Setting Saved!!");
        } catch (IOException e) {
            logger.error(e.getMessage());
            LogExceptions.trace(e);
        }
    }

    public static void read() {
        logger.info("Reading Setting...");
        Properties props = new Properties();

        File settingFile = new File(fileName);
        boolean fileExist = settingFile.exists();
        if (!fileExist) {
            try {
                settingFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage());
                LogExceptions.trace(e);
            }
        }

        try (InputStream input = new FileInputStream(fileName)) {
            props.load(input);

            apiToken = props.getProperty("apiToken");
            software = props.getProperty("software");
            exeName = props.getProperty("exe_name");
            config = props.getProperty("config");
            pid = props.getProperty("pid");

        } catch (IOException e) {
            logger.error(e.getMessage());
            LogExceptions.trace(e);
        }
    }
}
