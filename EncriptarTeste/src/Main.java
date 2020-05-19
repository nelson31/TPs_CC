import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class Main {

    /**
     * Método por onde se da inicio a execucao deste teste
     * @param args
     */
    public static void main(String[] args) {

        Encriptacao enc = new Encriptacao();

        String exemplo = "Trabalho Prático 2 de CC 2020!!!!!!!";

        try{
            String aux = "";
            // Input exemplo
            BufferedReader br2 = new BufferedReader(new FileReader("exemplo.txt"));

            StringBuilder sb = new StringBuilder();
            while((aux = br2.readLine())!=null){
                sb.append(aux);
            }
            exemplo = sb.toString();

            br2.close();
        }
        catch (IOException e){
            System.out.println("ERRO DE LEITURA");
        }


        try {

            // DEVIA DE SER PASSADO COMO PARAMETRO, PARA SER MAIS SEGURO( foi o stor que disse)
            BufferedReader br = new BufferedReader(new FileReader("configs.txt"));

            String password = br.readLine();

            //System.out.println(new String(enc.rotate(exemplo.getBytes(),20,exemplo.getBytes().length),"UTF-8"));
            //System.out.println(new String(enc.rotate(enc.rotate(exemplo.getBytes(),20,exemplo.getBytes().length),-20,exemplo.getBytes().length),"UTF-8"));

            int K1 = Integer.parseInt(br.readLine());
            System.out.println("Key1: " + K1);
            int K2 = Integer.parseInt(br.readLine());
            System.out.println("Key2: " + K2);
            byte[] exemploEnc = enc.encriptar(exemplo.getBytes(),password,K1,K2);
            String decode1 = new String(exemploEnc, "UTF-8");
            System.out.println(decode1);
            byte[] exemploDes = enc.desencriptar(exemploEnc,password,K1,K2);
            String decode2 = new String(exemploDes, "UTF-8");
            System.out.println(decode2);
        } catch (IOException e){
            System.out.println("ERRO DE LEITURA");
        }
    }
}
