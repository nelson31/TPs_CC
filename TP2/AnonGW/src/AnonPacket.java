import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class AnonPacket{

    /**
     * Variável final que define o tamanho
     * máximo de um pacote Anon
     */
    private static final int MAX_SIZE = 4096;

    /**
     * Variável que guarda o número
     * de sequencia do pacote em questão
     * no conjunto total dos dados a enviar
     */
    private int sequence;

    /**
     * Variável que guarda o endereço IP
     * do destinatário do pacote a ser enviado
     */
    private InetAddress destinationIP;

    /**
     * Variável que guarda a porta do destinatário
     * para o qual será enviado o pacote
     */
    private int port;

    /**
     * Variável que contém os próprios dados
     * a serem enviados pelo socket udp
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
     * Construtor para objetos da classe AnonPacket
     * @param sequence
     * @param destinationIP
     */
    public AnonPacket(byte[] data, int sequence, String destinationIP, int port)
        throws UnknownHostException {

        this.sequence = sequence;
        this.destinationIP = InetAddress.getByName(destinationIP);
        this.port = port;
        this.data = new byte[data.length];
        /* Copiamos o conteudo para o pacote */
        for(int i=0; i<data.length; i++)
            this.data[i] = data[i];
    }

    /**
     * Método que transforma um anon packet num array
     * de bytes para posteriormente poder ser enviado
     * por um datagram socket
     * @return
     */
    public byte[] toByteArray(){

        byte[] seqArray = this.intToBytes(sequence);
        byte[] addArray = this.destinationIP.getAddress();
        byte[] portArray = this.intToBytes(port);

        /* Array que irá conter o conteudo do pacote Anon */
        byte[] ret = new byte[seqArray.length + addArray.length + portArray.length + this.data.length];
        System.out.println("Tamaho total: " + ret.length);
        int i = 0;
        i += arraycpy(ret,seqArray,i,0,seqArray.length);
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
        byte[] seqArray = new byte[4], addArray = new byte[4], portArray = new byte[4], data;
        offset += arraycpy(seqArray,array,0,offset,4);
        offset += arraycpy(addArray,array,0,offset,4);
        offset += arraycpy(portArray,array,0,offset,4);
        data = new byte[array.length-offset];
        offset += arraycpy(data,array,0,offset, array.length-offset);
        InetAddress ip = InetAddress.getByAddress(addArray);
        return new AnonPacket(data,byteArrayToInt(seqArray),ip.getHostName(),byteArrayToInt(portArray));
    }

    /**
     * Implementação do método toString para
     * objetos da classe AnonPacket
     * @return
     */
    public String toString(){

        StringBuilder sb = new StringBuilder();

        sb.append("Nºo de sequência: ");
        sb.append(this.sequence);
        sb.append("; IP address: ");
        sb.append(this.destinationIP);
        sb.append("; Port: ");
        sb.append(this.port);
        sb.append("; Data: ");
        sb.append(this.data);

        return sb.toString();
    }
}
