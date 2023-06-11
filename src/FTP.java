import java.io.*;

public class FTP {
    public static boolean isNumeric(String s){
        for(int i=0;i<s.length();i++){
            if(!(Character.isDigit(s.charAt(i)))) return false;
        }
        return true;
    }
    public static synchronized void receive(String file, DataInputStream in, String d) throws IOException {

        if(in.readUTF().equalsIgnoreCase("yes")){
            System.out.print("Receiving File: " + file + " ");

            int bytes;
            byte[] buffer = new byte[4 * 1024];

            long size = in.readLong();
            System.out.println("Size: " + size + " bytes");

            File temp = new File(d, file);
            FileOutputStream fos = new FileOutputStream(temp);
            while(size > 0 && (bytes = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
                fos.write(buffer, 0, bytes);
                size -= bytes;
            }
            if(in.readUTF().equalsIgnoreCase("close")) System.out.println("File received");
            fos.close();
        }
        else{
            System.out.println("File Not Found");
        }
    }

    public static synchronized void send(String file, DataOutputStream out, String d) throws IOException{
        System.out.print("Sending File: " + file + " ");
        File temp = new File(d, file);

        if(temp.exists() && temp.isFile()){
            out.writeUTF("yes");
            System.out.println("Size: " + temp.length() + " bytes");
            out.writeLong(temp.length());

            FileInputStream fis = new FileInputStream(temp);
            int bytes;
            byte[] buffer = new byte[4 * 1024];
            while((bytes = fis.read(buffer)) != -1){
                out.write(buffer, 0, bytes);
            }
            out.flush();

            System.out.println("File Sent");
            fis.close();
        }
        else{
            System.out.println("File " + file + " Not Found");
            out.writeUTF("no");
        }
        out.writeUTF("close");
    }
}
