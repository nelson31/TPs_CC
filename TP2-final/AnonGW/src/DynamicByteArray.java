public class DynamicByteArray {

    private static final int initial_size = 4096;

    /**
     * Variável que guarda os dados
     */
    private byte[] data;

    /**
     * Variável que guarda o tamanho
     * atual do array
     */
    private int size;

    /**
     * Variável que guarda o numero de
     * posições ocupadas do array
     */
    private int ocup;

    /**
     * Método que permite criar um array
     * dinâmico de bytes
     */
    public DynamicByteArray(){

        this.data = new byte[initial_size];
        this.size = initial_size;
        this.ocup = 0;
    }

    /**
     * Método que permite adicionar conteudo
     * adicional no array dinâmico
     * @param moredata
     */
    public void append(byte[] moredata){

        /* Atualizamos o número de posições
        do array que ficariam ocupadas */
        this.ocup += moredata.length;

        /* Se não houver espaço suficiente teremos que
        gerir toda a política de realocação de memória */
        if(this.ocup>this.size){
            /* Atualizamos o novo tamanho
            do array dinâmico */
            this.size = this.ocup;

            byte[] novoArray = new byte[this.ocup];
            /* Copiamos o conteudo do antigo array
            para o novo */
            for(int i=0; i<this.ocup-moredata.length; i++){
                novoArray[i] = this.data[i];
            }
            /* Copiamos o conteudo adicional
            para o novo array */
            for(int i=this.ocup-moredata.length, k=0; i<novoArray.length; i++, k++){
                novoArray[i] = moredata[k];
            }
        }
        /* Caso exista espaço suficiente colocamos lá o conteúdo do moredata */
        else{
            for(int i=this.ocup-moredata.length, k=0; i<this.size; i++, k++){
                this.data[i] = moredata[k];
            }
        }
    }
}
