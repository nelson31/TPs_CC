package AnonStreamProtocol;

import AnonProtocol.AnonPacket;
import AnonProtocol.DataInfo;

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
     * Informação acerca do destino
     * final dos dados
     */
    private DataInfo targetInfo;

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
        this.targetInfo = new DataInfo();
        this.lwaitsize = new ReentrantLock();
        this.cwaitsize = this.lwaitsize.newCondition();
        this.lwaitPacketSequence = new ReentrantLock();
        this.cwaitPacketSequence = this.lwaitPacketSequence.newCondition();
    }

    /**
     * Método que retorna informações acerca do destino
     * final dos dados recebidos pela stream
     * @return
     */
    public DataInfo getTargetInfo() {

        return this.targetInfo;
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
            while (!this.recebidos.containsKey(this.nextSizePacketSequence)) {
                System.out.println("Estou à espera do pacote de size com sequencia " + this.nextSizePacketSequence);
                this.cwaitsize.await();
            }

            /* Quando esse pacote chegar vamos buscá-lo */
            AnonPacket sizepacket = this.recebidos.get(this.nextSizePacketSequence);
            this.nextPacketWaiting = this.nextSizePacketSequence+1;
            this.recebidos.remove(this.nextSizePacketSequence);

            /* Se for um pacote de fim de comunicação
            é retornado null */
            if(sizepacket.getPayloadSize() == -1){
                return null;
            }
            else {
                /* Atualizamos o valor da sequencia de próximo pacote size que esperamos */
                this.nextSizePacketSequence += sizepacket.getIsSizeArray() + 1;
                /* Vamos buscar o número de pacotes de dados que vamos ler */
                pacotesALer = sizepacket.getIsSizeArray();
            }
        } catch (InterruptedException exc) {
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
                while (!this.recebidos.containsKey(this.nextPacketWaiting)) {
                    System.out.println("Estou à espera do pacote com sequencia " + this.nextPacketWaiting);
                    this.cwaitPacketSequence.await();
                }

                /* Quando acordamos adicionamos o pacote
                à coleção para retornar */
                ret.add(this.recebidos.get(this.nextPacketWaiting));

                System.out.println("Li o pacote de seq: " + this.nextPacketWaiting);
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

            /* Adicionamos o pacote de dados à lista */
            this.recebidos.put(pack.getSequence(),pack);

            System.out.println("[Separe] Recebi pacote de size com seq: " + pack.getSequence() + " e espero o de seq: " + this.nextSizePacketSequence);

            /* Caso haja algúem à espera deste pacote
            acordamos a respetiva thread */
            if(pack.getSequence() == this.nextSizePacketSequence) {
                System.out.println("[Separe] Chegou pacote de size com seq: " + this.nextSizePacketSequence);
                this.cwaitsize.signal();
            }

            this.lwaitsize.unlock();
        }
        else{
            this.lwaitPacketSequence.lock();

            /* Adicionamos o pacote de dados à lista */
            this.recebidos.put(pack.getSequence(),pack);

            /* Sinalizamos caso esteja algúem
            à espera desta pacote */
            if(pack.getSequence() == this.nextPacketWaiting)
                this.cwaitPacketSequence.signal();


            this.lwaitPacketSequence.unlock();
        }
    }
}