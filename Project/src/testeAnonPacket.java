import AnonProtocol.AnonPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class testeAnonPacket {

    public static void main(String[] args){

        byte[] data = {12,23,42,12,13,23};
        try {
            AnonPacket packet = new AnonPacket(5, 2, 6, 80, InetAddress.getByName("localhost"), data);

            byte[] array = packet.toByteArray();
            for (int i = 0; i < array.length; i++)
                System.out.print(array[i]);

            System.out.println();

            AnonPacket reconstruido = AnonPacket.getFromByteArray(array);

            System.out.println(reconstruido.toString());
        }
        catch(UnknownHostException exc){
            System.out.println(exc.getMessage());
        }
    }
}
