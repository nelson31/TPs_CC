package SecureProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ListPacketReceiving {

    /**
     * Variável que guarda os pacotes
     * a enviar/receber
     */
    private List<SecurePacket> list;

    private Lock l;

    private Lock notAck;

    private Condition cnotAck;

    private Condition c;

    ////////////////////////////Variáveis para acordar threads que esperem por acks////////////////////////////////

    private Map<Integer, Lock> locks;

    private Map<Integer, Condition> conditions;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
     * da classe SecureProtocol.ListPacketSending
     */
    public ListPacketReceiving(){

        this.list = new ArrayList<>();
        this.l = new ReentrantLock();
        this.notAck = new ReentrantLock();
        this.c = l.newCondition();
        this.cnotAck = notAck.newCondition();
        this.locks = new HashMap<>();
        this.conditions = new HashMap<>();
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
        /* Se for um ack sinalizamos a thread que
        espera pelo respetivo ack */
        if(sp.isAck()){

            /* Vamos buscar o lock da thread que
            espera pelo ack que acabamos de receber */
            Lock l = this.locks.get(sp.getId());
            l.lock();

            /* Acordamos a thread que espera pelo lock */
            this.conditions.get(sp.getId()).signal();

            l.unlock();
        }

        this.notAck.unlock();

        this.l.unlock();
    }

    /**
     * Método que nos vai buscar o primeiro pacote que seja de ack
     * @return
     */
    private SecurePacket getFstNotAckPacket(){

        SecurePacket ret = null;
        int size = this.list.size();
        boolean found = false;
        for(int i=0; i<size && !found; i++){
            if(!this.list.get(i).isAck()) {
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

            ret = getFstNotAckPacket();

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

        this.l.unlock();

        return ret;
    }

    /**
     * Método que prepara a receção de um novo
     * ack por parte de uma thread
     * @param id
     * @param lwaitAck
     * @param cwaitAck
     */
    public void prepareRecebeAck(int id, Lock lwaitAck, Condition cwaitAck){

        this.l.lock();

        /* Adicionamos as variáveis para sinalizar
        a thread que espera pelo ack */
        if(!this.locks.containsKey(id)) {
            this.locks.put(id, lwaitAck);
            this.conditions.put(id, cwaitAck);
            System.out.println("Acicionei lock para o ack: " + id);
        }

        this.l.unlock();
    }

    /**
     * Método que permite remover um pacote
     * @param id
     */
    public void remove(int id){

        boolean found = false;

        this.l.lock();

        /* Removemos o pacote da lista e também as variáveis
        lock e condition que usamos para sinalizar a thread */
        for(int i=0; i<this.list.size() && !found; i++) {
            if (this.list.get(i).getId() == id) {
                this.list.remove(i);
                this.locks.remove(id);
                this.conditions.remove(id);
                found = true;
            }
        }

        this.l.unlock();
    }
}

