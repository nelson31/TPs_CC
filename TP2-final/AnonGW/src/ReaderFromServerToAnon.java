import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReaderFromServerToAnon implements Runnable {

    /**
     * Socket que se comporta
     * como input
     */
    private Socket cliente;

    /**
     * Socket que se comporta
     * como output
     */
    private AnonSocket anon;

    /**
     * Variável que guarda o endereço IP
     * do targetserver
     */
    private String destinationIP;

    /**
     * Variável que guarda a port do targetServer
     */
    private int destinationPort;

    /**
     * Valor que guarda o id da sessão
     */
    int sessionID;

    private byte[] transfer;

    /**
     * Construtor para objetos da classe ReaderFromClientToAnon
     * @param cliente
     * @param anon
     * @throws IOException
     */
    public ReaderFromServerToAnon(Socket cliente, AnonSocket anon, SessionGetter idSessionGetter,
                                  String destinationIP, int sessionID)
            throws IOException
    {
        this.cliente = cliente;
        this.anon = anon;
        this.transfer = new byte[4096];
        this.destinationIP = destinationIP;
        this.sessionID = sessionID;
    }

    public void run(){

        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        try {
            InputStream input = this.cliente.getInputStream();
            /* Lemos os dados do socket que se comporta
            como cliente para o buffer intermediário */
            while ((lidos = input.read(transfer, 0, Worker.MAX_SIZE_TRANSFER)) != -1)
                this.anon.send(sessionID,transfer,this.destinationIP,destinationPort);
        /* Copiamos o conteúdo do buffer intermediário
        para o socket destino */
        }
        catch(IOException exp){
            System.out.println("[" + exp.getClass().getSimpleName() + "] - " + exp.getMessage());
        }
    }
}
