import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CMD {
    private boolean start;
    private String pwd;

    CMD(File dir){
        start = true;
        pwd = dir.getAbsolutePath();
    }
    public void handleSocket(String[] cmd, DataInputStream in, DataOutputStream out, File dir, FTP ftp, HELPER h, String user) throws IOException{
        if(cmd[0].equalsIgnoreCase("list")){
            listDirectory(out, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("get")){
            for(int i=1;i<cmd.length;i++)
                ftp.send(cmd[i], out, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("put")){
            for(int i=1;i<cmd.length;i++)
                ftp.receive(cmd[i], in, returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("cd")){
            changeDirectory(cmd, out, dir, h);
        }
        else if(cmd[0].equalsIgnoreCase("mkdir")){
            makeDirectory(cmd, out, dir, h);
        }
        else if(cmd[0].equalsIgnoreCase("pwd")){
            out.writeUTF(returnDirectory(dir));
        }
        else if(cmd[0].equalsIgnoreCase("whoami")){
            out.writeUTF(user);
        }
        else if(cmd[0].equalsIgnoreCase("exit")){
            out.writeUTF("exit");
        }
        else{
            out.writeUTF("Invalid Command");
        }
    }

    public void listDirectory(DataOutputStream out, String d) throws IOException {
        File dir = new File(d);
        String[] dirs = dir.list();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i< Objects.requireNonNull(dirs).length; i++){
            res.append(dirs[i]).append("\t");
        }
        out.writeUTF(res.toString());
    }

    public void makeDirectory(String[] cmd, DataOutputStream out, File dir, HELPER h) throws IOException{
        if(cmd.length > 1 && cmd[1] != null) {
            String path = returnDirectory(dir) + h.path_sep + cmd[1];
            File fp = new File(path);
            if (!fp.exists()) {
                out.writeUTF("Directory created " + cmd[1] + " " + fp.mkdir());
            }
            else out.writeUTF("Directory Exists");
        }
        else out.writeUTF("Unable to create directory");
    }

    public void changeDirectory(String[] cmd, DataOutputStream out, File dir, HELPER h) throws IOException{
        if(cmd.length > 1 && cmd[1] != null) {
            if (cmd[1].equals("..")) {
                String cwd = returnDirectory(dir);
                String path = cwd.substring(0, cwd.lastIndexOf(h.path_sep));
                if(path.contains("jftp" + h.path_sep + "share")) {
                    start = false;
                    pwd = path;
                    out.writeUTF("Current directory " + path);
                }
                else out.writeUTF("Unable to change directory");
                return;
            }
            String path = returnDirectory(dir) + h.path_sep + cmd[1];
            File fp = new File(path);
            if (fp.exists() && fp.isDirectory()) {
                start = false;
                pwd =  fp.getAbsolutePath();
                out.writeUTF("Directory changed to " + cmd[1]);
            }
            else out.writeUTF("Directory Invalid");
        }
        else out.writeUTF("Unable to change directory");
    }

    public String returnDirectory(File dir){
        if(start) return dir.getAbsolutePath();
        else return pwd;
    }
}
