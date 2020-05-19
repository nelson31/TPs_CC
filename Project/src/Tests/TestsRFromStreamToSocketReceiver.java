package Tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TestsRFromStreamToSocketReceiver {

    public static void main(String[] args) {

        try {
            System.out.println("À espera de pedidos: ");
            ServerSocket accepter = new ServerSocket(80, 0, InetAddress.getByName(args[0]));
            Socket socket = accepter.accept();
            System.out.println("Pedido aceite");
            InputStream is = socket.getInputStream();
            byte[] data = new byte[1024]; int lidos = 0;
            while((lidos = is.read(data,0,data.length)) != -1){
                for(int i=0; i<lidos; i++)
                    System.out.print(data[i]);
                System.out.println();
            }
            socket.shutdownInput();
            socket.shutdownInput();
            socket.close();
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}