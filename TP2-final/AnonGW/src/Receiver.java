import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Receiver {

    private static AnonSocket asocket;

    private static String ipDest;

    public Receiver(int port, String IP){

        try {
            asocket = new AnonSocket(port, IP, new ForeignSessions(), new SessionGetter());
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println("Erro ao criar o socket");
        }
    }

    public static void main(String[] args){

        try {
            System.out.println("Estou à espera de mensagens:");
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                byte[] data = asocket.read(0, new TargetServerInfo());
                String message = new String(data);
                System.out.println("Mensagem lida: " + message);
            }
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao ler pedido");
        }
    }
}
