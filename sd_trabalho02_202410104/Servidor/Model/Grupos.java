package Model;
import java.io.Serializable;

public class Grupos implements Serializable {
      private String NomeGrupo; 
      private Usuario usuarioAdministrador; 
      private ListaDupla<Usuario> listaUsuarios;
      private ListaDupla<MensagemPendente> listaMensagensPendentes;
      private int quantidadeMembros = 0;
      private static final long serialVersionUID = 1L;
      private EstruturaControle estruturaControle;
      
      class MensagemPendente {
           private String mensagem;
           private int numeroVisualizacao = 0;
           public MensagemPendente(String mensagem) {
                 this.mensagem = mensagem;
           }
           public void atualizarMensagenpendente() {
                 numeroVisualizacao++;
           }
      }
      
      public Grupos(String NomeGrupo) {
            this.NomeGrupo = NomeGrupo;
            this.listaUsuarios = new ListaDupla<>();
      }

      public String obterNomeGrupo() {
           return NomeGrupo;
      }

      public ListaDupla<Usuario> obterListaUsuarios() {
           return listaUsuarios;
      }

      public Usuario obterProximoUsuarios() {
           return (listaUsuarios.getHead()).getValor();
      }
      
      public void inserirUsuario(Usuario usuario) {
         if(!procurar(usuario)) {
              listaUsuarios.inserirFim(usuario);
              quantidadeMembros++;
         }  
      }
     
      public String imprimirUsuarios() {
           String res = "";
           for(ListaDupla.Element ptr = listaUsuarios.getHead(); ptr != null; ptr = ptr.getNext()) {
                 res = res + " " + ((Usuario)ptr.getValor()).obterNome();
           } 
           return res;
      }

      private boolean procurar(Usuario usuario) {
          
           for(ListaDupla.Element ptr = listaUsuarios.getHead(); ptr != null; ptr = ptr.getNext()) {
                   if(((Usuario)ptr.getValor()).obterEnderecoIp().equals(usuario.obterEnderecoIp()) && ((Usuario)ptr.getValor()).obterNome().equals(usuario.obterNome())) {
                       return true;
                   }
           }          
           return false; 
     }
     
     
     public void registrarMensagemPendente(String mensagem) {
       //  listaMensagensPendentes.inserirFim(new MensagemPendente(mensagem));
     }
     
     public void remover(Usuario usuario) {
           listaUsuarios.remover(usuario);
           quantidadeMembros--;
     }
     
     public void atualizarMensagemPendentes() {
          for(ListaDupla.Element ptr = listaUsuarios.getHead(); ptr != null; ptr = ptr.getNext()) {
                 ((MensagemPendente)ptr.getValor()).atualizarMensagenpendente();
          }          
     }
     
      public Usuario obterUsuario(String nomeUsuario) {
           for(ListaDupla.Element ptr = listaUsuarios.getHead(); ptr != null; ptr = ptr.getNext()) {
                   String nome = ((Usuario)ptr.getValor()).obterNome();
                   if(nome.equals(nomeUsuario)) {
                       return (Usuario)ptr.getValor();
                   }
           }          
           return null; 
     }

     public int quantidadeMembros() {
         return quantidadeMembros;
     }
}
