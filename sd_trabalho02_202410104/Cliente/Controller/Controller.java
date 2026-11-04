/************************************************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 03/09/2026 
* Ultima alteracao.: 05/09/2026 
* Nome.............: Controller
* Funcao...........: estabelcer um sistema logico central de cordenação de logica procedural segundo a eventos de interface.
**************************************************************************************************/

package Controller;

public class Controller {
      private View.Janela janela;
      private ComponentesJanela coponentes;
      private View.ConversaPrivadaInterface componenteGraficoConversaPrivada;
      private GerenciadorEventos gerenciadorEventos;
      public Controller(View.Janela janela) {
             this.janela = janela;
             componenteGraficoConversaPrivada = new View.ConversaPrivadaInterface(janela);//permite estabelecer a interface para conversas privadas
             coponentes = new ComponentesJanela(janela);
             coponentes.adicionarComponentes();//aqui adicionamos componentes na tela de visualizacao com seus respetvos eventos associados
             gerenciadorEventos = new GerenciadorEventos(this);
      }
      
     /********************************************************************************************
      * Metodo: inciar
      * Funcao: define os eventos bascios de interface associado, como iniciar uma conexao, juntar a um grupo, enviar uma mensagem, dexar grupo, bloquear e desbloqeuar usuario
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void iniciar() {
         gerenciadorEventos.inciarConexao(janela.obterInformacaoTextFieldNome());   
         gerenciadorEventos.eventoJuntarGrupo();    
         gerenciadorEventos.eventoEntrarGrupo();
         gerenciadorEventos.eventoEnviarMensagem();
         gerenciadorEventos.eventoDeixarGrupo();
         gerenciadorEventos.eventoBloquearUsuario();
         gerenciadorEventos.eventodesBloquearUsuario();
         gerenciadorEventos.associarAcaoButaoEntrar();
         gerenciadorEventos.eventoSolicitarListagemUsuariosOnline();
         gerenciadorEventos.RegistrarConversaPrivada();
      }//fim do metodo iniciar
      
      
      /********************************************************************************************
      * Metodo: associarAcaoJoinComponteGrafico
      * Funcao: associa eventos referente a manifestacao da apdu join de um grupo ao componente de evento associado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void associarAcaoJoinComponteGrafico(EventoAPDUs funcaoAltaOrdem, FuncaoAltaOrdemInjetora funcaoInjetora) {
             janela.assciarAcaoButaoCriarEntrar(funcaoAltaOrdem, funcaoInjetora);
      }//fim do metodo associarAcaoJoinComponteGrafico
      
      
      /********************************************************************************************
      * Metodo: associarAcaoAoButaoEntar
      * Funcao: definir um evento em que ocorre quando o botao de acessar grupo eh disparado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void associarAcaoAoButaoEntar(FuncaoAltaOrdemInjetora funcaoInjetora, FuncaoAltaOrdemInjetora<HitoricoGrupo, String> W) {
             janela.injetarAcaoButaoEntrarTelaPopUp(funcaoInjetora, W);
      }//fim do metodo associarAcaoAoButaoEntar
      
      /********************************************************************************************
      * Metodo: estabelecerAcaoButaoEnviar
      * Funcao: associa eventos referente a manifestacao da apdu enviar de um grupo ao componente de evento associado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void estabelecerAcaoButaoEnviar(FuncaoAltaOrdemInjetora funcaoInjetero, FuncaoAltaOrdemInjetora<String, String> funcaoInjetoraNomeGrupo, EventoAPDUSend APDUSEND) {
            janela.estabelecerAcaoButaoEnviar(funcaoInjetero, funcaoInjetoraNomeGrupo, APDUSEND);
      }//fim do metodo estabelecerAcaoButaoEnviar
      
      /********************************************************************************************
      * Metodo: associarAcaoBloquearComponteGrafico
      * Funcao: associa eventos referente a manifestacao da apdu bloquear usuario de um grupo ao componente de evento associado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void associarAcaoBloquearComponteGrafico(EventoAPDUSend funcaoAltaOrdem) {
            janela.associarAcaoButaoBloquear(funcaoAltaOrdem);
      }//fim do metodo associarAcaoBloquearComponteGrafico
      
      public void associarAcaoDesBloquearComponteGrafico(EventoAPDUSend funcaoAltaOrdem) {
           janela.associarAcaoButaoDesbloquear(funcaoAltaOrdem);
      }
      
      public void associarAcaoRegistrarUsuarioPrivado(EventoAPDUSend funcaoAltaOrdem) {
           janela.associarAcaoButaoDesbloquear(funcaoAltaOrdem);
      }
      
      /********************************************************************************************
      * Metodo: associarAcaoButaoConversaPrivada
      * Funcao: associa eventos referente a manifestacao da apdu List ao butao de solicitacao de uma conversa privada
      * Parametros: (EventoAPDUSend : funcaoAltaOrdem)
      * Retorno: vazio
      ********************************************************************************************/
      public void associarAcaoButaoConversaPrivada(EventoAPDUList funcaoAltaOrdemListarUsuario) {
            //janela.associarAcaoButaoBloquear(funcaoAltaOrdem);
            componenteGraficoConversaPrivada.AcaoButaoSelecionarUsuario(funcaoAltaOrdemListarUsuario);
      }//fim do metodo associarAcaoBloquearComponteGrafico
      
      public void editarCampoTexto(HitoricoGrupo mensagem) {
            janela.editarCampoTexto(mensagem.obterTextoVerticalView().obterTextHbox());
      }
      public void associarAcaoLeaveComponteGrafico(EventoAPDUs funcaoAltaOrdem) {
            janela.associarAcaoButaoDeixarGrupo(funcaoAltaOrdem);
      }

      public void associarAcaoButaoEntrar(funcaoConfirmacao funcaoConfirmacaoMensagem) {
            janela.associarAcaoButaoEntrar(funcaoConfirmacaoMensagem);
      }
      
      public void associarAcaoButaoOKConversaPrivada(FuncaoAltaOrdemInjetora<Boolean, String> funcaoInjetora) {
          componenteGraficoConversaPrivada.estabelecerFuncaoAltaOrdem(funcaoInjetora);
      }
      
      
      /********************************************************************************************
      * Metodo: adicionarConversaPrivadaInterface
      * Funcao: associa o registro de uma nova conversa privada na omterfae, serve para registrar uma nova conversa automatica de um usuario em que manda uma mensagem privada 
      * Parametros: (EventoAPDUSend : funcaoAltaOrdem)
      * Retorno: vazio
      ********************************************************************************************/
      public void adicionarConversaPrivadaInterface(String nome) {
            janela.adicionarConversaPrivadaPainelEsquerdo(nome);
      }
      
      public ComponentesJanela obterComponentesJanela() {
             return coponentes;
      }
}//fim da classe 
