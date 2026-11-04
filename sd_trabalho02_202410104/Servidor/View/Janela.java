/************************************************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 10/06/2026 (entre 22 a 26)
* Ultima alteracao.: 10/06/2026 (entre 22 a 30)
* Nome.............: Janela
* Funcao...........: Estabelecer o alocamento de componentes de butões na tela de interação com o usuário.
**************************************************************************************************/

package View;

//Importações necessarias para a implementaçao funcional
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.application.Platform;

public class Janela {
    private Stage stage; 
    private double larguraJanela = 400;
    private double alturaJanela = 496; 
    private Pane janelaRaiz;
    private CaixaTexto caixaTexto;
     /********************************************************************************************
     * Metodo: Construtor da classe Janela

     * Funcao: Instancia e inicializa o alocamento de componentes na tela
     * Parametros: stage - instancia da classe Stage, fundamental para estabelecer o node
     *             raiz da interface  
     * Retorno: retorna a instância da classe  

     ********************************************************************************************/
    public Janela(Stage stage) {
          this.stage = stage;
          configurarSceneRaiz(); 
    }//fim construtor Janela
  
    /********************************************************************************************
    * Metodo: configurarSceneRaiz
    * Funcao: estableece a cena principal e carrega todos os componentes de iteracao da tela, chamando as fucoes auxiliares como:

    *          ->  configurarJanelaPrincipal()
    *          
    * Parametros: vazio  

    * Retorno: vazio  
    ********************************************************************************************/
    public void configurarSceneRaiz() { 
        janelaRaiz = new Pane(); 
        janelaRaiz.setStyle("-fx-background-color: GREEN;");
        gerenciarModulosGraficos();
        Scene scene = new Scene(janelaRaiz, larguraJanela, alturaJanela); //define o pane "janelaRaiz" como nodo grafico raiz da interface 
        stage.setScene(scene);  
        stage.setResizable(false);
        stage.show();
        stage.setTitle("Log do servidor");
        
    } //fim do metodo configurarSceneRaiz
    
    
    /********************************************************************************************
    * Metodo: gerenciarModulosGraficos
    * Funcao: repare que, nesse projeto, cada esturuta da interface represente um modulo grafico para iteracao.
              Sao eles: gerenciarPainelEsquerdo -> mostra ao utilizador o grupos em que pode se conectar, gerenciarPainelDireito ->
              mostra ao utilizador um historico grafico das conversas e oferece uma interface de envio de mensagem para a APDU do tipo 
              send  
    * Parametros: vazio  
    * Retorno: vazio  
    ********************************************************************************************/
    public void gerenciarModulosGraficos() {
            caixaTexto = new  CaixaTexto();
            alocarComponentes();  
    }
    /********************************************************************************************
    * Metodo: alocarComponentes
    * Funcao: simplesmente alocar componentes graficos na tela
    * Parametros: vazio  
    * Retorno: vazio  
    ********************************************************************************************/
    private void alocarComponentes() {
            janelaRaiz.getChildren().add(caixaTexto.getPainel());       
    }

    public void alocarMensagem(String registroLog) {
        Platform.runLater(() -> {
            caixaTexto.alocarMensagemNoPainel(registroLog);
        });
    }
    
}
