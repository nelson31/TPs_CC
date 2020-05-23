package Components;

/**
 * Classe Encriptacao que serve para encriptar uma certa sequencia de dados,
 * a partir do uso das funcoes encriptar e desencriptar
 * Para isso são necessarios os dados, a password ou segredo pelo que é a unica
 * forma de se conseguir encriptar e desencriptar sem problemas, pelo que é por isso
 * que é um tipo de encriptacao de chave simentrica, e ainda são necessárias duas chaves
 * que vao ser importantes para a altura de se rodarem os dados
 * @author Grupo 6 PL4
 * @version 1.0
 */

public class Encriptacao {

    /* Variavel que sinaliza que se pretende encriptar uma dada sequencia de dados */
    private static final int ENCRIPT = 1000;
    /* Variavel que sinaliza que se pretende encriptar uma dada sequencia de dados */
    private static final int DECRIPT = 1001;

    /**
     * Método que repete o padrao da password até ela ter o tamanho t
     * @param password
     * @param t
     * @return
     */
    private static byte[] repeat(String password, int t){
        int tpw = password.getBytes().length;
        int tcurr = 0;
        StringBuilder sb = new StringBuilder();
        while(t>tcurr){
            tcurr += tpw;
            if(t < tpw || tcurr > t) {
                int tam = tcurr - t;
                sb.append(password, 0, tpw - tam);
            } else {
                sb.append(password);
            }
        }
        return sb.toString().getBytes();
    }

    /**
     * Método que realiza o deslocamento de bytes para a esquerda
     * @param b
     * @param tamanho
     * @return
     */
    private static void rotateEsq(byte[] b, int tamanho){
        byte aux = b[0];

        for(int i=0;i<tamanho-1;i++){
            b[i] = b[i+1];
        }

        b[tamanho-1] = aux;
    }

    /**
     * Método que realiza o deslocamento de bytes para a direita
     * @param b
     * @param tamanho
     * @return
     */
    private static void rotateDir(byte[] b, int tamanho){
        byte aux = b[tamanho-1];

        for(int i=tamanho-1;i>0;i--){
            b[i] = b[i-1];
        }

        b[0] = aux;
    }

    /**
     * Método que serve para realizar as operaçoes de rotação dos dados presentes
     * na variavel pad!! Caso a key seja positiva faz um deslocamento de bytes
     * presentes no array para a direita, se a Key for negativa faz
     * o deslocamento para a esquerda
     * @param pad
     * @param Key
     * @param tamanho
     * @return
     */
    private static byte[] rotate(byte[] pad, int Key, int tamanho){

        if(Key>=0) {
            for(int i=0;i<Key;i++) {
                rotateDir(pad, tamanho);
            }
        } else {
            for(int i=0;i<-Key;i++) {
                rotateEsq(pad, tamanho);
            }
        }
        return pad;
    }

    /**
     * Método que serve para efetuar o ou exclusivo(XOR) dados dois arrays de
     * bytes de igual tamanho
     * @param pad
     * @param dados
     * @param tamanho
     * @return
     */
    private static byte[] ouExclusivo(byte[] pad, byte[] dados, int tamanho){

        byte[] newbyteArr = new byte[tamanho];
        for(int i=0;i<tamanho;i++){
            newbyteArr[i] = (byte) ((int)pad[i] ^ (int)dados[i]);
        }
        return newbyteArr;
    }

    /**
     * Metodo que tratav de executar o algoritmo de encriptacao e desencriptacao
     * @param dados
     * @param password
     * @param K1
     * @param K2
     * @param direcao
     * @return
     */
    private static byte[] f(byte[] dados, String password, int K1, int K2, int direcao){
        int t = dados.length;
        byte[] pad = repeat(password,t);
        pad = rotate(pad,K1,t);
        if(direcao == ENCRIPT)
            dados = rotate(dados,K2,t);
        else dados = rotate(dados,-(K1+K2),t);
        byte[] r = ouExclusivo(pad,dados,t);
        if(direcao == ENCRIPT)
            r = rotate(r,K1+K2,t);
        else r = rotate(r,-K2,t);
        return r;
    }

    /**
     * Método que serve para encriptar os dados recebidos como parametro
     * @param dados
     * @param password
     * @param KEY1
     * @param KEY2
     * @return
     */
    public static byte[] encriptar(byte[] dados, String password, int KEY1, int KEY2){
        return f(dados,password,KEY1,KEY2,ENCRIPT);
    }

    /**
     * Método que serve para desencriptar os dados recebidos como parametro
     * @param dadosEncriptados
     * @param password
     * @param KEY1
     * @param KEY2
     * @return
     */
    public static byte[] desencriptar(byte[] dadosEncriptados, String password, int KEY1, int KEY2){
        return f(dadosEncriptados,password,KEY1,KEY2,DECRIPT);
    }
}
