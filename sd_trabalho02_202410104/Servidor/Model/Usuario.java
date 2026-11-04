package Model;

import java.io.Serializable;

public class Usuario implements Serializable {
       private String nome;
       private String enderecoIp;
       private static final boolean bloqueado = false;
       private boolean estadoUsuario;
       private int portaAssociadaUDP;
       private int portaAssociadaTCP;
       private boolean admistrador;
       private static final long serialVersionUID = 1L;
       private ListaDupla<Usuario> listaUsuariosBloqueados;
       
       public Usuario(String nome, String enderecoIp) {
               this.nome = nome;
               this.enderecoIp = enderecoIp;
               this.estadoUsuario = !bloqueado; //na nossa politica o usuario comeca como debloqueado
               this.admistrador = false;
               this.listaUsuariosBloqueados = new ListaDupla<>();
      }   
      public void setPortaUDP(int portaAssociadaUDP) {
          this.portaAssociadaUDP = portaAssociadaUDP;
      }
      
      public void setEnderecoIP(String enderecoIP) {
          //this.enderecoIp = enderecoIP;
      }
      
      public void setPortaTCP(int portaAssociadaTCP) {
          this.portaAssociadaTCP = portaAssociadaTCP;
      }
      
      
       public String obterNome() {
               return nome;
       }
       
       
       public int ObterPortaUDP() {
               return portaAssociadaUDP;
       } 
       
       public int obterPortaTCP() {
             return portaAssociadaTCP;
       }
       
       public String obterEnderecoIp() {
               return enderecoIp;
       }
       
       public void bloquearUsuario(Usuario usuario) {
            listaUsuariosBloqueados.inserirFim(usuario);
       }
       
       public void desbloquearUsuario(Usuario usuario) {
            listaUsuariosBloqueados.remover(usuario);
       }
       
       public boolean verificarUsuarioBloqueado(Usuario usuario) {
            for(ListaDupla.Element ptr = listaUsuariosBloqueados.getHead(); ptr != null && usuario != null; ptr = ptr.getNext()) {
                  if(((Usuario)ptr.getValor()).obterNome().equals(usuario.obterNome())) return true;
            }
            return false;
       }
       
       public void estebelcerComoAdminstrador() {
             admistrador = true;
       }
       
       public boolean verificaarseEhAdminstrador() {
              return admistrador;
       }
       
       public boolean verificarEstaBloqueado() {
              return estadoUsuario; 
       }
       
       public void desbloquearUsuario() {
            estadoUsuario = !bloqueado;
       }

       
       public boolean verificarSeEhIgual(String nome) {
              return nome.equals(this.nome);
       }
       
}
