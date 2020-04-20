import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Worker implements Runnable{

    /**
     * Quantidade fixa de bytes para
     * escrever para o socket de output
     */
    public static final int MAX_SIZE_TRANSFER = 1024;
    /**
     * Socket a partir do qual cada
     * worker receberá dados
     */
    private Socket cliente;

    /**
     * Buffer que permite transferir os
     * dados de um socket para outro
     */
    private byte[] transfer;

    /**
     * Socket a partir do qual cada
     * worker enviará dados
     */
    private Socket server;

    /**
     * Construtor para objetos da
     * classe Worker
     * @param cliente
     * @param IPAdd
     * @param port
     */
    public Worker(Socket cliente, String IPAdd, String port) {

        try {
            this.cliente = cliente;
            this.server = new Socket(IPAdd, Integer.parseInt(port));
            this.transfer = new byte[MAX_SIZE_TRANSFER];
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Implementação do metodo run
     * para objetos da classe Worker
     */
    public void run(){

        try {
            /* Arrancamos um thread que lê do socket do cliente para o socket do servidor */
            Thread t1 = new Thread(new ReaderFromTo(cliente,server,transfer));
            Thread t2 = new Thread(new ReaderFromTo(server,cliente,transfer));
            t1.start();
            t2.start();
            /* Esperamos que ambas
            as threads terminem */
            t1.join();
            t2.join();
        }
        catch(IOException | InterruptedException exc){
            System.out.println(exc.getMessage());
        }
    }
}
