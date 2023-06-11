import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CMD {
    public static boolean start = true;
    public static void handleSocket(String[] cmd, DataInputStream in, DataOutputStream out, File dir) throws IOException{
        if(cmd[0].equalsIgnoreCase("list")){
            listDirectory(out, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("get")){
            for(int i=1;i<cmd.length;i++)
                FTP.send(cmd[i], out, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("put")){
            for(int i=1;i<cmd.length;i++)
                FTP.receive(cmd[i], in, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("cd")){
            changeDirectory(cmd, out, dir);
        }
        else if(cmd[0].equalsIgnoreCase("pwd")){
            out.writeUTF(returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("whoami")){
            out.writeUTF(HELPER.current_user);
        }
        else if(cmd[0].equalsIgnoreCase("exit")){
            out.writeUTF("exit");
        }
        else{
            out.writeUTF("Invalid Command");
        }
    }

    public static void listDirectory(DataOutputStream out, String d) throws IOException {
        File dir = new File(d);
        String[] dirs = dir.list();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i< Objects.requireNonNull(dirs).length; i++){
            res.append(dirs[i]).append("\t");
        }
        out.writeUTF(res.toString());
    }

    public static void changeDirectory(String[] cmd, DataOutputStream out, File dir) throws IOException{
        if(cmd.length > 1 && cmd[1] != null) {
            if (cmd[1].equals("..")) {
                String cwd = returnDirectory(dir);
                String path = cwd.substring(0, cwd.lastIndexOf(HELPER.path_sep));
                if(path.contains("jftp" + HELPER.path_sep + "share")) {
                    start = false;
                    System.setProperty("user.dir", path);
                    out.writeUTF("Current directory " + path);
                }
                else out.writeUTF("Unable to change directory");
                return;
            }
            String path = returnDirectory(dir) + HELPER.path_sep + cmd[1];
            File fp = new File(path);
            if (fp.exists() && fp.isDirectory()) {
                start = false;
                System.setProperty("user.dir", fp.getAbsolutePath());
                out.writeUTF("Directory changed to " + cmd[1]);
            }
            else out.writeUTF("Directory Invalid");
        }
        else out.writeUTF("Unable to change directory");
    }

    public static String returnDirectory(File dir){
        if(start) return dir.getAbsolutePath();
        else return System.getProperty("user.dir");
    }
}
