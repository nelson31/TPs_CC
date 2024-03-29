package SecureProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Writer implements Runnable {

    /**
     * Estrutura de dados para a qual o
     * writer envia os pacotes
     */
    private ListPacket sending;

    /**
     * Socket para o qual o writer
     * lê os dados
     */
    private DatagramSocket socket;

    /**
     * Construtor para objetos
     * da classe SecureProtocol.Writer
     */
    public Writer(DatagramSocket socket, ListPacket sending) {

        this.sending = sending;
        this.socket = socket;
    }

    public void run() {

        try {
            /* Ficamos permanentemente à espera
            de pacotes para encaminhar */
            while (true) {
                /* Obtemos o primeiro pacote
                disponível para enviar */
                SecurePacket sp = this.sending.getPacket();

                /* Construimos o datagram packet
                para enviar */
                DatagramPacket packet = new DatagramPacket(sp.toByteArray(),
                        sp.toByteArray().length, sp.getDestino(), sp.getPort());

                /* Enviamos o pacote */
                this.socket.send(packet);
            }
        }
        catch(IOException exc){
            System.out.println("Erro ao enviar o pacote - " + exc.getClass() + " - " + exc.getMessage());
        }
    }
}