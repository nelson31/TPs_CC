package AnonProtocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionGetter {

    public static final int MAX_SESSIONS_SIMULT = 25;

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
        this.isIndexAtributed = new boolean[MAX_SESSIONS_SIMULT];
        for(int i=0; i<MAX_SESSIONS_SIMULT; i++)
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
            while (this.num == MAX_SESSIONS_SIMULT)
                this.c.await();

            for(int i=0; i<MAX_SESSIONS_SIMULT && ret==-1; i++)
                if(!isIndexAtributed[i]){
                    ret = i;
                    /* Colocamos o indice como estando ocupado */
                    this.isIndexAtributed[i] = true;
                }

            /* Atribuimos mais um id, pelo que
            incrementamos o número de id's atribuidos */
            this.num++;

        }
        catch(InterruptedException exc){
            System.out.println("Erro ao obter id de sessão");
        }
        finally {
            this.l.unlock();
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
        if(this.isIndexAtributed[ID]) {
            this.isIndexAtributed[ID] = false;
            this.num--;
            this.c.signal();
        }

        /* Cedemos o lock */
        this.l.unlock();
    }
}
