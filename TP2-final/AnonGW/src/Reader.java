import AnonProto.*;
import Table.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Reader implements Runnable {

    /**
     * Socket UDP a partir do qual o reader
     * realizará as suas leituras
     */
    private DatagramSocket socket;

    /**
     * Lista que guarda todos os pacotes
     * que vão sendo lidos do datagram socket
     */
    private MappingTable table;

    /**
     * Variável que permite o envio de acks
     * mediante novos pacotes recebidos
     */
    private PacketQueue sendAcks;

    /**
     * Variável necessária para obter condition
     * para sinalizar o writer quando chegar um ack
     */
    private Lock l;

    /**
     * Variável usada para sinalizar o writer
     * quando chegar um novo acknowledgement
     */
    private Condition c;

    /**
     * Boolean usada para sinalizar o writer
     * sempre que seja recebido um acknowledgment
     */
    private Boolean successFlag;

    /**
     * Variável que nos diz se o writer está
     * a espera de acks
     */
    private Boolean isWriterWaitingForAcks;

    /**
     * Construtor para objetos da classe reader
     */
    public Reader(DatagramSocket socket, MappingTable table, PacketQueue sendAcks,
                  Lock l, Condition c, Boolean successFlag, Boolean isWriterWaitingForAcks){

        this.socket = socket;
        this.table = table;
        this.sendAcks = sendAcks;
        this.l = l;
        this.c = c;
        this.successFlag = successFlag;
        this.isWriterWaitingForAcks = isWriterWaitingForAcks;
    }

    /**
     * Método que permite inicial a
     * execução do Reader
     */
    public void run(){

        try {
        /* O reader ficará permanentemente
        à espera de novos pacotes */
            while (true) {
                byte[] buffer = new byte[4096];
                DatagramPacket dp = new DatagramPacket(buffer, 4096);
                this.socket.receive(dp);
                // Aqui teremos que desencriptar a informação quando tratarmos da segurança
                AnonPacket ap = AnonPacket.getFromByteArray(dp.getData());
                /* Se for ACK sinalizamos o writer */
                if(ap.isAcknowledgment()){
                    this.l.lock();
                    try{
                        /* Colocamos o valor do success a true
                        caso o writer esteja à espera */
                        if(this.isWriterWaitingForAcks) {
                            this.successFlag = true;
                            /* Sinalizamos o writer */
                            this.c.signal();
                        }
                    }
                    finally {
                        /* Cedemos o lock */
                        this.l.unlock();
                    }
                }
                else {
                    /* Colocamos o pacote na table */
                    this.table.addPacket(ap.getSession(), ap);
                    /* Enviamos o ack para o destino com
                    o respetivo ack */
                    AnonPacket ack = AnonPacket.getAcknowledgment(-ap.getSequence(),ap.getOwner().getHostAddress());
                    this.sendAcks.send(ack);
                }
            }
        }
        catch(IOException e){
            System.out.println("Ocorreu um erro na leitura do datagrama UDP do socket");
        }
    }
}
