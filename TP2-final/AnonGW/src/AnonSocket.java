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
     * Construtor para objetos da classe
     * AnonSocket
     * @param port
     * @throws SocketException
     */
    public AnonSocket(int port, String addIP)
        throws SocketException, UnknownHostException {

        /* Lock a ser usado pelo reader e writer
        para a gestão do envio e receção de ACK's */
        Lock l = new ReentrantLock();
        Condition c = l.newCondition();
        Boolean successFlag = Boolean.FALSE;
        Boolean waiting = Boolean.FALSE;

        this.s = new DatagramSocket(port, InetAddress.getByName(addIP));
        /* Criamos a estrutura para enviar pacotes */
        this.sending = new PacketQueue();
        this.receiving = new MappingTable();
        /* Criamos as instâncias para ler
        e escrever no socket UDP */
        this.reader = new Reader(this.s,receiving,sending,l,c,successFlag,waiting);
        this.writer = new Writer(this.s,sending,l,c,successFlag,waiting);
        /* Colocamos o reader e o writer a correr */
        new Thread(this.reader).start();
        new Thread(this.writer).start();
    }

    /**
     * Implementação do método read
     * @return
     */
    public void send(int session, byte[] data, String ipDest, int port)
            throws IOException {

        int k;
        // AQUI HAVERÁ ENCRIPTAÇÃO DOS DADOS

        /* Aqui partimos os dados por AnonPackets */
        List<AnonPacket> list = new ArrayList<>();
        for(int i=0, sequence = 1; i<data.length; sequence++){
            byte[] body = new byte[4080];
            k = 0;
            for(int j=i; k<4080 && j<data.length; j++,k++)
                body[k] = data[j];
            i += k;
            /* Adicionamos o pacote à lista para ser enviado */
            list.add(new AnonPacket(body,session,sequence,"ENDEREÇO DO OWNER", ipDest,port));
        }
        /* Enviamos o número de pacotes
        a serem recebidos */
        int size = list.size();
        /* O endereço IP não é relevante neste caso */
        AnonPacket pack = AnonPacket.getSizePacket(size,session,0,"localhost");
        /* Enviamos o pacote com o tamanho */
        this.sending.send(pack);

        /* Enviamos cada um dos pacotes pelo socket */
        for(AnonPacket packet : list){
            /* Enviamos o pacote para o seru destino */
            this.sending.send(packet);
        }
    }

    /**
     * Método que permite ler uma mensagem
     * do socket dada uma sessão
     * @param session
     * @param data
     * @throws InterruptedException
     */
    public int read(int session, byte[] data)
            throws InterruptedException{

        int messageSize = 0;
        Set<AnonPacket> packets = new TreeSet<>();
        /* Teremos que receber o número de pacotes
        a serem recebidos para remontar a string de
        dados enviada */

        /* Preparamos a table para receber o packet tamanho */
        this.receiving.newPacket(session,0);
        /* Lemos o pacote */
        AnonPacket ap = this.receiving.getPacket(session,0);
        /* Num pacote size o tamanho está representado na port */
        int size = ap.getPort();

        for(int i=0; i<size; i++){
            /* Preparamos a table para a receção de
            cada um dos pacotes com os dados */
            this.receiving.newPacket(session,i);
            /* Recebemos o respetivo pacote */
            ap = this.receiving.getPacket(session,i);
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
        return messageSize;
    }
}
