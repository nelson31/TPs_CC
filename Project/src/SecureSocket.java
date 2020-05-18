import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SecureSocket {

    /**
     * Variável que permite
     * enviar os dados
     */
    private DatagramSocket socket;

    /**
     * Estrutura de dados para enviar pacotes
     */
    private ListPacket sending;

    /**
     * Estrutura de dados para receber pacotes
     */
    private ListPacket receiving;

    private Reader reader;

    private Writer writer;

    /**
     * Construtor para objetos da
     * classe SecureSocket
     * @param port
     * @param localIP
     */
    public SecureSocket(int port, InetAddress localIP)
            throws SocketException {

        this.socket = new DatagramSocket(port,localIP);
        this.sending = new ListPacket();
        this.receiving = new ListPacket();
        this.reader = new Reader(socket,this.receiving);
        this.writer = new Writer(socket,this.sending);
        new Thread(this.reader).start();
        new Thread(this.writer).start();
    }


    /**
     * Método que permite enviar uma datagrama
     * @param packet
     */
    public void send(SecurePacket packet){

        /* Adicionamos o pacote à estrutura
        de dados para ser enviado */
        this.sending.addPacket(packet);
    }

    /**
     * Método que permite obter um pacote
     * que não seja ack
     * @return
     */
    public SecurePacket receiveNotAck(){

        return this.receiving.getDataPacket();
    }

    /**
     * Método que nos diz se existe na extremidade
     * de leitura um pacote com o id especificado
     * @param id
     * @return
     */
    public boolean contains(int id){

        return this.receiving.contains(id);
    }
}
