package View;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class JanelaBloqueio {
    private Stage popUpStage; //stage principal de alocamento da tela
    private Pane janelaLongin; //define a janela de longin em que aparecera
    private double larguraJanela = 220;
    private double alturaJanela = 230;
    private javafx.scene.control.TextField textoEntrada;
    private javafx.scene.control.Button butaoBloqueio;
    private javafx.scene.control.Button butaoDesbloqueio;
    private  Button butaoSelecionado;
    //private javafx.scene.control.Button butaoBloqueio;
    
    public JanelaBloqueio() {
         this.butaoBloqueio = new javafx.scene.control.Button();
         this.butaoDesbloqueio = new javafx.scene.control.Button();
         this.popUpStage = new Stage();
         this.textoEntrada = new javafx.scene.control.TextField();
         configurarJanelaLongin();
         gerenciarModulosGraficos();
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
              this.popUpStage.setTitle("Tela Bloqueio"); 
              //permite encerrar o processo associado ao fechar a janela do envento
              
              this.popUpStage.setOnCloseRequest(t -> {
                   this.popUpStage.close();
              });
      }//fim do metodo configurarJanela
      
       public void iniciarJanela() {
            popUpStage.showAndWait();
       }
       
      private void gerenciarModulosGraficos() {
            double largura = 120;
            double altura = 20;
            double distanciaEntreOsButoes = 34;
            configurarButaoEntrar((larguraJanela-largura)/2, 150, largura, altura, butaoBloqueio, "Bloquear");
            configurarButaoEntrar((larguraJanela-largura)/2, 150+distanciaEntreOsButoes, largura, altura, butaoDesbloqueio, "desbloquear");
            configurarTextoIP();
      }
       
       private void configurarTextoIP() {
             javafx.scene.control.Label labelTextoNome = new javafx.scene.control.Label();
             configurarLabelTexto(labelTextoNome, "Informe o nome do usuario:", 102);
             configurarTexto(textoEntrada, 76);
             janelaLongin.getChildren().add(butaoBloqueio);
             janelaLongin.getChildren().add(butaoDesbloqueio);
             janelaLongin.getChildren().add(textoEntrada);
             janelaLongin.getChildren().add(labelTextoNome);
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
       
       private void configurarButaoEntrar(double x, double y, double largura, double altura, Button ponteiro, String titulo) {
              String cor = "#C0C0C0";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "6";
              double posicaoX = x;
              double posicaoY =  y;
              String title = titulo;
              setAcaoButao(ponteiro);
              configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title, ponteiro);
      } 
      
      private void configurarButaoEnvio(String cor, String cor_fundo, String tamanho_fonte, String fonte, String radius, double x, double y, double largura, double altura, String title, javafx.scene.control.Button ponteiro) {
          ponteiro.setLayoutX(x); ponteiro.setLayoutY(y);
          ponteiro.setPrefSize(largura, altura);
          ponteiro.setStyle(
               "-fx-background-color: " + cor + ";" +
               "-fx-text-fill: " +  cor_fundo +  ";" +
               "-fx-font-size: " + tamanho_fonte + ";" +
               "-fx-font-family: " + fonte + ";" +
               "-fx-background-radius: " + radius + ";"  
          );
          ponteiro.setText(title);
    }//fim do metodo configurarButaoEnvio 
       
       
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
       
       public void estabelecerPonteiroButao(Button butaoSelecionado) {
         this.butaoSelecionado = butaoSelecionado;
       }
       
       public void injetarAcaoButaoBloquear(Controller.EventoAPDUSend funcaoAltaOrdem, String Usuario) { 
            
               butaoBloqueio.setOnAction((e) -> {
                  String UsuarioBloqueado = textoEntrada.getText();
                  
                  if(!Usuario.equals(UsuarioBloqueado)) {
                     funcaoAltaOrdem.enviarMensagemEvento("NULO", Usuario, UsuarioBloqueado, "");
                  } else {
                     Aviso aviso = new Aviso();
                     aviso.mostrarAlerta("Voce nao pode bloquear voce mesmo");
                  }
                  popUpStage.close();
               });
       }
       
       public void injetarAcaoButaoDesbloqueio(Controller.EventoAPDUSend funcaoAltaOrdem, String Usuario) { 
            
               butaoDesbloqueio.setOnAction((e) -> {
                  String nomeGrupo = butaoSelecionado.getText();
                  String UsuarioBloqueado = textoEntrada.getText();
                  if(!Usuario.equals(UsuarioBloqueado)) {
                     funcaoAltaOrdem.enviarMensagemEvento("NULO", Usuario, UsuarioBloqueado, "");
                  } else {
                     Aviso aviso = new Aviso();
                     aviso.mostrarAlerta("Voce nao pode desbloquear voce mesmo");
                  }
                  popUpStage.close();
               });
       }
       
    /********************************************************************************************

     * Metodo: setAcaoButao

     * Funcao: define comportamento do botao principal com base em uma funcao de alta ordem
     * Parametros: funcao - acao a ser executada


     * Retorno: vazio
     ********************************************************************************************/
    public void setAcaoButao(Button parametro) {
         
            parametro.setOnMouseEntered(e -> {
                   costumizar(
                      "lightblue", "black", "12", "Arial", "6", parametro  
                   );
            }); 
            parametro.setOnMouseExited(e -> {
                    costumizar(
                         "#C0C0C0", "black", "12", "ARIAL", "6", parametro
                    );

            });
            parametro.setOnMousePressed(e -> {
                   costumizar(
                         "#C0C0C0", "black", "12", "ARIAL", "6", parametro
                   );
                   
            });
            
             
    } // fim do metodo setAcaoButao
    
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
         return textoEntrada.getText();
    }
    
}
