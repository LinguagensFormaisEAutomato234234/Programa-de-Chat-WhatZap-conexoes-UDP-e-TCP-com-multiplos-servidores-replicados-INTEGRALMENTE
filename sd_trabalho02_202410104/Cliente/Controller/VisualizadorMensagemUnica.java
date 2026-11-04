package Controller;

import View.Aviso;
import javafx.application.Platform;


public class VisualizadorMensagemUnica extends VisualizadorMensagem {
     private javafx.scene.control.Label TextoGrupo;
     private enum Estado {
                   Aberta, NaoAberta;
              }
     private Estado estado;      
     public VisualizadorMensagemUnica(String Mensagem) {
           super(Mensagem);
           this.estado = Estado.NaoAberta;
           definirMensgem();
           acaoAoClique();
     }

    private void definirMensgem() {
          TextoGrupo = new javafx.scene.control.Label("Clique aqui para visualizar a mensagem");
    }
    
    
    public javafx.scene.control.Label obterLabelTexto() {
        return TextoGrupo;
    }



    public void adicionarImagem(String caminho) {
       Platform.runLater(() -> {
           javafx.scene.image.Image imegem = new javafx.scene.image.Image(caminho);
           javafx.scene.image.ImageView imagemView = new javafx.scene.image.ImageView(imegem);
           imagemView.setFitWidth(24);
           imagemView.setFitHeight(24);
           imagemView.setPreserveRatio(true);
           TextoGrupo.setGraphic(imagemView); 
           TextoGrupo.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
       });
    }

    private void acaoAoClique() {
        TextoGrupo.setOnMouseClicked((event) -> {
             if(estado == Estado.NaoAberta) {
               (new Aviso()).mostrarAviso(super.obterMensagem());
               estado = Estado.Aberta;
               TextoGrupo.setText("Mensagem aberta");
             }
        });
    }
}
