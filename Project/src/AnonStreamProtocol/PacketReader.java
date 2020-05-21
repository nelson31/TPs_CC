package AnonStreamProtocol;

import AnonProtocol.AnonPacket;
import AnonProtocol.AnonSocket;
import AnonProtocol.DataInfo;

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
            /* Vamos buscar a informação acerca do target server*/
            DataInfo info = this.listaPackets.getTargetInfo();
            if(!info.isComplete()){
                info.setTargetServer(ap.getTargetServerIP());
                info.setOwner(ap.getOwnerIP());
                info.setSession(ap.getSession());
                info.setTargetPort(ap.getTargetPort());
            }
        }
    }
}
