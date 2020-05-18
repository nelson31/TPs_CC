import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Sender {

    public static void main(String[] args){

        try {
            AnonSocket asocket = new AnonSocket(6666, args[0], new ForeignSessions(), new SessionGetter());
            System.out.println("Mensagem para enviar:");
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String message = null;
            while ((message = bf.readLine()) != null) {
                byte[] data = message.getBytes();
                asocket.send(0, data, args[1], args[0], 0);
                System.out.println("Mensagem para enviar:");
            }
        }
        catch(IOException exc){
            System.out.println("Erro ao ler pedido - " + exc.getClass() + exc.getMessage());
        }
    }
}
