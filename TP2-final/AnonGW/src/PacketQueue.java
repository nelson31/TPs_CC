import AnonProto.AnonPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PacketQueue {

    /**
     * Estrutura de dados que contém linhas
     * com pacotes a serem enviados através
     * do socket
     */
    private List<AnonPacket> queue;

    /**
     * Variável que garante exclusão mútua
     * no acesso à queue
     */
    private Lock l;

    /**
     * Variável que permite parar a thread
     */
    private Condition c;

    /**
     * Construtor para objetos da
     * classe PacketQueue
     */
    public PacketQueue(){

        this.queue = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    /**
     * Método que retorna o próximo
     * @return
     */
    public AnonPacket next(){

        AnonPacket ap = null;
        /* Garantimos a exclusão mútua da
        tabela para ler o próximo pacote a enviar */
        this.l.lock();
        try{
            /* Enquanto não houver pacotes
            para enviar esperamos */
            while(this.queue.size() == 0)
                this.c.await();

            ap = this.queue.get(0);
            /* Removemos o pacote que acabou
            de ser enviado */
            this.queue.remove(0);
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao obter proximo pacote para enviar");
        }
        finally {
            /* Cedemos o lock */
            this.l.unlock();
        }

        /* Retornamos o próximo pacote
            a ser enviado */
        return ap;
    }

    /**
     * Método que permite adicionar um pacote
     * à queue para ser enviado pelo socket
     */
    public void send(AnonPacket ap){

        /* Obtemos o lock para
        escrever na queue */
        this.l.lock();
        try{
            /* Adicionamos o respetivo
            pacote à queue */
            this.queue.add(ap);
            /* Acordamos a thread responsável
            por escrever no socket */
            this.c.signal();
        }
        finally {
            /* Cedemos o lock */
            this.l.unlock();
        }
    }
}
