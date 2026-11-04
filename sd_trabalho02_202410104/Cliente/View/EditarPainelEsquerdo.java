/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 20/06/2026
*Ultima alteracao.: 20/06/2026
*Nome.............: EditarPainelEsquerdo
*Funcao...........: configurar e gerenciar o painel esquerdo da interface grafica, sendo 
*                   responsavel pela manipulacao de elementos visuais como textos e 
*                   propriedades estruturais do painel
**************************************************************************************************/

package View;
import Controller.SelecaoMensagem;
import javafx.scene.control.ComboBox;
import javafx.scene.Node;

public class EditarPainelEsquerdo {
       private javafx.scene.layout.Pane painel;
       private javafx.scene.control.Label labelTextoGrupo;
       private javafx.scene.control.ScrollPane telaGrupos;
       private javafx.scene.layout.VBox caixaVerticalGrupos;
       private javafx.scene.control.TextField campoTextoNomeGrupo;
       private javafx.scene.control.Button butaoCriarEntrar;  
       private javafx.scene.control.Button butaoSelecionado;
       private JanelaBloqueio janelaBloqueio;
       private javafx.scene.control.Button butaoBloquear;
       private SelecaoMensagem selecaoMensagem;
       private ComboBox<String> comboBox; 
       private static final int larguraPanel = 200;
       private static final int alturaPanel = 650;
       private TelaPopUp telaPopUp;
       public EditarPainelEsquerdo(javafx.scene.layout.Pane painel, CaixaTexto caixaTexto, EditarPainelDireito editarPainelDireito, SelecaoMensagem mensagemSelecionada) {
              this.selecaoMensagem = mensagemSelecionada;
              this.painel = painel;
              this.comboBox = new ComboBox<String>();
              this.telaPopUp = new TelaPopUp(caixaTexto, editarPainelDireito);
       }

      public void gerenciarModulosGraficos() {
             rearranjarPainel();
             configurarLabelTexto("Grupos", 10,10);  
             configurarPainelListaGrupos();
             configurarCampoTexto();
             configurarButao();
           //  configurarCombobox();
             adicionarComponentesTela();
      }
      
      private void configurarCampoTexto() {
             double posicaoX = 10; double PosicaoY = 0.7*alturaPanel;
             double largura = larguraPanel-2*posicaoX; double comprimento = 10;
             campoTextoNomeGrupo = new javafx.scene.control.TextField();
             campoTextoNomeGrupo.setLayoutX(posicaoX); campoTextoNomeGrupo.setLayoutY(PosicaoY);
             campoTextoNomeGrupo.setPrefSize(largura, comprimento);
             campoTextoNomeGrupo.setStyle("-fx-text-alignment: left; -fx-alignment: top-left;" +
                              "-fx-control-inner-background: WHITE;" +
                              "-fx-text-fill: #000000;" +
                              "-fx-font-size: 12px;" +
                              "-fx-prompt-text-fill: #008000;");
             campoTextoNomeGrupo.setPromptText("Digite o nome do grupo"); 
      }

      private void configurarCombobox() {
          String cor = "#C0C0C0";
          String corFundo = "black";
          String tamanhoFonte = "12";
          String fonte = "'System'";
          String radius = "6";

          double largura = 150;
          double altura = 20;
          double posicaoX = 10;
          double posicaoY = 0.76 * alturaPanel + 2 * 17 + altura + 10;

         comboBox.getItems().addAll(
              "Mensagem publica",
              "Mensagem privada"
         );
         comboBox.getSelectionModel().select(0);
         comboBox.setLayoutX(posicaoX);
         comboBox.setLayoutY(posicaoY);
         comboBox.setPrefSize(largura, altura);

         comboBox.setStyle(
           "-fx-background-color: " + cor + ";" +
           "-fx-text-fill: " + corFundo + ";" +
           "-fx-font-size: " + tamanhoFonte + "px;" +
           "-fx-font-family: " + fonte + ";" +
           "-fx-background-radius: " + radius + ";" +
           "-fx-border-radius: " + radius + ";"
         );

           painel.getChildren().add(comboBox);
     }

