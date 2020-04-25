import java.net.DatagramSocket;
import java.net.SocketException;

public class AnonSocket {

    /**
     * Socket a partir do qual recebemos e
     * enviamos os dados para outros anonGW
     */
    private DatagramSocket s;

    /**
     * Construtor para objetos da classe
     * AnonSocket
     * @param port
     * @throws SocketException
     */
    public AnonSocket(int port)
        throws SocketException {

        this.s = new DatagramSocket(port);
    }

    /**
     * Implementação do método read
     * @return
     */
    public int send(AnonPacket ap){

        return -1;
    }
}
