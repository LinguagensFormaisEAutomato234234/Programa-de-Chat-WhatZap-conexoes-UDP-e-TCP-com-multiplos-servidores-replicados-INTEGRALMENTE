package View;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class JanelaLogin {
    private Pane janelaRaiz;
    private Stage popUpStage; //stage principal de alocamento da tela
    private Pane janelaLongin; //define a janela de longin em que aparecera
    private double larguraJanela = 220;
    private double alturaJanela = 115;
    private javafx.scene.control.TextField textoEntradaNome;
    private javafx.scene.control.Button butaoLogar;
    
    
    public JanelaLogin(Pane janelaRaiz) {
         this.janelaRaiz = janelaRaiz;
         this.butaoLogar = new javafx.scene.control.Button();
         this.popUpStage = new Stage();
         this.textoEntradaNome = new javafx.scene.control.TextField();
         configurarJanelaLongin();
    }
    
    /********************************************************************************************
      * Metodo: configurarJanela
      * Funcao: estabelecer uma interface de comunicacao com o usuario de maneira em que este possa fornecer informacoes essenciais para 
                realizar o procedimento de longin
      * Parametros: nenhum
      * Retorno: vazio

      ********************************************************************************************/ 
      private void configurarJanelaLongin() {
              this.janelaLongin = new Pane();
              this.janelaLongin.setStyle("-fx-background-color: #FFDBBB;");
              this.popUpStage.initModality(Modality.APPLICATION_MODAL);
              Scene scene = new Scene(janelaLongin, larguraJanela, alturaJanela);
              this.popUpStage.setScene(scene);
              this.popUpStage.setResizable(false);
              this.popUpStage.setTitle("Tela Cadastro"); 
              //this.popUpStage.getScene().getRoot().setStyle("-fx-background-radius: 20;");
              //permite encerrar o processo associado ao fechar a janela do envento
              this.popUpStage.setOnCloseRequest(t -> {
                   t.consume();
                   this.popUpStage.close();
                   javafx.application.Platform.exit();
                   System.exit(0);
              });
      }//fim do metodo configurarJanela
      
       public void iniciarJanela() {
            configurarTexto();
            popUpStage.showAndWait();
       }
       
       private void configurarTexto() { 
             javafx.scene.control.Label labelTextoNome = new javafx.scene.control.Label();
             configurarLabelTexto(labelTextoNome, "Informe o seu nome:", 16);
             configurarTexto(textoEntradaNome, 38);
             configurarButao();
             janelaLongin.getChildren().add(butaoLogar);
             janelaLongin.getChildren().add(labelTextoNome);
             janelaLongin.getChildren().add(textoEntradaNome);
       }
       private void configurarLabelTexto(javafx.scene.control.Label labelTexto, String texto, double altura) {
              labelTexto.setStyle(
                  "-fx-control-inner-background: black;" +
                  "-fx-text-fill: #000000;" + 
                  "-fx-font-size: 12px;"
              );
              labelTexto.setLayoutX((larguraJanela-180)/2);
              labelTexto.setLayoutY(altura);
              labelTexto.setText(texto);
       }
       
       private void configurarButao() {
              String cor = "#C0C0C0";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "6";
              double largura = 120;
              double altura = 20; 
              double posicaoX = (larguraJanela-largura)/2;
              double posicaoY =  80;
              String title = "logar";
              this.butaoLogar = configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title);
              estruturarAcao();
       }
       
       private void estruturarAcao() {
            butaoLogar.setOnMouseEntered(e -> {
                   costumizar(
                      "lightblue", "black", "12", "Arial", "6", butaoLogar  
                   );
            }); 
            butaoLogar.setOnMouseExited(e -> {
                    costumizar(
                         "#C0C0C0", "black", "12", "ARIAL", "6",  butaoLogar
                    );

            });
            butaoLogar.setOnMousePressed(e -> {
                   costumizar(
                         "#C0C0C0", "black", "12", "ARIAL", "6",  butaoLogar
                   );
                   
            });
            butaoLogar.setOnMouseReleased(e -> {
                   costumizar(
                         "#C0C0C0", "black", "12", "ARIAL", "6",  butaoLogar
                   );
            });
            butaoLogar.setOnAction(e -> {
                   // System.out.println("asdadasd");
                    popUpStage.close();
            });
       }
       
       private void configurarTexto(javafx.scene.control.TextField componente, double y) {
              componente.setStyle("-fx-text-alignment: left; -fx-alignment: top-left;" +
                              "-fx-control-inner-background: black;" +
                              "-fx-text-fill: #00FF00;" +
                              "-fx-font-size: 12px;" +
                              "-fx-prompt-text-fill: #008000;"); 
             double largura = 180, altura = 180;     
             componente.setPrefWidth(largura);
             componente.setPrefHeight(10);
             componente.setLayoutX((larguraJanela-largura)/2);
             componente.setLayoutY(y);
       }
    /********************************************************************************************
     * Metodo: configurarButaoEnvio
     * Funcao: cria um botao com propriedades visuais customizadas
     * Parametros: propriedades de estilo e dimensoes
     * Retorno: objeto Button configurado
     ********************************************************************************************/
    private Button configurarButaoEnvio(String cor, String cor_fundo, String tamanho_fonte, String fonte, String radius, double x, double y, double largura, double altura, String title) {
          Button butao = new Button(title);
          butao.setLayoutX(x); butao.setLayoutY(y);
          butao.setPrefSize(largura, altura);
          butao.setStyle(
               "-fx-background-color: " + cor + ";" +
               "-fx-text-fill: " +  cor_fundo +  ";" +
               "-fx-font-size: " + tamanho_fonte + ";" +
               "-fx-font-family: " + fonte + ";" +
               "-fx-background-radius: " + radius + ";"  
          );
          return butao;
    }//fim do metodo configurarButaoEnvio
    
    /********************************************************************************************
    * Metodo: costumizar

    * Funcao: aplica estilo customizado a um botao
    * Parametros: propriedades visuais e botao alvo
    * Retorno: vazio
    ********************************************************************************************/     
    private void costumizar(String x, String y, String z, String w, String r, Button k) {
             k.setStyle(
                      "-fx-background-color: " + x + ";" +
                      "-fx-text-fill: " +  y +  ";" +
                      "-fx-font-size: " + z + ";" +
                      "-fx-font-family: " + w + ";" +
                      "-fx-background-radius: " + r + ";"  
            );
    }//fim do metodo costumizar
    
    public String obterInformacaoEntrada() {
         return "--ERRO--";
    }
    
    public String obterInformacaoEntradaNome() {
         return textoEntradaNome.getText();
    }    
}
