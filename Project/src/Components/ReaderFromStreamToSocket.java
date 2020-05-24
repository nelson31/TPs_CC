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

    ////////////////////////////Dados usados na cifragem das mensagens//////////////////////////

    private String password;

    private int Key1, Key2;

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construtor para objetos da classe
     * ReaderFromStreamToSocket
     */
    public ReaderFromStreamToSocket(AnonStream astream, Socket socket, int sessionID, String password, int Key1, int Key2) {

        this.stream = astream;
        /* Ativação da extremidade de leitura da stream */
        this.stream.enableInputStream();
        this.socket = socket;
        this.sessionID = sessionID;
        this.password = password;
        this.Key1 = Key1;
        this.Key2 = Key2;
    }

    public void run() {

        try {
            OutputStream os = this.socket.getOutputStream();
            byte[] data; String message;
            while ((data = this.stream.read()) != null) {
                message = new String(data, StandardCharsets.UTF_8);
                System.out.println("[ReaderFromStream] Recebi dados da stream: " + message);
                /* Desencriptamos os dados antes de
                enviar para o socket */
                data = Encriptacao.desencriptar(data,this.password,this.Key1,this.Key2);
                /* Enviamos os dados para o socket */
                os.write(data,0,data.length);
            }
            System.out.println("[ReaderFromStream] Fechei a ligação");
            /* Removemos uma linha da foreign sessions
            se se tratar de uma sessão externa */
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}