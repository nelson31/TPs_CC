import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Worker implements Runnable{

    /**
     * Quantidade fixa de bytes para
     * escrever para o socket de output
     */
    private static final int MAX_SIZE_TRANSFER = 1024*1024;
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
        int bytesReaded = 0;
        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        /* Lemos os dados do socket que se comporta
        como cliente para o buffer intermediário */
        while((lidos = read.read(transfer,bytesReaded,MAX_SIZE_TRANSFER)) != -1)
            bytesReaded += lidos;
        /* Copiamos o conteúdo do buffer intermediário
        para o socket destino */
        write.write(transfer,0,bytesReaded);
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
                /* Alternamos a leitura e escrita
                entre o servidor e o cliente */
                if(turn%2 == 0){
                    readFromTo(cliente,server);
                }
                else{
                    readFromTo(server,cliente);
                }
                turn++;
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
