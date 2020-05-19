package AnonProtocol;

import SecureProtocol.SecurePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que permite adicionar AnonPackets
 * que serão encaminhados pelo writer
 * através de um secureSocket para o seu
 * destino final
 */
public class TableQueue {

    /**
     * Variável que guarda a lista
     * de AnonPackets já encapsulados
     * em SecurePackets
     */
    private List<SecurePacket> queue;

    /**
     * Variável que permite exclusão mútua
     */
    private Lock l;

    private Condition c;

    /**
     * Construtor para objetos da
     * classe TableQueue
     */
    public TableQueue(){

        this.queue = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = this.l.newCondition();
    }
}
