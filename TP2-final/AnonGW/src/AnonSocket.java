import AnonProto.AnonPacket;
import Table.MappingTable;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AnonSocket {

    /**
     * Socket a partir do qual recebemos e
     * enviamos os dados para outros anonGW
     */
    private DatagramSocket s;

    /**
     * Instância runnable responsável
     * por ler do socket para a tabela
     */
    private Reader reader;

    /**
     * Thread que será responsável por
     * escrever no socket
     */
    private Writer writer;

    /**
     * Estrutura de dados a partir da
     * qual vamos recever pacotes UDP
     */
    private MappingTable receiving;

    /**
     * Estrutura de dados a partir da
     * qual vamos enviar pacotes UDP
     */
    private PacketQueue sending;

    /**
     * Variável que guarda o IP do anonGW local
     */
    private String localIP;

    private ForeignSessions foreignTable;

    private SessionGetter idSessionGetter;

    private CurrentSequence activeSessions;

    /**
     * Construtor para objetos da classe
     * AnonSocket
     * @param port
     * @throws SocketException
     */
    public AnonSocket(int port, String addIP, ForeignSessions foreignTable, SessionGetter idSessionGetter)
        throws SocketException, UnknownHostException {

        /* Lock a ser usado pelo reader e writer
        para a gestão do envio e receção de ACK's */
        Lock l = new ReentrantLock();
        Condition c = l.newCondition();
        this.foreignTable = foreignTable;
        this.idSessionGetter = idSessionGetter;
        BooleanEncapsuler successFlag = new BooleanEncapsuler(false);
        BooleanEncapsuler waiting = new BooleanEncapsuler(false);

        this.s = new DatagramSocket(port, InetAddress.getByName(addIP));
        /* Criamos a estrutura para enviar pacotes */
        this.sending = new PacketQueue();
        this.receiving = new MappingTable();
        this.activeSessions = new CurrentSequence();
        /* Criamos as instâncias para ler
        e escrever no socket UDP */
        Integer actualAckSeq = 0;
        this.reader = new Reader(this.s,receiving,sending,l,c,successFlag,waiting,addIP,foreignTable,this.idSessionGetter,0);
        this.writer = new Writer(this.s,sending,l,c,successFlag,waiting,0);
        /* Colocamos o reader e o writer a correr */
        new Thread(this.reader).start();
        new Thread(this.writer).start();
    }

    /**
     * Implementação do método read
     * @return
     */
    public void send(int session, byte[] data, String nextPeerIP, String targetIP, int port)
            throws IOException {

        int k;
        // AQUI HAVERÁ ENCRIPTAÇÃO DOS DADOS
        /* Se a sessão ainda não estiver ativa
        ativamos uma nova */
        if(!this.activeSessions.contains(session))
            this.activeSessions.put(session,0);

        String ownerIP = this.s.getLocalAddress().getHostAddress();
        /* Se o id de sessão for local então o
        owner da sessão é um outro anonGW */
        if(this.foreignTable.isForeign(session)){
            SessionInfo info = this.foreignTable.get(session);
            ownerIP = info.getOwnerIP();
            session = info.getOwnerSessionID();
        }
        int sizeSequence = this.activeSessions.getAndIncrement(session);
        int sequence = this.activeSessions.getAndIncrement(session);
        /* Aqui partimos os dados por AnonPackets */
        List<AnonPacket> list = new ArrayList<>();
        for(int i=0; i<data.length; sequence = this.activeSessions.getAndIncrement(session)){
            byte[] body = new byte[4080];
            k = 0;
            for(int j=i; k<4080 && j<data.length; j++,k++)
                body[k] = data[j];
            i += k;
            /* Adicionamos o pacote à lista para ser enviado */
            list.add(new AnonPacket(body,session,sequence,ownerIP,targetIP,port,-1));
        }
        /* Enviamos o número de pacotes
        a serem recebidos */
        int size = list.size();
        /* O endereço IP não é relevante neste caso */
        AnonPacket pack = AnonPacket.getSizePacket(size,session,sizeSequence,"0.0.0.0",ownerIP);
        /* Enviamos o pacote com o tamanho */
        this.sending.send(nextPeerIP, pack);

        /* Enviamos cada um dos pacotes pelo socket */
        for(AnonPacket packet : list){
            /* Adicionamos os pacotes à estrutura Queue.PacketQueue
            para estes serem reencaminhados pelo writer */
            this.sending.send(nextPeerIP, packet);
        }
    }

    /**
     * Método que permite ler uma mensagem
     * do socket dada uma sessão
     * @param session
     * @throws InterruptedException
     */
    public byte[] read(int session, TargetServerInfo target)
            throws InterruptedException{

        byte[] data;
        int messageSize = 0;
        Set<AnonPacket> packets = new TreeSet<>();
        /* Teremos que receber o número de pacotes
        a serem recebidos para remontar a string de
        dados enviada */
        /* Se a sessão não estiver ativa ativamos */
        if(!this.activeSessions.contains(session))
            this.activeSessions.put(session, 0);

        /* Lemos o pacote */
        AnonPacket ap = this.receiving.getPacket(session,this.activeSessions.getAndIncrement(session));
        /* Num pacote size o tamanho está representado na port */
        int size = ap.getPort();

        for(int i=0; i<size; i++){
            /* Recebemos o respetivo pacote */
            ap = this.receiving.getPacket(session,this.activeSessions.getAndIncrement(session));
            if(i==0) {
                target.setTargetIP(ap.getDestinationIP());
                target.setTargetPort(ap.getPort());
            }
            /* Adicionamos o pacote ao Set */
            packets.add(ap);
            /* Aumentamos ao tamanho total
            da mensagem */
            messageSize += ap.getData().length;
        }

        data = new byte[messageSize];

        /* Copiamos o conteudo de cada packet
        para a mensagem de destino */
        Iterator it = packets.iterator();

        /* Copiamos os dados para o array da mensagem */
        byte[] dados; int length;
        for(int ind = 0; it.hasNext(); ){
            ap = (AnonPacket) it.next();
            dados = ap.getData();
            length = dados.length;
            for(int i=0; i<length; i++, ind++){
                data[ind] = dados[i];
            }
        }
        return data;
    }

    /**
     * Método que dado o id de sessão liberta o
     * espaço na foreign table caso seja uma
     * sessão externa e cede o id
     * @param idSession
     */
    public void endSession(int idSession){

        /* Caso estejamos perante uma sessão externa
        eliminamos essa linha da tabela foreign */
        if(this.foreignTable.isForeign(idSession))
            this.foreignTable.remove(idSession);
        /* Cedemos o id da sessão mesmo que não se
        trate de uma sessão externa */
        this.idSessionGetter.cedeID(idSession);
        /* Removemos a respetiva linha da sessão */
        this.activeSessions.remove(idSession);
    }
}
