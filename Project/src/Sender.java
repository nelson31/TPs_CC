import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Sender {

    public static void main(String[] args){

        try {
            AnonSocket asocket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            InetAddress destination = InetAddress.getByName(args[1]);
            String message;
            while((message = bf.readLine()) != null){

                byte[] data = {12,42,23,43,23};
                SecurePacket sp = new SecurePacket(0,InetAddress.getByName(args[0]),destination,6666,data);
                asocket.send(sp);
            }
        }
        catch(IOException exc){
            System.out.println("Erro ao criar socket");
        }
    }
}
