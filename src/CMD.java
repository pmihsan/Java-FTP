import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CMD {
    public static void handleSocket(String[] cmd, DataInputStream in, DataOutputStream out, File dir) throws IOException{
        if(cmd[0].equalsIgnoreCase("list")){
            listDirectory(out, dir);
        }
        else if(cmd[0].equalsIgnoreCase("get")){
            for(int i=1;i<cmd.length;i++)
                FTP.send(cmd[i], out, dir.getCanonicalPath());
        }
        else if(cmd[0].equalsIgnoreCase("put")){
            for(int i=1;i<cmd.length;i++)
                FTP.receive(cmd[i], in, dir.getCanonicalPath());
        }
        else if(cmd[0].equalsIgnoreCase("pwd")){
            out.writeUTF(dir.getCanonicalPath());
        }
        else if(cmd[0].equalsIgnoreCase("whoami")){
            out.writeUTF(System.getProperty("user.name"));
        }
        else if(cmd[0].equalsIgnoreCase("exit")){
            out.writeUTF("exit");
        }
        else{
            out.writeUTF("Invalid Command");
        }
    }

    public static void listDirectory(DataOutputStream out, File d) throws IOException {
        String[] dirs = d.list();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i< Objects.requireNonNull(dirs).length; i++){
            res.append(dirs[i]).append("\t");
        }
        out.writeUTF(res.toString());
    }
}
