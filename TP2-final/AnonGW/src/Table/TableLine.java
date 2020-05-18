package Table;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class TableLine implements ITableLine{

    /**
     * Variável que identifica a sequencia
     * do pacote a ser lido
     */
    private int session;

    /**
     * Variável que representa uma
     * linha da Table
     */
    private Map<Integer,PacketIdentifier> line;

    /**
     * Variável que garante exclusão mútua
     */
    private Lock l;

    /**
     * Construtor para objetos da classe TableLine
     */
    public TableLine(int session){

        this.session = session;
        this.line = new HashMap<>();
    }

    /**
     * Método que permite adicionar um novo
     * objeto para receber um pacote
     * @param pi
     */
    public void createLine(PacketIdentifier pi){

        /* Obtemos o lock para escrever */
        this.l.lock();
        try{
            /* Adicionamos a linha se ainda não existir */
            if(!this.line.containsKey(pi.getSequence()))
                /* Colocamos o novo PacketIdentifier */
                this.line.put(pi.getSequence(), pi);
        }
        finally {
            /* Cedemos o lock */
            this.l.unlock();
        }
    }

    /**
     * Método que permite apagar uma linha
     * @param session
     */
    public void deleteLine(int session){

        /* Obtemos o lock para eliminar uma linha */
        this.l.lock();
        try{
            /* */
            this.line.remove(session);
        }
        finally {
            this.l.unlock();
        }
    }

    /**
     * Método que permite adicionar um pacote a um
     * determinado packetidentifier na linha
     * @param sequence
     */
    public PacketIdentifier getIdentifier(int sequence){

        PacketIdentifier p = null;
        this.l.lock();
        try{
            p = this.line.get(sequence);
        }
        finally {
            this.l.unlock();
        }
        return p;
    }

    /**
     * Método que permite eliminar todos
     * os pacotes de uma sessão
     */
    public void clear(){

        this.l.lock();

        this.line.clear();

        this.l.unlock();
    }
}
