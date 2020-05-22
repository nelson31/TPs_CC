package Components;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.DataInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ReaderFromStreamToSocket implements Runnable{

    /**
     * Stream a partir da qual recebemos dados
     * de sessões para enviar para um socket TCP
     */
    private AnonStream stream;

    /**
     * Socket para o qual vamos enviar os
     * dados lidos através da stream
     */
    private Socket socket;

    /**
     * Variável que guarda o id da sessão
     * da qual estamos a tratar
     */
    private int sessionID;

    /**
     * Construtor para objetos da classe
     * ReaderFromStreamToSocket
     */
    public ReaderFromStreamToSocket(AnonStream astream, Socket socket, int sessionID) {

        this.stream = astream;
        /* Ativação da extremidade de leitura da stream */
        this.stream.enableInputStream();
        this.socket = socket;
        this.sessionID = sessionID;
    }

    public void run() {

        try {
            OutputStream os = this.socket.getOutputStream();
            byte[] data; String message;
            while ((data = this.stream.read()) != null) {
                message = new String(data, StandardCharsets.UTF_8);
                System.out.println("[ReaderFromStream] Recebi dados da stream: " + message);
                /* Enviamos os dados para o socket */
                os.write(data,0,data.length);
            }
            System.out.println("[ReaderFromSocket] Fechei a ligação");
            /* Removemos uma linha da foreign sessions
            se se tratar de uma sessão externa */
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}