package FinalTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerWorker implements Runnable {

    /**
     * Variável que permite comunicar
     * com o cliente
     */
    private Socket socket;

    /**
     * Construtor para objetos da classe
     * ServerWorker
     *
     * @param socket
     */
    public ServerWorker(Socket socket) {

        this.socket = socket;
    }

    public void run() {

        int size;
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String message;
            while ((message = bf.readLine()) != null) {
                size = message.length();
                pw.println("Tamanho da string: " + size);
                pw.flush();
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
        }
        catch(IOException exc){
            System.out.println(exc.getLocalizedMessage());
        }
    }
}