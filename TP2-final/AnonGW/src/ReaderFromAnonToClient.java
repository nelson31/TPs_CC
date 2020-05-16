import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ReaderFromAnonToClient implements Runnable {

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
    public ReaderFromAnonToClient(Socket cliente, AnonSocket anon, SessionGetter idSessionGetter,
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
            OutputStream output = this.cliente.getOutputStream();
            /* Lemos os dados do AnonSocket que se comporta
            como cliente para o buffer intermediário */
            this.anon.read(this.sessionID,transfer);
            /* Enviamos os dados de uma vez para o cliente */
            output.write(transfer,0,transfer.length);
        }
        catch(IOException | InterruptedException exp){
            System.out.println("[" + exp.getClass().getSimpleName() + "] - " + exp.getMessage());
        }
    }
}
