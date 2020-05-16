import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionGetter {

    private static final int MAX_SIZE = 50;

    /**
     * Variável que permite garantir exclusão
     * mútua para obter o número de sessão
     */
    private Lock l;

    /**
     * Variável Condition
     */
    private Condition c;

    /**
     * Variável que nos diz se o respetivo
     * indice está atribuido com ID de sessão
     */
    private boolean[] isIndexAtributed;

    /**
     * Variável que guarda o número de ids de sessão
     * atribuidos num determinado momento
     */
    private int num;

    /**
     * Construtor para objetos da
     * classe SessionGetter
     */
    public SessionGetter(){

        this.l = new ReentrantLock();
        this.c = l.newCondition();
        this.isIndexAtributed = new boolean[MAX_SIZE];
        for(int i=0; i<MAX_SIZE; i++)
            this.isIndexAtributed[i] = false;
        this.num = 0;
    }

    /**
     * Método que permite obter o primeiro
     * id de sessão livre
     * @return
     */
    public int getID(){

        int ret = -1;
        /* Obtemos o lock */
        this.l.lock();

        try {
            /* Esperamos enquanto não houver
            id de sessão disponível */
            while (this.num == MAX_SIZE)
                this.c.await();

            for(int i=0; i<MAX_SIZE && ret==-1; i++)
                if(!isIndexAtributed[i]) ret = i;

        }
        catch(InterruptedException exc){
            System.out.println("Erro ao obter id de sessão");
        }
        return ret;
    }

    /**
     * Método que permite ceder o valor
     * do id de sessão usado
     */
    public void cedeID(int ID){

        /* Obtemos o lock para aceder aos dados */
        this.l.lock();

        /* Sinalizamos a entrega do id */
        this.isIndexAtributed[ID] = false;
        this.c.signal();

        /* Cedemos o lock */
        this.l.unlock();
    }
}
