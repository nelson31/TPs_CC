package AnonProtocol;

import SecureProtocol.SecurePacket;
import SecureProtocol.SecureSocket;

import java.net.InetAddress;
import java.net.SocketException;

public class AnonSocket {

    /**
     * Variável que permite comunicar com
     * outras máquina com UDP de forma algo efetiva
     */
    private SecureSocket socket;

    /**
     * Variável que guarda o endereço IP local
     */
    private InetAddress localIp;

    /**
     * Variável que mantém associação entre o id
     * de uma sessão e os respetivos pacotes
     * que pertencem a essa mesma sessão
     */
    private MappingTable received;

    /**
     * Construtor para objetos da classe
     * AnonSocket
     */
    public AnonSocket(int port, InetAddress localIp)
            throws SocketException {

        this.localIp = localIp;
        this.socket = new SecureSocket(port,localIp);
        this.received = new MappingTable();
    }


    /**
     * Método que permite enviar um AnonPacket
     * para um determinado destino
     * @param ap
     * @param origem
     * @param destino
     * @param destPort
     */
    public void send(AnonPacket ap, InetAddress origem, InetAddress destino, int destPort){

        /* Convertemos o anonPacket para bytes */
        byte[] body = ap.toByteArray();
        /* Encapsulamos o anonPacket num SecurePacket */
        SecurePacket sp = new SecurePacket(-1,origem,destino,destPort,body.length,body);
        /* Enviamos para o destino respetivo */
        this.socket.send(sp);
    }

    /**
     * Método que permite receber um anonPacket
     * com um determinado id de sessão
     * @param session
     */
    public AnonPacket receive(int session){

        return this.received.getPacket(session);
    }
}
