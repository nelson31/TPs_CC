import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PacketIdGetter {

    private static final int max_id = 1000;

    /**
     * Variável que guarda o próximo
     * id a atribuir
     */
    private int nextID;

    /**
     * Variável que garante exclusão mútua
     */
    private Lock l;

    /**
     * Construtor para objetos da
     * classe PacketIdGetter
     */
    public PacketIdGetter(){

        this.nextID = 1;
        this.l = new ReentrantLock();
    }

    /**
     * Método que permite obter um
     * identificador
     * @return
     */
    public int get(){

        this.l.lock();

        int ret = (this.nextID+1)%max_id;

        if(ret == 0) ret++;

        this.l.unlock();

        return ret;
    }
}
