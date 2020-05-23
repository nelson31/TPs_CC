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
    }

    /**
     * Método que permite enviar
     * um packet secure
     * @param ss
     */
    public void send(SecurePacket ss) {

        int id = this.idGetter.get();
        ss.setId(id);
        boolean received = false;
        while (!received) {
            /* Enviamos o pacote */
            this.ssocket.send(ss);
            /* Verificamos se chegou o ack */
            if(this.ssocket.waitForAck(-ss.getId(),250))
                received = true;
        }
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