package SecureProtocol;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ThreadSocket {

    /**
     * Variável que permite
     * enviar os dados
     */
    private DatagramSocket socket;

    /**
     * Estrutura de dados para enviar pacotes
     */
    private ListPacketSending sending;

    /**
     * Estrutura de dados para receber pacotes
     */
    private ListPacketReceiving receiving;

    private Reader reader;

    private Writer writer;

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
        this.sending = new ListPacketSending();
        this.receiving = new ListPacketReceiving();
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
    public boolean waitForAck(int id, int milis, Lock l, Condition c) {

        boolean ret = false;
        BooleanEncapsuler timeoutreached = new BooleanEncapsuler(false);
        /* Obtens o lock */
        l.lock();

        try {
            /* Colocamos o timeout a correr */
            new Thread(new TimeOut(milis,l,c,timeoutreached)).start();
            /* Enquanto o ack não chegar */
            while (!this.contains(id) && !timeoutreached.getB())
                c.await();

            /* Se o pacote tiver chegado retornamos true
            e eliminamos o pacote da lista de chegada */
            if(this.contains(id)) {
                ret = true;
                this.receiving.remove(id);
            }
        }
        catch(InterruptedException exc){
            System.out.println(exc.getLocalizedMessage());
        }
        finally {
            l.unlock();
        }

        return ret;
    }

    /**
     * Método que prepara a receção de um ack
     * @param id
     * @param l
     * @param c
     */
    public void prepareRecebeAck(int id, Lock l, Condition c){

        this.receiving.prepareRecebeAck(id, l, c);
    }
}