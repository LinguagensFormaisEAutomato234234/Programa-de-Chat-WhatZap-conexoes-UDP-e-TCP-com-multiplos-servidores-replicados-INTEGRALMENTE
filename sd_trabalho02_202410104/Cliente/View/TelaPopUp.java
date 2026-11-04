package View;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.event.ActionEvent;


public class TelaPopUp {
      private Stage popUpStage;
      private Pane janelaPrincipal;
      private double larguraJanela = 200;
      private double alturaJanela = 150;
      private Button butaoEntrar; //definido para entrar num grupo
      private Button butaoSair; //um butao definido para sair do grupo associado
      private String nomeGrupo;
      private CaixaTexto caixaTexto;
      private Label labelTexto;
      private Button butaoSelecionado;
      private VBox caixaVerticalGrupos;
      private EditarPainelDireito editarPainelDireito;
      StackPane pane;
      public TelaPopUp(CaixaTexto caixaTexto, EditarPainelDireito editarPainelDireito) {
            this.popUpStage = new Stage();
            this.butaoEntrar = new Button();
            this.butaoSair = new Button();
            this.editarPainelDireito = editarPainelDireito;
            this.caixaTexto = caixaTexto;
            gerenciarModulosGraficos();
      }
 
      private void gerenciarModulosGraficos() {
              configurarPainel(); 
              configurarStage();
              double largura = 120;
              double altura = 20;
              double distanciaEntreOsButoes = 34;
              configurarLabel();
              configurarButaoEntrar((larguraJanela-largura)/2, 50, largura, altura, butaoEntrar, "acessar grupo");
              configurarButaoEntrar((larguraJanela-largura)/2, 50 + distanciaEntreOsButoes, largura, altura, butaoSair, "sair do grupo");//associamos as acoes iterativas relacionadas a esse componente
            //  configurarButaoEntrar((larguraJanela-largura)/2, 50 + 2*distanciaEntreOsButoes, largura, altura, butaoBloquear, "Bloquear/desbloquear membro");//associamos as acoes iterativas relacionadas a esse componente
              adicionarComponentes();
      }
      
      private void configurarLabel() {
            labelTexto = new Label("     Escolha uma das opcoes: ");
            double largura = 180;
            double altura = 20;
            labelTexto.setPrefSize(largura, altura);
            pane = new StackPane(labelTexto);
            pane.setPrefSize(largura, altura);
            pane.setLayoutX((larguraJanela-largura)/2);
      }
      
      private void adicionarComponentes() {
              janelaPrincipal.getChildren().add(butaoEntrar);
              janelaPrincipal.getChildren().add(butaoSair);
              janelaPrincipal.getChildren().add(pane);
      }
      
      private void configurarStage() {
             popUpStage.initModality(Modality.APPLICATION_MODAL);
             Scene scene = new Scene(janelaPrincipal, larguraJanela, alturaJanela);
             popUpStage.setScene(scene); 
             popUpStage.setResizable(false);
             popUpStage.setTitle("Escolhas as opcoes");
      }
      
      private void configurarPainel() {
             janelaPrincipal = new Pane();
             janelaPrincipal.setStyle("-fx-background-color: #FFDBBB;");
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
      
      public void mostrarJanela() {
             popUpStage.showAndWait();
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
    
    public void estabelecerNomeConversaPrivada() {
          butaoEntrar.setText("Entrar Conversa");
          butaoSair.setText("Sair Conversa");
    }
    
    public void estabelecerNomeConversaGrupo() {
          butaoEntrar.setText("Acessar Grupo");
          butaoSair.setText("sair do grupo");
    }
    
    public void estabelecerPonteiroButao(Button butaoSelecionado) {
         this.butaoSelecionado = butaoSelecionado;
    }
    
    

    public void injetarAcaoConfirmacaoMensagemButaoEntrar(Controller.funcaoConfirmacao funcao) {
           butaoEntrar.addEventHandler(ActionEvent.ACTION, e -> {
                  funcao.executar();
           });
    }
    
    public void injetarAcaoButaoEntrar(Controller.FuncaoAltaOrdemInjetora<Boolean, String> funcaoInjetero, Controller.FuncaoAltaOrdemInjetora<Controller.HitoricoGrupo, String> W) {
           butaoEntrar.addEventHandler(ActionEvent.ACTION, e -> {
                funcaoInjetero.FuncaoAltaOrdemInjetora(nomeGrupo);
                editarPainelDireito.editarTextoGrupo(butaoSelecionado.getText());
                caixaTexto.alocarMensagemNoPainel(W.FuncaoAltaOrdemInjetora("").obterTextoVerticalView().obterTextHbox());//aqui obtemos o texto vertical representando a Vbox
                popUpStage.close();
           });
    }
    
    public void injetarAcaoButaoSair(Controller.EventoAPDUs funcaoAltaOrdem, String Usuario) { 
               
               butaoSair.setOnAction((e) -> {
                  String nomeGrupo = butaoSelecionado.getText();
                  funcaoAltaOrdem.enviarMensagemEvento(nomeGrupo, Usuario);
                  removerButao(nomeGrupo);
                  popUpStage.close();
               });
    }
    
    public void removerButao(String nome) {
          for (int i = caixaVerticalGrupos.getChildren().size() - 1; i >= 0; i--) {
             Button node = (Button)caixaVerticalGrupos.getChildren().get(i);
             if (nome.equals(node.getText())) {
                caixaVerticalGrupos.getChildren().remove(i);
             }
          }
    }

    public void definirNomeGrupo(String nomeGrupo) {
         this.nomeGrupo = nomeGrupo;
    }
    
    public void estabelecerEstruturaVertical(javafx.scene.layout.VBox caixaVerticalGrupos) {
          this.caixaVerticalGrupos = caixaVerticalGrupos;
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
    
    
}
