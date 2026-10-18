/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 31/08/2026
*Ultima alteracao.: 04/08/2026
*Nome.............: Cliente
*Funcao...........: definir uma interface em que permite aaplicar todas as ações 
                    representativa da comunicação da arquitertura cliente servidor.
**************************************************************************************************/

package Model;
import java.util.concurrent.Semaphore;
import Protocol.APDU;

public class Cliente {
      private ClienteUDP clienteUDP;
      private ClienteTCP clienteTCP;
      private APDU apdu;
      private String enderecoIP, nome;
      private Fila<APDU> filaCompartilhada;
      private Semaphore mutex;
      private Semaphore cheio;
      private int portaServidorUDP, portaServidorTCP;
      private PropagarRequisicaoComunicacaoBroadcast broadcast;
      private GerenciadorGrupos gerenciadorGrupos;
      
      public Cliente(String nome) {
          this.nome = nome;
          this.broadcast = new PropagarRequisicaoComunicacaoBroadcast();
          this.mutex = new Semaphore(1);
          this.cheio = new Semaphore(0);
          this.filaCompartilhada = new Fila<APDU>(); //recebe um conjunto de apdu do servidor e a registra na fila 
          this.gerenciadorGrupos = new GerenciadorGrupos(filaCompartilhada, mutex, cheio, this);
          this.clienteUDP = new ClienteUDP(mutex, cheio, filaCompartilhada);
          this.clienteTCP = new ClienteTCP(mutex, cheio, filaCompartilhada); 
      }
      
      /********************************************************************************************
      * Metodo: conectar
      * Funcao: definir uma comunicação utlizando os protocolos de comunicação TCP e UDP da camada de transporte. 
      * Parametros: Vazio
      * Retorno: Vazio
      ********************************************************************************************/
      public void conectar() {
            broadcast.descobrirServidor();
            portaServidorUDP = broadcast.obterPortaUDP();
            portaServidorTCP = broadcast.obterPortaTCP();
            enderecoIP = broadcast.obterEnderecoIP();
            if(enderecoIP == null) throw new RuntimeException("Erro ao encontrar o endereco Ip no servidor, (50, class: cliente)");
            clienteUDP.gerenciarConexao(portaServidorUDP, enderecoIP); //inicializa o soquete UDP associado a porta de operacao do processo e define o endereco IP e a porta do servidor em que esta escutando
            clienteTCP.iniciarConexaoClienteTCP(portaServidorTCP, enderecoIP);
      }
      
      
      /********************************************************************************************
      * Metodo: gerenciarFluxoEntradaMensagens
      * Funcao: a estrutura de comunicacao que gera uma estrutura logica em que permite escutar o meio, tratando diferentes mensagens chegada pela rede de comunicacao 
      * Parametros: Vazio
      * Retorno: Vazio
      ********************************************************************************************/
      public void gerenciarFluxoEntradaMensagens() {
             clienteUDP.gerenciarFluxoEntrada();
             gerenciadorGrupos.gerenciarMensagens();//essa funcao segue o paradgma de orientacao a eventos, permitindo com que tenhamos uma thread em que consome intes de uma fila compartilhada entre a thread produtora, qualquer linha de execucao em que receba uma mensagem e a aloque na fila.
      }
      
      /********************************************************************************************
      * Metodo: RegistrarServidor
      * Funcao: estabelece uma APDU especial em que solicitara o registro deste referido usuario relativo a sua estrutura de dados interna de registros  
      * Parametros: Vazio
      * Retorno: Vazio
      ********************************************************************************************/
      public void RegistrarServidor() {
           clienteTCP.iniciarConexao(nome); 
      }
      
