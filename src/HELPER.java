import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class HELPER {
    static char path_sep = File.separatorChar;
    static HashMap<String, String> user_pass;
    public static boolean initializeServer(File root){
        if(root.mkdir()) {
            File config = new File(root.getPath(), "config");
            File share = new File(root.getPath(), "share");
            File log = new File(root.getPath(), "log");

            return config.mkdir() && share.mkdir() && log.mkdir();
        }
        return false;
    }

    public static void createLogFile(File root) throws IOException {
        File access_log = new File(root.getPath() + path_sep + "log", "access_log");
        File startup = new File(root.getPath() + path_sep + "log", "startup");

        boolean s1 = false, s2 = false;
        String s = "Server Started " + new Date();

        if(!access_log.exists()) s1 = access_log.createNewFile();
        if(!startup.exists()) s2 = startup.createNewFile();

        if(s1 || access_log.exists()) updateLog(access_log, s);
        if(s2 || startup.exists()) updateLog(startup, s);
    }

    public static synchronized void updateLog(File log, String s) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(log, true));
        pw.println(s);
        pw.flush();
        pw.close();
    }

    public static void createUser(File root, String pass) throws IOException {
        File user_hash = new File(root.getPath() + path_sep + "config", "user_hash");

        String hash = getSHA256Hash(pass);
        String username = System.getProperty("user.name").replace(" ", "");
        String store = username + ":" + hash;

        user_pass = new HashMap<>();
        user_pass.put(username, hash);

        PrintWriter pw = new PrintWriter(user_hash);
        pw.println(store);
        pw.close();
    }

    public static void loadUser(File root) throws FileNotFoundException {
        File user_hash = new File(root.getPath() + path_sep + "config", "user_hash");
        Scanner in = new Scanner(user_hash);
        user_pass = new HashMap<>();
        while(in.hasNext()){
            String[] pair = in.nextLine().split(":");
            user_pass.put(pair[0], pair[1]);
        }
    }

    public static synchronized boolean verifyUser(String[] data){
        return user_pass.containsKey(data[0]) && user_pass.get(data[0]).equals(getSHA256Hash(data[1]));
    }

    private static synchronized String getSHA256Hash(String input) {
        try {
            byte[] hashBytes = MessageDigest.getInstance("SHA-256").digest(input.getBytes());
            StringBuilder hashBuilder = new StringBuilder();

            for (byte b : hashBytes) {
                hashBuilder.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }

            return hashBuilder.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
