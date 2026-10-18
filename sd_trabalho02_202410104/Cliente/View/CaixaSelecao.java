/************************************************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 03/07/2026
* Ultima alteracao.: 03/07/2026
* Nome.............: CaixaSelecao
* Funcao...........: estabelcer uma interface que permite mecanismo de serviço referente a visualizacao unica
**************************************************************************************************/

package View;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class CaixaSelecao {
     private CheckBox caixaMarcadora;
     private Label textoVisualizacaoUnica; 
     public CaixaSelecao() {
          this.caixaMarcadora = new CheckBox();
          this.textoVisualizacaoUnica = new Label();
          configurarcaixaMarcadora();
          editarTexto();
     }
     
     private void editarTexto() {
        configurarLabelTexto(textoVisualizacaoUnica, "Visualizacao Unica",610, 44);
     }
     
     private void configurarcaixaMarcadora() {
            caixaMarcadora.setLayoutX(12);
            caixaMarcadora.setLayoutY(610);
     }
     
     public boolean estaMarcado() {
           return caixaMarcadora.isSelected();
     }

     public CheckBox obterTextoMarcador() {
           return caixaMarcadora;
     }
     
     public Label obterTextoVisualizacaoUnica() {
           return textoVisualizacaoUnica;
     }
     
     private void configurarLabelTexto(Label labelTexto, String texto, double y, double x) {
              labelTexto.setStyle(
                  "-fx-control-inner-background: black;" +
                  "-fx-text-fill: #000000;" + 
                  "-fx-font-size: 12px;"
              );
              labelTexto.setLayoutX(x);
              labelTexto.setLayoutY(y);
              labelTexto.setText(texto);
     }
}