      /********************************************************************************************
      * Metodo: JuntarGrupo
      * Funcao: definir um padrão para que o usuario se junte a um grupo solicitado 
      * Parametros: String : grupo, String : usuario
      * Retorno: Bolena = {TRUE, FALSE}
      ********************************************************************************************/
      public boolean JuntarGrupo(String grupo, String usuario) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.JOIN);
           APDU mensagemAPDUJuntarGrupo = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuario, null, clienteUDP.portaUDP());
           if(clienteTCP.SEND(mensagemAPDUJuntarGrupo)) {
               registrarGrupo(grupo);
               return true;
           };
           return false;
      }//fim do metodo JuntarGrupo
      
      
      
      /********************************************************************************************
      * Metodo: deixarGrupo
      * Funcao: definir um padrão para que o usuario se retire de um grupo solicitado 
      * Parametros: String : grupo, String : usuario
      * Retorno: Bolena = {TRUE, FALSE}
      ********************************************************************************************/
      public boolean deixarGrupo(String grupo, String usuario) {
             TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.LEAVE);
            APDU mensagemAPDUDEIXARGrupo = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuario, null, clienteTCP.portaTCP());
            if(clienteTCP.SEND(mensagemAPDUDEIXARGrupo)) {
                 excluirGrupo(grupo);
                return true;
            }
            return false;
      }//fim do metodo deixarGrupo 
      
      
      /********************************************************************************************
      * Metodo: enviarMensagemGrupo
      * Funcao: define um apdu em que sera enviada para o servidor via rede solicitando para distribuir sua mensagem a sotodos os membros do grupo 
      * Parametros: String : grupo, String : usuario, String : mensagem
      * Retorno: Vazio
      ********************************************************************************************/
      public void enviarMensagemGrupo(String grupo, String usuario, String mensagem) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.SEND);          
           APDU mensagemAPDUEnviar = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuario, mensagem, clienteUDP.portaUDP());
           gerenciadorGrupos.alocarMensagemPendente(mensagemAPDUEnviar);
           clienteUDP.SEND(mensagemAPDUEnviar, enderecoIP, portaServidorUDP);
           
      }//fim do evento enviarMensagemGrupo
      
      /********************************************************************************************
      * Metodo: enviarMensagemUsuarioPrivado
      * Funcao: define uma apdu em que sera enviada para o servidor via rede solicitando para distribuir sua mensagem somente ao referido usuario da conversa 
      * Parametros: String : grupo, String : usuario, String : mensagem
      * Retorno: Vazio
      ********************************************************************************************/
      public void enviarMensagemPrivada(String usuarioOrigem, String usuarioDestino, String mensagem) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.SENDPVT);          
           APDU mensagemAPDUEnviar = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), null, usuarioOrigem, mensagem, clienteUDP.portaUDP(), usuarioDestino);
           gerenciadorGrupos.alocarMensagemPendente(mensagemAPDUEnviar);//registra a mensagem pendente de confirmacao durante o procedimento de envio da mensagem
           clienteUDP.SEND(mensagemAPDUEnviar, enderecoIP, portaServidorUDP);
      }//fim do evento enviarMensagemGrupo

     /********************************************************************************************
      * Metodo: solicitarConfirmacaoMensagem
      * Funcao: define um apdu em que sera enviada para o servidor via rede solicitando para registrar uma mensagem pendente na estrutura de dados do servidor 
      * Parametros: String : grupo, String : usuario, String : mensagem
      * Retorno: Vazio
      ********************************************************************************************/
      public void confirmacaoMensagem(String usuario, int tick, APDU apdu, String nomeGrupo) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.CONFIRM);
           APDU mensagemConfirm = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), apdu.getIdMensagem(), tick, apdu.getDestinatario(), nomeGrupo, apdu.getNomeUsuario());
           clienteUDP.SEND(mensagemConfirm, enderecoIP, portaServidorUDP);
      }//fim do evento enviarMensagemGrupo
      
      /********************************************************************************************
      * Metodo: listarUsuariosOnline
      * Funcao: define um apdu em que sera enviada para o servidor via rede solicitando para retornar a lista de usuarios online
      * Parametros: String : grupo, String : usuario, String : mensagem
      * Retorno: Vazio
      ********************************************************************************************/
      public String listarUsuariosOnline(String nomeGrupo, String usuario) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.Users);
           APDU mensagemList = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), nomeGrupo, usuario, null, clienteUDP.portaUDP());
           return clienteTCP.enviarMensagemComRespostaServidor(mensagemList);
      }//fim do evento enviarMensagemGrupo
 
      
      /********************************************************************************************
      * Metodo: enviarMensagemGrupoVisualizacaoUnica
      * Funcao: define um apdu em que sera enviada para o servidor via rede solicitando para distribuir sua mensagem a sotodos os membros do grupo, com o padrao de visualizacao unica 
      * Parametros: String : grupo, String : usuario, String : mensagem
      * Retorno: Vazio
      ********************************************************************************************/
      public void enviarMensagemGrupoVisualizacaoUnica(String grupo, String usuario, String mensagem) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.SENDUV);
           APDU mensagemAPDUEnviar = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuario, mensagem, clienteUDP.portaUDP(), true);
           gerenciadorGrupos.alocarMensagemPendente(mensagemAPDUEnviar);
           
           clienteUDP.SEND(mensagemAPDUEnviar, enderecoIP, portaServidorUDP);
      }//fim do metodo enviarMensagemGrupoVisualizacaoUnica
      
      /********************************************************************************************
      * Metodo: solicitarBloqueio
      * Funcao: define um padrao de comunicacao de uma mensagem enviada ao servidor solicitando o bloqueio de um determinado usuario 
      * Parametros: String : grupo, String : usuario, String : mensagem, String : usuarioSolicitante
      * Retorno: Vazio
      ********************************************************************************************/
      public void solicitarBloqueio(String grupo, String usuarioSolicitante, String usuarioBloqueado) {
           TraduzirAPDU.TipoAPDU TIPO = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.Block);
           APDU mensagemAPDUEnviar = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuarioSolicitante, null, clienteTCP.portaTCP(), usuarioBloqueado);
           clienteTCP.SEND(mensagemAPDUEnviar);
      }//fim do metodo solicitarBloqueio
      
      public void solicitarDesBloqueio(String grupo, String usuarioSolicitante, String usuarioDesbloqueado) {
           TraduzirAPDU.TipoAPDU TIPO  = new TraduzirAPDU.TipoAPDU(TraduzirAPDU.TipoAPDU.UNBLOCK);
           APDU mensagemAPDUEnviar = new APDU(new TraduzirAPDU().obterTipoAPDU(TIPO), grupo, usuarioSolicitante, null, clienteTCP.portaTCP(), usuarioDesbloqueado);
           clienteTCP.SEND(mensagemAPDUEnviar);
      }//fim do metodo solicitarDesBloqueio
      
      public void estabelecerRegistro(Controller.Registro registro) {
             gerenciadorGrupos.estabelecerRegistro(registro); //recebe uma mensagem do servidor de apdu do tipo send e a registra no historico de conversa do grupo
      }
      
      
      public void registrarGrupo(String grupo) {
                gerenciadorGrupos.inserir(grupo);
      }
      
      public void excluirGrupo(String grupo) {
               gerenciadorGrupos.excluirGrupo(grupo);
      }
      
      public Boolean verificarConsistencia(String nome) {
             return gerenciadorGrupos.verificarConsistencia(nome);
      }
      
      public void estabelecarGrupoAtual(String nome) {
               gerenciadorGrupos.estabelecerGrupoAtual(nome);
      }
      
      public void enviarMensagem(APDU apdu) throws Exception { //alteramos posteriormente para usar servicos do protocolo TCp 
           clienteTCP.SEND(apdu); 
      }
      //fazer uma sobrecarga quando a mensagem vier do servidor
      public void receberMensagem(String mensagem) { //alteramos posteriormente para usar servicos do protocolo TCP
           gerenciadorGrupos.registrarMensagem(mensagem);
      }
      
      public void receberMensagemVisualizacaoUnica(String mensagem) { //alteramos posteriormente para usar servicos do protocolo TCP
           gerenciadorGrupos.registrarMensagemUnica(mensagem);
      }
      
      public Grupo obterGrupoAtual() { //alteramos posteriormente para usar servicos do protocolo TCP
           return gerenciadorGrupos.obterGrupoAtual();
      }
      
      /********************************************************************************************
      * Metodo: retornarAPDUAtual
      * Funcao: estabelece qual a apdu atual associada a mensagem mais recentemente utilizada 
      * Parametros: vazio
      * Retorno: Vazio
      ********************************************************************************************/
      public APDU retornarAPDUAtual() {
            return apdu;
      }

      public void enviarConfirmacaoMensagem() {
           gerenciadorGrupos.confirmarMensagem(gerenciadorGrupos.obterGrupoAtual());
      }
      
      public void alterarPortaTCP(int clienteTCP) {
         this.portaServidorTCP = clienteTCP;
      }
      
      public void alterarPortaUDP(int clienteUDP) {
         this.portaServidorUDP = clienteUDP;
      }
      
      public void alterarEnderecoIP(String enderecoIP) {
         this.enderecoIP = enderecoIP;
      }
      
      public void novaRequisicaoBroadCast() {
            broadcast.descobrirServidor();
            portaServidorUDP = broadcast.obterPortaUDP();
            portaServidorTCP = broadcast.obterPortaTCP();
            enderecoIP = broadcast.obterEnderecoIP();
            clienteTCP.iniciarConexaoClienteTCP(portaServidorTCP,  enderecoIP);
      }
}
