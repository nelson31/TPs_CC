import AnonProto.AnonPacket;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Writer implements Runnable {

    /**
     * Socket UDP para o qual vamos escrever
     * os pacotes presentes na MappingTable
     */
    private DatagramSocket socket;

    /**
     * Tabela que contem os pacotes a serem
     * enviados através do DatagramSocket
     */
    private PacketQueue queue;

    /**
     * Monitor para obter exclusão mútua e
     * permitir suspender a thread
     */
    private Lock l;

    /**
     * Condição que permitirá ao writer esperar
     * pela confirmação do sucesso do envio de
     * um pacote UDP para um determinado destino
     */
    private Condition c;

    /**
     * Boolean que nos diz se o último packet
     * enviado foi recebido com sucesso no destino
     */
    private BooleanEncapsuler successFlag;

    /**
     * Boolean que nos diz quando o timeout
     * foi excedido
     */
    private BooleanEncapsuler timeoutReached;

    /**
     * Variável que nos diz se o writer
     * está de momento à espera de um ack
     */
    private BooleanEncapsuler waiting;

    /**
     * Variável que guarda os valores atribuidos
     * aos campos ackseq de cada um dos pacotes
     * que passam pelo writer
     */
    private int nextAckSeq;

    /**
     * Variável que guarda o valor do ack do
     * pacote que está a tentar ser enviado
     * num dado momento
     */
    private Integer actualAckSeq;

    /**
     * Construtor para objetos da classe Writer
     * @param socket
     */
    public Writer(DatagramSocket socket, PacketQueue queue, Lock l, Condition c,
                  BooleanEncapsuler sucessFlag, BooleanEncapsuler waiting, Integer actualAckSeq){

        this.socket = socket;
        this.queue = queue;
        this.l = l;
        this.c = c;
        this.successFlag = sucessFlag;
        this.timeoutReached = new BooleanEncapsuler(true);
        /* Aqui não estamos a espera
        de nenhum ack - inicialmente será FALSE */
        this.waiting = waiting;
        this.nextAckSeq = 0;
        this.actualAckSeq = actualAckSeq;
    }

    /**
     * Método que arranca a execução do writer
     */
    public void run(){

        try{
            while(true){
                /* Vamos buscar o próximo pacote a enviar */
                AnonPacket ap = this.queue.next();
                // Corrigir InetAdress para o do anonGW que for escolhido para a sessão em questão
                DatagramPacket dp = new DatagramPacket(ap.toByteArray(),
                        ap.toByteArray().length, InetAddress.getByName(ap.getNextPeerIP()), 6666);

                /* Atribuimos um valor ao campo
                que guarda a seq para o ack */
                ap.setAckseq(this.nextAckSeq++);

                System.out.println("[Writer] Pacote para enviar: ");
                System.out.println(ap.toString());

                /* Se for um ack simplesmente enviamos
                e não esperamos novo ack */
                if(ap.isAcknowledgment()) {
                    System.out.println("Vou enviar ack");
                    dp = new DatagramPacket(ap.toByteArray(),ap.toByteArray().length,
                            InetAddress.getByName(ap.getNextPeerIP()),6666);
                    this.socket.send(dp);
                }
                /* Caso contrário teremos de gerir o protocolo
                relacionado com a receção e envio de acks */
                else {
                    /* Enquanto o writer não receber confirmação
                    que o pacote foi recebido no destino espera */
                    while (!this.successFlag.getB()) {
                        /* Inicializamos o timeoutReached a false */
                        this.timeoutReached.setB(false);
                        /* Enviamos o respetivo pacote */
                        this.socket.send(dp);
                        /* Esperamos no máximo um RTT */
                        this.l.lock();
                        /* Atualizamos o valor atual do pacote
                        que pretendemos receber o ack */
                        this.actualAckSeq = this.nextAckSeq;
                        /* Se estamos num novo ciclo não houve
                        sucesso no ciclo anterior */
                        this.successFlag.setB(false);

                        /* Criamos já a thread que implementa o timeout e
                        pômo-la a correr SÓ DEPOIS DE TER OBTIDO O LOCK */
                        new Thread(new TimeoutSignal(this.l, this.c, this.timeoutReached)).start();
                        try {
                            /* Suspendo se ainda não tiver confirmação */
                            while (!this.successFlag.getB() && !this.timeoutReached.getB()) {
                                /* Aqui estamos à espera de um pacote */
                                this.waiting.setB(true);
                                this.c.await();
                                /* Aqui deixamos de estar à espera de um pacote */
                                this.waiting.setB(false);
                            }
                        /* Quando acordar temos que verificar se foi
                        porque o pacote chegou ou porque deu timeout.
                        Se a timeoutReached continuar a true não houve sucesso
                        na comunicação e o ciclo repetir-se-á */
                        } catch (InterruptedException exc) {
                            System.out.println("Erro na espera do acknowlegment - " + exc.getMessage());
                        } finally {
                            /* Cedemos o lock */
                            this.l.unlock();
                        }
                    }
                }
            }
        }
        catch(IOException exc){
            System.out.println("Erro ao enviar pacote pelo socket UDP - " + exc.getMessage());
        }
    }
}