      private void configurarButao() {
              String cor = "#C0C0C0";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "6";
              double largura = 120;
              double altura = 20; 
              double posicaoX = 10;
              double posicaoY =  0.76*alturaPanel;
              String title = "Criar/Entrar";
              double distanciaEntreOsButoes = 17;
              butaoCriarEntrar = new javafx.scene.control.Button();
              butaoBloquear = new javafx.scene.control.Button();
              configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title, butaoCriarEntrar);
              setAcaoButao(butaoCriarEntrar, cor, radius, largura, altura, tamanho_fonte);
              configurarButaoEntrar(posicaoX, posicaoY + 2*distanciaEntreOsButoes, largura, altura, butaoBloquear, "Bloquear/desbloquear membro");//associamos as acoes iterativas relacionadas a esse componente
      }

      private void configurarPainelListaGrupos() {
              double posicaoX = 10; double PosicaoY = 35;
              double largura = larguraPanel-2*posicaoX;//essa configuracao permite simetria e centralizacao horizontal na tela
              double comprimento = 0.6*alturaPanel; 
              telaGrupos = new javafx.scene.control.ScrollPane();
              telaGrupos.setLayoutX(posicaoX); telaGrupos.setLayoutY(PosicaoY);
              telaGrupos.setPrefSize(largura, comprimento); 
              caixaVerticalGrupos = new javafx.scene.layout.VBox();
              telaGrupos.setContent(caixaVerticalGrupos);
              telaGrupos.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
      }

      private void configurarButaoEntrar(double x, double y, double largura, double altura, javafx.scene.control.Button ponteiro, String titulo) {
              String cor = "#C0C0C0";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "6";
              double posicaoX = x;
              double posicaoY =  y;
              String title = titulo;
              setAcaoButao(ponteiro, cor, radius, largura, altura, tamanho_fonte);
              configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title, ponteiro);
      } 

      private void adicionarComponentesTela() {
             painel.getChildren().add(labelTextoGrupo);
             painel.getChildren().add(telaGrupos);   
             painel.getChildren().add(campoTextoNomeGrupo);
             painel.getChildren().add(butaoCriarEntrar);
             painel.getChildren().add(butaoBloquear);
      } 
      /********************************************************************************************
      * Metodo: rearranjarPainel
      * Funcao: define propriedades visuais e estruturais do painel direito, como cor de fundo,
      *         posicao e dimensoes
      * Parametros: nenhum
      * Retorno: vazio
      ********************************************************************************************/
      private void rearranjarPainel() {               
 	      painel.setStyle("-fx-background-color: #d3d3d3;");
              painel.setLayoutX(0);
              painel.setPrefSize(larguraPanel, alturaPanel);  
      } //fim do metodo rearranjarPainel

      private void configurarLabelTexto(String texto, double altura, double horizonte) { 
              labelTextoGrupo = new javafx.scene.control.Label();
              labelTextoGrupo.setStyle(
                  "-fx-control-inner-background: black;" +
                  "-fx-text-fill: #000000;" + 
                  "-fx-font-size: 12px;"
              );
              labelTextoGrupo.setLayoutX(horizonte);
              labelTextoGrupo.setLayoutY(altura);
              labelTextoGrupo.setText(texto);
       }
      private void configurarButaoEnvio(String cor, String cor_fundo, String tamanho_fonte, String fonte, String radius, double x, double y, double largura, double altura, String title, javafx.scene.control.Button ponteiro) {
          ponteiro.setLayoutX(x); ponteiro.setLayoutY(y);
          ponteiro.setStyle(
               "-fx-background-color: " + cor + ";" +
               "-fx-text-fill: " +  cor_fundo +  ";" +
               "-fx-font-size: " + tamanho_fonte + ";" +
               "-fx-font-family: " + fonte + ";" +
               "-fx-background-radius: " + radius + ";"  
          );
          ponteiro.setPrefSize(largura, altura);
          ponteiro.setText(title);
    }//fim do metodo configurarButaoEnvio

     /********************************************************************************************
     * Metodo: setAcaoButao

     * Funcao: define comportamento do botao principal com base em uma funcao de alta ordem
     * Parametros: funcao - acao a ser executada

     * Retorno: vazio
     ********************************************************************************************/
    public void setAcaoButao(javafx.scene.control.Button butaoPonteiro, String cor, String raio, double largura, double altura, String tamanho_fonte) {
            butaoPonteiro.setOnMouseEntered(e -> {
                   butaoPonteiro.setStyle("-fx-background-color: lightblue;" + "-fx-background-radius: "+ raio +";" + "-fx-text-fill: black;"+"-fx-font-size: " + tamanho_fonte + ";" );
                   butaoPonteiro.setPrefSize(largura, altura);
            }); 
            butaoPonteiro.setOnMouseExited(e -> {
                    butaoPonteiro.setStyle("-fx-background-color:" + cor + ";"+ "-fx-background-radius: "+ raio +";"+ "-fx-text-fill: black;"+"-fx-font-size: " + tamanho_fonte + ";");
                   butaoPonteiro.setPrefSize(largura, altura);
            });
            butaoPonteiro.setOnMousePressed(e -> {
                   butaoPonteiro.setStyle("-fx-background-color:" + cor + ";" + "-fx-background-radius: "+ raio +";"+ "-fx-text-fill: black;"+"-fx-font-size: " + tamanho_fonte + ";");
                  butaoPonteiro.setPrefSize(largura, altura);
            });
            butaoPonteiro.setOnMouseReleased(e -> {
                   butaoPonteiro.setStyle("-fx-background-color:" + cor + ";" + "-fx-background-radius: "+ raio +";"+ "-fx-text-fill: black;"+"-fx-font-size: " + tamanho_fonte + ";"); 
                 butaoPonteiro.setPrefSize(largura, altura);
            });
             
    } // fim do metodo setAcaoButao

    public void injetarAcaoButaoCriarEntrar(Controller.EventoAPDUs funcaoAltaOrdem, String NomeUsuario, Controller.FuncaoAltaOrdemInjetora funcaoInjetero) {
           
           butaoCriarEntrar.setOnAction((e) -> {
                  String texto1 = campoTextoNomeGrupo.getText(); String texto2 = NomeUsuario;
                  if(!((Boolean)funcaoInjetero.FuncaoAltaOrdemInjetora(texto1)) && !texto1.equals("")) {//verifica se grupo existe
                     javafx.scene.control.Button selecaoGrupo = new javafx.scene.control.Button();
                     if(funcaoAltaOrdem.enviarMensagemEvento(texto1, texto2)) {
                        configurarButaoGrupo(selecaoGrupo, texto1);
                        estabelecerAcao(selecaoGrupo);
                        caixaVerticalGrupos.getChildren().add(selecaoGrupo);
                     } else {
                        Aviso aviso = new Aviso();
                        aviso.mostrarAlerta("Erro ao conectar com o servidor");
                     }
                     
                  } else if(!texto1.equals("")) {
                     Aviso aviso = new Aviso();
                     aviso.mostrarAlerta("grupo ja inserido");
                  } else {
                      Aviso aviso = new Aviso();
                      aviso.mostrarAlerta("Preencha o campo de texto");
                  }
           });
    }
    
    public void adicionarUsuarioPrivadoNaInterfaceSelecaoGrupos(String nomeUsuario) {
           javafx.scene.control.Button selecaoGrupo = new javafx.scene.control.Button();
           configurarButaoGrupo(selecaoGrupo, nomeUsuario.substring(1));
           selecaoGrupo.setOnAction((e) -> {
                         telaPopUp.definirNomeGrupo(nomeUsuario);
                         butaoSelecionado = selecaoGrupo;
                         telaPopUp.estabelecerNomeConversaPrivada();
                         telaPopUp.estabelecerPonteiroButao(selecaoGrupo);
                         janelaBloqueio.estabelecerPonteiroButao(selecaoGrupo);
                         telaPopUp.estabelecerEstruturaVertical(caixaVerticalGrupos);
                         telaPopUp.mostrarJanela();
           });
            caixaVerticalGrupos.getChildren().add(selecaoGrupo);
    }

    public void injetarAcaoButaoEntrarNoGrupo(Controller.funcaoConfirmacao funcaoAltaOrdem) {
           telaPopUp.injetarAcaoConfirmacaoMensagemButaoEntrar(funcaoAltaOrdem);
    }
    
    public void injetarAcaoButaoSairTelaPopUp(Controller.EventoAPDUs funcaoAltaOrdem, String NomeUsuario) {
                telaPopUp.injetarAcaoButaoSair(funcaoAltaOrdem, NomeUsuario);
    }
    private void estabelecerAcao(javafx.scene.control.Button selecaoGrupo) {
             selecaoGrupo.setOnAction((e) -> {
                         telaPopUp.definirNomeGrupo(selecaoGrupo.getText());
                         butaoSelecionado = selecaoGrupo;
                         telaPopUp.estabelecerNomeConversaGrupo();
                         telaPopUp.estabelecerPonteiroButao(selecaoGrupo);
                         janelaBloqueio.estabelecerPonteiroButao(selecaoGrupo);
                         telaPopUp.estabelecerEstruturaVertical(caixaVerticalGrupos);
                         telaPopUp.mostrarJanela();
             });
    }
 
    private void configurarButaoGrupo(javafx.scene.control.Button selecaoGrupo, String nomeGrupo) {
              String cor = "#90EE90";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "0";
              double largura = telaGrupos.getWidth()-2; //esse -2 eh somente para ajustar o layout e evitar o scroll vertical
              double altura = 20; 
              String title = nomeGrupo;
              configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, 0, 0, largura, altura, title, selecaoGrupo);
              setAcaoButao(selecaoGrupo, "#90EE90", radius, largura, altura, tamanho_fonte);
      }
    
    public String obterTextoNomeGrupo() {
         if(campoTextoNomeGrupo == null) {
             throw new RuntimeException("campoTextoNomeGrupo esta nulo");     
         }
         return campoTextoNomeGrupo.getText();
    }
    /********************************************************************************************
     * Metodo: costumizar

     * Funcao: aplica estilo customizado a um botao
     * Parametros: propriedades visuais e botao alvo

     * Retorno: vazio
     ********************************************************************************************/     
    private void costumizar(String x, javafx.scene.control.Button k) {
             k.setStyle(
                      "-fx-background-color: " + x + ";" 
            );
    }//fim do metodo costumizar
    
    
   public void injetarAcaoButaoEntrarTelaPopUp(Controller.FuncaoAltaOrdemInjetora funcaoInjetero,  Controller.FuncaoAltaOrdemInjetora<Controller.HitoricoGrupo, String> W) {
                    telaPopUp.injetarAcaoButaoEntrar(funcaoInjetero, W);
   }
   
   public void injetarAcaoButaoBloquear(Controller.EventoAPDUSend funcaoInjetera, String NomeUsuario) {
                    janelaBloqueio.injetarAcaoButaoBloquear(funcaoInjetera, NomeUsuario);
   }
   
   public void injetarAcaoButaoDesbloquear(Controller.EventoAPDUSend funcaoInjetera, String NomeUsuario) {
                    janelaBloqueio.injetarAcaoButaoDesbloqueio(funcaoInjetera, NomeUsuario);
   }
   
   public void estabelecerJanelaBloqueio(JanelaBloqueio janelaBloqueio) {
              this.janelaBloqueio = janelaBloqueio;             
              //telaPopUp.estabelecerAcaoButaoBloquear(janelaBloqueio);
              butaoBloquear.setOnAction((e) -> {
                 janelaBloqueio.iniciarJanela();
              });
   }
   
   public void adicionarComponentesPainelEsquerdo(Node no) {
             painel.getChildren().add(no);
   }
}
