package Components;

import AnonStreamProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ReaderFromSocketToStream implements Runnable {

    /**
     * Variável que permite enviar
     * dados para outro anonGW
     */
    private AnonStream stream;

    /**
     * Variável da qual iremos
     * ler os dados
     */
    private Socket socket;

    /**
     * Estrutura de dados que permite
     * obter um id para as sessões
     */
    private int idSession;

    /**
     * Variável que guarda a sequencia do pacote
     */
    private IntegerEncapsuler sequence;

    /**
     * Próximo anon pelo qual os dados
     * irão passar
     */
    private InetAddress destinoIp;

    private int destinoPort;


    private InetAddress destinoFinalIP;

    private int destinoFinalPort;

    /**
     * Construtor para objetos da
     * classe SessionGetter
     *
     * @param stream
     * @param socket
     * @param idSession
     */
    public ReaderFromSocketToStream(AnonStream stream, Socket socket,
                                    int idSession, IntegerEncapsuler sequence,
                                    InetAddress destinoIp, int portIp,
                                    InetAddress destinoFinalIP, int destinoFinalPort) {

        this.stream = stream;
        this.socket = socket;
        this.idSession = idSession;
        this.sequence = sequence;
        this.destinoIp = destinoIp;
        this.destinoPort = portIp;
        this.destinoFinalIP = destinoFinalIP;
        this.destinoFinalPort = destinoFinalPort;
    }

    public void run() {

        try {
            InputStream os = this.socket.getInputStream();
            int lidos;
            /* Enquanto houver dados para
            ler do socket TCP */
            byte[] data = new byte[1024];
            System.out.println("À espera de dados: ");
            System.out.println("Origem: " + this.socket.getLocalAddress());
            while ((lidos = os.read(data,0,1024)) != -1) {
                byte[] dat = new byte[lidos];
                for(int i=0; i<lidos; i++){
                    dat[i] = data[i];
                }
                this.stream.send(dat,sequence,this.socket.getLocalAddress(),this.destinoIp,
                        this.destinoFinalIP,this.socket.getLocalAddress(),this.destinoPort,
                        this.destinoFinalPort);
                System.out.println("À espera de dados");
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}