package Cliente;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author taina
 */
public class ChatMessage implements Serializable {
    
    private String nome;
    private String texto;
    private String reservado_nome;
    private Set<String> setOnlines = new HashSet<String>(); // Lista de usuarios online onde casda aplicação cliente vai receber
    private Action acao; // vai conter o enum porque se torna mais facil trasbvalhaer com enum do que com variaveis estaticas

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getReservado_nome() {
        return reservado_nome;
    }

    public void setReservado_nome(String reservado_nome) {
        this.reservado_nome = reservado_nome;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAcao() {
        return acao;
    }

    public void setAcao(Action acao) {
        this.acao = acao;
    }
    
    
    public enum Action{
        CONECTADO, DESCONECTADO,SEND_ONE, SEND_ALL, USUARIOS_ONLINE
    }
    // Cada mensagem que o cliente manda para o sevidor qual é a ação que ele quer executar 
    
}
