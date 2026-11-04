package Model;
import Protocol.APDU;
import java.io.Serializable;
import java.util.Hashtable;

public class AdministrarMensagensPendentes implements Serializable {
       class GrupoUsuarioMensagemPendente implements Serializable {
             private ListaDupla<Usuario> listaUsuarios;
             private int confirmacaoMensagens = 0;
             private String mensagem;
             private static final long serialVersionUID = 1L;
             public GrupoUsuarioMensagemPendente(ListaDupla<Usuario> listaUsuarios) {
                  this.listaUsuarios = listaUsuarios;
             } 
             
             public GrupoUsuarioMensagemPendente(String mensagem, ListaDupla<Usuario> listaUsuarios) {
                  this.listaUsuarios = listaUsuarios;
                  this.mensagem = mensagem;
             }
   
             public void atualizarConfirmacao() {
                    confirmacaoMensagens++;
             }
 
             public boolean foiConfirmada() {
                    return (confirmacaoMensagens+1) >= listaUsuarios.getComprimentoLista();
             }
             
             public String obterMensagem() {
                    return mensagem + " numero de confirmacao " + confirmacaoMensagens;
             }

       }         
       private Hashtable<String, GrupoUsuarioMensagemPendente> mensagemPendentesEnvio;
       private String lista = "";
       private Hashtable<String, GrupoUsuarioMensagemPendente> mensagemPendentesLeitura;
       private static final long serialVersionUID = 1L;
       
       public AdministrarMensagensPendentes() {
              this.mensagemPendentesEnvio = new Hashtable<String, GrupoUsuarioMensagemPendente>();
              this.mensagemPendentesLeitura = new Hashtable<String, GrupoUsuarioMensagemPendente>();
       }
       
       public void adicionarMensagemPendente(String idMensagem, ListaDupla<Usuario> listaUsuarios, String mensagem) {
               mensagemPendentesEnvio.put(idMensagem, new GrupoUsuarioMensagemPendente(mensagem, listaUsuarios));
               mensagemPendentesLeitura.put(idMensagem, new GrupoUsuarioMensagemPendente(mensagem, listaUsuarios));
       }  
     
       public boolean acumularMensagemPendenteEnvio(String idMensagem) {
              System.out.println(mensagemPendentesEnvio);
              mensagemPendentesEnvio.get(idMensagem).atualizarConfirmacao();
              return (mensagemPendentesEnvio.get(idMensagem).foiConfirmada()); 
       }

       public boolean acumularMensagemPendenteLeitura(String idMensagem) {
              
              mensagemPendentesLeitura.get(idMensagem).atualizarConfirmacao();
              return (mensagemPendentesLeitura.get(idMensagem).foiConfirmada()); 
       }  
 
       public String listaMensagens() {
          // System.out.println("Antes: " + mensagemPendentesLeitura);
           mensagemPendentesLeitura.forEach((chave, valor) -> {
             // lista = lista + "Chave: " + chave + " | Valor: " + valor + " ";  
            });
          // System.out.println("Depois: " + mensagemPendentesLeitura);
           return lista;
       }	
}
