import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class ClientAccepter implements Runnable{

    /**
     * Variável que recebe pedidos de conexão TCP
     * provenientes de clientes e processa a
     * leitura dos dados para enviar para outro
     * anon. Isto é, cria sessões
     */
    private ServerSocket accepter;

    private AnonSocket asocket;

    /**
     * Variável que permite obter um id para
     * a uma sessão
     */
    private SessionGetter idSessionGetter;

    private String destinationIP;

    private int destinationPort;

    private List<String> peersIP;

    private int nextPeer;

    /**
     * Construtor para objetos da classe ClienteAccepter
     * @param accepter
     * @param asocket
     * @param idSessionGetter
     */
    public ClientAccepter(ServerSocket accepter, AnonSocket asocket,
                          SessionGetter idSessionGetter, String destinationIP,
                          int destinationPort, List<String> peersIP){

        this.accepter = accepter;
        this.asocket = asocket;
        this.idSessionGetter = idSessionGetter;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.peersIP = peersIP;
        this.nextPeer = 0;
    }

    public void run(){

        while(true){

            try {
                this.nextPeer = (this.nextPeer + 1) % this.peersIP.size();
                new Thread(new SessionHandler(accepter.accept(), asocket, destinationIP,
                        destinationPort, idSessionGetter.getID(), this.peersIP.get(this.nextPeer))).start();
            }
            catch(IOException exc){
                System.out.println("Erro ao receber um novo pedido de cliente - " + exc.getMessage());
            }
        }
    }
}
