import java.net.ServerSocket;
import java.net.Socket;

public class AnonGW {

    /**
     * Socket TCP através do qual o anonGW
     * aceita conexões provenientes de
     * clientes que se pretendem anonimizar
     * para comunicar com um dado servidor
     */
    private ServerSocket listen;

    /**
     * Socket que permite receber dados
     * de um cliente
     */
    private Socket input;

    /**
     * Socket que será usado para comunicar
     * com o servidor. (Apenas nesta primeira fase)
     */
    private Socket output;

    public static void main(String[] args){

        /* Colocamos o server socket
        permanentemente à escuta*/

        System.exit(0);

    }
}
