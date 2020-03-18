import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class Worker implements Runnable{

    /**
     * Quantidade fixa de bytes para
     * escrever para o socket de output
     */
    private static final int MAX_SIZE_TRANSFER = 64;
    /**
     * Socket a partir do qual cada
     * worker receberá dados
     */
    private Socket receive;

    /**
     * Buffer que permite transferir os
     * dados de um socket para outro
     */
    private byte[] transfer;

    /**
     * Socket a partir do qual cada
     * worker enviará dados
     */
    private Socket send;

    /**
     * Construtor para objetos da
     * classe Worker
     * @param receive
     * @param IPAdd
     * @param port
     */
    public Worker(Socket receive, String IPAdd, String port) {

        try {
            this.receive = receive;
            this.send = new Socket(IPAdd, Integer.parseInt(port));
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
            InputStream read = this.receive.getInputStream();
            OutputStream write = this.send.getOutputStream();
            int lidos;
            /* Lemos os dados de um socket para outro*/
            while((lidos = read.read(transfer,0,MAX_SIZE_TRANSFER)) != 0){
                write.write(transfer,0,lidos);
            }

        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
