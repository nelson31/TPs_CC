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
        this.peersIP = new ArrayList<>();
        this.ownerSessionIds = new ArrayList<>();
        this.l = new ReentrantLock();
    }

    /**
     * Método que permite
     * @return
     */
    public int get(String peerIP, Integer session){

        int ret = -1;
        /* Obtemos o lock */
        this.l.lock();

        try {

            /* Esperamos até que chegue um id de sessão */
            while (this.foreignIds.size() == 0)
                this.c.await();

            ret = this.foreignIds.get(0);
            this.foreignIds.remove(0);
            peerIP = this.peersIP.get(0);
            this.peersIP.remove(0);
            session = this.ownerSessionIds.get(0);
            this.ownerSessionIds.remove(0);
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao obter sessão externa - " + exc.getMessage());
        }
        return ret;
    }

    /**
     * Método que permite obter um id de sessão
     * @param session
     */
    public void add(int session, String peerIP, int foreignsession){

        /* Obtemos o lock para aceder
        à estrutura de dados */
        this.l.lock();

        this.foreignIds.add(session);

        this.c.signal();
        /* Cedemos o lock */
        this.l.unlock();
    }
}
