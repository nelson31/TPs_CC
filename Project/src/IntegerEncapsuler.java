import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IntegerEncapsuler {

    private Integer i;

    private Lock l;

    public IntegerEncapsuler(Integer i){

        this.i = i;
        this.l = new ReentrantLock();
    }

    public Integer getB() {

        this.l.lock();

        Integer i = this.i;

        this.l.unlock();

        return i;
    }

    public void setB(Integer i) {

        this.l.lock();

        this.i = i;

        this.l.unlock();
    }
}