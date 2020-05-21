package Tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketReader implements Runnable {

    private Socket socket;

    public SocketReader(Socket socket){

        this.socket = socket;
    }

    public void run(){

        try {
            InputStream is = socket.getInputStream();
            byte[] data = new byte[1024];
            String message; int lidos = 0;
            while((lidos = is.read(data,0,1024)) != -1){
                message = new String(data, StandardCharsets.UTF_8);
                System.out.println(message);
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
