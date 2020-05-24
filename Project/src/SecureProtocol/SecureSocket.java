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

    private Lock send;

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
        this.send = new ReentrantLock();
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
        int i=0;
        this.l.lock();
        while (!received) {
            /* Enviamos o pacote */
            this.ssocket.send(ss);
            /* Verificamos se chegou o ack */
            if(this.ssocket.waitForAck(-ss.getId(),50))
                received = true;
            if(i>0)
                System.out.println("Vou reenviar pacote de seq: " + seq);
            i++;
        }
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
        System.out.println("[Separe]Recebi novo pacote de dados");
        /* Enviamos um ack para o destino */
        SecurePacket pack = SecurePacket.getAck(data.getId(),data.getDestino(),data.getOrigem(),data.getPort());
        this.ssocket.send(pack);
        //System.out.println("[SecureSocket]Vou enviar ack");
        return data;
    }
}
