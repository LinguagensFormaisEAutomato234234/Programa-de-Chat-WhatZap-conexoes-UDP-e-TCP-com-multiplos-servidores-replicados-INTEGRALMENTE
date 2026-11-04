package Model;

import java.io.Serializable;

public class EstruturaControle implements Serializable {
      private ListaDupla<Usuario> listaDinamica;
      private static final long serialVersionUID = 1L;
      public EstruturaControle() {
          this.listaDinamica = new ListaDupla();
      }
      
      public void registrarNome(String nome, String enderecoIP, int portaAssociada) {
            Usuario usuario = procurarUsuario(nome);
            if(usuario == null) {
                 usuario = new Usuario(nome, enderecoIP);
                 usuario.setPortaUDP(portaAssociada);
                 listaDinamica.inserirFim(usuario);
            }
      }
      
      public boolean verificarRedundanciaUsuario(String nome, String enderecoIP) {
         /**  System.out.println("O que esta nulo eh: " + listaDinamica);
           System.out.println("O que esta nulo eh: " + listaDinamica.getHead());**/
           for(ListaDupla.Element ptr = listaDinamica.getHead(); ptr != null; ptr = ptr.getNext()) {
             //    System.out.println("Estrutura de controle: " + ((Usuario)ptr.getValor()).nome + "  " + ((Usuario)ptr.getValor()).enderecoIP + " | " + nome + " " + enderecoIP);
            /** System.out.println("O que esta nulo eh: " + (Usuario)ptr.getValor());
             System.out.println("O que esta nulo eh: " + ((Usuario)ptr.getValor()).obterNome());**/
                 if((((Usuario)ptr.getValor()).obterNome()) != null && (((Usuario)ptr.getValor()).obterNome()).equals(nome) && (((Usuario)ptr.getValor()).obterEnderecoIp()).equals(enderecoIP)) {
                        return false; //a semanica pode parecer estranha, porem a ideia eh que esse usuario ja logou antes, portanto nao eh um novo usuario solicitando um servico, em outras aplicacao, o endereco de emaail ou numero de telefone seria mais adequado.
                 } else if((((Usuario)ptr.getValor()).obterNome()).equals(nome) && !(((Usuario)ptr.getValor()).obterEnderecoIp()).equals(enderecoIP)) {
                       return true; //temos redudancia de usuario
                 }
           }
           return false;
      }
      
      public Usuario procurarUsuario(String nome) {
          for(ListaDupla.Element ptr = listaDinamica.getHead(); ptr != null; ptr = ptr.getNext()) {
               //  System.out.println(((Usuario)ptr.getValor()).obterNome() + " | " + nome);
                 if((((Usuario)ptr.getValor()).obterNome()).equals(nome)) {
                        return ((Usuario)ptr.getValor());
                 }
           }
           return null;
      }
     public void setListaUsuario(ListaDupla<Usuario> listaDinamica) {
         this.listaDinamica = listaDinamica;
     }
     public ListaDupla<Usuario> obterListaUsuario() {
          return listaDinamica;
     }
}
