import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ReaderFromAnonToServer implements Runnable {

    /**
     * Socket que se comporta
     * como input
     */
    private Socket server;

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
     * @param server
     * @param anon
     * @throws IOException
     */
    public ReaderFromAnonToServer(Socket server, AnonSocket anon, SessionGetter idSessionGetter,
                                  String destinationIP, int sessionID)
            throws IOException
    {
        this.server = server;
        this.anon = anon;
        this.transfer = new byte[4096];
        this.destinationIP = destinationIP;
        this.sessionID = sessionID;
    }

    public void run(){

        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        try {
            OutputStream output = this.server.getOutputStream();
            /* Lemos os dados do AnonSocket que se comporta
            como cliente para o buffer intermediário */
            this.anon.read(this.sessionID,this.transfer);
            /* Enviamos os dados de uma vez para o cliente */
            output.write(transfer,0,transfer.length);
        }
        catch(IOException | InterruptedException exp){
            System.out.println("[" + exp.getClass().getSimpleName() + "] - " + exp.getMessage());
        }
    }
}
