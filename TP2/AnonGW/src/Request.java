import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Request {

    /**
     * Associamos o valor true ao estado
     * do pedido quando considerado "tratado"/"feito"
     */
    private static final boolean FEITO = true;

    /**
     * Associamos o valor false ao estado
     * do pedido quando este se encontra em "espera"
     */
    private static final boolean ESPERA = false;

    /**
     * Mensagem enviada
     * pelo cliente
     */
    private String message;

    /**
     * Socket a partir do qual o downloader irá
     * comunicar com o cliente durante o processo
     * de download de um ficheiro de música
     */
    private Socket s;

    /**
     * Variável que nos diz se o Request já
     * foi tratado ou não
     */
    private boolean status;

    /**
     * Objeto Condition usado para alertar o
     * originador do pedido quando ele for tratado
     */
    private Condition c;

    /**
     * Lock ao qual a variável de instância
     * descrita anteriormente está associada
     */
    private Lock l;

    /**
     * Construtor para objetos da classe Download.Request
     * @param message
     */
    public Request(String message, Socket s){

        this.message = message;
        this.s = s;
        /* Inicialmente, o pedido
        está em espera */
        this.status = ESPERA;
    }

    /**
     * Método que retorna a mensagem existente
     * no objeto da classe Download.Request ao qual é
     * enviado o método
     * @return
     */
    public String getMessage() {

        return this.message;
    }

    /**
     * Método que retorna o objeto a partir do
     * qual se escrevem linhas de texto para o cliente
     * @return
     */
    public Socket getS() {

        return s;
    }

    /**
     * Método que permite atribuir uma condição
     * ao objeto classe Request ao qual é enviado
     * o método
     * @param c
     */
    public void setC(Condition c) {

        this.c = c;
    }

    /**
     * Método que permite atribuir um lock
     * ao objeto da classe Request ao qual
     * é enviado o método
     * @param l
     */
    public void setL(Lock l) {

        this.l = l;
    }

    /**
     * Método que permite declarar um pedido
     * como tratado e, do mesmo modo acordar
     * a thread que colocou o pedido numa pool
     * de threads
     */
    public void checkRequest() {

        /* Marcamos o status como feito */
        this.status = FEITO;
        /* Quando o pedido for marcado como
        checked acordamos o processo que o originou.
        É de notar que a sinalização deve ocorrer
        dentro de um lock */
        this.l.lock();
        this.c.signal();
        this.l.unlock();
    }

    /**
     * Método que nos diz so o request ao qual é
     * enviado o método está tratado ou não
     * @return
     */
    public boolean isChecked(){

        return this.status;
    }

    @Override
    /**
     * Implementação do método toString para
     * objetos da classe Download.Request
     */
    public String toString() {

        return "String lida: " + this.message;
    }
}
