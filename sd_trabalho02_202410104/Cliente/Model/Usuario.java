package Model;
import Controller.Registro;
import Controller.VisualizadorMensagem;
import Controller.HitoricoGrupo;
import Protocol.APDU;

public class Usuario {
       private String nome;
       private String enderecoIp;
       private static final boolean bloqueado = false;
       private boolean estadoUsuario;
       private HitoricoGrupo historicoConversasPrivadas;
       private ListaDupla<APDU> listaMensagensPendentesLeitura;
       public Usuario(String nome, String enderecoIp) {
               this.nome = nome;
               this.enderecoIp = enderecoIp;
               this.estadoUsuario = !bloqueado; //na nossa politica o usuario comeca como debloqueado
      }   
       public String obterNome() {
               return nome;
       }
       public String obterEnderecoIp() {
               return enderecoIp;
       }
       
       
       public boolean verificarSeEhIgual(String nome, String enderecoIp) {
              return nome.equals(this.nome) && enderecoIp.equals(this.enderecoIp);
       }
       
       public void registrarMensagem(String mensagem) {
              historicoConversasPrivadas.adicionarTexto("Voce: " + mensagem);
       }
       
}
