package AnonProtocol;

import SecureProtocol.SecurePacket;
import SecureProtocol.SecureSocket;

import java.net.UnknownHostException;

public class SessionSepare implements Runnable {

    /**
     * Socket a partir do qual vamos ler
     * pacotes para os separar por sessões -
     * O mesmo socket que o AnonSocket mantem
     */
    private SecureSocket ssocket;

    /**
     * Variável que vai armazenando os diferentes
     * packets anon separados por sessão
     */
    private MappingTable incoming;

    /**
     * Construtor para objetos da classe SessionSepare
     *
     * @param ssocket
     * @param incoming
     */
    public SessionSepare(SecureSocket ssocket, MappingTable incoming) {

        this.ssocket = ssocket;
        this.incoming = incoming;
    }

    public void run() {

        /**
         * Ficamos permanentemente à espera
         * de novos pacotes anon
         */
        while (true) {

            try {
                /* Recebemos um secure packet */
                SecurePacket sp = this.ssocket.receive();
                /* Vamos buscar o anonpacket encapsulado */
                if(sp.isAck())
                    System.out.println("Recebi um ACK PERIGO");
                AnonPacket ap = AnonPacket.getFromByteArray(sp.getData());
                /* Adicionamos o pacote à tabela */
                this.incoming.addPacket(ap,ap.getOwnerIP());
            }
            catch(UnknownHostException exc){
                System.out.println("Host inexistente");
            }
        }
    }
}