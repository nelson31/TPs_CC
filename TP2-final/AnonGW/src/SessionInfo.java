public class SessionInfo {

    /**
     * Variável que guarda o valor do
     * id da sessão no owner
     */
    private int ownerSessionID;

    /**
     * Variável que guarda o endereço
     * IP do owner do pacote
     */
    private String ownerIP;

    /**
     * Construtor para objetos da classe SessionInfo
     * @param ownerSessionID
     * @param ownerIP
     */
    public SessionInfo(int ownerSessionID, String ownerIP){

        this.ownerSessionID = ownerSessionID;
        this.ownerIP = ownerIP;
    }

    public int getOwnerSessionID() {

        return ownerSessionID;
    }

    public String getOwnerIP() {

        return ownerIP;
    }
}
