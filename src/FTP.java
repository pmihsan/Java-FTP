import java.io.*;

public class FTP {
    public static boolean isNumeric(String s){
        for(int i=0;i<s.length();i++){
            if(!(Character.isDigit(s.charAt(i)))) return false;
        }
        return true;
    }
    public void receive(String file, DataInputStream in, String d) throws IOException {

        if(in.readUTF().equalsIgnoreCase("yes")){
            System.out.print("Receiving File: " + file + " ");

            long size = in.readLong();
            System.out.println("Size: " + size + " bytes");

            int bytes;
            byte[] buffer = new byte[getLength(size) * 1024];

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

    public void send(String file, DataOutputStream out, String d) throws IOException{
        System.out.print("Sending File: " + file + " ");
        File temp = new File(d, file);

        if(temp.exists() && temp.isFile()){
            out.writeUTF("yes");
            System.out.println("Size: " + temp.length() + " bytes");
            long size = temp.length();
            out.writeLong(size);

            FileInputStream fis = new FileInputStream(temp);
            int bytes;
            byte[] buffer = new byte[getLength(size) * 1024];
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

    public int getLength(long size){
        double kb = size / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        if(gb >= 1) {
            System.out.println("Data Transfer at 10 MB/s");
            return 10000;
        }
        else if(mb >= 1) {
            System.out.println("Data Transfer at 2 MB/s");
            return 2000;
        }
        else if(kb >= 1) {
            System.out.println("Data Transfer at 20 KB/s");
            return 20;
        }
        System.out.println("Data Transfer at 5 KB/s");
        return 5;
    }

}
