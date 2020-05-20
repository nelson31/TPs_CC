package AnonStreamProtocol;

import AnonProtocol.AnonPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StreamList {

    /**
     * Estrutura de dados que vão
     * sendo lidos do socket
     */
    private Map<Integer, AnonPacket> recebidos;

    /**
     * Variável que guarda o valor do campo
     * sequencia do próximo pacote que informa
     * o número de pacotes a serem recebidos
     * resultante do envio de um array do
     * outro lado da stream
     */
    private int nextSizePacketSequence;

    /**
     * Variável para garantir exclusão mútua,
     * tendo em conta que teremos uma thread a
     * ler do socket e a escrever para aqui e
     * a thread dedicada à sessão a ler dados daqui
     */
    private Lock lwaitsize;

    private Condition cwaitsize;

    private Lock lwaitPacketSequence;

    private Condition cwaitPacketSequence;

    /**
     * Variável que a cada momento guarda o id
     * da sequencia do próximo pacote pelo qual
     * a thread que lê está à espera
     */
    private int nextPacketWaiting;

    /**
     * Construtor para objetos da classe
     * StreamList
     */
    public StreamList() {

        this.recebidos = new HashMap<>();
        /* O primeiro pacote de size
        tem sequence 0 */
        this.nextSizePacketSequence = 0;
        this.nextPacketWaiting = 1;
        this.lwaitsize = new ReentrantLock();
        this.cwaitsize = this.lwaitsize.newCondition();
        this.lwaitPacketSequence = new ReentrantLock();
        this.cwaitPacketSequence = this.lwaitPacketSequence.newCondition();
    }

    /**
     * Método que apenas será chamado pela thread que
     * pretende ler uma mensagem da stream
     *
     * @return
     */
    public Set<AnonPacket> getNextMessage() {

        int pacotesALer = 0;
        Set<AnonPacket> ret = new TreeSet<>();
        /* Acedemos ao lock para aceder ao
        próximo pacote de size */
        this.lwaitsize.lock();

        try {
            /* Esperamos enquanto o próximo
            pacote de size não chegar */
            while (!this.recebidos.containsKey(this.nextSizePacketSequence))
                this.cwaitsize.await();

            /* Quando esse pacote chegar vamos buscá-lo */
            AnonPacket sizepacket = this.recebidos.get(this.nextSizePacketSequence);
            this.nextPacketWaiting = this.nextSizePacketSequence+1;
            this.recebidos.remove(this.nextSizePacketSequence);
            /* Atualizamos o valor da sequencia de próximo pacote size que esperamos */
            this.nextSizePacketSequence += sizepacket.getIsSizeArray()+1;
            pacotesALer = sizepacket.getIsSizeArray();
        } catch (InterruptedException exc) {
            System.out.println("Erro ao obter pacote de tamanho");
        } finally {
            this.lwaitsize.unlock();
        }

        /* Obtemos o lock para aceder aos
        pacotes que contêm os dados */
        this.lwaitPacketSequence.lock();

        try {
            for (int i = 0; i < pacotesALer; i++) {
                /* Enquanto não chegar o próximo pacote pelo qual
                esperamos suspendemos */
                while (!this.recebidos.containsKey(this.nextPacketWaiting))
                    this.cwaitPacketSequence.await();

                /* Quando acordamos adicionamos o pacote
                à coleção para retornar */
                ret.add(this.recebidos.get(this.nextPacketWaiting));
                /* Incrementamos o valor da sequencia do
                pacote pelo qual esperamos */
                this.nextPacketWaiting++;
            }
        }
        catch(InterruptedException exc){
            System.out.println("Erro ao ler pacote de dados");
        }
        finally {
            /* Cedemos o lock */
            this.lwaitPacketSequence.unlock();
        }
        return ret;
    }

    /**
     * Método que permite adicionar um pacote para a
     * Lista. Método apenas chamado por uma thread que
     * lê pacotes do anonSocket
     */
    public void addPacket(AnonPacket pack){

        /* Se estivermos perante um pacote de
        size apenas obtemos o lock para as threads
        que esperam pelo pacote de size */
        if(pack.isSizePacket()){

            this.lwaitsize.lock();

            /* Caso haja algúem à espera deste pacote
            acordamos a respetiva thread */
            if(pack.getSequence() == this.nextSizePacketSequence)
                this.cwaitsize.signal();
            System.out.println("Li novo pacote de size com sequence: " + pack.getSequence());

            this.lwaitsize.unlock();
        }
        else{
            this.lwaitPacketSequence.lock();

            /* Sinalizamos caso esteja algúem
            à espera desta pacote */
            if(pack.getSequence() == this.nextPacketWaiting)
                this.cwaitPacketSequence.signal();

            System.out.println("Li novo pacote com sequence: " + pack.getSequence());

            this.lwaitPacketSequence.unlock();
        }
    }
}