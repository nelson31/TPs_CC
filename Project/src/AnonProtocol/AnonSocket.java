package AnonProtocol;

import Components.AnonAccepter;
import Components.ForeignSessions;
import SecureProtocol.SecurePacket;
import SecureProtocol.SecureSocket;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AnonSocket {

    /**
     * Variável que permite comunicar com
     * outras máquina com UDP de forma algo efetiva
     */
    private SecureSocket socket;

    /**
     * Variável que guarda o endereço IP local
     */
    private InetAddress localIp;

    /**
     * Variável que mantém associação entre o id
     * de uma sessão e os respetivos pacotes
     * que pertencem a essa mesma sessão
     */
    private MappingTable received;

    /**
     * Runnable que le os pacotes Anon e os separa
     * por sessões para a MappingTable
     */
    private SessionSepare ssepare;

    ///////////////////////////////ATRIBUIR ID'S LOCAIS A SESSÕES EXTERNAS//////////////////////////////////////

    /**
     * Estrutura de dados que permite a pacotes de
     * owner externo obterem um id de sessão local
     */
    private ForeignSessions foreignSessions;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Lock l;

    /**
     * Construtor para objetos da classe
     * AnonSocket
     */
    public AnonSocket(int port, InetAddress localIp, ForeignSessions foreignSessions)
            throws SocketException {

        this.localIp = localIp;
        this.socket = new SecureSocket(port,localIp);
        this.received = new MappingTable();
        this.foreignSessions = foreignSessions;
        this.l = new ReentrantLock();
        this.ssepare = new SessionSepare(this.socket,this.received,this.foreignSessions,this.localIp);
        /* Colocamos o reader a correr */
        new Thread(this.ssepare).start();
    }


    /**
     * Método que permite enviar um AnonPacket
     * para um determinado destino
     * @param ap
     * @param origem
     * @param destino
     * @param destPort
     */
    public void send(AnonPacket ap, InetAddress origem, InetAddress destino, int destPort){

        /* Convertemos o anonPacket para bytes */
        byte[] body = ap.toByteArray();
        /* Encapsulamos o anonPacket num SecurePacket */
        SecurePacket sp = new SecurePacket(-1,origem,destino,destPort,body.length,body);
        this.l.lock();
        /* Enviamos para o destino respetivo */
        this.socket.send(sp);

        this.l.unlock();
    }

    /**
     * Método que permite receber um anonPacket
     * com um determinado id de sessão
     * @param session
     */
    public AnonPacket receive(int session){

        this.l.lock();

        AnonPacket ap = this.received.getPacket(session);

        this.l.unlock();
        return ap;
    }

    public InetAddress getLocalIp(){

        return this.localIp;
    }
}
