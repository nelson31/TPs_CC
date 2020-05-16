import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TimeoutSignal implements Runnable{

    /**
     * Tempo máximo de espera para a receção de
     * um acknowlegment relativo a um datagrama
     * UDP enviado
     */
    public static final int TIMEOUT = 250;

    /**
     * Variável para obter
     * a condition
     */
    private Lock l;

    /**
     * Variável para permitir acordar
     * uma thread após X ms
     */
    private Condition c;

    /**
     * Variável para sinalizar writer
     * que o timeout foi atingido
     */
    private Boolean timeoutReached;

    /**
     * Construtor para objetos da classe TimeoutSignal
     * @param l
     */
    public TimeoutSignal(Lock l, Condition c, Boolean timeoutReached){

        this.l = l;
        this.c = c;
        this.timeoutReached = timeoutReached;
    }

    /**
     * Implementação do método run para
     * objetos da classe TimeoutSignal
     */
    public void run() {

        try {
            /* Esperamos o timeout */
            Thread.sleep(TIMEOUT);
            /* Obtemos o lock */
            this.l.lock();
            /* Alteramos o valor do boolean para true,
            sinalizando que o timeout foi atingido */
            this.timeoutReached = true;
            /* Após esperar o time out acordamos
            que está suspenso - no máximo estará
            uma única thread que corresponderá
            ao writer */
            this.c.signal();
        }
        catch(InterruptedException exc){
            System.out.println("Erro no timeoutSignal - " + exc.getMessage());
        }
        finally {
            /* No final cedemos o lock */
            this.l.unlock();
        }
    }
}
