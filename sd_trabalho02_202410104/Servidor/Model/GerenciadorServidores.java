package Model;

import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import Protocol.APDU;
import Protocol.APDUDATA;
import Controller.Registro;
import java.util.concurrent.atomic.AtomicInteger;

public class GerenciadorServidores extends Thread {
  private volatile ListaDupla<ServidoresAtivos> listaServidoresAtivos;
  private Hashtable<String, Hashtable<Integer, ServidoresAtivos>> tabelaServidoresAtivos;
  private PropagarRequisicaoComunicacaoBroadcast propagarRequisicaoComunicacao;
  private volatile EstruturaControle estruturaControle; 
  private Semaphore semaforo;
  private Semaphore semaforoSiconizacaoLider;
  private Semaphore mutex;
  private Semaphore cheio;
  private TabelaMapeamento<FuncaoAltaOrdem<SaidaAPDU>, SaidaAPDU> tabelaFuncional;
  private Fila<SaidaAPDU> filaCompartilhada; 
  private ServidorTCP servidorTCP;
  private ServidorUDP servidorUDP;
  private ServidoresAtivos Lider;
  private String enderecoIPLocal;
  private Registro registro;
  private int portaUDPLocal, portaTCPLocal;
  private volatile EstruturaDados estruturaDados;
  private EleicaoLider eleicao;
  private static AtomicInteger TIMESTAMPATUAL = new AtomicInteger(0);//altermos posteriormente essa variavel para ser de estado atomico
  public GerenciadorServidores(Fila<SaidaAPDU> filaCompartilhada, ServidorTCP servidorTCP, ServidorUDP servidorUDP, Semaphore mutex, Semaphore cheio, Registro registroMensagem, String enderecoIPLocal, ListaDupla<ServidoresAtivos> listaServidoresAtivos, Semaphore semaforo) {
      this.semaforo = semaforo;
      this.semaforoSiconizacaoLider = new Semaphore(1);
      this.mutex = mutex;
      this.cheio = cheio;
      this.registro = registroMensagem;
      this.servidorTCP = servidorTCP;
      this.servidorUDP = servidorUDP;
      this.enderecoIPLocal = enderecoIPLocal;
      this.portaUDPLocal = servidorUDP.obterPortaUDP();
      this.portaTCPLocal = servidorTCP.obterPortaTCP();
      this.listaServidoresAtivos = listaServidoresAtivos;
      this.tabelaServidoresAtivos = new Hashtable<String, Hashtable<Integer, ServidoresAtivos>>();
      this.propagarRequisicaoComunicacao = new PropagarRequisicaoComunicacaoBroadcast(listaServidoresAtivos, semaforo, tabelaServidoresAtivos);
      this.filaCompartilhada = filaCompartilhada;
      this.eleicao =  new EleicaoLider(servidorTCP, servidorUDP, registro);
      this.tabelaFuncional = new TabelaMapeamento<FuncaoAltaOrdem<SaidaAPDU>, SaidaAPDU>();
      processarAPDU();
  }

  @Override
  public void run() {
       testarConexaoNosRede();//testa continuamente a conexao com o servidor tido como lider
       gerenciarMensagens();//chama uma subrotina para tratamento de evento
  }
  
    /********************************************************************************************
    * Metodo: gerenciarMensagens
    * Funcao: definir um processo consumidor em que reaja a chegada de diferentes mensagens requeridas pela rede com base numa apdu, um protocolo de comunicacao padronizada. 
    * Parametros: Vazio
    * Retorno: Vazio
    ********************************************************************************************/
    public void gerenciarMensagens() {
             while(true) {
                  down(cheio);
                  down(mutex);
                  try {
                      SaidaAPDU apdusaida = filaCompartilhada.desenfileirar();
                      FuncaoAltaOrdem<SaidaAPDU> funcao = tabelaFuncional.obterItem((new TraduzirAPDU().obterTipoAPDU(apdusaida.obterMensagem().getOperacao())).tipoAtual);
                      funcao.funcaoAltaOrdem(apdusaida); 
                  } catch(Exception ex) {
                      ex.printStackTrace();
                  } finally {                   
                      up(mutex);
                 }   
             }
    }//fim do metodo gerenciarMensagens
 
