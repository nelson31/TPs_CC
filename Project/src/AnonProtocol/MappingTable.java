package AnonProtocol;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class MappingTable {

    /**
     * Número máximo de sessões em
     * simultâneo
     */
    private static final int max_simultaneous = 25;

    /**
     * Tabela que guarda os pacotes de
     * uma determinada sessão
     */
    private Map<Integer,SessionLine> table;

    /**
     * Construtor para objetos da classe
     * MappingTable
     */
    public MappingTable(){

        this.table = new HashMap<>();
        /* Criamos tantas linhas de entrada como
        o nº máximo de sessões em simultâneo */
        for(int i=0; i<max_simultaneous; i++)
            this.table.put(i,new SessionLine(i));
    }

    /**
     * Método que permite adicionar um
     * pacote anon à lista de chegadas. É
     * de notar que não é necessário exclusão mútua
     * nesta classe visto que table nunca é alterada,
     * mas apenas o seu conteudo (SessionLine)
     */
    public void addPacket(AnonPacket ap, InetAddress owner){

        /* Vamos buscar a linha respetiva e adicionamos
        o pacote */
        this.table.get(ap.getSession()).addPacket(ap, owner);
    }

    /**
     * Método que permite obter um pacote,
     * fornecendo o seu id de sessão
     * @param session
     */
    public AnonPacket getPacket(int session){

        return this.table.get(session).getPacket();
    }
}
