package AnonStreamProtocol;

import AnonProtocol.AnonPacket;
import AnonProtocol.AnonSocket;
import AnonProtocol.DataInfo;
import AnonProtocol.IntegerEncapsuler;
import SecureProtocol.BooleanEncapsuler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private int localSession;

    /**
     * Variável que guarda o id de sessão com o qual
     * os pacotes terão de ser enviados
     */
    private int ownerSession;

    /**
     * Variável que vai guardando os pacotes
     * que vão sendo recebidos
     */
    private StreamList listAnonPackets;

    /**
     * Variável que permite ler constantemente
     * pacotes do AnonSocket para a stream list
     */
    private PacketReader reader;

    /**
     * Variável que guarda as sequencias
     * dos pacotes a serem enviados
     */
    private IntegerEncapsuler sequence;

    /**
     * Construtor para objetos da classe
     * AnonStreamProtocol.AnonStream
     */
    public AnonStream(AnonSocket asocket, int localSession, int ownerSession) {

        this.asocket = asocket;
        this.localSession = localSession;
        this.ownerSession = ownerSession;
        this.sequence = new IntegerEncapsuler(0);
        this.listAnonPackets = new StreamList();
        this.reader = new PacketReader(this.asocket,this.listAnonPackets,this.localSession);
    }

    /**
     * Método que ativa a extremidade de leitura
     * para ler dados da stream
     */
    public void enableInputStream(){

        /* Colocamos o reader a correr para
        poder ler dados da stream */
        new Thread(this.reader).start();
    }

    /**
     * Método que permite enviar um array
     * de para um determinado host
     * @param dados
     * @param origem
     * @param destino
     * @param destPort
     */
    public void send(byte[] dados, InetAddress origem,
                     InetAddress destino, InetAddress finalDestIp, InetAddress owner,
                     int destPort, int finalDestPort){

        int count = 0;
        int sizeSequence = this.sequence.getI();
        int sequence = sizeSequence+1;
        byte[] body;
        int dadosSize = dados.length;
        List<AnonPacket> sending = new ArrayList<>();
        int ind = 0;
        for(int i=0; i<dadosSize; sequence++){
            if(dadosSize-i<maxsize_payload)
                ind = dadosSize-i;
            else
                ind = maxsize_payload;
            /* Alocamos espaço para o body
            do AnonPacket */
            body = new byte[ind];

            /* Agora copiamos os dados */
            for(int j=0; j<ind; j++, i++)
                body[j] = dados[i];

            /* Construimos novo datagrama */
            AnonPacket sp = new AnonPacket(this.ownerSession,sequence,ind,finalDestPort,finalDestIp,owner,-1,body);

            count++;

            sending.add(sp);
        }
        /* Enviamos o pacote que refere o tamanho */
        AnonPacket apack = AnonPacket.getSizePacket(this.ownerSession,sizeSequence,finalDestPort,finalDestIp,owner,count);
        this.asocket.send(apack,origem,destino,destPort);

        /* Agora enviamos os pacotes */
        for(AnonPacket ap : sending){
            /* Enviamos através do AnonSocket */
            this.asocket.send(ap,origem,destino,destPort);
        }
        /* Atualizamos o valor da sequence
        em vigor */
        this.sequence.setI(sequence);
    }

    /**
     * Método que permite ler da stream
     * um certo conteudo de dados
     * @return
     */
    public byte[] read(){

        byte[] ret;
        int finalSize = 0;
        /* Vamos buscar os pacotes que correspondem à próxima
        mensagem enviada através da outra extremidade da stream */
        Set<AnonPacket> packs = this.listAnonPackets.getNextMessage();

        /* Se tiver recebido fim de comunicação
        retorna-se null */
        if(packs == null)
            return null;

        /* Percorremos cada um dos pacotes para
        obter o tamanho total dos dados */
        for(AnonPacket ap : packs){
            finalSize += ap.getPayloadSize();
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

    /**
     * Método que retorna o endereço IP local
     * para o qual a stream está conectada
     * @return
     */
    public InetAddress getLocalIp(){

        return this.asocket.getLocalIp();
    }

    /**
     * Método que retorna as informações acerca do destino
     * final dos dados recebidos através da stream
     * @return
     */
    public DataInfo getTargetInfo(){

        return this.listAnonPackets.getTargetInfo();
    }

    /**
     * Método que permite fechar a stream para um determinado destino
     */
    public void close(InetAddress origem, InetAddress destino, InetAddress owner, int destPort)
            throws IOException {

        /* Vamos buscar a sequencia */
        int sequence = this.sequence.getI();
        /* Enviamos um anonPacket de fecho */
        AnonPacket fecho = new AnonPacket(this.localSession,sequence,-1,80,
                owner,owner,1,new byte[0]);

        this.asocket.send(fecho,origem,destino,destPort);
    }
}
