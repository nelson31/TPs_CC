package AnonStreamProtocol;

import AnonProtocol.AnonPacket;
import AnonProtocol.AnonSocket;

public class PacketReader implements Runnable{

    /**
     * Estrutura de dados para a qual
     * vão sendo lidos os pacotes
     */
    private StreamList listaPackets;

    /**
     * Socket a partir do qual vão
     * sendo lidos os pacotes
     */
    private AnonSocket asocket;

    private int session;

    /**
     * Construtor para objetos da
     * classe PacketReader
     * @param asocket
     * @param listaPackets
     */
    public PacketReader(AnonSocket asocket, StreamList listaPackets, int session){

        this.asocket = asocket;
        this.listaPackets = listaPackets;
        this.session = session;
    }

    /**
     * Implementação do método run para
     * objetos da classe PacketReader
     */
    public void run(){

        while(true){
            /* Lemos pacotes do socket */
            AnonPacket ap = this.asocket.receive(session);
            /* Adicionamos o respetivo pacote à lista */
            this.listaPackets.addPacket(ap);
        }
    }
}
