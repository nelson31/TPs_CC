import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer implements IBoundedBuffer{

    private int nMaxRequests;
    private int nRequests;
    private Request[] requests;

    private Lock l = new ReentrantLock();
    private Condition writers = l.newCondition();
    private Condition readers = l.newCondition();

    /**
     * Construtor para objetos da
     * classe Download.BoundedBuffer
     *
     * @param nMaxRequests
     */
    public BoundedBuffer(int nMaxRequests) {

        this.nMaxRequests = nMaxRequests;
        this.requests = new Request[nMaxRequests];
        this.nRequests = 0;
    }

    /**
     * Método que permite obter o lock de um
     * objeto da classe Download.BoundedBuffer
     */
    private void lock() {

        this.l.lock();
    }

    /**
     * Método que permite ceder um lock de
     * um objeto da classe pooldownload
     */
    private void unlock() {

        this.l.unlock();
    }

    public int getnMaxRequests() {

        return this.nMaxRequests;
    }

    /**
     * Método que permite adicionar um pedido
     * de Download para ser realizado por
     * um downloader
     *
     * @param newRequest
     */
    public void putRequest(Request newRequest){

        try {
            /* Obtemos o lock da pool */
            this.lock();
            /* Enquanto estiver cheio, esperamos */
            while (this.nRequests >= this.nMaxRequests)
                this.writers.await();
            /* Quando obtivermos o lock aumentamos o
            numero de pedidos em simultâneo
            e adicionamos o novo pedido à lista */
            this.requests[this.nRequests++] = newRequest;
            /* Notificamos leitores de que há um novo pedido */
            this.readers.signal();

        }
        catch(InterruptedException exp){
            System.out.println(exp.getMessage());
        }
        finally {
            /* Cedemos o lock do objeto */
            this.unlock();
        }
    }

    /**
     * Método que permite tratar
     * um pedido de Download
     */
    public Request getRequest() {

        Request r = null;
        try {
            /* Obtemos o lock da pool */
            this.lock();
            /* Enquanto não houver pedidos esperamos */
            while (this.nRequests == 0)
                this.readers.await();
            /* Quando obtivermos o lock diminuimos o numero
            de downloads em simultaneo e obtemos o proximo pedido */
            r = this.requests[--this.nRequests];
            /* Notificamos escritores de um
            novo espaço para colocar novo pedido */
            this.writers.signal();

        } catch (InterruptedException exp) {
            System.out.println(exp.getMessage());
        } finally {
            /* Cedemos o lock do objeto */
            this.unlock();
        }
        return r;
    }
}
