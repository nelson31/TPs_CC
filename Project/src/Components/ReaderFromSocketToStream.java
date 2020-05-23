package Components;

import AnonProtocol.SessionGetter;
import AnonStreamProtocol.AnonStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

    //////////////////////////////////////Next-Hop//////////////////////////////////////////////

    /**
     * Próximo anon pelo qual os dados
     * irão passar
     */
    private InetAddress destinoIp;

    private int destinoPort;


    ///////////////////////////////////Target Server////////////////////////////////////////////

    private InetAddress destinoFinalIP;

    private int destinoFinalPort;

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Estrutura de dados que guarda as informações
     * acerca das sessões externas
     */
    private ForeignSessions foreignSessions;

    /**
     * Variável que permite ceder o id quando
     * é terminada a conexão com o socket tcp
     */
    private SessionGetter cedeId;

    /**
     * Construtor para objetos da
     * classe SessionGetter
     *
     * @param stream
     * @param socket
     * @param idSession
     */
    public ReaderFromSocketToStream(AnonStream stream, Socket socket,
                                    int idSession, SessionGetter cedeId, ForeignSessions foreignSessions,
                                    InetAddress destinoIp, int portIp,
                                    InetAddress destinoFinalIP, int destinoFinalPort) {

        this.stream = stream;
        this.socket = socket;
        this.idSession = idSession;
        this.destinoIp = destinoIp;
        this.destinoPort = portIp;
        this.destinoFinalIP = destinoFinalIP;
        this.destinoFinalPort = destinoFinalPort;
        this.foreignSessions = foreignSessions;
        this.cedeId = cedeId;
    }

    public void run() {

        try {
            InputStream os = this.socket.getInputStream();
            int lidos;
            InetAddress owner;
            /* Enquanto houver dados para
            ler do socket TCP */
            byte[] data = new byte[1024];
            while ((lidos = os.read(data,0,1024)) != -1) {
                System.out.println("[ReaderFromSocket] Li dados do socket: " + new String(data, StandardCharsets.UTF_8));
                byte[] dat = new byte[lidos];
                for(int i=0; i<lidos; i++){
                    dat[i] = data[i];
                }
                System.out.println("[ReaderFromSocket] enviei dados para " + this.destinoIp + " com id se sessão: " + this.idSession);
                /* Temos que verificar se esta sessão é externa */
                if(this.foreignSessions.isForeign(this.idSession)){
                    /* Se for o owner não somos nós */
                    owner = this.foreignSessions.getInfo(this.idSession).getOwnerIP();
                }
                /* Se não for externa, o owner é
                o próprio anon */
                else {
                    owner = this.stream.getLocalIp();
                    System.out.println("O owner da session nº" + this.idSession + "sou eu");
                }

                this.stream.send(dat,this.socket.getLocalAddress(),this.destinoIp,
                        this.destinoFinalIP,owner,this.destinoPort,
                        this.destinoFinalPort);

                System.out.println("[ReaderFromSocket] Acabei de enviar dados para" + this.destinoIp);
            }
            /* No final fazemos close da stream */
            /* Temos que verificar se esta sessão é externa */
            if(this.foreignSessions.isForeign(this.idSession)){
                /* Se for o owner não somos nós */
                owner = this.foreignSessions.getInfo(this.idSession).getOwnerIP();
            }
                /* Se não for externa, o owner é
                o próprio anon */
            else
                owner = this.stream.getLocalIp();
            this.stream.close(this.socket.getLocalAddress(),this.destinoIp,owner,this.destinoPort);
            /* Libertamos o id utilizado */
            this.cedeId.cedeID(this.idSession);
            /* Se for uma sessão externa removemos a
            linha da foreign sessions table */
            if(this.foreignSessions.isForeign(this.idSession))
                this.foreignSessions.removeAssociation(this.idSession,owner);
            System.out.println("Fechei a conexão");
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}