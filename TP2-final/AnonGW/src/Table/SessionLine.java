package Table;

import AnonProto.AnonPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que representa uma lista com
 * todos os pacotes pertencentes a uma
 * determinada sessão. É de notar que uma
 * sessão ficará atribuida apenas a uma thread.
 */
public class SessionLine {

    /**
     * Variável que identifica a sequencia
     * do pacote a ser lido
     */
    private int session;

    /**
     * Variável que representa uma
     * linha da Table
     */
    private Map<Integer, AnonPacket> line;

    /**
     * Variável que nos diz se um pacote já
     * foi recebido com sucesso pela thread
     */
    private Map<Integer, Boolean> received;

    /**
     * Variável que garante exclusão
     * mútua da linha
     */
    private Lock l;


    private Condition c;

    /**
     * Método que cria uma nova linha para uma
     * sessão de comunicação de um AnonGW para outro
     *
     * @param session
     */
    public SessionLine(int session) {

        this.session = session;
        this.line = new HashMap<>();
        this.received = new HashMap<>();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    /**
     * Método que nos diz se já existe um
     * pacote com uma determinada sequence na linha
     * @param sequence
     * @return
     */
    public boolean containsPacket(int sequence){

        boolean ret = false;

        /* Obtemos o lock */
        this.l.lock();

        ret = this.line.containsKey(sequence);

        /* Cedemos o lock */
        this.l.unlock();

        return ret;
    }

    /**
     * Método que permite adicionar um novo
     * anonPacket de uma dada sessão
     */
    public void addPacket(AnonPacket ap) {

        /* Obtemos o lock */
        this.l.lock();

        /* Só adicionamos o pacote à linha se este
        ainda não tiver sido lido pela thread */
        if(!this.received.containsKey(ap.getSequence()) || !this.received.get(ap.getSequence())) {
            this.line.put(ap.getSequence(), ap);
            this.received.put(ap.getSequence(), true);
            /* Sinalizamos a thread que
            já chegou um pacote */
            this.c.signal();
        }

        /* Cedemos o lock */
        this.l.unlock();
    }

    /**
     * Método que permite obter um pacote
     * existente na linha quando este chegar
     *
     * @param sequence
     */
    public AnonPacket getPacket(int sequence) {

        AnonPacket ap = null;
        /* Obtemos o lock */
        this.l.lock();

        try {
            /* Se o pacote ainda não tiver
            chegado esperamos */
            while (!this.line.containsKey(sequence))
                this.c.await();

            /* Quando a thread for notificada
            vamos buscar o packet */
            ap = this.line.get(sequence);
            /* De seguida removemos o pacote lido */
            this.line.remove(sequence);
            /* Sinalizamos o pacote como
            recebido pela aplicação */
            this.received.put(sequence,true);
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao obter o pacote - " + exc.getMessage());
        }
        finally {
            /* Cedemos o lock */
            this.l.unlock();
        }

        return ap;
    }

    public void clearLine(){

        this.l.lock();

        this.line.clear();

        this.l.unlock();
    }
}