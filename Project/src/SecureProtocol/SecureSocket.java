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

    private Lock lock;

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
        this.lock = new ReentrantLock();
        this.c = lock.newCondition();
    }

    /**
     * Método que permite enviar
     * um packet secure
     * @param ss
     */
    public void send(SecurePacket ss) {

        Lock l = new ReentrantLock();
        Condition c = l.newCondition();
        System.out.println("Estou à espera para enviar secure packet");
        int id = this.idGetter.get();
        ss.setId(id);
        boolean received = false;
        this.lock.lock();
        while (!received) {
            this.ssocket.prepareRecebeAck(-ss.getId(),l,c);
            System.out.println("[SecureSocket] Vou enviar novo secure packet");
            /* Enviamos o pacote */
            this.ssocket.send(ss);
            /* Verificamos se chegou o ack */
            if(this.ssocket.waitForAck(-ss.getId(),250,l,c))
                received = true;
        }
        this.lock.unlock();
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
        SecurePacket pack = SecurePacket.getAck(data.getId(),data.getDestino(),data.getOrigem(),data.getPort());
        this.ssocket.send(pack);
        return data;
    }
}