package SecureProtocol;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSocket {

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

    private Lock l;

    /**
     * Construtor para objetos da
     * classe SecureProtocol.ThreadSocket
     *
     * @param port
     * @param localIP
     */
    public ThreadSocket(int port, InetAddress localIP)
            throws SocketException {

        this.socket = new DatagramSocket(port, localIP);
        this.sending = new ListPacket();
        this.l = new ReentrantLock();
        this.receiving = new ListPacket();
        this.reader = new Reader(socket, this.receiving);
        this.writer = new Writer(socket, this.sending);
        new Thread(this.reader).start();
        new Thread(this.writer).start();
    }


    /**
     * Método que permite enviar uma datagrama
     *
     * @param packet
     */
    public void send(SecurePacket packet) {

        /* Adicionamos o pacote à estrutura
        de dados para ser enviado */
        this.sending.addPacket(packet);
    }

    /**
     * Método que permite obter um pacote
     * que não seja ack
     *
     * @return
     */
    public SecurePacket receiveNotAck() {

        return this.receiving.getDataPacket();
    }

    /**
     * Método que nos diz se existe na extremidade
     * de leitura um pacote com o id especificado
     *
     * @param id
     * @return
     */
    private boolean contains(int id) {

        return this.receiving.contains(id);
    }

    /**
     * Método que permite a uma thread esperar por um
     * ack após o envio de um determinado pacote
     */
    public boolean waitForAck(int id, int milis) {

        int waitmilis = 0;
        boolean ret = false;
        this.l.lock();
        try {
            /* Enquanto o ack não chegar */
            while (!this.contains(id) && waitmilis < milis) {
                Thread.sleep(10);
                waitmilis += 10;
            }
            /* Se o pacote tiver chegado retornamos true
            e eliminamos o pacote da lista de chegada */
            if (this.contains(id)) {
                ret = true;
                this.receiving.remove(id);
            }
        }
        catch(InterruptedException exc){
            System.out.println(exc.getLocalizedMessage());
        }
        finally {
            this.l.unlock();
        }
        return ret;
    }
}
