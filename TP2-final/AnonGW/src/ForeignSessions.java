import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ForeignSessions {

    /**
     * Variável que guarda os IDs do
     * todas as sessões que pertencem
     * a outro anonGW
     */
    private List<Integer> foreignIds;

    /**
     * Variável que guarda os id's que ainda
     * não estão a ser tratados
     */
    private List<Integer> available;

    /**
     * Lista que guarda os IP's dos anonGW aos
     * quais se referem cada um dos foreign ids atribuidos
     */
    private List<String> peersIP;

    /**
     * Lista que guarda os ids das respetivas
     * sessões no anon Owner
     */
    private List<Integer> ownerSessionIds;

    /**
     * Variável que guarda os destinos
     * finais dos pacotes
     */
    private List<String> targetServersIPs;

    /**
     * Variável que guarda as portas dos
     * destinos finais dos pacotes
     */
    private List<Integer> targetServersPorts;

    /**
     * Variável que garante exclusão
     * mútua no acesso à estrutura de dados
     */
    private Lock l;

    /**
     * Variável que permite notificar quando
     * aparece uma nova sessão externa ao anonGW
     */
    private Condition c;

    /**
     * Construtor para objetos da classe ForeignSessions
     */
    public ForeignSessions(){

        this.foreignIds = new ArrayList<>();
        this.available = new ArrayList<>();
        this.peersIP = new ArrayList<>();
        this.targetServersIPs = new ArrayList<>();
        this.targetServersPorts = new ArrayList<>();
        this.ownerSessionIds = new ArrayList<>();
        this.l = new ReentrantLock();
    }

    /**
     * Método que permite a uma thread ficar
     * responsável por uma sessão externa
     * @return
     */
    public int next(TargetServerInfo target){

        int ret = -1;
        this.l.lock();

        try{
            while(this.available.size() == 0)
                this.c.await();

            ret = this.available.get(0);
            this.available.remove(0);
            target.setTargetIP(InetAddress.getByName(targetServersIPs.get(0)));
            target.setTargetPort(this.targetServersPorts.get(0));
        }
        catch(InterruptedException | UnknownHostException exc){
            System.out.println("Erro a obter uma sessão externa - " + exc.getMessage());
        }
        finally {
            this.l.unlock();
        }
        return ret;
    }

    /**
     * Método que permite
     * @return
     */
    public SessionInfo get(Integer foreignId){

        SessionInfo si;

        /* Obtemos o lock */
        this.l.lock();

        int index = this.foreignIds.indexOf(foreignId);

        si = new SessionInfo(this.ownerSessionIds.get(index),this.peersIP.get(index));
        return si;
    }

    /**
     * Método que permite adicionar um id de sessão
     * @param session
     */
    public void add(int session, String peerIP, int foreignsession, String targetServerIP, int targetServerPort){

        /* Obtemos o lock para aceder
        à estrutura de dados */
        this.l.lock();

        this.ownerSessionIds.add(session);
        this.available.add(session);
        this.peersIP.add(peerIP);
        this.foreignIds.add(foreignsession);
        this.targetServersIPs.add(targetServerIP);
        this.targetServersPorts.add(targetServerPort);

        this.c.signal();
        /* Cedemos o lock */
        this.l.unlock();
    }

    /**
     * Método que permite remover uma linha da tabela
     * de associação entre indices de sessão externos
     * e locais
     * @param foreignID
     */
    public void remove(int foreignID){

        /* Obtemos o lock */
        this.l.lock();

        int index = this.ownerSessionIds.indexOf(foreignID);
        this.ownerSessionIds.remove(foreignID);
        this.peersIP.remove(index);
        this.foreignIds.remove(index);
        this.targetServersIPs.remove(index);
        this.targetServersPorts.remove(index);
        /* Pelo sim pelo não removemos tambem da lista available */
        this.available.remove(foreignID);

        /* Cedemos o lock */
        this.l.unlock();
    }

    /**
     * Método que nos diz se um determinado id de
     * sessão pertence a uma session cujo owner é um peer
     * @param idSession
     * @return
     */
    public boolean isForeign(int idSession){

        boolean ret = false;

        /* Obtemos o lock para a estrutura de dados */
        this.l.lock();

        if(this.foreignIds.contains(idSession))
            ret = true;
        /* Cedemos o lock para a estrutura de dados */
        this.l.unlock();

        return ret;
    }
}
