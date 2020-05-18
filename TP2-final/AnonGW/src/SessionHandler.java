import java.io.IOException;
import java.net.Socket;

public class SessionHandler implements Runnable{

    /**
     * Variável que permite aceitar
     * pedidos de clientes
     */
    private Socket cliente;

    /**
     * Variável que permite comunicar
     * com um outro AnonGW
     */
    private AnonSocket asocket;

    private String destinationIP;

    private int destinationPort;

    private int sessionID;

    private String peerIP;

    /**
     * Programa que le de um socket de um cliente e
     * escreve para um determinado anonGW e vice versa
     * @param cliente
     * @param asocket
     * @param destinationIP
     * @param destinationPort
     * @param sessionID
     * @param peerIP
     */
    public SessionHandler(Socket cliente, AnonSocket asocket,
                          String destinationIP, int destinationPort, int sessionID, String peerIP){

        this.cliente = cliente;
        this.asocket = asocket;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.sessionID = sessionID;
        this.peerIP = peerIP;
    }


    public void run(){

        try {

            /* Colocamos as 2 threads a correr: Uma que
            le do cliente e escreve para o anon e vice versa */
            Thread t1 = new Thread(new ReaderFromSocketToAnon(cliente, asocket, destinationIP,
                    destinationPort, sessionID, peerIP));

            Thread t2 = new Thread(new ReaderFromAnonToSocket(cliente, asocket, destinationIP,
                    destinationPort, sessionID));

            t1.start();
            t2.start();

            /* Esperamos por ambas as threads */
            t1.join();
            t2.join();

            /* Aqui teremos que ceder o id de sessão e ainda, se
             tiver sido atribuida um sessão externa, teremos
            que remover essa informação da tabela foreign */
            this.asocket.endSession(this.sessionID);
        }
        catch(IOException | InterruptedException exc){
            System.out.println("Erro ao iniciar as threads de leitura dos sockets - " + exc.getMessage());
        }
    }
}
