package app.Helper;

import app.Interface.Logging;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class LogExceptions implements Logging {

    public static void trace(Exception exception) {
        logger.debug("");
        logger.debug(exception);
        logger.debug("");
    }
}
