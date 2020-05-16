import AnonProto.AnonPacket;

import java.net.UnknownHostException;

public class testePacket {

    public static void main(String args[]){

        try {
            byte[] data = {14,37,00,45,12,45};
            AnonPacket ap = new AnonPacket(data,12, 14, "localhost", "localhost", 80);

            byte[] packet = ap.toByteArray();



            AnonPacket pack = AnonPacket.getFromByteArray(packet);

            System.out.println(pack);

            for(int i=0; i<data.length; i++){
                System.out.print(data[i]);
            }
            System.out.println();
        }
        catch(UnknownHostException exc){
            System.out.println(exc.getMessage());
        }
    }
}
