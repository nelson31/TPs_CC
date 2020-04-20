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

    private static final boolean FROM_CLIENT = true;

    private static final boolean FROM_SERVER = false;
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
     * Variável que guarda o sentido
     * atual dos dados
     */
    private boolean sentido;

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
            this.sentido = FROM_CLIENT;
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Método que permite ler uma certa quantidade
     * de bytes de um socket para outro
     * @param input
     * @param output
     * @throws IOException
     */
    public void readFromTo(Socket input, Socket output)
        throws IOException{

        InputStream read = input.getInputStream();
        OutputStream write = output.getOutputStream();
        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        /* Lemos os dados do socket que se comporta
        como cliente para o buffer intermediário */
        while((lidos = read.read(transfer,0,MAX_SIZE_TRANSFER)) != -1)
            write.write(transfer,0,lidos);
        /* Copiamos o conteúdo do buffer intermediário
        para o socket destino */
    }

    /**
     * Implementação do metodo run
     * para objetos da classe Worker
     */
    public void run(){

        try {
            int turn = 0;
            int lidos;
            /* Lemos os dados de um socket para outro*/
            while(!cliente.isClosed() && !server.isClosed()){
                Thread t1 = new Thread(new ReaderFromTo(cliente,server,transfer));
                Thread t2 = new Thread(new ReaderFromTo(server,cliente,transfer));
                t1.start();
                t2.start();
                /* Esperamos que ambas
                as threads terminem */
                t1.join();
                t2.join();
            }
        }
        catch(IOException | InterruptedException exc){
            System.out.println(exc.getMessage());
        }
    }
}
