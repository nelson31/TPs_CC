import java.io.IOException;
import java.net.Socket;

public class PeerAccepter implements Runnable{

    /**
     * Variável que permite a comunicação
     * com outro AnonGW
     */
    private AnonSocket asocket;

    /**
     * Tabela que regista a chegada de
     * pedidos de outros peers
     */
    private ForeignSessions foreignSessions;

    /**
     * Construtor para objetos da classe PeerAccepter
     * @param asocket
     * @param foreignSessions
     */
    public PeerAccepter(AnonSocket asocket, ForeignSessions foreignSessions){

        this.asocket = asocket;
        this.foreignSessions = foreignSessions;
    }

    public void run(){

        while(true){
            try {
                TargetServerInfo target = new TargetServerInfo();
                /* Vamos buscar a próxima sessão externa que chegou */
                int foreignID = this.foreignSessions.next(target);
                Socket server = new Socket(target.getTargetIP(), target.getTargetPort());

                /* Criamos uma thread que media a comunicação entre o owner da sessão e o destino */
                new Thread(new SessionHandler(server,asocket,target.getTargetIP().getHostAddress(),
                        target.getTargetPort(),foreignID,this.foreignSessions.get(foreignID).getOwnerIP()));
            }
            catch(IOException exc){
                System.out.println("Erro a efetuar conexão para o target server - " + exc.getMessage());
            }
        }
    }
}
