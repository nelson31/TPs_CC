package AnonProtocol;

import java.net.InetAddress;

public class DataInfo {

    /**
     * Variável que guarda a sessão à qual
     * pertence a informação lida da stream
     */
    private int session;

    /**
     * Variável que guarda o id de sessão
     * à qual pertence a informação no contexto
     * do anon que a enviou caso não seja o próprio
     */
    private int foreignSession;

    /**
     * Variável que guarda o owner da sessão
     */
    private InetAddress owner;

    /**
     * Variável que guarda o endereço IP
     * do targetServer
     */
    private InetAddress targetServer;

    /**
     * Variável que guarda a port destino
     */
    private int targetPort;

    /**
     * Construtor para objetos da
     * classe DataInfo
     * @param session
     * @param foreignSession
     * @param owner
     * @param targetServer
     * @param targetPort
     */
    public DataInfo(int session, int foreignSession, InetAddress owner,
                    InetAddress targetServer, int targetPort){

        this.session = session;
        this.foreignSession = foreignSession;
        this.owner = owner;
        this.targetServer = targetServer;
        this.targetPort = targetPort;
    }

    /**
     * Construtor por defeito para
     * objetos da classe DataInfo
     */
    public DataInfo(){

        this.session = -1;
        this.foreignSession = -1;
        this.owner = null;
        this.targetServer = null;
        this.targetPort = -1;
    }

    public boolean isComplete(){

        return this.session > 0;
    }

    public void setSession(int session) {

        this.session = session;
    }

    public void setTargetPort(int targetPort) {

        this.targetPort = targetPort;
    }

    public void setForeignSession(int foreignSession) {

        this.foreignSession = foreignSession;
    }

    public void setOwner(InetAddress owner) {

        this.owner = owner;
    }

    public void setTargetServer(InetAddress targetServer) {

        this.targetServer = targetServer;
    }

    /**
     * Implementação do método toString
     * @return
     */
    public String toString(){

        StringBuilder sb = new StringBuilder();

        sb.append("Session: ");
        sb.append(this.session);
        sb.append("; Owner: ");
        sb.append(this.owner);
        sb.append("; TargetServer: ");
        sb.append(this.targetServer);
        sb.append("; TargetPort: ");
        sb.append(this.targetPort);

        return sb.toString();
    }
}
