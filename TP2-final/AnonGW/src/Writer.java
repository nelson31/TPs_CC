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
    private Boolean successFlag;

    /**
     * Boolean que nos diz quando o timeout
     * foi excedido
     */
    private Boolean timeoutReached;

    /**
     * Variável que nos diz se o writer
     * está de momento à espera de um ack
     */
    private Boolean waiting;

    /**
     * Construtor para objetos da classe Writer
     * @param socket
     */
    public Writer(DatagramSocket socket, PacketQueue queue, Lock l, Condition c, Boolean sucessFlag, Boolean waiting){

        this.socket = socket;
        this.queue = queue;
        this.l = l;
        this.c = c;
        this.successFlag = sucessFlag;
        this.timeoutReached = true;
        /* Aqui não estamos a espera
        de nenhum ack - inicialmente será FALSE */
        this.waiting = waiting;
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
                        4096, InetAddress.getByName("0.0.0.0"), ap.getPort());

                /* Se for um ack simplesmente enviamos
                e não esperamos novo ack */
                if(ap.isAcknowledgment())
                    this.socket.send(dp);
                /* Caso contrário teremos de gerir o protocolo
                relacionado com a receção e envio de acks */
                else {
                /* Enquanto o writer não receber confirmação
                que o pacote foi recebido no destino espera */
                    while (this.timeoutReached) {
                        /* Inicializamos o timeoutReached a false */
                        this.timeoutReached = false;
                        /* Enviamos o respetivo pacote */
                        this.socket.send(dp);
                        /* Esperamos no máximo um RTT */
                        this.l.lock();
                        /* Se estamos num novo ciclo não houve
                        sucesso no ciclo anterior */
                        this.successFlag = false;

                        /* Criamos já a thread que implementa o timeout e
                        pômo-la a correr SÓ DEPOIS DE TER OBTIDO O LOCK */
                        new Thread(new TimeoutSignal(this.l, this.c, this.timeoutReached)).start();
                        try {
                            /* Suspendo se ainda não tiver confirmação */
                            while (!this.successFlag && !this.timeoutReached) {
                                /* Aqui estamos à espera de um pacote */
                                this.waiting = true;
                                this.c.await();
                                /* Aqui deixamos de estar à espera de um pacote */
                                this.waiting = false;
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
