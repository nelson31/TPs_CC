import java.io.IOException;
import java.net.Socket;

public class ReaderFromTo implements Runnable {

    /**
     * Socket que se comporta
     * como input
     */
    private Socket input;

    /**
     * Socket que se comporta
     * como output
     */
    private AnonSocket output;

    private byte[] transfer;

    /**
     * Construtor para objetos da classe ReaderFromTo
     * @param input
     * @param output
     * @throws IOException
     */
    public ReaderFromTo(Socket input, Socket output, byte[] transfer)
            throws IOException
    {
        this.input = input.getInputStream();
        this.output = output.getOutputStream();
        this.transfer = transfer;
    }


    public void run(){

        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        try {
        /* Lemos os dados do socket que se comporta
        como cliente para o buffer intermediário */
            while ((lidos = input.read(transfer, 0, Worker.MAX_SIZE_TRANSFER)) != -1)
                output.write(transfer, 0, lidos);
        /* Copiamos o conteúdo do buffer intermediário
        para o socket destino */
        }
        catch(IOException exp){
            System.out.println("[" + exp.getClass().getSimpleName() + "] - " + exp.getMessage());
        }
    }
}
