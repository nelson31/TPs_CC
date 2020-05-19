package AnonProtocol;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IntegerEncapsuler {

    private Integer i;

    private Lock l;

    public IntegerEncapsuler(int i){

        this.i=i;
        this.l = new ReentrantLock();
    }

    public Integer getI(){

        this.l.lock();

        int ret = this.i;

        this.l.unlock();

        return ret;
    }

    public void setI(Integer i) {

        this.l.lock();

        this.i = i;

        this.l.unlock();
    }

    public boolean equals(Object o){

        if(o == null) return false;

        if(this.getClass() != o.getClass()) return false;

        IntegerEncapsuler ie = (IntegerEncapsuler)o;

        return this.i == ie.i;
    }
}
