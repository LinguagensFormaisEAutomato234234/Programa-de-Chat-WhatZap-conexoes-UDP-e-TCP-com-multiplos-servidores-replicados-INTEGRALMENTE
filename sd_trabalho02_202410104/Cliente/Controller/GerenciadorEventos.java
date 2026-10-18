/************************************************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 05/09/2026 
* Ultima alteracao.: 05/09/2026 
* Nome.............: GerenciadorEventos
* Funcao...........: estabelcer um sistema logico central de cordenação de logica procedural segundo a eventos de interface.
**************************************************************************************************/

package Controller;
import Model.Cliente;
import Model.GerenciadorGrupos;
//essa classe tem como responsabilidade injetar a logica aos eventos de iteracao com os componentes graficos, mediante a interface controller. Classe ainda nao pronta
public class GerenciadorEventos {
      private Controller controller;
      private Cliente cliente;
     
      public GerenciadorEventos(Controller controller) {
              this.controller = controller;
      }
       
     /********************************************************************************************
      * Metodo: inciarConexao
      * Funcao: usando sockets, este metodo oferece uma interface ao controller para iniciar uma conexao com servidor, ambos concordando em receber e enviar pacotes entre si
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void inciarConexao(String NomeUsuario) {
              cliente = new Cliente(NomeUsuario);
              cliente.conectar();//permite gerar uma comunicacao com novos servidores em que estao presente pela subrede 
              cliente.gerenciarFluxoEntradaMensagens();//permite com que o cliente adote uma estrutura em que escuta o meio de comunicacao passivamente ate que receba uma mensagem
              cliente.RegistrarServidor(); //permite o registro desse cliente na estrutural geral de controle no servidor principal em que requer o servico
              estabelcePonteiroEdicaoCampoTexto();
      }//fim do metodo inciarConexao

       
      private void estabelcePonteiroEdicaoCampoTexto() {
              cliente.estabelecerRegistro(new Registro(controller));//define o ponteiro de controller
      }
      

     /********************************************************************************************
      * Metodo: eventoJuntarGrupo
      * Funcao: associa o evento enviar uma APDU do tipo join para o servidor, permtindo que este cria ou o insira num grupo existente, a um evento de cliacar no butao num layout grafico
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/     
      public void eventoJuntarGrupo() {
            
            EventoAPDUs APDUJOIN = (grupo, usuario) -> {
                 return cliente.JuntarGrupo(grupo, usuario);
            };
            FuncaoAltaOrdemInjetora<Boolean, String> funcaoInjetora = (grupo) -> {
                      return cliente.verificarConsistencia(grupo);      
            };
            controller.associarAcaoJoinComponteGrafico(APDUJOIN, funcaoInjetora);
      }//fim do metodo eventoJuntarGrupo
      
      public void RegistrarConversaPrivada() {
            FuncaoAltaOrdemInjetora<Boolean, String> funcaoInjetora = (nomeUsuario) -> {
                      cliente.registrarGrupo(nomeUsuario);
                      return true;      
            };
            controller.associarAcaoButaoOKConversaPrivada(funcaoInjetora);
      }
      
      
      /********************************************************************************************
      * Metodo: eventoBloquearUsuario
      * Funcao: associa um evento ao clicar no componente bloquear usuario
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/   
      public void eventoBloquearUsuario() {
            EventoAPDUSend APDUBlock = (grupo, usuarioSolicitante, UsuarioBloqueado, tipoMensagem) -> {
                        cliente.solicitarBloqueio(grupo, usuarioSolicitante, UsuarioBloqueado);
            };
            controller.associarAcaoBloquearComponteGrafico(APDUBlock);
      }//fim do metodo eventoBloquearUsuario()

      /********************************************************************************************
      * Metodo: eventodesBloquearUsuario
      * Funcao: associa um evento ao clicar no componente desbloquear usuario, solicitando uma apdu de desbloqueio de usuario
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void eventodesBloquearUsuario() {
            EventoAPDUSend APDUUNBLOCK= (grupo, usuarioSolicitante, UsuarioBloqueado, tipoMensagem) -> {
                       cliente.solicitarDesBloqueio(grupo, usuarioSolicitante, UsuarioBloqueado);
            };
            controller.associarAcaoDesBloquearComponteGrafico(APDUUNBLOCK);
      }//fim do metodo eventodesBloquearUsuario
      

      /********************************************************************************************
      * Metodo: eventoDeixarGrupo
      * Funcao: associa um evento ao clicar no componente deixar grupo, solicitando uma apdu para retirar um usuario de um grupo especificado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void eventoDeixarGrupo() {
            EventoAPDUs APDULEAVE = (grupo, usuario) -> {
              //  if(grupo )
                 return cliente.deixarGrupo(grupo, usuario);
            };
            controller.associarAcaoLeaveComponteGrafico(APDULEAVE);
      }//fim do metodo eventoDeixarGrupo
      

      /********************************************************************************************
      * Metodo: eventoEntrarGrupo
      * Funcao: associa um evento ao clicar no componente acesar grupo, solicitando uma apdu para um usuario se associar a um grupo especificado
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/
      public void eventoEntrarGrupo() {
            FuncaoAltaOrdemInjetora<Boolean, String> funcaoInjetora = (grupo) -> {
                    cliente.estabelecarGrupoAtual(grupo); //registra qual o grupo atual em que o usuario acessa
                    return true;
            };
            FuncaoAltaOrdemInjetora<HitoricoGrupo, String> W = (mensage) -> {
                                 if(cliente.obterGrupoAtual() == null) {
                                      return null;    
                                 } 
                                 if(!mensage.equals("")) {//assim nao permitimos que haja o envio de mensagem vazias
                                     cliente.receberMensagem(mensage);
                                 }
                                 return (cliente.obterGrupoAtual()).obterTextoHistoricoGrupo();
           };
            controller.associarAcaoAoButaoEntar(funcaoInjetora, W);
      }//fim do metodo eventoEntrarGrupo
      
      /********************************************************************************************
      * Metodo: eventoEnviarMensagem
      * Funcao: associar eventos relativos a envios de mensagem para grupos
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/ 
      public void eventoEnviarMensagem() {//associa a acao de enviar uma mensagem do tipo send a um butaoasd
           EventoAPDUSend APDUSEND = (grupo, usuario, mensagem, operacao) -> {
                        if(controller.obterComponentesJanela().verificarCaixaMarcada()) {
                          cliente.enviarMensagemGrupoVisualizacaoUnica(grupo, usuario, mensagem); //se a caixa estiver marcada, entao enviaremos a apdu de visualizacao unica
                        }
                        if(!controller.obterComponentesJanela().verificarCaixaMarcada()) cliente.enviarMensagemGrupo(grupo, usuario, mensagem); //caso contrario, enviaremos a mensagem de visualizacao comum
           };
           FuncaoAltaOrdemInjetora<HitoricoGrupo, String> funcaoInjetora = (mensage) -> {
                                 
                                 if(cliente.obterGrupoAtual() == null) {
                                      return null;    
                                 }
                                 if(!controller.obterComponentesJanela().verificarCaixaMarcada()) cliente.receberMensagem(mensage); //simplismente, levamos somente em consideracao o alocamento de mensagem comum
                                 if(controller.obterComponentesJanela().verificarCaixaMarcada()) cliente.receberMensagemVisualizacaoUnica(mensage);
                                 return (cliente.obterGrupoAtual()).obterTextoHistoricoGrupo(); //obtem uma referencia para o historico do grupo associado
           };
          
           
           FuncaoAltaOrdemInjetora<String, String> funcaoInjetoraNomeGrupo = (mensage) -> {
                                 if(cliente.obterGrupoAtual() == null) {
                                      return null;    
                                 }
                                 return (cliente.obterGrupoAtual()).obterNomeGrupo();
           };
           
           controller.estabelecerAcaoButaoEnviar(funcaoInjetora, funcaoInjetoraNomeGrupo, APDUSEND);
      }
      
      /********************************************************************************************
      * Metodo: eventoSolicitarListagemUsuariosOnline
      * Funcao: associar eventos relativos a solicitacao de listagem de usuarios online para o servidor 
      * Parametros: vazio
      * Retorno: vazio
      ********************************************************************************************/ 
      public void eventoSolicitarListagemUsuariosOnline() {
            EventoAPDUList funcaoSolicitarListagemUsuariosOnline = (grupo, usuario) -> {
                return cliente.listarUsuariosOnline(grupo, usuario);
            };
            controller.associarAcaoButaoConversaPrivada(funcaoSolicitarListagemUsuariosOnline);
      }//fim do metodo eventoSolicitarListagemUsuariosOnline

      public void associarAcaoButaoEntrar() {
           funcaoConfirmacao funcaoConfirmacaoMensagem = () -> {
                        cliente.enviarConfirmacaoMensagem();
           };
          controller.associarAcaoButaoEntrar(funcaoConfirmacaoMensagem);
      }
}      
