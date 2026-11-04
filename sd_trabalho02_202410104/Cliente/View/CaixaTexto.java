package View;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CaixaTexto {
       private javafx.scene.control.ScrollPane CampoMensagem;
       private javafx.scene.control.Label labelTextoGrupo;
       private VBox textoDEMensagem;
       private  int largura;//essa configuracao permite simetria e centralizacao horizontal na tela
       private  int altura;
       
       public CaixaTexto(int largura, int altura) {
             this.CampoMensagem = new javafx.scene.control.ScrollPane();
             this.textoDEMensagem = new VBox();
             this.largura = largura;
             this.altura = altura;
             configurarLabel();
             configurarPainelListaGrupos();
       }
       
       
       public void alocarMensagemNoPainel(VBox mensagem) {
              this.textoDEMensagem = mensagem;
              CampoMensagem.setContent(textoDEMensagem);
       }
       
       private void configurarLabel() {
             labelTextoGrupo = new javafx.scene.control.Label();
             labelTextoGrupo.setWrapText(true);
       }
       
       private void configurarPainelListaGrupos() {
              double posicaoX = 10; double PosicaoY = 35;
              CampoMensagem = new javafx.scene.control.ScrollPane();
              CampoMensagem.setLayoutX(posicaoX); CampoMensagem.setLayoutY(PosicaoY);
              CampoMensagem.setPrefSize(largura, altura); 
              CampoMensagem.setFitToWidth(true);
              CampoMensagem.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
      } 
      
      public javafx.scene.control.ScrollPane getPainel() {
             return CampoMensagem;
      }
}
