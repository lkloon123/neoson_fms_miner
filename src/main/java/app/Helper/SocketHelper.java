package app.Helper;

import java.net.Socket;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class SocketHelper {
    public static int getPort() {
        int port = 4068;

        while (true) {
            if (isSocketAvailable(port)) {
                return port;
            }

            port++;
        }
    }

    private static boolean isSocketAvailable(int port) {
        Socket s = null;
        try {
            s = new Socket("127.0.0.1", port);
            return false;
        } catch (Exception e) {
            return true;
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (Exception e) {
                }
        }
    }
}
