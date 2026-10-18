/************************************************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 03/07/2026 (entre 22 a 26)
* Ultima alteracao.: 03/07/2026 (entre 22 a 30)
* Nome.............: ComponentesJanela
* Funcao...........: adicionas componentes graficos a tela de iteracao bem como suas respectivas acoes associadas
**************************************************************************************************/

package Controller;
import View.Janela;
public class ComponentesJanela {
     private Janela janela;
     private CaixaSelecaoController caixa;
     public ComponentesJanela(Janela janela) {
           this.janela = janela;
           caixa = new CaixaSelecaoController();
     }
     
      /********************************************************************************************
      * Metodo: adicionarComponentes
      * Funcao: adicionar componentes a visualizacao bem como seus eventos
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
     public void adicionarComponentes() {
          janela.adicionarComponentePainelDireito(caixa.obterCaixaMarcado().obterTextoMarcador());
          janela.adicionarComponentePainelDireito(caixa.obterCaixaMarcado().obterTextoVisualizacaoUnica());
     }
     
     public boolean verificarCaixaMarcada() {
         return caixa.verificarMarcadorSelecionado();
     }
}

