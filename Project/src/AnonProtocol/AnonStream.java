package AnonProtocol;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnonStream {

    private static final int maxsize_payload = 796;

    /**
     * Variável para a qual vão sendo enviados
     * os pacotes resultantes da fragmentação
     * dos dados a enviar
     */
    private AnonSocket asocket;

    /**
     * Sessão para a qual a stream se
     * encontra dedicada
     */
    private int session;

    /**
     * Construtor para objetos da classe
     * AnonProtocol.AnonStream
     */
    public AnonStream(AnonSocket asocket, int session) {

        this.asocket = asocket;
        this.session = session;
    }

    /**
     * Método que permite enviar um array
     * de para um determinado host
     * @param dados
     * @param origem
     * @param destino
     * @param destPort
     */
    public void send(byte[] dados, IntegerEncapsuler actualSequence, InetAddress origem,
                     InetAddress destino, InetAddress owner, int destPort, int finalDestPort){

        int count = 0;
        int sizeSequence = actualSequence.getI();
        int sequence = sizeSequence+1;
        byte[] body;
        int dadosSize = dados.length;
        List<AnonPacket> sending = new ArrayList<>();
        int ind;
        for(int i=0; i<dadosSize; sequence++){
            if(dadosSize<maxsize_payload)
                ind = dadosSize;
            else
                ind = maxsize_payload;
            /* Alocamos espaço para o body
            do AnonPacket */
            body = new byte[ind];

            /* Agora copiamos os dados */
            for(int j=0; j<ind; j++, i++)
                body[j] = dados[i];

            /* Construimos novo datagrama */
            AnonPacket sp = new AnonPacket(this.session,sequence,ind,finalDestPort,destino,owner,-1,body);

            count++;

            sending.add(sp);
        }
        /* Enviamos o pacote que refere o tamanho */
        AnonPacket apack = AnonPacket.getSizePacket(session,sizeSequence,finalDestPort,destino,owner,count);
        this.asocket.send(apack,origem,destino,destPort);

        /* Agora enviamos os pacotes */
        for(AnonPacket ap : sending){
            /* Enviamos através do AnonSocket */
            this.asocket.send(ap,origem,destino,destPort);
        }
        /* Atualizamos o valor da sequence
        em vigor */
        actualSequence.setI(sequence);
    }


    public byte[] read(){

        byte[] ret;
        int finalSize = 0;
        AnonPacket ap = null;
        /* Guarda os pacotes que vão sendo
        lidos ordenando-os por sequencia */
        Set<AnonPacket> packs = new TreeSet<>();
        int count = 0;

        do{
            ap = this.asocket.receive(session);
            if(!ap.isSizePacket()){
                count++;
                packs.add(ap);
                finalSize += ap.getPayloadSize();
            }
        }
        while(!ap.isSizePacket());
        /* Aqui o ap é um pacote de size */
        int numReaded = ap.getIsSizeArray();
        while(count<numReaded){
            ap = this.asocket.receive(session);
            finalSize += ap.getPayloadSize();
            packs.add(ap);
            count++;
        }
        ret = new byte[finalSize];
        int ind = 0;
        byte[] body;
        for(AnonPacket pack : packs){
            body = pack.getData();
            for(int i=0; i<pack.getPayloadSize(); i++){
                ret[ind] = body[i];
                ind++;
            }
        }
        return ret;
    }
}