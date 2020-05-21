package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.DataInfo;
import AnonStreamProtocol.AnonStream;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class OwnServer {

    public static void main(String[] args) {

        try {
            InetAddress origem = InetAddress.getByName(args[0]);
            /* Criamos um anon socket e associamo-lo para o endereço local */
            AnonSocket socket = new AnonSocket(6666, origem);
            AnonStream stream = new AnonStream(socket,0);
            /* Ativamos a extremidade de leitura */
            stream.enableInputStream();
            DataInfo info = new DataInfo();
            byte[] lido; String message, feedback;
            while ((lido = stream.read()) != null){
                System.out.println("Acabei de ler da stream");
                message = new String(lido, StandardCharsets.UTF_8);
                System.out.println("String lida da stream: " + message);
                /* Obtemos a informação para obter o owner */
                if(!info.isComplete())
                    info = stream.getTargetInfo();
                /* Enviamos feedback da stream */
                feedback = "Conteúdo lido: " + message;
                System.out.println("Vou enviar feedback");
                stream.send(feedback.getBytes(),origem,info.getOwner(),
                        InetAddress.getByName("localhost"),info.getOwner(),6666,80);
                System.out.println("Enviei feedback");
            }
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println(exc.getMessage());
        }
    }
}
