package SecureProtocol;

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
     * classe SecureProtocol.PacketIdGetter
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

        if(this.nextID == 0)
            this.nextID++;

        int ret = this.nextID;

        this.nextID = (this.nextID+1)%max_id;

        this.l.unlock();

        return ret;
    }
}
