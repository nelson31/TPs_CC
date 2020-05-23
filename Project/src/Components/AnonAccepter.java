package Components;

import AnonProtocol.AnonSocket;
import AnonProtocol.SessionGetter;

import java.io.IOException;
import java.net.Socket;

public class AnonAccepter implements Runnable {

    /**
     *
     */
    private AnonSocket asocket;

    /**
     * Variável a partir da qual vamos aguardar novos
     * pedidos de comunicação provenientes de outros anonGW
     */
    private ForeignSessions foreignSessions;

    /**
     * Variável que permite atribuir id's à sessão
     */
    private SessionGetter sessionGetter;

    ////////////////////////////Dados usados na cifragem das mensagens//////////////////////////

    private String password;

    private int Key1, Key2;

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construtor para objetos da classe AnonAccepter
     *
     * @param asocket
     * @param sessionGetter
     */
    public AnonAccepter(AnonSocket asocket, SessionGetter sessionGetter, ForeignSessions foreignSessions,
                        String password, int Key1, int Key2) {

        this.asocket = asocket;
        this.sessionGetter = sessionGetter;
        this.foreignSessions = foreignSessions;
        this.password = password;
        this.Key1 = Key1;
        this.Key2 = Key2;
    }

    public void run() {

        /* Ficamos permanentemente à espera de
        novos pedidos por parte de outros peers */
        while (true) {

            /* Criamos a veriável que vai receber
            os dados da sessão */
            SessionData data = new SessionData();
            int incoming = this.foreignSessions.accept(data);
            int outgoing = data.getId();

            System.out.println("[AnonAccepter] Novo id para uma sessão externa - id atribuido: " + incoming +
                    "; id no owner: " + outgoing + "; target server: " + data.getTargetIp());

            try {
                Worker w = new Worker(incoming, outgoing, new Socket(data.getTargetIp(),data.getTargetPort()),
                        this.asocket, data.getOwnerIP(), data.getTargetIp(), data.getTargetPort(),
                        this.sessionGetter, this.foreignSessions, this.password, this.Key1, this.Key2);

                /* Colocamos o worker a correr */
                new Thread(w).start();
            }
            catch(IOException exc){
                System.out.println("Erro ao criar conexão para o target server - " + exc.getMessage());
            }
        }
    }
}