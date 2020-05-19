import java.net.InetAddress;
import java.net.SocketException;

public class AnonSocket {

    private SecureSocket ssocket;

    public AnonSocket(int port, InetAddress localIP)
            throws SocketException {

        this.ssocket = new SecureSocket(port, localIP);
    }

    /**
     * Método que permite enviar
     * um packet secure
     * @param ss
     */
    public void send(SecurePacket ss) {

        try {
            boolean received = false;
            while (!received) {
                this.ssocket.send(ss);
                Thread.sleep(250);
                if (this.ssocket.contains(-ss.getId()))
                    received = true;
            }
        }
        catch(InterruptedException exc){
            System.out.println(exc.getMessage());
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