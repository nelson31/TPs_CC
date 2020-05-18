import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sender {

    private static AnonSocket asocket;

    private static String ipDest;

    public Sender(int port, String IP){

        try {
            asocket = new AnonSocket(port, IP, new ForeignSessions(), new SessionGetter());
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println("Erro ao criar o socket");
        }
    }

    public static void main(String[] args){

        while(true){
            try {
                System.out.println("Mensagem para enviar:");
                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                String message = null;
                while ((message = bf.readLine()) != null) {
                    byte[] data = message.getBytes();
                    asocket.send(0, data, ipDest, ipDest, 0);
                    System.out.println("Mensagem para enviar:");
                }
            }
            catch(IOException exc){
                System.out.println("Erro ao ler pedido");
            }
        }
    }
}
