package AnonProtocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class AnonPacket implements Comparable{

    /**
     * Variável que identifica a
     * sessão de comunicação
     */
    private int session;

    /**
     * Variável que identifica o offset dos
     * dados do pacote no total dos dados
     */
    private int sequence;

    /**
     * Variável que indica o tamanho do
     * campo dados do pacote
     */
    private int payloadSize;

    /**
     * Variável que guarda a porta
     * do target server
     */
    private int targetPort;

    /**
     * Variável que guarda o endereço
     * IP do targetServer final
     */
    private InetAddress targetServerIP;

    /**
     * Variável que guarda o endereço
     * IP do owner
     */
    private InetAddress ownerIP;

    /**
     * Variável que nos diz se o pacote
     * é do tipo de tamanho
     */
    private int isSizeArray;

    /**
     * Estrutura de dados que armazena
     * o conteúdo do pacote
     */
    private byte[] data;

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
     * Método que converte um securePacket
     * num array de bytes
     * @return
     */
    public byte[] toByteArray(){

        /* Convertemos o id para bytes */
        byte[] sessArray = intToBytes(this.session);
        byte[] seqArray = intToBytes(this.sequence);
        byte[] sizeArray = intToBytes(this.payloadSize);
        byte[] portArray = intToBytes(this.targetPort);
        byte[] targArray = this.targetServerIP.getAddress();
        byte[] ownArray = this.ownerIP.getAddress();
        byte[] isSArray = intToBytes(this.isSizeArray);
        /* Array que irá conter o conteudo do pacote Anon */
        byte[] ret = new byte[sessArray.length + seqArray.length + sizeArray.length +
                portArray.length + targArray.length + ownArray.length + isSArray.length +
                data.length];

        int i = 0;
        i += arraycpy(ret,sessArray,i,0,sessArray.length);
        i += arraycpy(ret,seqArray,i,0,seqArray.length);
        i += arraycpy(ret,sizeArray,i,0,sizeArray.length);
        i += arraycpy(ret,portArray,i,0,portArray.length);
        i += arraycpy(ret,targArray,i,0,targArray.length);
        i += arraycpy(ret,ownArray,i,0,ownArray.length);
        i += arraycpy(ret,isSArray,i,0,isSArray.length);
        arraycpy(ret,this.data,i,0,this.data.length);

        return ret;
    }

    /**
     * Método que permite obter um securePacket a
     * partir de uma sequencia de bytes
     * @param data
     * @return
     */
    public static AnonPacket getFromByteArray(byte[] data)
            throws UnknownHostException {

        int offset = 0;
        byte[] sessArray = new byte[4];
        byte[] seqArray = new byte[4];
        byte[] sizeArray = new byte[4];
        byte[] portArray = new byte[4];
        byte[] targArray = new byte[4];
        byte[] ownArray = new byte[4];
        byte[] isSArray = new byte[4];
        offset += arraycpy(sessArray,data,0,offset,4);
        offset += arraycpy(seqArray,data,0,offset,4);
        offset += arraycpy(sizeArray,data,0,offset,4);
        offset += arraycpy(portArray,data,0,offset,4);
        offset += arraycpy(targArray,data,0,offset,4);
        offset += arraycpy(ownArray,data,0,offset,4);
        offset += arraycpy(isSArray,data,0,offset,4);
        byte[] body = new byte[data.length-offset];
        arraycpy(body,data,0,offset, data.length-offset);

        InetAddress targetServerIP = InetAddress.getByAddress(targArray);
        InetAddress ownerIP = InetAddress.getByAddress(ownArray);

        return new AnonPacket(byteArrayToInt(sessArray),byteArrayToInt(seqArray),byteArrayToInt(sizeArray),
                byteArrayToInt(portArray),targetServerIP,ownerIP,byteArrayToInt(isSArray),body);
    }

    /**
     * Construtor para objetos da classe AnonProtocol.AnonPacket
     * @param session
     * @param sequence
     * @param payloadSize
     * @param data
     */
    public AnonPacket(int session, int sequence,
                      int payloadSize, int port, InetAddress targetServerIP,
                      InetAddress ownerIP, int isSizeArray, byte[] data){

        this.session = session;
        this.sequence = sequence;
        this.payloadSize = payloadSize;
        this.targetPort = port;
        this.targetServerIP = targetServerIP;
        this.ownerIP = ownerIP;
        this.isSizeArray = isSizeArray;
        this.data = new byte[data.length];
        for(int i=0; i<data.length; i++){
            this.data[i] = data[i];
        }
    }

    /**
     * Variável que permite obter um packet
     * anon do tipo size
     * @return
     */
    public static AnonPacket getSizePacket(int session, int sequence,
                                           int port, InetAddress targetServerIP, InetAddress ownerIP,
                                           int size){

        return new AnonPacket(session,sequence,0,port,targetServerIP,ownerIP,size,new byte[0]);
    }

    /**
     * Método que nos diz se um AnonPacket
     * é um pacote de tamanho
     * @return
     */
    public boolean isSizePacket(){

        return this.isSizeArray>0;
    }

    public int getSession() {

        return this.session;
    }

    public int getSequence(){

        return this.sequence;
    }

    public InetAddress getOwnerIP() {

        return ownerIP;
    }

    public int getIsSizeArray() {

        return isSizeArray;
    }

    public int getPayloadSize() {

        return payloadSize;
    }

    public byte[] getData() {

        return data;
    }

    public void setSession(int session) {

        this.session = session;
    }

    /**
     * Implementação do método toString para
     * objetos da classe AnonProtocol.AnonPacket
     * @return
     */
    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Session: ");
        sb.append(this.session);
        sb.append("; Sequence: ");
        sb.append(this.sequence);
        sb.append("; Payload size: ");
        sb.append(this.payloadSize);
        sb.append("; Target port: ");
        sb.append(this.targetPort);
        sb.append("; Target server IP: ");
        sb.append(this.targetServerIP);
        sb.append("; Owner IP: ");
        sb.append(this.ownerIP);
        sb.append("; Is Size: ");
        sb.append(this.isSizeArray);
        sb.append("; Data: ");
        for(int i=0; i<this.data.length; i++){
            sb.append(this.data[i]);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {

        AnonPacket ap = (AnonPacket) o;
        return Integer.compare(this.getSequence(), ap.getSequence());
    }
}
