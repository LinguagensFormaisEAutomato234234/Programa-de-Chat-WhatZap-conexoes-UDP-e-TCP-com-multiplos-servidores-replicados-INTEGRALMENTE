package View;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.application.Platform;

public class TextoVerticalView {
    private VBox textoVertical;
    public TextoVerticalView() {
          textoVertical = new VBox();
          textoVertical.setSpacing(10);
    }
    
    public void adicionarTexto(Label texto) {
      Platform.runLater(() -> {
         textoVertical.getChildren().add(texto);
      });    
    }
    
    public VBox obterTextHbox() {
         return textoVertical;
    }
    
}//fim da classe TextoVerticalView
