import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SERVER implements Runnable {

    private Socket client;
    private File  log, dir;
    private HELPER h;
    private String user;

    SERVER(Socket client, File dir, File log, HELPER h){
        this.client = client;
        this.log = log;
        this.dir = dir;
        this.h = h;
    }

    public void run() {
        try {
            CMD c = new CMD(dir);
            FTP ftp = new FTP();

            InetAddress ia = client.getInetAddress();
            h.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " connected " + new Date());

            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            String user = in.readUTF();
            String[] d = user.split("@");
            if (h.verifyUser(d)) {
                user = d[0];
                out.write(1);
                out.flush();
            } else {
                out.write(0);
                out.flush();
                System.exit(-1);
            }
            h.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " " + d[0] + " authenticated " + new Date());
            String data;
            do {
                data = in.readUTF();

                String[] cmd = data.split(" ");
                h.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " " + d[0] + " executing '" + data + "' " + new Date());
                c.handleSocket(cmd, in, out, dir, ftp, h, user);

            } while (!data.equalsIgnoreCase("exit"));

            in.close();
            out.close();
            client.close();

            h.updateLog(log, ia.getHostAddress() + ":" + client.getPort() + " disconnected " + new Date());
            System.out.println();
        }
        catch (FileNotFoundException fe){
            System.out.println("Log file not Found");
        }
        catch (IOException ie){
            System.out.println("Socket Stream Error");
        }
    }
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
        HELPER h = new HELPER();
        String home = System.getProperty("user.home") + h.path_sep + "jftp";
        File root = new File(home);

        startServer(root, h);

        File startup = null;
        try (ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]))) {

            File dir = new File(home, "share");
            File log = new File(home + h.path_sep + "log", "access_log");
            startup = new File(home + h.path_sep + "log", "startup");

            runServer(dir, startup, args[0], h);

            while (true) {
                Socket client = ss.accept();
                SERVER s = new SERVER(client, dir, log, h);
                Thread t = new Thread(s);
                t.start();
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + ": Internal Server Error");
        }
        finally {
            h.updateLog(startup, "Server Stopped " + new Date() + "\n");
            System.out.println("Server Stopped");
        }
    }

    public static void startServer(File root, HELPER h) throws IOException{
        if (!root.exists()) {
            if (h.initializeServer(root)) {
                String pass = h.getPassword();
                h.createUser(root, pass != null ? pass : "1234");
                h.createLogFile(root);
                System.out.println("FTP Default Initialization");
            } else {
                System.out.println("SERVER: Error in Configuration");
                System.exit(-1);
            }
        } else {
            h.loadUser(root);
            h.serverStartup(root);
        }
    }

    public static void runServer(File dir, File startup, String port, HELPER h) throws IOException{
        boolean status = dir.mkdir();
        if (status) {
            System.out.println("FTP Default Directory Created");
        } else {
            System.out.println("FTP Directory Status: " + true);
        }

        System.out.println("Server Running on PORT " + port);
        h.updateLog(startup, "Server Started " + new Date());
        System.out.println();
    }
}
