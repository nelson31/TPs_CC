package Table;

import AnonProto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Classe que representa um mapeamento de
 * endereço IP destino para pacotes a serem
 * enviados para esse mesmo destino
 */
public class MappingTable {

    /**
     * Variável para garantir exclusão
     * mútua no acesso à Table
     */
    private Lock l;

    /**
     * Tabela que guarda o conjunto de pacotes a
     * serem enviados para cada um dos destinos
     */
    private Map<Integer, TableLine> table;

    /**
     * Construtor para objetos da
     * classe Table.MappingTable
     */
    public MappingTable(){

        this.table = new HashMap<>();
    }

    /**
     * Método que regista a existência
     * de uma nova session
     * @param session
     */
    public void newPacket(int session, int sequence){

        if(!this.table.containsKey(session)) {
            TableLine line = new TableLine(session);
            line.createLine(new PacketIdentifier(session, sequence));
            /* Obtemos o lock para escrever na tabela */
            this.l.lock();
            try{
                this.table.put(session, line);
            }
            finally {
                /* Cedemos o lock */
                this.l.unlock();
            }
        }
        else{
            TableLine mp;
            /* Obtemos o lock para ler da tabela */
            this.l.lock();
            try{
                mp = this.table.get(session);
            }
            finally {
                /* Cedemos o lock após ler a linhas
                correspondente à sessão respetiva */
                this.l.unlock();
            }
            /* Escrevemos o pacote */
            if(mp != null)
                mp.createLine(new PacketIdentifier(session,sequence));
        }
    }

    /**
     * Método que permte adicionar um novo pacote
     * a ser enviado para um dado destino
     * @param session
     * @param packet
     */
    public void addPacket(int session, AnonPacket packet){

        TableLine mp = null;
        /* Obtemos o lock para escrever
        na tabela */
        this.l.lock();
        try{
            mp = this.table.get(session);
        }
        finally {
            this.l.unlock();
        }
        /* Adicionamos o respetivo pacote */
        if(mp != null)
            mp.getIdentifier(packet.getSequence()).put(packet);
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

        TableLine tl = null;
        /* Obtemos o lock para aceder
        à linha respetiva */
        this.l.lock();
        try{
            tl = this.table.get(session);
        }
        finally {
            /* Cedemos o lock apos aceder à linha */
            this.l.unlock();
        }
        return tl.getIdentifier(sequence).get();
    }
}
