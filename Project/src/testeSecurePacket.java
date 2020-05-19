import SecureProtocol.SecurePacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class testeSecurePacket {

    public static void main(String[] args){

        byte[] data = {12,23,42,12};
        try {
            SecurePacket packet = SecurePacket.getAck(5,InetAddress.getByName("localhost"),InetAddress.getByName("localhost"),6666);

            byte[] array = packet.toByteArray();
            for(int i=0; i<array.length; i++)
                System.out.print(array[i]);

            SecurePacket reconstruido = SecurePacket.getFromByteArray(array);

            System.out.println(reconstruido.toString());
        }
        catch(UnknownHostException exc){
            System.out.println("Endereço ip desconhecido");
        }
    }
}
