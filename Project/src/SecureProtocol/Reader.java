package SecureProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Reader implements Runnable {

    /**
     * Variável para a qual vão sendo
     * encaminhados os pacotes que são
     * lidos do socket
     */
    private ListPacketReceiving receiving;

    /**
     * Socket a partir do qual vão
     * sendo lidos os pacotes
     */
    private DatagramSocket socket;

    public Reader(DatagramSocket socket, ListPacketReceiving receiving) {

        this.receiving = receiving;
        this.socket = socket;
    }

    public void run() {

        try {
            /* Ficamos repetidamente
            a fazer o mesmo */
            while (true) {

                DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
                socket.receive(dp);
                /* Obtemos o pacote Secure */
                SecurePacket sp = SecurePacket.getFromByteArray(dp.getData());
                /* Criamos o novo campo data */
                byte[] data = new byte[sp.getPayloadSize()];
                byte[] antigo = sp.getData();
                for(int i=0; i<data.length; i++){
                    data[i] = antigo[i];
                }
                /* Atualizamos o campo data para o novo vetor */
                sp.setData(data);
                /* Colocamos o pacote na estrutura */
                this.receiving.addPacket(sp);
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}