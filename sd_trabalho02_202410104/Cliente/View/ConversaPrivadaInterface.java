package View;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;

public class ConversaPrivadaInterface {
     private Stage popUpStage; //stage principal de alocamento da tela
     private CaixaTexto listaUsuariosOnline;
     private Button butaoSelecionarGrupoPrivado;
     private Janela ponteiroJanela;
     private ListView<String> listaView;
     private static final double largura = 260;
     private static final double altura = 200;
     private Button butaoOKParaSelecionarUsuarioPrivado;
     private Controller.FuncaoAltaOrdemInjetora funcaoAltaOrdemRegistrarUsuario;
     private Pane janelaSelecionarUsuariosPrivados; //define a janela de longin em que aparecera
     public ConversaPrivadaInterface(Janela ponteiroJanela) {
         this.popUpStage = new Stage();
         this.butaoSelecionarGrupoPrivado = new Button();
         this.butaoOKParaSelecionarUsuarioPrivado = new Button();
         this.listaView = new ListView<>();
         this.ponteiroJanela = ponteiroJanela;
         configurarComponentes(); 
     }
     
     private void configurarComponentes() {
       new ConfigurarButao(butaoSelecionarGrupoPrivado, "conversa privada", 10, 494+67, 120, 20).editarconfigurarButao();//permite editar a estrutura estetica do butao
       this.listaUsuariosOnline = new CaixaTexto((int)largura/2, (int)altura/2);
       new ConfigurarButao(butaoOKParaSelecionarUsuarioPrivado, "Ok", 70, (altura-150)/2+altura/2+42, 120, 20).editarconfigurarButao();//permite editar a estrutura estetica do butao
       configurarJanelaSelecionarUsuariosPrivados();
       adicionarButaoTela();
     }
     
     /********************************************************************************************
      * Metodo: configurarJanela
      * Funcao: estabelecer uma interface de comunicacao com o usuario de maneira em que este possa fornecer informacoes essenciais para 
                realizar o procedimento de longin
      * Parametros: nenhum
      * Retorno: vazio
      ********************************************************************************************/ 
      private void configurarJanelaSelecionarUsuariosPrivados() {
              this.janelaSelecionarUsuariosPrivados = new Pane();
              this.janelaSelecionarUsuariosPrivados.setStyle("-fx-background-color: #FFDBBB;");
              this.listaView.setLayoutX(10);
              this.listaView.setLayoutY(10);
              this.listaView.setPrefWidth(240);
              this.listaView.setPrefHeight(150);
              this.popUpStage.initModality(Modality.APPLICATION_MODAL);
              Scene scene = new Scene(janelaSelecionarUsuariosPrivados, largura, altura);
              this.popUpStage.setScene(scene);
              this.popUpStage.setResizable(false);
              this.popUpStage.setTitle("Usuarios Online"); 
              //permite encerrar o processo associado ao fechar a janela do envento
              this.janelaSelecionarUsuariosPrivados.getChildren().add(listaView);
              this.popUpStage.setOnCloseRequest(t -> {
                   this.popUpStage.close();
              });
              
      }//fim do metodo configurarJanela
      
      /********************************************************************************************
      * Metodo: AcaoButaoSelecionarUsuario
      * Funcao: estabelece uma acao reativo ao evento de clique na interface
      * Parametros: (EventoAPDUList : funcaoSolicitarListagemUsuariosOnline)
      * Retorno: vazio
      ********************************************************************************************/
      public void AcaoButaoSelecionarUsuario(Controller.EventoAPDUList funcaoSolicitarListagemUsuariosOnline) {
           butaoSelecionarGrupoPrivado.addEventHandler(ActionEvent.ACTION, e -> {
               String lista = funcaoSolicitarListagemUsuariosOnline.funcaoExcutora(null, ponteiroJanela.obterInformacaoTextFieldNome());
               lista = lista.substring(4);
               //System.out.println(lista);
               String[] listaNomes = lista.split(",");
               for(int i = 0; i < listaNomes.length; i++) {
                   if(ponteiroJanela.obterInformacaoTextFieldNome() != listaNomes[i]) listaView.getItems().add(listaNomes[i]);
               }
           });
           
           butaoSelecionarGrupoPrivado.addEventHandler(ActionEvent.ACTION, e -> {
                 popUpStage.showAndWait();
                 listaView.getItems().clear();
           });
      }//fim do metodo AcaoButaoSelecionarUsuario
      
      private void adicionarButaoTela() {
            // ponteiroJanela.adicionarComponentePainelEsquerdo(butaoSelecionarGrupoPrivado);
             //janelaSelecionarUsuariosPrivados.getChildren().add(butaoOKParaSelecionarUsuarioPrivado);
      }
      
      public void estabelecerFuncaoAltaOrdem(Controller.FuncaoAltaOrdemInjetora funcaoAltaOrdemRegistrarUsuario) {
           butaoOKParaSelecionarUsuarioPrivado.addEventHandler(ActionEvent.ACTION, e -> {
                     String selecionado = listaView.getSelectionModel().getSelectedItem(); //captura o item que esta selecionado no listView
                     if(selecionado != null && !selecionado.equals("")) { 
                        this.popUpStage.close();
                        selecionado = "@" + selecionado;
                        funcaoAltaOrdemRegistrarUsuario.FuncaoAltaOrdemInjetora(selecionado); 
                        ponteiroJanela.adicionarConversaPrivadaPainelEsquerdo(selecionado);
                     }
           });
      }
}
