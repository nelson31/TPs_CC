import java.util.ArrayList;
import java.util.List;

public class CurrentSequence {

    /**
     * Variável que guarda uma lista
     * com os ids de sessão em vigor
     */
    private List<Integer> session;

    /**
     * Variável que guarda uma lista
     * com os valores de sequence em
     * vigor para cada uma das sessões
     */
    private List<Integer> sequence;

    /**
     * Construtor para objetos da
     * classe CurrentSequence
     */
    public CurrentSequence(){

        this.session = new ArrayList<>();
        this.sequence = new ArrayList<>();
    }


    public boolean contains(int session){

        return this.session.contains(session);
    }

    /**
     * Variável que permite adicionar
     * uma linha à tabela
     * @param session
     * @param sequence
     */
    public void put(int session, int sequence){

        this.session.add(session);
        this.sequence.add(sequence);
    }

    /**
     * Método que permite obter o próximo
     * id de sequencia e incrementar o valor
     * para o próximo pacote
     * @param session
     */
    public int getAndIncrement(int session){

        int index = this.session.indexOf(session);
        int sequence = this.sequence.get(index);
        /* Removemos o valor atual da lista de sequencia */
        int r = this.sequence.remove(index);

        this.sequence.add(index,++sequence);
        return sequence-1;
    }

    /**
     * Método que permite eliminar uma
     * linha da table. Só faz sentido
     * usar quando a sessão terminar
     * @param session
     */
    public void remove(int session){

        int index = this.session.indexOf(session);
        this.session.remove(session);
        this.sequence.remove(index);
    }
}
