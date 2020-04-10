import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer implements IBoundedBuffer{

    private Request[] list;
    private int size;
    private int nResquests;

    private Lock l;
    private Condition writers;
    private Condition readers;

    /**
     * Construtor para um objeto da
     * classe BoundedBufferQueue
     * @param size
     */
    public BoundedBuffer(int size) {

        this.size = size;
        this.list = new Request[size];
        this.l = new ReentrantLock();
        this.nResquests = 0;
        writers = l.newCondition();
        readers = l.newCondition();
    }

    /**
     * Método que permite adicionar um pedido
     * de Download para ser realizado por
     * um downloader
     *
     * @param r
     */
    public void putRequest(Request r) {

        try {
            this.l.lock();

            while (nResquests == size)
                this.writers.await();

            this.list[this.nResquests++] = r;

            this.readers.signal();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            this.l.unlock();
        }
    }

    /**
     * Método que permite tratar
     * um pedido de Download
     */
    public Request getRequest() {

        Request r = null;
        try {
            this.l.lock();

            while(this.nResquests == 0)
                readers.await();

            r = this.list[0];

            for(int i=0; i<nResquests-1; i++)
                list[i] = list[i+1];

            this.nResquests--;

            this.writers.signal();

        }
        catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
        finally {
            this.l.unlock();
        }
        return r;
    }
}
