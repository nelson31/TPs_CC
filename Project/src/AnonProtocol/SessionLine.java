package AnonProtocol;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionLine {

    /**
     * Lista que guarda os anon
     * packets referentes a uma
     * dada sessão
     */
    private List<AnonPacket> list;

    /**
     * Variável que contém o endereço
     * do owner
     */
    private InetAddress owner;

    /**
     * Variável que identifica
     * a sessão
     */
    private int session;

    /**
     * Variável que permite exclusão mútua,
     * visto que teremos mais que uma thread
     * a aceder a esta estrutura - reader e threads
     * que fazem receive do AnonSocket
     */
    private Lock l;

    private Condition c;

    /**
     * Construtor para objetos da
     * classe AnonProtocol.SessionLine
     *
     * @param session
     */
    public SessionLine(int session) {

        this.list = new ArrayList<>();
        this.session = session;
        this.l = new ReentrantLock();
        this.c = this.l.newCondition();
        this.owner = null;
    }

    /**
     * Método que permite adicionar um
     * packet à tabela de chegadas. O reader
     * executará este método
     *
     * @param ap
     */
    public void addPacket(AnonPacket ap, InetAddress owner) {

        /* Obtemos o lock */
        this.l.lock();

        /* Adicionamos o pacote à
        lista */
        this.list.add(ap);

        /* Caso ainda não esteja definido
        o owner definimos */
        if(this.owner == null)
            this.owner = owner;

        /* Sinalizamos a thread responsável
        pela sessão */
        this.c.signal();

        /* Cedemos o lock */
        this.l.lock();
    }

    /**
     * Método que permite obter um AnonPacket
     *
     * @return
     */
    public AnonPacket getPacket() {

        AnonPacket ret = null;
        /* Obtemos o lock */
        this.l.lock();

        try {
            /* Enquanto não tiver recebido
            pacotes espero */
            while (this.list.size() == 0)
                this.c.await();

            /* Vamos buscar o pacote e
            removêmo-lo da lista */
            ret = this.list.get(0);
            this.list.remove(0);
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao ler o AnonPacket");
        }

        return ret;
    }
}