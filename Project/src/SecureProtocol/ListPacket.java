package SecureProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ListPacket {

    /**
     * Variável que guarda os pacotes
     * a enviar/receber
     */
    private List<SecurePacket> list;

    private Lock l;

    private Lock notAck;

    private Condition cnotAck;

    private Condition c;

    /**
     * Método que nos diz o número de pacotes
     * que se encontram na lista de pacotes e
     * não são packets
     * @return
     */
    private int numNotAcks(){

        int count = 0;
        for(SecurePacket sp : this.list)
            if(!sp.isAck())
                count++;
        return count;
    }

    /**
     * Construtores para objetos
     * da classe SecureProtocol.ListPacket
     */
    public ListPacket(){

        this.list = new ArrayList<>();
        this.l = new ReentrantLock();
        this.notAck = new ReentrantLock();
        this.c = l.newCondition();
        this.cnotAck = notAck.newCondition();
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

        /* Impedimos as threads de ler
        um pacote que não é ack */
        this.notAck.lock();

        this.list.add(sp);

        /* Sinalizamos quem estava à
        espera de ler do socket */
        this.c.signal();

        /* Sinalizamos quem estava à espera
        de ler um pacote não ACK*/
        if(!sp.isAck())
            this.cnotAck.signal();

        this.notAck.unlock();

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

    /**
     * Método que nos vai buscar o primeiro pacote que seja de ack
     * @return
     */
    private SecurePacket getFstAckPacket(){

        SecurePacket ret = null;
        int size = this.list.size();
        boolean found = false;
        for(int i=0; i<size && !found; i++){
            if(this.list.get(i).isAck()) {
                ret = this.list.get(i);
                found = true;
                this.remove(ret.getId());
            }
        }
        return ret;
    }

    /**
     * Método que permite obter um
     * pacote que não é ack
     * @return
     */
    public SecurePacket getDataPacket(){

        SecurePacket ret = null;
        this.notAck.lock();

        try {
            /* Enquanto não houver nada
            para ler esperamos que seja
            registado um packet de dados */
            while (this.numNotAcks() == 0)
                this.cnotAck.await();

            ret = getFstAckPacket();
        }
        catch(InterruptedException exc){

        }
        finally {
            this.notAck.unlock();
        }

        return ret;
    }

    /**
     * Método que nos diz se existe
     * um dado pacote na lista
     * @param id
     * @return
     */
    public boolean contains(int id){

        this.l.lock();

        boolean ret = this.list.contains(new SecurePacket(id,null,null,0,0,new byte[0]));
        /* Caso exista removemos o
        respetivo pacote de ack */
        if(ret)
            this.remove(id);

        this.l.unlock();

        return ret;
    }

    /**
     * Método que permite remover um pacote
     * @param id
     */
    private void remove(int id){

        boolean found = false;
        for(int i=0; i<this.list.size() && !found; i++) {
            if (this.list.get(i).getId() == id) {
                this.list.remove(i);
                found = true;
            }
        }
    }
}
