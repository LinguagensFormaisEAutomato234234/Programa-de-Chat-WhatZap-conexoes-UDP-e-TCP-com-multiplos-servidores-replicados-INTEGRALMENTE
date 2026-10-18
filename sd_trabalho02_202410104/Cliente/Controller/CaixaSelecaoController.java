package Controller;
import View.CaixaSelecao;

public class CaixaSelecaoController {
      private CaixaSelecao caixaMarcada;
      public CaixaSelecaoController() {
           this.caixaMarcada = new CaixaSelecao();
      }
      
      public boolean verificarMarcadorSelecionado() {
          return caixaMarcada.estaMarcado();
      }
      
      public CaixaSelecao obterCaixaMarcado() {
          return caixaMarcada;
      }
}
