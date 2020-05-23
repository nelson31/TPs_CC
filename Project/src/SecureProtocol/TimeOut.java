package SecureProtocol;

public class TimeOut implements Runnable {

    private int milis;

    private BooleanEncapsuler timeoutreached;

    public TimeOut(int milis) {
        this.milis = milis;
        this.timeoutreached = timeoutreached;
    }

    public void run() {

        try {
            Thread.sleep(this.milis);
        }
        catch(InterruptedException exc){
            System.out.println(exc.getLocalizedMessage());
        }
    }
}