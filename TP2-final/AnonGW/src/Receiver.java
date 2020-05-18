import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Receiver {

    public static void main(String[] args){

        try {
            AnonSocket asocket = new AnonSocket(6666, args[1], new ForeignSessions(), new SessionGetter());
            System.out.println("Estou à espera de mensagens:");
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                byte[] data = asocket.read(0, new TargetServerInfo());
                String message = new String(data);
                System.out.println("Mensagem lida: " + message);
            }
        }
        catch(InterruptedException | SocketException | UnknownHostException exc){
            System.out.println("Erro ao ler pedido");
        }
    }
}
