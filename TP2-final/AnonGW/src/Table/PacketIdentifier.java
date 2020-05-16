package Table;

import AnonProto.AnonPacket;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PacketIdentifier {

    /**
     * Variável que guarda o número
     * de sessão do packote ao qual
     * o identifier está associado
     */
    private int session;

    /**
     * Variável que guarda o número
     */
    private int sequence;

    /**
     * Monitor que permite exclusão mútua
     * para o pacote que o identifier guarda
     */
    private Lock l;

    private Condition c;

    /**
     * Variável que guarda o pacote
     * pelo qual o identifier espera
     */
    private AnonPacket packet;

    /**
     * Construtor para objetos da
     * classe Table.PacketIdentifier
     * @param session
     * @param sequence
     */
    public PacketIdentifier(int session, int sequence){

        this.session = session;
        this.sequence = sequence;
        this.l = new ReentrantLock();
        this.c = l.newCondition();
        this.packet = null;
    }

    /**
     * Método que retorna a sequencia do packet
     * identifier ao qual é enviado o método
     * @return
     */
    public int getSequence() {

        return sequence;
    }

    /**
     * Método que permite registar a chegada
     * do AnonProto.AnonPacket e notificar a thread
     * que espera por ele
     * @param ap
     */
    public void put(AnonPacket ap){

        /* Obtemos o lock */
        this.l.lock();
        try{
            /* É registado o pacote */
            this.packet = ap;
            /* Acordamos a thread que
            espera pelo pacote */
            this.c.signal();
        }
        finally {
            this.l.unlock();
        }
    }

    /**
     * Método que permite obter o pacote ao
     * qual o identifier corresponde. É de
     * notar que o método bloqueia até que
     * o packet chegue.
     * @return
     * @throws InterruptedException
     */
    public AnonPacket get() throws InterruptedException{

        AnonPacket ap = null;
        /* Obtemos o lock */
        this.l.lock();
        try{
            /* Enquanto não chegar o
            pacote a thread dorme */
            while(this.packet == null)
                this.c.await();

            ap = this.packet;
            return ap;
        }
        finally {
            this.l.unlock();
        }
    }
}
