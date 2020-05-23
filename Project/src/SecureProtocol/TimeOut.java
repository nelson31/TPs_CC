package SecureProtocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TimeOut implements Runnable {

    private Lock l;

    private Condition c;

    private int milis;

    private BooleanEncapsuler timeoutreached;

    public TimeOut(int milis, Lock l, Condition c, BooleanEncapsuler timeoutreached) {

        this.l = l;
        this.c = c;
        this.milis = milis;
        this.timeoutreached = timeoutreached;
    }

    public void run() {

        this.l.lock();

        /* Esperamos o valor do time out */

        try {
            Thread.sleep(this.milis);

            this.timeoutreached.setB(true);
            /* Quando o tempo do timeout
            passar acordamos a thread */
            this.c.signal();
        }
        catch(InterruptedException exc){
            System.out.println(exc.getLocalizedMessage());
        }
        finally {
            this.l.unlock();
        }
    }
}