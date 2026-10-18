package Model;
import Protocol.APDU;
import java.io.Serializable;
import java.util.Hashtable;

public class AdministrarMensagensPendentes implements Serializable {
       class GrupoUsuarioMensagemPendente implements Serializable {
             private ListaDupla<Usuario> listaUsuarios;
             private int confirmacaoMensagens = 0;
             private static final long serialVersionUID = 1L;
             public GrupoUsuarioMensagemPendente(ListaDupla<Usuario> listaUsuarios) {
                  this.listaUsuarios = listaUsuarios;
             } 
              
             public void atualizarConfirmacao() {
                    confirmacaoMensagens++;
             }
 
             public boolean foiConfirmada() {
                    return (confirmacaoMensagens+1) >= listaUsuarios.getComprimentoLista();
             }

       }         
       private Hashtable<String, GrupoUsuarioMensagemPendente> mensagemPendentesEnvio;
       private Hashtable<String, GrupoUsuarioMensagemPendente> mensagemPendentesLeitura;
       private static final long serialVersionUID = 1L;
       
       public AdministrarMensagensPendentes() {
              this.mensagemPendentesEnvio = new Hashtable<String, GrupoUsuarioMensagemPendente>();
              this.mensagemPendentesLeitura = new Hashtable<String, GrupoUsuarioMensagemPendente>();
       }
       
       public void adicionarMensagemPendente(String idMensagem, ListaDupla<Usuario> listaUsuarios) {
             //  System.out.println(idMensagem + " associado a " + new GrupoUsuarioMensagemPendente(listaUsuarios) + " e a " + listaUsuarios);
               mensagemPendentesEnvio.put(idMensagem, new GrupoUsuarioMensagemPendente(listaUsuarios));
               mensagemPendentesLeitura.put(idMensagem, new GrupoUsuarioMensagemPendente(listaUsuarios));
       }  
     
       public boolean acumularMensagemPendenteEnvio(String idMensagem) {
           //   System.out.println(idMensagem + " " + mensagemPendentesEnvio.get(idMensagem));
              mensagemPendentesEnvio.get(idMensagem).atualizarConfirmacao();
              return (mensagemPendentesEnvio.get(idMensagem).foiConfirmada()); 
       }

       public boolean acumularMensagemPendenteLeitura(String idMensagem) {
              mensagemPendentesLeitura.get(idMensagem).atualizarConfirmacao();
              return (mensagemPendentesLeitura.get(idMensagem).foiConfirmada()); 
       }  
}
