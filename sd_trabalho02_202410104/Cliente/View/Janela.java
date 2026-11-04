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
import javafx.scene.layout.VBox;

public class Janela {
    private Stage stage; 
    private double larguraJanela = 1000;
    private double alturaJanela = 650; 
    private Pane janelaRaiz;
    private Pane painelEsquerdo;
    private Pane painelDireito;
    private JanelaLogin janelaLongin;
    private EditarPainelEsquerdo editarPainelEsquerdo;
    private EditarPainelDireito editarPainelDireito;
    private Controller.SelecaoMensagem selecaoMensagem;
    private JanelaBloqueio janelaBloqueio;
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
        stage.setTitle("Whatszap da deep weeb");
        
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
            alocarPrimeiraTela(); //aloca a tela de login para a janela de inicio
            gerenciarPainelDireito();
            gerenciarPainelEsquerdo();
            alocarComponentes();  
    }//fim do metodo gerenciarModulosGraficos
    
    /********************************************************************************************
    * Metodo: alocarComponentes
    * Funcao: simplesmente alocar componentes graficos na tela
    * Parametros: vazio  
    * Retorno: vazio  
    ********************************************************************************************/
    private void alocarComponentes() {
            janelaRaiz.getChildren().add(painelEsquerdo);
            janelaRaiz.getChildren().add(painelDireito);           
    }//fim do metodo alocarComponentes
    /*****************************************************************************************************************************
    * Metodo: alocarPrimeiraTela
    * Funcao: nesse caso, teremos uma tela de longin em que pede para o usuraio implementar as informacoes referentes aos grupo conjuntamente com seu endereço IP. Para isso instanciamos uma abstracao dessa tela, a tela de longin
    * Parametros: vazio
    * Retorno: vazio
    *******************************************************************************************************************************/
    private void alocarPrimeiraTela() {
           janelaLongin = new JanelaLogin(janelaRaiz); //instanciamos a tela de longin
           janelaLongin.iniciarJanela();
    }//fim do metodo alocarPrimeiraTela
    
    public void adicionarConversaPrivadaPainelEsquerdo(String nomeUsuario) {
         Platform.runLater(() -> {
            editarPainelEsquerdo.adicionarUsuarioPrivadoNaInterfaceSelecaoGrupos(nomeUsuario);
         });
    }
    
    public String obterInformacaoTextFieldIP() {
          return janelaLongin.obterInformacaoEntrada();
    }
    
    public String obterInformacaoTextFieldNome() {
        return janelaLongin.obterInformacaoEntradaNome();
    }
    
    /********************************************************************************************
    * Metodo: gerenciarPainelEsquerdo
    * Funcao: simplismente, controla a estrutura de interface do painel esquerdo em que o usuario iteragira         
    * Parametros: vazio  
    * Retorno: vazio  
    ********************************************************************************************/
    public void gerenciarPainelEsquerdo() {
        this.painelEsquerdo = new Pane();
        this.janelaBloqueio = new JanelaBloqueio();
        editarPainelEsquerdo = new EditarPainelEsquerdo(painelEsquerdo, caixaTexto, editarPainelDireito, selecaoMensagem);
        editarPainelEsquerdo.gerenciarModulosGraficos();
        editarPainelEsquerdo.estabelecerJanelaBloqueio(janelaBloqueio);
    }

    public void assciarAcaoButaoCriarEntrar(Controller.EventoAPDUs funcaoAltaOrdem, Controller.FuncaoAltaOrdemInjetora funcaoInjetero) {
                 if(painelEsquerdo == null) {
                     throw new RuntimeException("campoTextoNomeGrupo esta nulo");                    
                 } 
                 editarPainelEsquerdo.injetarAcaoButaoCriarEntrar(funcaoAltaOrdem, obterInformacaoTextFieldNome(), funcaoInjetero);
    }
    /********************************************************************************************
    * Metodo: gerenciarPainelDireito
    * Funcao: simplismente, controla a estrutura de interface do painel esquerdo em que o usuario iteragira         
    * Parametros: vazio  
    * Retorno: vazio  
    ********************************************************************************************/
    public void gerenciarPainelDireito() {
         this.painelDireito = new Pane();
         this.caixaTexto = new CaixaTexto(778, 507);
         editarPainelDireito = new EditarPainelDireito(painelDireito, caixaTexto);
         editarPainelDireito.gerenciarModulosGraficos();
    }
    

    /********************************************************************************************
    * Metodo: injetarAcaoButaoEntrarTelaPopUp
    * Funcao: simplismente, estabelece um evento com o componente grafico associado a acessar grupo         
    * Parametros: (Controller.FuncaoAltaOrdemInjetora : funcaoInjetero), (Controller.FuncaoAltaOrdemInjetora<String, String> : W)
    * Retorno: vazio  
    ********************************************************************************************/
    public void injetarAcaoButaoEntrarTelaPopUp(Controller.FuncaoAltaOrdemInjetora funcaoInjetero, Controller.FuncaoAltaOrdemInjetora<Controller.HitoricoGrupo, String> W) {
            editarPainelEsquerdo.injetarAcaoButaoEntrarTelaPopUp(funcaoInjetero, W);
    }//fim do metodo injetarAcaoButaoEntrarTelaPopUp
    
    public void adicionarComponentePainelDireito(Node no) {
           editarPainelDireito.adicionarComponentePainelDireito(no);
    }
    
    public void adicionarComponentePainelEsquerdo(Node no) {
           editarPainelEsquerdo.adicionarComponentesPainelEsquerdo(no);
    }
    
    public void estabelecerAcaoButaoEnviar(Controller.FuncaoAltaOrdemInjetora funcaoInjetero, Controller.FuncaoAltaOrdemInjetora funcaoInjetoraNomeGrupo, Controller.EventoAPDUSend APDUSEND) {
            editarPainelDireito.estabelecerAcaoButaoEnviar(funcaoInjetero, APDUSEND, funcaoInjetoraNomeGrupo, obterInformacaoTextFieldNome());
    }
    
    public void associarAcaoButaoDeixarGrupo(Controller.EventoAPDUs funcaoAltaOrdem) {
            editarPainelEsquerdo.injetarAcaoButaoSairTelaPopUp(funcaoAltaOrdem, janelaLongin.obterInformacaoEntradaNome());
    }
    public void associarAcaoButaoBloquear(Controller.EventoAPDUSend funcaoAltaOrdem) {
            editarPainelEsquerdo.injetarAcaoButaoBloquear(funcaoAltaOrdem, janelaLongin.obterInformacaoEntradaNome());
    }
    
    public void associarAcaoButaoDesbloquear(Controller.EventoAPDUSend funcaoAltaOrdem) {
            editarPainelEsquerdo.injetarAcaoButaoDesbloquear(funcaoAltaOrdem, janelaLongin.obterInformacaoEntradaNome());
    }
    
    public void associarAcaoButaoEntrar(Controller.funcaoConfirmacao funcaoAltaOrdem) {
            editarPainelEsquerdo.injetarAcaoButaoEntrarNoGrupo(funcaoAltaOrdem);
    }
    
    public void editarCampoTexto(VBox texto) {
       Platform.runLater(() -> {
          if(caixaTexto != null) { 
             caixaTexto.alocarMensagemNoPainel(texto);
          } else {
            System.out.println("erro, caixa de texto nula");
          }
       });    
    }
    
}
