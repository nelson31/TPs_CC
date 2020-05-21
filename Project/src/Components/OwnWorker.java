package Components;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;

import java.net.InetAddress;
import java.net.Socket;

public class OwnWorker implements Runnable {

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
    private int sessionID;

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

    /////////////////////////////////////////////////////////////////////////////////////////




    /**
     * COnstrutor para objetos da
     * classe OwnWorker
     *
     * @param asocket
     * @param socket
     * @param sessionID
     */
    public OwnWorker(int sessionID, Socket socket, AnonSocket asocket, InetAddress nextHopIp,
                     InetAddress targetServerIp, int targetPort) {

        this.asocket = asocket;
        this.socket = socket;
        this.sessionID = sessionID;
        this.nextHopIp = nextHopIp;
        this.targetServerIp = targetServerIp;
        this.targetPort = targetPort;
    }

    public void run() {

        /* Criamos uma stream para ler e receber dados */
        AnonStream stream = new AnonStream(this.asocket,0);

        /* Criamos a thread que lê do socket TCP e
        envia os dados para o próximo AnonGW */
        ReaderFromSocketToStream sockToStream = new ReaderFromSocketToStream(stream,
                this.socket,this.sessionID,this.nextHopIp,
                6666,this.targetServerIp,this.targetPort);

        /* Criamos a thread que lê da stream Anon e
        envia os dados de volta para o cliente */
        ReaderFromStreamToSocket streamToSock = new ReaderFromStreamToSocket(stream,this.socket,this.sessionID);

        /* Colocamos ambas as threads a correr */
        Thread t1 = new Thread(sockToStream);
        Thread t2 = new Thread(streamToSock);

        t1.start();
        t2.start();
    }
}