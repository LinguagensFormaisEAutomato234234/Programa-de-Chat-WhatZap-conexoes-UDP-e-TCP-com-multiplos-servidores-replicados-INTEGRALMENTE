package Controller;

import javafx.application.Platform;

public class VisualizadorMensagem {
     private String Mensagem;
     private javafx.scene.control.Label labelTextoGrupo;
     
     public VisualizadorMensagem(String Mensagem) {
           this.Mensagem = Mensagem;
           definirMensgem();
           definirAcaoLabel();
     }
     
    private void definirMensgem() {
          labelTextoGrupo = new javafx.scene.control.Label(Mensagem);

    }
    
    private void definirAcaoLabel() {
          labelTextoGrupo.setOnMouseEntered(event -> {
          }); 
    }

    public void adicionarImagem(String caminho) {
        Platform.runLater(() -> {
           javafx.scene.image.Image imegem = new javafx.scene.image.Image(caminho);
           javafx.scene.image.ImageView imagemView = new javafx.scene.image.ImageView(imegem);
           imagemView.setFitWidth(24);
           imagemView.setFitHeight(24);
           imagemView.setPreserveRatio(true);
           labelTextoGrupo.setGraphic(imagemView); 
           labelTextoGrupo.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
       });
    }
    
    public javafx.scene.control.Label obterLabel() {
           return labelTextoGrupo;
    }
    
    public String obterMensagem() {
           return Mensagem;
    }
}
