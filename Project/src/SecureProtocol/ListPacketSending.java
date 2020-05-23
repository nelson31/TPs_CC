package SecureProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ListPacketSending {

    /**
     * Variável que guarda os pacotes
     * a enviar/receber
     */
    private List<SecurePacket> list;

    private Lock l;

    private Condition c;

    /**
     * Construtores para objetos
     * da classe SecureProtocol.ListPacketSending
     */
    public ListPacketSending(){

        this.list = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    /**
     * Método que serve para adicionar um
     * secure packet para a lista de pacotes
     * @param sp
     */
    public void addPacket(SecurePacket sp){

        /* Impedimos qualquer thread
        de ler um pacote */
        this.l.lock();

        this.list.add(sp);

        /* Sinalizamos quem estava à
        espera de ler do socket */
        this.c.signal();

        this.l.unlock();
    }



    /**
     * Método que permite obter o
     * próximo packet da queue
     * @return
     */
    public SecurePacket getPacket(){

        SecurePacket ret = null;
        this.l.lock();

        try {
            /* Enquanto não houver nada
            para ler esperamos */
            while (this.list.size() == 0)
                this.c.await();

            ret = this.list.get(0);
            this.list.remove(0);
        }
        catch(InterruptedException exc){

        }
        finally {
            this.l.unlock();
        }

        return ret;
    }
}
