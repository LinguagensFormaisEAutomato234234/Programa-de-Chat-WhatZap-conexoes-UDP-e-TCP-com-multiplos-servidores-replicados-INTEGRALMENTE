package Model;
import Controller.Registro;
import Controller.VisualizadorMensagem;
import Controller.HitoricoGrupo;
import Protocol.APDU;
public class Grupo {
       private String nomeGrupo;
       private String historico = "";
       private HitoricoGrupo historicoGrupo;
       private static int valor = 0;
       private ListaDupla<APDU> listaMensagensPendentesLeitura;
       public Grupo(String nomeGrupo) {
           this.nomeGrupo = nomeGrupo;
           this.historicoGrupo = new HitoricoGrupo();
           this.listaMensagensPendentesLeitura = new ListaDupla<APDU>();
       }
       
       public void registrarMensagem(String mensagem) {
              historicoGrupo.adicionarTexto("Voce: " + mensagem);
       }
       
       public void registrarMensagemUnica(String mensagem) {
              historicoGrupo.adicionarTextoVisualizacaoUnica("Voce: " + mensagem);
       }
       
       public void registrarMensagemRecebida(String mensagem, String Usuario, APDU apdu) {
              listaMensagensPendentesLeitura.inserirFim(apdu);
              historicoGrupo.adicionarTexto(Usuario + ": " + mensagem);
       }
       
       public void registrarMensagemUnicaRecebida(String mensagem, String Usuario, APDU apdu) {
              listaMensagensPendentesLeitura.inserirFim(apdu);
              historicoGrupo.adicionarTextoVisualizacaoUnica(Usuario + ": " + mensagem);
       }
       
       public String obterTextoHistorico() {
             return historico;
       }
   
       public VisualizadorMensagem obterMensagemRotuladaAtual() {
             return historicoGrupo.obterMensagemRotuladaAtual();
       }
       
       public HitoricoGrupo obterTextoHistoricoGrupo() {
             return historicoGrupo;
       }
       
       public String obterNomeGrupo() {
            return nomeGrupo;
       }

       public APDU obterProximaMensagemPendente() {
             return listaMensagensPendentesLeitura.retirar();
       }
}
