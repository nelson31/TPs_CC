package Components;

import AnonProtocol.AnonSocket;
import AnonProtocol.SessionGetter;
import AnonStreamProtocol.AnonStream;

import java.net.InetAddress;
import java.net.Socket;

public class Worker implements Runnable {

    /**
     * Variável que permite comunicar
     * com outros anonGW
     */
    private AnonSocket asocket;

    /**
     * Socket a partir do qual vamos ler
     * dados provenientes de um ciente
     */
    private Socket socket;

    /**
     * Variável que guarda a sessão à qual
     * o worker se encontra dedicado
     */
    private int incomingSessionId;

    /**
     * Id de sessão a considerar quando enviamos
     * pacotes para o anonGW
     */
    private int outgoingSessionId;

    /**
     * Variável para, no final da comunicação
     * com o cliente, ceder o id de sessão
     */
    private SessionGetter cedeId;

    /**
     * Estrutura de dados que permite reconhecer
     * se uma sessão é local ou externa
     */
    private ForeignSessions foreignSessions;

    ///////////////////////////////TARGET-SERVER/////////////////////////////////////////////

    /**
     * Variável que guarda o endereço IP do
     * targetServer para o qual o AnonGW garante o anonimo
     */
    private InetAddress targetServerIp;

    /**
     * Variável que guarda a porta de destino
     */
    private int targetPort;


    ///////////////////////////////Next-Hop//////////////////////////////////////////////////

    /**
     * Variável que guarda o endereço IP do
     * próximo AnonGW para o qual são enviados os dados
     */
    private InetAddress nextHopIp;

    ////////////////////////////Dados usados na cifragem das mensagens//////////////////////////

    private String password;

    private int Key1, Key2;

    ////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * COnstrutor para objetos da
     * classe Worker
     *
     * @param asocket
     * @param socket
     */
    public Worker(int incomingSessionId, int outgoingSessionId, Socket socket,
                  AnonSocket asocket, InetAddress nextHopIp, InetAddress targetServerIp,
                  int targetPort, SessionGetter cedeId, ForeignSessions foreignSessions,
                  String password, int Key1, int Key2) {

        this.asocket = asocket;
        this.socket = socket;
        this.incomingSessionId = incomingSessionId;
        this.outgoingSessionId = outgoingSessionId;
        this.nextHopIp = nextHopIp;
        this.targetServerIp = targetServerIp;
        this.targetPort = targetPort;
        this.cedeId = cedeId;
        this.foreignSessions = foreignSessions;
        this.password = password;
        this.Key1 = Key1;
        this.Key2 = Key2;
    }

    public void run() {

        /* Criamos uma stream para ler e receber dados */
        AnonStream stream = new AnonStream(this.asocket, this.incomingSessionId, this.outgoingSessionId);

        /* Criamos a thread que lê do socket TCP e
        envia os dados para o próximo AnonGW */
        ReaderFromSocketToStream sockToStream = new ReaderFromSocketToStream(stream,
                this.socket, this.incomingSessionId, this.cedeId, this.foreignSessions, this.nextHopIp,
                6666, this.targetServerIp, this.targetPort, this.password, this.Key1, this.Key2);

        /* Criamos a thread que lê da stream Anon e
        envia os dados de volta para o cliente */
        ReaderFromStreamToSocket streamToSock = new ReaderFromStreamToSocket(stream, this.socket,
                this.incomingSessionId,this.password, this.Key1, this.Key2);

        /* Colocamos ambas as threads a correr */
        Thread t1 = new Thread(sockToStream);
        Thread t2 = new Thread(streamToSock);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();

            /* Libertamos o id utilizado */
            this.cedeId.cedeID(this.incomingSessionId);
            /* Se for uma sessão externa removemos a
            linha da foreign sessions table */
            if(this.foreignSessions.isForeign(this.incomingSessionId)) {
                InetAddress owner = this.foreignSessions.getInfo(this.incomingSessionId).getOwnerIP();
                this.foreignSessions.removeAssociation(this.foreignSessions.getInfo(this.incomingSessionId).getId(), owner);
            }
        }
        catch(InterruptedException exc){
            System.out.println(exc.getLocalizedMessage());
        }

        System.out.println("[Worker] Conexão terminada");
    }
}