  /********************************************************************************************
  * Metodo: procurarServidoresAtivo
  * Funcao: busca descobrir quais sao todas a replicacoes de servidores em que estao ativos pela rede  
  * Parametros: Vazio
  * Retorno: Vazio
  ********************************************************************************************/
  public void procurarServidoresAtivo() {
        propagarRequisicaoComunicacao.descobrirServidor();
        aguardarRespostanosAtivos(1000);//aguarda resposta dos servidores ativos por 1 segundos   
        //testando a eficacia do algoritmo de descoberta de todos os nos ativos pela redeasd
  }
  
  public void processarAPDU() {
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.RequestMaster, (apdusaida) -> {//essa mensagem refere-se ao fato de que um novo no ativo entrou na rede e a mensagem geral de anuncio do servidor fora recebido por esse no
                       APDU mensagem = new APDUDATA("UPDATEDATA", apdusaida.obterMensagem().obterPortaTCP(), apdusaida.obterMensagem().getPortaClienteUDP(),  apdusaida.obterMensagem().obterEnderecoIp(), estruturaDados.obterListaGrupos() , estruturaDados.obterEstruturaControle(), estruturaDados.obterAdministrarMensagensPendentes());//este protocolo permite haver a atualizacao da estrutura de dados referente
                       String reposta = "Replicando Estrutura de Dados";
                       enviarMensagemTCP(apdusaida, reposta);
                       servidorTCP.sendTCP(mensagem, apdusaida.obterMensagem().obterEnderecoIp(), apdusaida.obterMensagem().obterPortaTCP());                       
                       registro.registrarMensagem("Replicando estrutura de dados para novo no ativo na rede portaTCP: " + apdusaida.obterMensagem().obterPortaTCP() + " e IP: " + apdusaida.obterMensagem().obterEnderecoIp());
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.AVISOLIDER, (apdusaida) -> {//apdu de padronizacao em que sinaliza uma mensagem de um servidor em que eh o lider, portanto permitindo descobrir qual o no ativo lidera a subrede 
                   int portaDestinoUDP = (apdusaida.obterMensagem()).getPortaClienteUDP();//tomamaos a porta UDP da mensagem recebida 
                   int portaDestinoTCP = (apdusaida.obterMensagem()).obterPortaTCP(); //posteriormente, tomamos a porta TCP referente
                   String enderecoIp = (apdusaida.obterMensagem()).obterEnderecoIp();//e basta tomamos o endereco Ip do no de origem para finalmente termos informacao o suficiente para testar o canal de comunicacao
                   inserirNoAtivoEstruturaControle(enderecoIp, portaDestinoTCP, portaDestinoUDP);//se alguma abstracao referente a esse no externo existir, entao eis que essa insercao sera ignorada
                   ServidoresAtivos alegador = (tabelaServidoresAtivos.get(enderecoIp)).get(portaDestinoUDP);//posteriormente, desenvolverei um protocolo de resolucao de conflito para lideres
                   
                   APDU apdu = new APDU("RequestMaster", portaTCPLocal, portaUDPLocal, enderecoIPLocal);//essa apdu determinara uma padronizacao de comunicacao para distribuicao da estrutura de dados em que serao replicadas em diversos locais dos nos ativos da rede
                      down(semaforoSiconizacaoLider);
                   if(Lider == null || (alegador.obterPortaUDP() != Lider.obterPortaUDP() || alegador.obterPortaTCP() != Lider.obterPortaTCP() || !alegador.obterEnderecoIP().equals(Lider.obterEnderecoIP()))) {
                       Lider = alegador;//adiante desenvolveremos um protocolo para tratar o caso de haver dois lideres
                       registro.registrarMensagem("O no " + Lider.obterEnderecoIP() + " e portas TCP e UDP " +  Lider.obterPortaUDP() + " : " + alegador.obterPortaTCP() + " alegam ser o no lider do sistema");
                       up(semaforoSiconizacaoLider);   
                       String RespostaServidor = servidorTCP.sendTCP(apdu, enderecoIp, portaDestinoTCP);
                       if(RespostaServidor != null) registro.registrarMensagem("[Resposta Servidor lider]: " + RespostaServidor);
                       if(RespostaServidor == null) registro.registrarMensagem("[Resposta Servidor lider]: nao respondeu");
                   } else {
                       up(semaforoSiconizacaoLider);  
                   }
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.TIMESTAMPTEST, (apdusaida) -> {
                String reposta = Integer.toString(TIMESTAMPATUAL.get());
                enviarMensagemTCP(apdusaida, reposta);
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.CONNECTIONREQUEST, (apdusaida) -> {
                String reposta = "Estou ativo";
                enviarMensagemTCP(apdusaida, reposta);
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.PROCURARLIDER, (apdusaida) -> {
                if(sevidorLider()) {
                  registro.registrarMensagem("Algum no ativo esta requisitando a existencia de um lider, enviando confirmacao");
                  enviarMensagemTCP(apdusaida, "SIM");
                  APDU mensagem = new APDUDATA("UPDATEDATA", apdusaida.obterMensagem().obterPortaTCP(), apdusaida.obterMensagem().getPortaClienteUDP(),  apdusaida.obterMensagem().obterEnderecoIp(), estruturaDados.obterListaGrupos() , estruturaDados.obterEstruturaControle(), estruturaDados.obterAdministrarMensagensPendentes());//este protocolo permite haver a atualizacao da estrutura de dados referente
                  servidorTCP.sendTCP(mensagem, apdusaida.obterMensagem().obterEnderecoIp(), apdusaida.obterMensagem().obterPortaTCP()); 
                  inserirNoAtivoEstruturaControle(apdusaida.obterMensagem().obterEnderecoIp(), apdusaida.obterMensagem().obterPortaTCP(), apdusaida.obterMensagem().getPortaClienteUDP());
                  registro.registrarMensagem("Replicando estrutura de dados para novo no ativo na rede portaTCP: " + apdusaida.obterMensagem().obterPortaTCP() + " e IP: " + apdusaida.obterMensagem().obterEnderecoIp());
                } else {
                  enviarMensagemTCP(apdusaida, "NAO");
                }
         });
  }
  
  public void aletarTodosNosRede() {
   
    while(sevidorLider()) {
      ListaDupla<Usuario> listaClientes = estruturaControle.obterListaUsuario();
      ListaDupla<ServidoresAtivos> listaServidor = listaServidoresAtivos;
      for(ListaDupla.Element ptr = listaClientes.getHead(); ptr != null; ptr = ptr.getNext()) {
             APDU mensagem = new APDU("NEWMISTER", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
             int portaTCP = ((Usuario)ptr.getValor()).obterPortaTCP();
             int portaUDP = ((Usuario)ptr.getValor()).ObterPortaUDP();
             String enderecoIp = ((Usuario)ptr.getValor()).obterEnderecoIp();
             servidorUDP.enviarMensagens(mensagem, enderecoIp, portaUDP);
      }
    
      for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
             APDU mensagem = new APDU("AVISOLIDER", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
             int portaTCP = ((ServidoresAtivos)ptr.getValor()).obterPortaTCP();
             int portaUDP = ((ServidoresAtivos)ptr.getValor()).obterPortaUDP();
             String enderecoIp = ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP();
             if(portaTCP != portaTCPLocal || portaUDP != portaUDPLocal || !enderecoIp.equals(enderecoIPLocal)) { //envia para todos os nos menos para o servidor local
                  servidorUDP.enviarMensagens(mensagem, enderecoIp, portaUDP);
             }
      }
      aguardarRespostanosAtivos(1000);
    } 
  }

  private void aguardarRespostanosAtivos(long tempo) {
       try {
          Thread.sleep(tempo);
       } catch(Exception ex) {
          ex.printStackTrace();
       } 
  } 
  
  public void setEstruturaControler(EstruturaControle estruturaControle) {
          this.estruturaControle = estruturaControle;
  }
  
  private void down(Semaphore semaforoEntrada) {
       try {
          semaforoEntrada.acquire();
       } catch(Exception ex) {
          ex.printStackTrace();
       } 
  }
  
  private void up(Semaphore semaforoEntrada) {
      semaforoEntrada.release();
  }

   private Hashtable<Integer, ServidoresAtivos> criarRelacao(int porta, ServidoresAtivos servidor) {
         Hashtable<Integer, ServidoresAtivos> tabela = new Hashtable<>();
         tabela.put(porta, servidor);
         return tabela;
   }
   
   private void enviarMensagemTCP(SaidaAPDU apdusaida, String Mensagem) {
        try {
           apdusaida.obterSaida().writeObject(Mensagem);
        } catch(Exception ex) {
           ex.printStackTrace();
        }
   }

   private void testarConexaoNosRede() {
        Thread processo = new Thread(() -> {//nao precisa de semaforo pois essa Thread somente reliza a leitura do servidor Lider
             procurarServidoresAtivo();//procuaramos por todos os servidores em que esta ativos pela rede
             down(semaforoSiconizacaoLider);
             Lider = eleicao.procurarExistenciaLiderSelecionado(portaUDPLocal, portaTCPLocal, enderecoIPLocal, listaServidoresAtivos);
             up(semaforoSiconizacaoLider); 
             do {
               if(!verificarLiderEhNulo() && !sevidorLider()) {
                  down(semaforoSiconizacaoLider);
                  int portaTCPLider = Lider.obterPortaTCP();
                  int portaUDPLider = Lider.obterPortaUDP();
                  String enderecoIPLider = Lider.obterEnderecoIP();
                  APDU apdu = new APDU("CONNECTIONREQUEST", portaTCPLider, portaUDPLider, enderecoIPLider);
                  up(semaforoSiconizacaoLider); 
                  String reposta = servidorTCP.sendTCP(apdu, enderecoIPLider, portaTCPLider);//aguader alguns segundos ate a reposta
                  if(reposta == null) {
                   down(semaforoSiconizacaoLider);
                     Lider = null;
                   up(semaforoSiconizacaoLider);   
                  }
               }
               if(verificarLiderEhNulo()) {
                  
                  selecionarLider();
               }
               //sSystem.out.println("escolhendo lider");
               if(sevidorLider()) aletarTodosNosRede();
               //System.out.println("aqui");
               procurarServidoresAtivo();//procuaramos por todos os servidores em que esta ativos pela rede
               
             }while(true);
        });
        processo.start();
   }
   
   private void selecionarLider() {
            down(semaforoSiconizacaoLider);
            if(Lider == null)  Lider = eleicao.protocoloSelecao(listaServidoresAtivos);
            up(semaforoSiconizacaoLider);
   }
   
   public void setEstruturaDados(EstruturaDados estruturaDados) {
          this.estruturaDados = estruturaDados;
   }

   public boolean sevidorLider() {
     down(semaforoSiconizacaoLider);
        boolean resultado =  Lider != null && (Lider.obterPortaUDP() == portaUDPLocal && Lider.obterPortaTCP() == portaTCPLocal && enderecoIPLocal.equals(Lider.obterEnderecoIP()));
     up(semaforoSiconizacaoLider);    
        return resultado;    
   }
   
   public static void incrementarTIMEStamp() {
          TIMESTAMPATUAL.incrementAndGet();//alterar para ATOMIC posteriormente
   }
   
   private void inserirNoAtivoEstruturaControle(String enderecoIP, int portaTCP, int portaUDP) {
          if(tabelaServidoresAtivos.get(enderecoIP) != null && (tabelaServidoresAtivos.get(enderecoIP)).get(portaUDP) == null) {  
                       ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaUDP, portaTCP, enderecoIP);
                       listaServidoresAtivos.inserirFim(servidorAtivo);
                       (tabelaServidoresAtivos.get(enderecoIP)).put(portaUDP, servidorAtivo); 
          }
           
          if(tabelaServidoresAtivos.get(enderecoIP) == null) {
                       ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaUDP, portaTCP, enderecoIP);
                       listaServidoresAtivos.inserirFim(servidorAtivo);
                       tabelaServidoresAtivos.put(enderecoIP, criarRelacao(portaUDP, servidorAtivo));
          }
   }
   
   public static Integer obterTimeStamp() {
       return TIMESTAMPATUAL.get();
   }
   
   public boolean verificarLiderEhNulo() {
        down(semaforoSiconizacaoLider);
        boolean resultado =  Lider == null;
        up(semaforoSiconizacaoLider);    
        return resultado;    
   }
}
