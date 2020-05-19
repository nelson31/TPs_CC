import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Reader implements Runnable {

    /**
     * Variável para a qual vão sendo
     * encaminhados os pacotes que são
     * lidos do socket
     */
    private ListPacket receiving;

    /**
     * Socket a partir do qual vão
     * sendo lidos os pacotes
     */
    private DatagramSocket socket;

    public Reader(DatagramSocket socket, ListPacket receiving) {

        this.receiving = receiving;
        this.socket = socket;
    }

    public void run() {

        try {
            /* Ficamos repetidamente
            a fazer o mesmo */
            while (true) {

                DatagramPacket dp = new DatagramPacket(new byte[21], 21);
                socket.receive(dp);
                /* Obtemos o pacote Secure */
                SecurePacket sp = SecurePacket.getFromByteArray(dp.getData());
                System.out.println("[Reader] Novo pacote lido: ");
                System.out.println(sp.toString());
                /* Colocamos o pacote na estrutura */
                this.receiving.addPacket(sp);
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}