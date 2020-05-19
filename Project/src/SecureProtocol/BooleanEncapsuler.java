package SecureProtocol;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BooleanEncapsuler {

    private Boolean b;

    private Lock l;

    public BooleanEncapsuler(Boolean b){

        this.b = b;
        this.l = new ReentrantLock();
    }

    public Boolean getB() {

        this.l.lock();

        boolean b = this.b;

        this.l.unlock();

        return b;
    }

    public void setB(Boolean b) {

        this.l.lock();

        this.b = b;

        this.l.unlock();
    }
}