import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CLIENT {
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("CLIENT: Unable to connect to server");
            System.out.println("USAGE: java CLIENT user@ip_address port");
            System.exit(-1);
        }
        if(args.length == 1){
            System.out.println("CLIENT: Provide port number");
            System.exit(-1);
        }
        if(!FTP.isNumeric(args[1])){
            System.out.println("CLIENT: Invalid port number");
            System.exit(-1);
        }
        if(!args[0].contains("@")){
            System.out.println("CLIENT: Invalid hostname");
            System.exit(-1);
        }

        String[] details = args[0].split("@");
        String user = details[0];
        String ip = details[1];
        String pass =  "1234";
        int port = Integer.parseInt(args[1]);

        Console c = System.console();
        if(c != null){
            System.out.print("Enter your password: ");
            pass = new String(c.readPassword());
        }

        Scanner sc = new Scanner(System.in);
        DataOutputStream out;
        DataInputStream in;

        try {
            Socket s = new Socket(ip, port);
            out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(user + "@" + pass);
            out.flush();

            in = new DataInputStream(s.getInputStream());
            int res = in.read();
            if(res == 0){
                System.out.println("CLIENT: Invalid User or Password");
                System.exit(-1);
            }
            System.out.println("Connected to FTP Server");

            String data;
            do {
                System.out.print("jftp> ");
                data = sc.nextLine();
                out.writeUTF(data);
                String[] cmd = data.split("\s");
                if(cmd[0].equalsIgnoreCase("get")) {
                    for(int i=1;i<cmd.length;i++)
                        FTP.receive(cmd[i], in, ".");
                }
                else if(cmd[0].equalsIgnoreCase("put")) {
                    for(int i=1;i<cmd.length;i++)
                        FTP.send(cmd[i], out, ".");
                }
                else{
                    System.out.println(in.readUTF());
                }
            } while(!data.equalsIgnoreCase("exit"));

            in.close();
            out.close();
        }
        catch(IOException ie){
            System.out.println(ie.getMessage());
        }
        System.out.println("Connection closed with the server");
    }
}
