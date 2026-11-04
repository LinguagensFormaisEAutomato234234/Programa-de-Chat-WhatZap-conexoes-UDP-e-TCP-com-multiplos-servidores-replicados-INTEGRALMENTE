package View;

import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class EditarPainelDireito {
     private javafx.scene.layout.Pane painel;
     private static final int larguraPanelDireito = 800;
     private static final int alturaPanelDireito = 650;
     private javafx.scene.control.TextField campoTextoEnvioMensagem;
     private CaixaTexto caixaTexto;      
     private javafx.scene.control.Button enviar;
     private Label labelTexto;
     public EditarPainelDireito(javafx.scene.layout.Pane painel, CaixaTexto caixaTexto) {
            this.painel = painel; 
            this.caixaTexto = caixaTexto;
     }

     public void gerenciarModulosGraficos() {
         rearranjarPainel();
         configurarTexto();
         configurarCampoTexto();
         configurarButaoEnviar();
         adicionarComponentesTela();
         
     }

     private void configurarTexto() {
        labelTexto = new Label();
        configurarLabelTexto(labelTexto, "Grupo selecionado: Nenhum", 10,10);
     }

     public void editarTextoGrupo(String texto) {
           labelTexto.setText("Grupo selecionado: " + texto);
     }

     private void configurarCampoTexto() {
             double posicaoX = 10; double PosicaoY = 580;
             double largura = larguraPanelDireito-2*posicaoX-140;; double comprimento = 10;
             campoTextoEnvioMensagem = new javafx.scene.control.TextField();
             campoTextoEnvioMensagem.setLayoutX(posicaoX); campoTextoEnvioMensagem.setLayoutY(PosicaoY);
             campoTextoEnvioMensagem.setPrefSize(largura, comprimento);
             campoTextoEnvioMensagem.setStyle("-fx-text-alignment: left; -fx-alignment: top-left;" +
                              "-fx-control-inner-background: WHITE;" +
                              "-fx-text-fill: #000000;" +
                              "-fx-font-size: 12px;" +
                              "-fx-prompt-text-fill: #008000;");
             campoTextoEnvioMensagem.setPromptText("Digite o nome do grupo"); 
      }  
      
      private void configurarButaoEnviar() {
           String cor = "#C0C0C0";
           String cor_fundo = "black";
           String tamanho_fonte = "12";
           String fonte = "'System'";
           String radius = "6";
           double largura = 120;
           double altura = 20; 
           double posicaoX = 658;
           double posicaoY =  580;
           String title = "enviar";
           this.enviar = new javafx.scene.control.Button();
           configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title, enviar);
           setAcaoButao(enviar);
           
      }
       
      private void configurarLabelTexto(javafx.scene.control.Label labelTexto, String texto, double altura, double posicaoX) {
              labelTexto.setStyle(
                  "-fx-control-inner-background: black;" +
                  "-fx-text-fill: #000000;" + 
                  "-fx-font-size: 12px;"
              );
              labelTexto.setLayoutX(posicaoX);
              labelTexto.setLayoutY(altura);
              labelTexto.setText(texto);
       }


      public void estabelecerAcaoButaoEnviar(Controller.FuncaoAltaOrdemInjetora<String, String> funcaoInjetora, Controller.EventoAPDUSend APDUSEND, Controller.FuncaoAltaOrdemInjetora<String, String> funcaoInjetoraNomeGrupo, String nomeUsuario) {
          enviar.setOnAction((e) -> {
                 String mensagem = campoTextoEnvioMensagem.getText();
                 String nomeGrupo =  funcaoInjetoraNomeGrupo.FuncaoAltaOrdemInjetora("");
                 Object texto = funcaoInjetora.FuncaoAltaOrdemInjetora(mensagem);
                 if(texto == null) {
                     Aviso aviso = new Aviso();
                     aviso.mostrarAlerta("Acesse algum grupo. Para acessar, basta clicar no grupo desejado disponivel na aba esquerda");
                     return; //interrompe fluxo
                 }
                 VBox textoGrupoLabel =  ((Controller.HitoricoGrupo)texto).obterTextoVerticalView().obterTextHbox(); //aqui obbtemos todos o texo referente ao historico do grupo, oara isso retornamos o hisotorico do grupo, em seguida a isso, obtemos um abstracao logica da HBOX em que obetmos o componente Hbox em que contem o historico de mensagem do grupo referente
            
                 caixaTexto.alocarMensagemNoPainel(textoGrupoLabel);
                 APDUSEND.enviarMensagemEvento(nomeGrupo, nomeUsuario, mensagem, "SEND");
          });
      }
      
      private void adicionarComponentesTela() {
             painel.getChildren().add(caixaTexto.getPainel());
             painel.getChildren().add(labelTexto);
             painel.getChildren().add(campoTextoEnvioMensagem);
             painel.getChildren().add(enviar);
      }
      
      public void adicionarComponentePainelDireito(Node node) {
             painel.getChildren().add(node);
      }
      
     /********************************************************************************************
      * Metodo: rearranjarPainel
      * Funcao: define propriedades visuais e estruturais do painel direito, como cor de fundo,
      *         posicao e dimensoes
      * Parametros: nenhum
      * Retorno: vazio
      ********************************************************************************************/
      private void rearranjarPainel() {               
 	      painel.setStyle("-fx-background-color: #FFDBBB;");
              painel.setLayoutX(202);
              painel.setPrefSize(larguraPanelDireito, alturaPanelDireito);  
      } //fim do metodo rearranjarPainel

      /********************************************************************************************
     * Metodo: setAcaoButao

     * Funcao: define comportamento do botao principal com base em uma funcao de alta ordem
     * Parametros: funcao - acao a ser executada

     * Retorno: vazio
     ********************************************************************************************/
    public void setAcaoButao(javafx.scene.control.Button parametro) {
         
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
    private void costumizar(String x, String y, String z, String w, String r, javafx.scene.control.Button k) {
             k.setStyle(
                      "-fx-background-color: " + x + ";" +
                      "-fx-text-fill: " +  y +  ";" +
                      "-fx-font-size: " + z + ";" +
                      "-fx-font-family: " + w + ";" +
                      "-fx-background-radius: " + r + ";"  
            );
    }//fim do metodo costumizar
    
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
}
