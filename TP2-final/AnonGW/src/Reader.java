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
    private BooleanEncapsuler successFlag;

    /**
     * Variável que nos diz se o writer está
     * a espera de acks
     */
    private BooleanEncapsuler isWriterWaitingForAcks;

    /**
     * Variável que guarda o endereço
     * IP do anonGW local
     */
    private String localIP;

    /**
     * Variável que guarda os Id's que são
     * atribuidos a sessões cujo owner é
     * outro AnonGW que não o local
     */
    private ForeignSessions foreignTable;

    /**
     * Variável que permite obter a ids de
     * sessão para o anonGW local
     */
    private SessionGetter idSessionGetter;

    /**
     * Variável que guarda o valor do ack do
     * pacote que está a tentar ser enviado
     * num dado momento
     */
    private IntegerEncapsuler actualAckSeq;

    /**
     * Construtor para objetos da classe reader
     */
    public Reader(DatagramSocket socket, MappingTable table, PacketQueue sendAcks,
                  Lock l, Condition c, BooleanEncapsuler successFlag, BooleanEncapsuler isWriterWaitingForAcks,
                  String localIP, ForeignSessions foreignTable, SessionGetter idSessionGetter,
                  IntegerEncapsuler actualAckSeq){

        this.socket = socket;
        this.table = table;
        this.sendAcks = sendAcks;
        this.l = l;
        this.c = c;
        this.successFlag = successFlag;
        this.isWriterWaitingForAcks = isWriterWaitingForAcks;
        this.localIP = localIP;
        this.foreignTable = foreignTable;
        this.idSessionGetter = idSessionGetter;
        this.actualAckSeq = actualAckSeq;
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
                System.out.println("[Reader] Pacote recebido: " + ap.toString());
                /* Se for ACK sinalizamos o writer */
                if(ap.isAcknowledgment()){
                    this.l.lock();
                    try{
                        /* Colocamos o valor do success a true
                        caso o writer esteja à espera */
                        if(this.isWriterWaitingForAcks.getB() && this.actualAckSeq.equals(-ap.getSession())) {
                            this.successFlag.setB(true);
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
                    int sessionHere = this.idSessionGetter.getID();
                    /* Caso o pacote não pertence ao anonGW local,
                    atribuimos-lhe um id de sessão local */
                    if(!this.localIP.equals(ap.getOwner().getHostAddress()))
                        ap.setSession(sessionHere);

                    /* Colocamos o pacote na table */
                    this.table.addPacket(ap.getSession(), ap);
                    /* Enviamos o ack para o destino com
                    o respetivo ack */
                    AnonPacket ack = AnonPacket.getAcknowledgment(ap.getAckseq(),ap.getOwner().getHostAddress());
                    this.sendAcks.send(ack.getDestinationIP().getHostAddress(), ack);
                    /* Se o owner do pacote não for o anonGW local
                    adicionamos uma entrada à foreignTable */
                    if(!this.localIP.equals(ap.getOwner().getHostAddress())){
		            System.out.println(this.localIP + " " + ap.getOwner().getHostAddress());
                        this.foreignTable.add(sessionHere,ap.getOwner().getHostAddress(),
                                ap.getSession(),ap.getDestinationIP().getHostAddress(),ap.getPort());
	 	            }
                }
            }
        }
        catch(IOException e){
            System.out.println("Ocorreu um erro na leitura do datagrama UDP do socket");
        }
    }
}
