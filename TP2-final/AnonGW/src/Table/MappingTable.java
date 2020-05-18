package Table;

import AnonProto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Classe que representa um mapeamento de
 * id de sessão para a lista de pacotes
 * pertencentes a uma determinada sessão
 */
public class MappingTable {

    public static final int MAX_SESSIONS_SIMULT = 50;

    /**
     * Variável para garantir exclusão
     * mútua no acesso à Table
     */
    private Lock l;

    /**
     * Tabela que guarda o conjunto de pacotes a
     * serem enviados para cada um dos destinos
     */
    private Map<Integer, SessionLine> table;

    /**
     * Construtor para objetos da
     * classe Table.MappingTable
     */
    public MappingTable(){

        this.table = new HashMap<>();
        /* Inicializamos a estrutura com o número
        de linhas igual ao número máximo de sessões
        em simultâneo */
        for(int i=0; i<MAX_SESSIONS_SIMULT; i++){
            this.table.put(i,new SessionLine(i));
        }
    }

    /**
     * Método que permte adicionar um novo pacote
     * a ser enviado para um dado destino
     * @param session
     * @param packet
     */
    public void addPacket(int session, AnonPacket packet){

        SessionLine mp = null;
        /* Obtemos o lock para escrever
        na tabela */
        this.l.lock();

        /* Obtemos a linha respetiva à sessão */
        mp = this.table.get(session);

        /* Cedemos o lock */
        this.l.unlock();

        /* Adicionamos o respetivo pacote */
        mp.addPacket(packet);
    }

    /**
     * Método que dado o número de sessão e o número de
     * sequencia do packet pretendido o retorna. O método
     * bloqueia até que o pacote chegue
     * @param session
     * @return
     */
    public AnonPacket getPacket(int session, int sequence)
            throws InterruptedException{

        SessionLine sl = null;
        /* Obtemos o lock para aceder
        à linha respetiva */
        this.l.lock();

        /* Vamos buscar a linha referente
        à sessão fornecida */
        sl = this.table.get(session);

        /* Cedemos o lock apos aceder à linha */
        this.l.unlock();

        return sl.getPacket(sequence);
    }

    /**
     * Método que permite eliminar todos os pacotes
     * de uma linha correspondente a uma sessão
     * @param session
     */
    public void clearSession(int session){

        this.l.lock();

        this.table.get(session).clearLine();

        this.l.unlock();
    }
}
