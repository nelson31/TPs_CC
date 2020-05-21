package Components;

import java.net.InetAddress;

public class SessionData {

    /**
     * Variável que guarda o id
     * de sessão no owner
     */
    private int id;

    /**
     * Variável que guarda o endereço
     * IP do owner da sessão
     */
    private InetAddress ownerIP;

    /////////////////////////////////////Informações acerca do target server////////////////////////////////////////

    /**
     * Variável que guarda o endereço
     * IP do targetServer
     */
    private InetAddress targetIp;

    /**
     * Variável que guarda a porta do target
     * server à qual vamos entregar os dados
     */
    private int targetPort;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construtor por defeito para objetos
     * da classe SessionData
     */
    public SessionData(){

        this.id = -1;
        this.ownerIP = null;
        this.targetIp = null;
        this.targetPort = -1;
    }

    /**
     * Construtor para objetos da classe
     * SessionData
     * @param id
     * @param ownerIP
     */
    public SessionData(int id, InetAddress ownerIP, InetAddress targetIp, int targetPort){

        this.id = id;
        this.ownerIP = ownerIP;
        this.targetIp = targetIp;
        this.targetPort = targetPort;
    }

    /**
     * Implementação do método equals
     * @param o
     * @return
     */
    public boolean equals(Object o){

        if(this == o)
            return true;

        if(o == null || o.getClass() != this.getClass())
            return false;

        SessionData sd = (SessionData)o;

        return this.ownerIP.toString().equals(sd.getOwnerIP().toString()) &&
                this.id == sd.getId();
    }

    /**
     * Método que retorna o endereço
     * ip do owner da sessão
     * @return
     */
    public InetAddress getOwnerIP() {

        return this.ownerIP;
    }

    /**
     * Método que retorna o id da sessão
     * à qual é enviada o método
     * @return
     */
    public int getId() {

        return this.id;
    }

    public InetAddress getTargetIp() {

        return this.targetIp;
    }

    public int getTargetPort() {

        return this.targetPort;
    }

    public void setOwnerIP(InetAddress ownerIP) {

        this.ownerIP = ownerIP;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setTargetIp(InetAddress targetIp) {

        this.targetIp = targetIp;
    }

    public void setTargetPort(int targetPort) {

        this.targetPort = targetPort;
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Id: ");
        sb.append(this.id);
        sb.append("; owner: ");
        sb.append(this.ownerIP);

        return sb.toString();
    }
}
