package Components;

import AnonProtocol.AnonSocket;
import AnonProtocol.SessionGetter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;

public class ClientAccepter implements Runnable {

    /**
     * Socket para o qual enviamos dados
     * mediante pedidos de novos clientes
     */
    private AnonSocket asocket;

    /**
     * Socket que aceita pedidos
     * de novos clientes
     */
    private ServerSocket accepter;

    /**
     * Variável que permite receber novos
     * pedidos de acesso a um target server
     * por parte de clientes
     */
    private SessionGetter sessionGetter;

    /**
     * Estrutura de dados que guarda informações
     * acerca de sessões externas
     */
    private ForeignSessions foreignSessions;

    /**
     * Estrutura de dados que guarda os
     * endereços IP de todos os peers
     */
    private List<InetAddress> peers;

    /**
     * Variável que guarda o id do próximo
     * peer que será solicitado para contactar
     * o target server
     */
    private int nextPeer;


    ////////////////Informações acerca do target-server protegido pelo AnonGW///////////////////

    /**
     * Variável que guarda o endereço
     * IP do target server
     */
    private InetAddress targetServer;

    /**
     * Variável que guarda a port do
     * target server
     */
    private int targetPort;

    ////////////////////////////Dados usados na cifragem das mensagens//////////////////////////

    private String password;

    private int Key1, Key2;

    ////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Construtor para a thread que aceita
     * pedidos de novos clientes
     */
    public ClientAccepter(AnonSocket asocket, ServerSocket accepter,
                          SessionGetter sessionGetter, ForeignSessions foreignSessions,
                          List<InetAddress> peers,
                          InetAddress targetServer, int targetPort,
                          String password, int Key1, int Key2) {

        this.asocket = asocket;
        this.accepter = accepter;
        this.peers = peers;
        this.nextPeer = 0;
        this.sessionGetter = sessionGetter;
        this.foreignSessions = foreignSessions;
        this.targetServer = targetServer;
        this.targetPort = targetPort;
        this.password = password;
        this.Key1 = Key1;
        this.Key2 = Key2;
    }

    /**
     * Método que retorna o endereço
     * IP do próximo peer
     *
     * @return
     */
    private InetAddress getNextPeer() {

        InetAddress ret = this.peers.get(this.nextPeer);
        this.nextPeer = (this.nextPeer + 1) % this.peers.size();
        return ret;
    }

    /**
     * Implementação do método run para
     * objetos da classe ClientAccepter
     */
    public void run() {

        int id;
        /* Ficamos constantemente à espera de
        novas conexões TCP por parte de clientes */
        while (true) {

            try {
                id = this.sessionGetter.getID();
                /* Colocamos o worker a correr */
                Worker w = new Worker(id, id,
                        accepter.accept(), asocket, this.getNextPeer(),
                        this.targetServer, this.targetPort,this.sessionGetter,this.foreignSessions,this.password,this.Key1,this.Key2);

                new Thread(w).start();

                System.out.println("[ClienteAccepter] Recebido novo pedido de cliente - id sessão local: " + id);
            }
            catch(IOException exc){
                System.out.println(exc.getMessage());
            }
        }
    }
}