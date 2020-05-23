package AnonProtocol;

import Components.ForeignSessions;
import Components.SessionData;
import SecureProtocol.SecurePacket;
import SecureProtocol.SecureSocket;

import java.net.InetAddress;
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
     * Variável para registar a chegada de uma
     * sessão externa
     */
    private ForeignSessions foreignSessions;

    ///////////////////////////////ENDEREÇO IP DO ANON LOCAL///////////////////////////////////

    /**
     * Variável que guarda o endereço
     * IP do AnonGW local
     */
    private InetAddress localIp;


    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construtor para objetos da classe SessionSepare
     *
     * @param ssocket
     * @param incoming
     */
    public SessionSepare(SecureSocket ssocket, MappingTable incoming,
                         ForeignSessions foreignSessions, InetAddress localIp) {

        this.ssocket = ssocket;
        this.incoming = incoming;
        this.foreignSessions = foreignSessions;
        this.localIp = localIp;
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

                /* Vamos buscar o id da sessão e o owner do pacote anon */
                int id = ap.getSession();
                InetAddress owner = ap.getOwnerIP();
                /* Se o owner não for o anon local, teremos
                que converter o id de sessão */
                if(!owner.toString().equals(this.localIp.toString())) {
                    System.out.println("[Separe] Recebi novo pedido de peer: Owner do pack: " + owner.toString() + "; Local Ip: " + this.localIp.toString());
                    /* Se for uma sessão externa temos que verificar na
                    foreign table e se não existir teremos que a adicionar */
                    if (!this.foreignSessions.contains(id, owner)) {
                        this.foreignSessions.addForeignSession(id, owner, ap.getTargetServerIP(), ap.getTargetPort());
                        //System.out.println("Criei nova entrada para a sessão: id: " + id + "; owner: " + owner);
                    }
                    ap.setSession(this.foreignSessions.getLocalSession(id,owner));
                }
                /* Adicionamos o pacote à tabela */
                this.incoming.addPacket(ap,ap.getOwnerIP());
                if(ap.getPayloadSize() == -1)
                    System.out.println("[Separe] Recebi pacote de fecho");
            }
            catch(UnknownHostException exc){
                System.out.println("Host inexistente");
            }
        }
    }
}