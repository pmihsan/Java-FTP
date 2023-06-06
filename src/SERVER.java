import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SERVER {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("SERVER: Unable to start the server");
            System.out.println("USAGE: java SERVER port");
            System.exit(-1);
        }
        if (!FTP.isNumeric(args[0])) {
            System.out.println("SERVER: Invalid port number");
            System.exit(-1);
        }
        String home = System.getProperty("user.home") + HELPER.path_sep + "jftp";
        File root = new File(home);

        Console c = System.console();
        String pass = "1234";
        if (c != null) {
            System.out.print("Enter your password: ");
            pass = new String(c.readPassword());
        }

        if (!root.exists()) {
            if (HELPER.initializeServer(root)) {
                HELPER.createUser(root, pass);
                HELPER.createLogFile(root);
                System.out.println("FTP Default Initialization");
            } else {
                System.out.println("SERVER: Error in Configuration");
                System.exit(-1);
            }
        } else {
            HELPER.loadUser(root);
        }

        File startup = null;
        try (ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]))) {

            File dir = new File(home, "share");
            File log = new File(home + HELPER.path_sep + "log", "access_log");
            startup = new File(home + HELPER.path_sep + "log", "startup");

            boolean status = dir.mkdir();
            if (status) {
                System.out.println("FTP Default Directory Created");
            } else {
                System.out.println("FTP Directory Status: " + true);
            }

            System.out.println("Server Running on PORT 9809");
            HELPER.updateLog(startup, "Server Started " + new Date());
            System.out.println();

            while (true) {
                Socket client = ss.accept();
                InetAddress ia = client.getInetAddress();
                HELPER.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " connected " + new Date());

                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                String user = in.readUTF();
                String[] d = user.split("@");
                if (HELPER.verifyUser(d)) {
                    out.write(1);
                    out.flush();
                } else {
                    out.write(0);
                    out.flush();
                    System.exit(-1);
                }
                HELPER.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " " + d[0] + " authenticated " + new Date());
                String data;
                do {
                    data = in.readUTF();

                    String[] cmd = data.split(" ");
                    HELPER.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " " + d[0] + " executing '" + data + "' " + new Date());
                    CMD.handleSocket(cmd, in, out, dir);

                } while (!data.equalsIgnoreCase("exit"));

                in.close();
                out.close();
                client.close();

                HELPER.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " disconnected " + new Date());
                System.out.println();
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + ": Internal Server Error");
        }
        finally {
            HELPER.updateLog(startup, "Server Stopped " + new Date() + "\n");
            System.out.println("Server Stopped");
        }
    }
}
