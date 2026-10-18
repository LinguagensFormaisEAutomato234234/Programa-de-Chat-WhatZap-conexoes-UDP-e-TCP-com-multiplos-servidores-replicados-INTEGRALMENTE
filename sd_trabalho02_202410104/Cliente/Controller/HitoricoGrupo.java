/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 04/06/2026
*Ultima alteracao.: 04/06/2026
*Nome.............: HitoricoGrupo
*Funcao...........: definir a estrutura de agregcao de mensagem do grupo, bem como gerenciar mensagens de comando temporario 
**************************************************************************************************/

package Controller;

import View.TextoVerticalView;

public class HitoricoGrupo {
     private TextoVerticalView textoVerticalView;
     private VisualizadorMensagem mensagemRotulada;
     public HitoricoGrupo() {
           textoVerticalView = new TextoVerticalView();
     }
     
     public void adicionarTexto(String texto) {
          this.mensagemRotulada = new VisualizadorMensagem(texto);
          textoVerticalView.adicionarTexto(mensagemRotulada.obterLabel());
     }
     
     public void adicionarTextoVisualizacaoUnica(String texto) {
          this.mensagemRotulada = new VisualizadorMensagemUnica(texto);
          textoVerticalView.adicionarTexto(((VisualizadorMensagemUnica)mensagemRotulada).obterLabelTexto());
     }
     
     public TextoVerticalView obterTextoVerticalView() {
          return textoVerticalView;
     }

     public VisualizadorMensagem obterMensagemRotuladaAtual() {
           return mensagemRotulada;
     }
}//fim da classe HitoricoGrupo
