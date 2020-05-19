import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class SecurePacket {

    /**
     * Variável que identifica o
     * pacote para receber acks
     */
    private int id;

    /**
     * Variável que contém os
     * dados a serem enviados
     */
    private byte[] data;

    /**
     * Variável que guarda a
     * origem do pacote
     */
    private InetAddress origem;

    /**
     * Variável que guarda o destino
     * do pacote
     */
    private InetAddress destino;

    /**
     * Variável que guarda a porta
     * de destino do pacote
     */
    private int port;

    /**
     * Variável que nos informa acerca
     * do tamanho do payload
     */
    private int payloadSize;

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
     * Método que permite obter um ack para um determinado
     * pacote. O id de um ack é o simétrico do pacote ao
     * qual ele confirma a sua chegada.
     * @param id
     * @param destinoDoAck
     * @param portDestino
     * @return
     */
    public static SecurePacket getAck(int id, InetAddress origemDoAck, InetAddress destinoDoAck, int portDestino){

        return new SecurePacket(-id,origemDoAck,destinoDoAck,portDestino,0,new byte[0]);
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
        byte[] idArray = intToBytes(this.id);
        byte[] origemArray = this.origem.getAddress();
        byte[] destinoArray = this.destino.getAddress();
        byte[] portArray = intToBytes(this.port);
        byte[] sizeArray = intToBytes(this.payloadSize);
        /* Array que irá conter o conteudo do pacote Anon */
        byte[] ret = new byte[idArray.length + origemArray.length + destinoArray.length + portArray.length + sizeArray.length + this.data.length];

        int i = 0;
        i += arraycpy(ret,idArray,i,0,idArray.length);
        i += arraycpy(ret,origemArray,i,0,origemArray.length);
        i += arraycpy(ret,destinoArray,i,0,destinoArray.length);
        i += arraycpy(ret,portArray,i,0,portArray.length);
        i += arraycpy(ret,sizeArray,i,0,sizeArray.length);
        arraycpy(ret,this.data,i,0,this.data.length);

        return ret;
    }

    /**
     * Método que permite obter um securePacket a
     * partir de uma sequencia de bytes
     * @param data
     * @return
     */
    public static SecurePacket getFromByteArray(byte[] data)
            throws UnknownHostException {

        int offset = 0;
        byte[] idArray = new byte[4];
        byte[] origemArray = new byte[4];
        byte[] destinoArray = new byte[4];
        byte[] portArray = new byte[4];
        byte[] sizeArray = new byte[4];
        offset += arraycpy(idArray,data,0,offset,4);
        offset += arraycpy(origemArray,data,0,offset,4);
        offset += arraycpy(destinoArray,data,0,offset,4);
        offset += arraycpy(portArray,data,0,offset,4);
        offset += arraycpy(sizeArray,data,0,offset,4);
        byte[] body = new byte[data.length-offset];
        arraycpy(body,data,0,offset, data.length-offset);

        InetAddress destino = InetAddress.getByAddress(destinoArray);
        InetAddress origem = InetAddress.getByAddress(origemArray);

        return new SecurePacket(byteArrayToInt(idArray),origem,destino,byteArrayToInt(portArray),byteArrayToInt(sizeArray),body);
    }

    /**
     * Construtor para objetos da classe SecurePacket
     */
    public SecurePacket(int id, InetAddress origem, InetAddress destino, int port, int payloadSize, byte[] data){

        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.port = port;
        this.payloadSize = payloadSize;
        this.data = new byte[data.length];
        for(int i=0; i<data.length; i++){
            this.data[i] = data[i];
        }
    }

    public InetAddress getOrigem() {

        return origem;
    }

    public InetAddress getDestino() {

        return destino;
    }

    public int getPort() {

        return port;
    }

    public byte[] getData(){

        return this.data;
    }

    public int getId() {

        return id;
    }

    public int getPayloadSize() {

        return payloadSize;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setData(byte[] data) {

        this.data = data;
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Id: ");
        sb.append(this.id);
        sb.append("; IP origem: ");
        sb.append(this.origem);
        sb.append("; IP destino: ");
        sb.append(this.destino);
        sb.append("; Port: ");
        sb.append(this.port);
        sb.append("; PayloadSize: ");
        sb.append(this.payloadSize);
        sb.append("; Data: ");
        for(int i=0; i<this.data.length; i++){
            sb.append(this.data[i]);
        }
        System.out.println();
        return sb.toString();
    }

    /**
     * Método que nos diz se um determinado
     * SecurePacket é um Ack
     * @return
     */
    public boolean isAck(){

        return this.id<0;
    }

    /**
     * Implementação do método equals para
     * objetos da classe SecurePacket
     */
    public boolean equals(Object o){

        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        SecurePacket sp = (SecurePacket) o;

        return this.getId() == sp.getId();
    }
}
