package AnonProto;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class AnonPacket implements Comparable{

    /**
     * Variável final que define o tamanho
     * máximo de um pacote Anon
     */
    private static final int MAX_SIZE = 4096;

    /**
     * Variável que guarda o número de
     * sessão ao qual pertence o pacote
     */
    private int session;

    /**
     * Variável que guarda o número
     * de sequencia do pacote em questão
     * no conjunto total dos dados a enviar
     */
    private int sequence;

    /**
     * Variável que guarda a sequencia do pacote
     * no reader para o identificar para tratar
     * da gestão de acks
     */
    private int ackseq;

    /**
     * Variável que guarda o endereço ip do
     * anonGW que é o criador do AnonPacket
     */
    private InetAddress ownerIP;

    /**
     * Variável que guarda o endereço IP
     * do destinatário do pacote a ser enviado
     */
    private InetAddress destinationIP;

    /**
     * Variável que guarda o endereço IP da
     * origem do pacote a ser enviado
     */
    private int port;

    /**
     * Variável que contém os próprios dados
     * a serem enviados pelo socket udp
     */
    private byte[] data;

    /**
     * Variável que guarda o endereço IP do
     * peer para o qual vamos envaminhar o pacote
     */
    private String nextPeerIP;

    /**
     * Variável que converte um inteiro no
     * array de bytes para ser enviado por
     * um datagram socket
     * @param i
     * @return
     */
    private byte[] intToBytes(int i) {

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    /**
     * Método que converte um array
     * de bytes para inteiro
     * @param intBytes
     * @return
     */
    private static int byteArrayToInt(byte[] intBytes){

        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    /**
     * Método que copia o conteudo do array source
     * para o array dest, a partir do offset fornecido.
     * Retorna o número efetivo de bytes copiados
     * @param dest
     * @param source
     * @param destOffset
     */
    private static int arraycpy(byte[] dest, byte[] source, int destOffset, int sourceOffset, int number){

        int num = 0;
        for(int i=0; i<number; i++) {
            dest[destOffset] = source[sourceOffset];
            destOffset++;
            sourceOffset++;
            num++;
        }
        return num;
    }

    /**
     * Construtor para objetos da classe AnonProto.AnonPacket
     * @param sequence
     * @param destinationIP
     */
    public AnonPacket(byte[] data, int session, int sequence, String ownerIP, String destinationIP, int port, int ackseq)
        throws UnknownHostException {

        this.sequence = sequence;
        this.session = session;
        this.ownerIP = InetAddress.getByName(ownerIP);
        this.destinationIP = InetAddress.getByName(destinationIP);
        this.port = port;
        this.data = new byte[data.length];
        this.ackseq = ackseq;
        /* Copiamos o conteudo para o pacote */
        for(int i=0; i<data.length; i++)
            this.data[i] = data[i];
    }

    /**
     * Método que permite obter um pacote de ACK
     * @param ackSeq
     * @return
     */
    public static AnonPacket getAcknowledgment(int ackSeq, String destinationIP)
        throws UnknownHostException{

        /* O endereço do owner não é importante neste caso */
        return new AnonPacket(new byte[0],ackSeq,-1,"localhost",destinationIP,0,ackSeq);
    }

    /**
     * Método que permite obter um pacote que guarda
     * o número de pacotes a receber na dada sessão
     * @param session
     * @param sequence
     * @return
     */
    public static AnonPacket getSizePacket(int size, int session, int sequence, String destinationIP, String ownerIP)
        throws UnknownHostException{

        return new AnonPacket(new byte[0],session,sequence,ownerIP, destinationIP, size, -1);
    }

    /**
     * Método que retorna o valor da variável
     * sequence do objeto da classe AnonProto.AnonPacket
     * ao qual é enviado o método
     * @return
     */
    public int getSequence(){

        return this.sequence;
    }

    public int getSession(){

        return this.session;
    }

    public int getPort() {

        return port;
    }

    public InetAddress getOwner(){

        return this.ownerIP;
    }

    public int getAckseq() {

        return ackseq;
    }

    public String getNextPeerIP() {

        return nextPeerIP;
    }

    public InetAddress getDestinationIP() {

        return destinationIP;
    }

    /**
     * Método que retorna o campo
     * de dados do pacote
     * @return
     */
    public byte[] getData() {

        return data;
    }

    /**
     * Método que permite alterar a variável
     * ackseq do objeto da classe AnonPacket
     * ao qual é enviado o método
     * @param ackseq
     */
    public void setAckseq(int ackseq) {

        this.ackseq = ackseq;
    }

    public void setSession(int session) {

        this.session = session;
    }

    public void setNextPeerIP(String nextPeerIP) {

        this.nextPeerIP = nextPeerIP;
    }

    /**
     * Método que nos diz se um determinado
     * packet é um acknowledgement
     * @return
     */
    public boolean isAcknowledgment(){

        return this.session<0;
    }

    /**
     * Método que transforma um anon packet num array
     * de bytes para posteriormente poder ser enviado
     * por um datagram socket
     * @return
     */
    public byte[] toByteArray(){

        byte[] sessArray = this.intToBytes(this.session);
        byte[] seqArray = this.intToBytes(sequence);
        byte[] ackArray = this.intToBytes(this.ackseq);
        byte[] ownArray = this.ownerIP.getAddress();
        byte[] addArray = this.destinationIP.getAddress();
        byte[] portArray = this.intToBytes(this.port);

        /* Array que irá conter o conteudo do pacote Anon */
        byte[] ret = new byte[sessArray.length + seqArray.length + ackArray.length + ownArray.length + addArray.length + portArray.length + this.data.length];
        System.out.println("Tamaho total: " + ret.length);
        int i = 0;
        i += arraycpy(ret,sessArray,i,0,sessArray.length);
        i += arraycpy(ret,seqArray,i,0,seqArray.length);
        i += arraycpy(ret,ackArray,i,0,ackArray.length);
        i += arraycpy(ret,ownArray,i,0,ownArray.length);
        i += arraycpy(ret,addArray,i,0,addArray.length);
        i += arraycpy(ret,portArray,i,0,portArray.length);
        i += arraycpy(ret,this.data,i,0,this.data.length);

        return ret;
    }

    /**
     * Método que retorna um anonpacket tendo em
     * conta sendo-lhe fornecido um array de bytes
     * @param array
     * @return
     * @throws UnknownHostException
     */
    public static AnonPacket getFromByteArray(byte[] array)
        throws UnknownHostException{

        int offset = 0;
        byte[] seqArray = new byte[4], addArray = new byte[4],
                portArray = new byte[4], data, sessArray = new byte[4],
                ownArray = new byte[4], ackArray = new byte[4];
        offset += arraycpy(sessArray,array,0,offset,4);
        offset += arraycpy(seqArray,array,0,offset,4);
        offset += arraycpy(ackArray,array,0,offset,4);
        offset += arraycpy(ownArray,array,0,offset,4);
        offset += arraycpy(addArray,array,0,offset,4);
        offset += arraycpy(portArray,array,0,offset,4);
        data = new byte[array.length-offset];
        arraycpy(data,array,0,offset, array.length-offset);
        InetAddress owner = InetAddress.getByAddress(ownArray);
        InetAddress dest = InetAddress.getByAddress(addArray);
        int port = byteArrayToInt(portArray);
        return new AnonPacket(data,byteArrayToInt(sessArray),byteArrayToInt(seqArray),
                owner.getHostName(),dest.getHostName(),port,byteArrayToInt(ackArray));
    }

    /**
     * Implementação do método toString para
     * objetos da classe AnonProto.AnonPacket
     * @return
     */
    public String toString(){

        StringBuilder sb = new StringBuilder();

        sb.append("Id de sessão: ");
        sb.append(this.session);
        sb.append("; Nºo de sequência: ");
        sb.append(this.sequence);
        sb.append("; Ack sequence: ");
        sb.append(this.ackseq);
        sb.append("; Owner IP address: ");
        sb.append(this.ownerIP);
        sb.append("; Destination IP address: ");
        sb.append(this.destinationIP);
        sb.append("; Port destination: ");
        sb.append(this.port);
        sb.append("; Data: ");
        for(int i=0; i<data.length; i++)
            sb.append(this.data[i]);
        return sb.toString();
    }

    /**
     * Implementação do método compareTo para
     * objetos da classe AnonProto.AnonPacket
     * @param o
     * @return
     */
    public int compareTo(Object o){

        AnonPacket ap = (AnonPacket) o;
        return Integer.compare(this.getSequence(), ap.getSequence());
    }
}
