import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReaderFromSocketToAnon implements Runnable {


    private static final int MAX_SIZE_TRANSFER = 4096;
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
     * Variável que guarda uma lista com os
     * endereços IP dos pares de Anon
     */
    private String peerIP;

    /**
     * Construtor para objetos da classe ReaderFromSocketToAnon
     * @param cliente
     * @param anon
     * @throws IOException
     */
    public ReaderFromSocketToAnon(Socket cliente, AnonSocket anon,
                                  String destinationIP, int destintationPort, int sessionID, String peerIP)
            throws IOException
    {
        this.cliente = cliente;
        this.anon = anon;
        this.transfer = new byte[4096];
        this.destinationIP = destinationIP;
        this.destinationPort = destintationPort;
        this.sessionID = sessionID;
        this.peerIP = peerIP;
    }

    public void run(){

        /* Vamos buscar o número de bytes a serem lidos */
        int lidos;
        try {
            List<Byte> list = new ArrayList<>();
            InputStream input = this.cliente.getInputStream();
            /* Lemos os dados do socket que se comporta
            como cliente para o buffer intermediário */
            while ((lidos = input.read(transfer, 0, MAX_SIZE_TRANSFER)) != -1)
                this.anon.send(sessionID,transfer,peerIP,this.destinationIP,destinationPort);
        /* Copiamos o conteúdo do buffer intermediário
        para o socket destino */
        }
        catch(IOException exp){
            System.out.println("[" + exp.getClass().getSimpleName() + "] - " + exp.getMessage());
        }
    }
}
