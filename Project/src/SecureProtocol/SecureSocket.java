package SecureProtocol;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SecureSocket {

    /**
     * Estrutura de dados que permite
     * comunicação
     */
    private ThreadSocket ssocket;

    /**
     * Estrutura de dados que permite atribuir id's
     * aos pacotes para tratar o envio de acks
     */
    private PacketIdGetter idGetter;

    private Lock l;

    private Condition c;

    /**
     * Construtor para objetos da
     * classe SecureProtocol.SecureSocket
     * @param port
     * @param localIP
     * @throws SocketException
     */
    public SecureSocket(int port, InetAddress localIP)
            throws SocketException {

        this.ssocket = new ThreadSocket(port, localIP);
        this.idGetter = new PacketIdGetter();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    /**
     * Método que permite enviar
     * um packet secure
     * @param ss
     */
    public void send(SecurePacket ss, int seq) {

        int id = this.idGetter.get();
        ss.setId(id);
        boolean received = false;
        this.l.lock(); int i=0;
            /* Enviamos o pacote */
            this.ssocket.send(ss);
        this.l.unlock();
    }

    /**
     * Método que permite obter
     * um pacote secure
     * @return
     */
    public SecurePacket receive(){

        SecurePacket data = null;
        /* Recebemos o primeiro pacote
        que não seja ack */
        data = this.ssocket.receiveNotAck();
        /* Enviamos um ack para o destino */
        return data;
    }
}
