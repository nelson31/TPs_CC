package Components;

import AnonProtocol.SessionGetter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ForeignSessions {

    /**
     * Variável que guarda os IDs do
     * todas as sessões que pertencem
     * a outro anonGW
     */
    private Map<SessionData, Integer> association;

    /**
     * Id's de sessão de outro owner que espera
     * atribuição de um id de sessão local
     */
    private List<SessionData> waiting;

    /**
     * Variável que garante exclusão
     * mútua no acesso à estrutura de dados
     */
    private Lock l;

    /**
     * Variável que permite notificar quando
     * aparece uma nova sessão externa ao anonGW
     */
    private Condition c;

    ///////////////////////////////ACESSO AOS IDS DE SESSÃO DISPONÍVEIS/////////////////////////////////

    /**
     * Variável que nos permite aceder ao id's de
     * sessão para atribuir um novo id a uma nova
     * sessão externa
     */
    private SessionGetter sessionGetter;

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construtor para objetos da classe
     * ForeignSessions
     */
    public ForeignSessions(SessionGetter sessionGetter){

        this.association = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = this.l.newCondition();
        this.sessionGetter = sessionGetter;
    }

    /**
     * Método que permite a uma thread ficar
     * responsável por uma sessão externa
     * @return
     */
    public int accept(SessionData sessionData){

        int id = -1;
        SessionData sdata = null;
        this.l.lock();

        try{
            /* Enquanto não existir nenhuma sessão
            externa à espera aguardamos */
            while(this.waiting.size() == 0)
                this.c.await();

            sdata = this.waiting.get(0);
            this.waiting.remove(0);
            /* Atribuimos os valores à variavel
            session data de saida */
            sessionData.setId(sdata.getId());
            sessionData.setOwnerIP(sdata.getOwnerIP());
            sessionData.setTargetIp(sdata.getTargetIp());
            sessionData.setTargetPort(sdata.getTargetPort());
            /* Retornamos o valor do id no anon local */
            id = this.association.get(sessionData);

        }
        catch(InterruptedException exc){
            System.out.println("Erro a obter uma sessão externa - " + exc.getMessage());
        }
        finally {
            this.l.unlock();
        }
        return id;
    }

    /**
     * Método que permite adicionar uma nova sessão
     * externa que se encontra à espera de uma
     * atribuição de id local
     * @return
     */
    public void addForeignSession(int id, InetAddress owner, InetAddress targetIp, int targetPort){

        SessionData data = new SessionData(id,owner, targetIp, targetPort);

        /* Obtemos o lock */
        this.l.lock();

        /* Adicionamos a informação acerca de uma sessão
        para ser tratada por um worker se ela ainda não
        existir na lista de espera */
        if(!this.waiting.contains(data))
            this.waiting.add(data);

        /* Atribuimos logo um id à sessão no anon local */
        this.association.put(data,this.sessionGetter.getID());

        /* Sinalizamos a thread que se encontra à
        espera de novas sessões provenientes de
        outro anonGW */
        this.c.signal();

        /* Cedemos o lock */
        this.l.unlock();
    }

    /**
     * Método que nos diz se existe alguma informação
     * acerca da sessão em questão
     * @param id
     * @param owner
     * @return
     */
    public boolean contains(int id, InetAddress owner){

        SessionData data = new SessionData(id,owner,null,0);
        boolean ret = false;

        /* Obtemos o lock */
        this.l.lock();

        /* Se existir informação retornamos true */
        ret = this.association.containsKey(data);

        /* Cedemos o lock */
        this.l.unlock();

        return ret;
    }

    /**
     * Método que permite eliminar uma associação de
     * sessão externa, id de sessão no AnonGw local
     */
    public void removeAssociation(int id, InetAddress owner){

        SessionData data = new SessionData(id,owner,null,0);

        /* Obtemos o lock para a
        estrutura de dados */
        this.l.lock();

        /* Cedemos também o id que tinhamos
        reservado para a sessão */
        this.sessionGetter.cedeID(this.association.get(data));

        /* Removemos uma linha da tabela */
        this.association.remove(data);

        /* Cedemos o lock */
        this.l.unlock();
    }

    /**
     * Método que dada a informação acerca de uma
     * sessão retorna o seu id no owner externo
     */
    public int getLocalSession(int id, InetAddress owner){

        SessionData data = new SessionData(id,owner,null,0);
        int ret = -1;
        this.l.lock();

        /* Vamos buscar a informação
        acerca da sessão */
        ret = this.association.get(data);

        this.l.unlock();

        return ret;
    }
}
