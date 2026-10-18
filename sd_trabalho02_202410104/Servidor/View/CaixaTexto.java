package View;

public class CaixaTexto {
       private javafx.scene.control.ScrollPane CampoMensagem;
       private javafx.scene.control.Label labelTextoGrupo;
       private static final int largura = 352;//essa configuracao permite simetria e centralizacao horizontal na tela
       private static final int altura = 440;
       
       public CaixaTexto() {
             this.CampoMensagem = new javafx.scene.control.ScrollPane();
             configurarLabel();
             configurarPainelListaGrupos();
       }
       
       public void alocarMensagemNoPainel(String mensagem) {
              labelTextoGrupo.setText(mensagem);
       }
       
       private void configurarLabel() {
             labelTextoGrupo = new javafx.scene.control.Label();
             labelTextoGrupo.setWrapText(true);
       }
       
       private void configurarPainelListaGrupos() {
              double posicaoX = (400-largura)/2; double PosicaoY = (496-altura)/2;
              CampoMensagem = new javafx.scene.control.ScrollPane();
              CampoMensagem.setLayoutX(posicaoX); CampoMensagem.setLayoutY(PosicaoY);
              CampoMensagem.setPrefSize(largura, altura); 
              CampoMensagem.setFitToWidth(true);
              CampoMensagem.setContent(labelTextoGrupo);
              CampoMensagem.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
      } 
      
      public javafx.scene.control.ScrollPane getPainel() {
             return CampoMensagem;
      }
}
