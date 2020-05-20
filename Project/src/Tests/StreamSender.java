package Tests;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class StreamSender {

    /**
     * Variável que converte um inteiro no
     * array de bytes para ser enviado por
     * um datagram socket
     * @param i
     * @return
     */
    private static byte[] intToBytes(int i) {

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    public static void main(String[] args) {

        try {
            InetAddress origem = InetAddress.getByName(args[0]);
            InetAddress destino;
            AnonStream as = new AnonStream(new AnonSocket(6666, origem),0);
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String message;
            byte[] data = new byte[4096];
            byte[] valor;
            System.out.println("Dados enviados: ");
            for(int i = 0; i<1024; i++){
                valor = intToBytes(i);
                data[i++] = valor[0];
                data[i++] = valor[1];
                data[i++] = valor[2];
                data[i++] = valor[3];
            }
            for(int i=0; i<4096; i++)
                System.out.print(data[i]);
            System.out.println();
            System.out.println("Inserir destino: ");
            while((message = bf.readLine()) != null){
                destino = InetAddress.getByName(message);
                as.send(data,origem,destino,destino,origem,6666,80);
                System.out.println("Inserir destino: ");
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}