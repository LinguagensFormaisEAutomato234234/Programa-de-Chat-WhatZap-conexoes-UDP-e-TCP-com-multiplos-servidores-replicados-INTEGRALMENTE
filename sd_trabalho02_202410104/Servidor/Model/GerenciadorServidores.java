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

public class GerenciadorServidores extends Thread {
  private ListaDupla<ServidoresAtivos> listaServidoresAtivos;
  private Hashtable<String, Hashtable<Integer, ServidoresAtivos>> tabelaServidoresAtivos;
  private PropagarRequisicaoComunicacaoBroadcast propagarRequisicaoComunicacao;
  private EstruturaControle estruturaControle; 
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
  private EstruturaDados estruturaDados;
  private static Integer TIMESTAMPATUAL = 0;//altermos posteriormente essa variavel para ser de estado atomico
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
      this.tabelaFuncional = new TabelaMapeamento<FuncaoAltaOrdem<SaidaAPDU>, SaidaAPDU>();
      processarAPDU();
  }

  @Override
  public void run() {
       procurarServidoresAtivo();//procuaramos por todos os servidores em que esta ativos pela rede
       sinalizarExistenciaTodosServidoresAtivo();//sinalizamos a exitencia de um novo no ativo para todos os nos ativos da rede local
       verificarExistenciaServidorLider();//verifica a existncia de um servidor principal
       testarConexaoServidorLider();//testa continuamente a conexao com o servidor tido como lider
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
        aguardarRespostanosAtivos();//aguarda resposta dos servidores ativos por 3 segundos   
        //testando a eficacia do algoritmo de descoberta de todos os nos ativos pela redeasd
        for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
            System.out.println("Lista dos servidores descobertos: " + "PortaUDP: " + ((ServidoresAtivos)ptr.getValor()).obterPortaUDP() + " portaTCP: " +((ServidoresAtivos)ptr.getValor()).obterPortaTCP() + " enderecoIp: " + ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP());
        }
  }
  
  
  public void sinalizarExistenciaTodosServidoresAtivo() {
       for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
               int portaDestinoUDP = ((ServidoresAtivos)ptr.getValor()).obterPortaUDP();
               int portaDestinoTCP = ((ServidoresAtivos)ptr.getValor()).obterPortaTCP();
               String enderecoIp = ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP();
               if(portaDestinoUDP != portaUDPLocal || portaTCPLocal != portaDestinoTCP || !enderecoIp.equals(enderecoIPLocal)) {
                 APDU apdu = new APDU("SinalizarExstencia", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
                 servidorUDP.enviarMensagens(apdu, enderecoIp, portaDestinoUDP);
               }
       }
  }
  
  private void verificarExistenciaServidorLider() {
     new Thread(() -> {
       for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
            int portaDestinoUDP = ((ServidoresAtivos)ptr.getValor()).obterPortaUDP();
            int portaDestinoTCP = ((ServidoresAtivos)ptr.getValor()).obterPortaTCP();
            String enderecoIp = ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP(); 
            APDU apdu = new APDU("RequestMaster", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
            String resposta = servidorTCP.sendTCP(apdu, enderecoIp, portaDestinoTCP);
            if(resposta != null) {
               String[] parRepostaTimeStamp = resposta.split(",");
               String respostaRequisicaoLider =  parRepostaTimeStamp[0];
               int timeStamp = Integer.parseInt(parRepostaTimeStamp[1]);
               if(respostaRequisicaoLider.equals("SIM") && timeStamp >= TIMESTAMPATUAL) {//selecionamos o servidor que tiver o maior time-Stamp, maior timestamp significa estrutura de dados mais consistente 
                  Lider = ((ServidoresAtivos)ptr.getValor()); 
                  TIMESTAMPATUAL = timeStamp;//atualizamos o time-stamp atual
                  registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " respondeu: " + resposta );
               }
            } else {
               registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " nao respondeu a pergunta se eh lider");
            }
        }
        if(Lider == null) {//para o caso de nenhum servidor ter respondido a solicitcao, teremos que escolher um novo lider
            selecionarLider();
        }
     }).start();
  }
  
  public void processarAPDU() {
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SinalizarExstencia, (apdusaida) -> {
                down(semaforo);
                   int portaDestinoUDP = (apdusaida.obterMensagem()).getPortaClienteUDP();
                   int portaDestinoTCP = (apdusaida.obterMensagem()).obterPortaTCP();
                   String enderecoIp = (apdusaida.obterMensagem()).obterEnderecoIp();
                   if(tabelaServidoresAtivos.get(enderecoIp) != null && (tabelaServidoresAtivos.get(enderecoIp)).get(portaDestinoUDP) == null) {  
                       ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaDestinoUDP, portaDestinoTCP, enderecoIp);
                       listaServidoresAtivos.inserirFim(servidorAtivo);
                       (tabelaServidoresAtivos.get(enderecoIp)).put(portaDestinoUDP, servidorAtivo); 
                   }
           
                   if(tabelaServidoresAtivos.get(enderecoIp) == null) {
                       ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaDestinoUDP, portaDestinoTCP, enderecoIp);
                       listaServidoresAtivos.inserirFim(servidorAtivo);
                       tabelaServidoresAtivos.put(enderecoIp, criarRelacao(portaDestinoUDP, servidorAtivo));
                   }
                        registro.registrarMensagem("Lista dos servidores descobertos atualizada solicitacao vinda do No servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP);
                        for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
                            System.out.println("PortaUDP: " + ((ServidoresAtivos)ptr.getValor()).obterPortaUDP() + " portaTCP: " +((ServidoresAtivos)ptr.getValor()).obterPortaTCP() + " enderecoIp: " + ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP());
                       }
                   
                up(semaforo);   
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.RequestMaster, (apdusaida) -> {
                   if(Lider == null || (Lider != null && (Lider.obterPortaUDP() != portaUDPLocal || Lider.obterPortaTCP() != portaTCPLocal || !enderecoIPLocal.equals(Lider.obterEnderecoIP())))){
                       String reposta = "NAO,"+Integer.toString(TIMESTAMPATUAL);
                       enviarMensagemTCP(apdusaida, reposta);
                   } else {//para quem eh bom em logica, sabe que se a sentenca acima for falsa, entao necessariamente este host eh o lider
                       
                      APDU mensagem = new APDUDATA("UPDATEDATA", apdusaida.obterMensagem().obterPortaTCP(), apdusaida.obterMensagem().getPortaClienteUDP(),  apdusaida.obterMensagem().obterEnderecoIp(), estruturaDados.obterListaGrupos() , estruturaDados.obterEstruturaControle(), estruturaDados.obterAdministrarMensagensPendentes());
                       String reposta = "SIM," + Integer.toString(TIMESTAMPATUAL);
                       enviarMensagemTCP(apdusaida, reposta);
                       servidorTCP.sendTCP(mensagem, apdusaida.obterMensagem().obterEnderecoIp(), apdusaida.obterMensagem().obterPortaTCP());
                       registro.registrarMensagem("Replicando estrutura de dados para novo no ativo na rede portaTCP: " + apdusaida.obterMensagem().obterPortaTCP() + " e IP: " + apdusaida.obterMensagem().obterEnderecoIp());
                   }
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.TIMESTAMPTEST, (apdusaida) -> {
                String reposta = Integer.toString(TIMESTAMPATUAL);
                enviarMensagemTCP(apdusaida, reposta);
         });
         
         tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.CONNECTIONREQUEST, (apdusaida) -> {
                String reposta = "Estou ativo";
                enviarMensagemTCP(apdusaida, reposta);
         });
         
  }
  
  public void aletarTodosClientes() {
    while(sevidorLider()) {
      ListaDupla<Usuario> listaClientes = estruturaControle.obterListaUsuario();
      for(ListaDupla.Element ptr = listaClientes.getHead(); ptr != null; ptr = ptr.getNext()) {
             APDU mensagem = new APDU("NEWMISTER", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
             int portaTCP = ((Usuario)ptr.getValor()).obterPortaTCP();
             int portaUDP = ((Usuario)ptr.getValor()).ObterPortaUDP();
             String enderecoIp = ((Usuario)ptr.getValor()).obterEnderecoIp();
             servidorUDP.enviarMensagens(mensagem, enderecoIp, portaUDP);
      }
      try {//aguarda por 1 segundo ate recomunicar novamente, futuramente vamos propor uma otimizacao, por agora sem tempo
          Thread.sleep(1000);
      } catch(Exception ex) {
          ex.printStackTrace();
      } 
    } 
  }

  private void aguardarRespostanosAtivos() {
       try {
          Thread.sleep(1000);
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

   private void testarConexaoServidorLider() {
        Thread processo = new Thread(() -> {//nao precisa de semaforo pois essa Thread somente reliza a leitura do servidor Lider
             do {
              down(semaforoSiconizacaoLider);
               
               if(Lider != null && (Lider.obterPortaUDP() != portaUDPLocal || Lider.obterPortaTCP() != portaTCPLocal || !enderecoIPLocal.equals(Lider.obterEnderecoIP())))   {
                int portaTCPLider = Lider.obterPortaTCP();
                int portaUDPLider = Lider.obterPortaUDP();
                String enderecoIPLider = Lider.obterEnderecoIP();
                APDU apdu = new APDU("CONNECTIONREQUEST", portaTCPLider, portaUDPLider, enderecoIPLider);
                String reposta = servidorTCP.sendTCP(apdu, enderecoIPLider, portaTCPLider);//aguader alguns segundos ate a reposta
                if(reposta == null) {
                  
                   selecionarLider();
                } 
               } 
               up(semaforoSiconizacaoLider);
             }while(true);
        });
        processo.start();
   }
   
   private void selecionarLider() {
            EleicaoLider eleicao =  new EleicaoLider(listaServidoresAtivos);
            boolean testeConexao = false;
            boolean testeTimeStamp = false;
            
            do {
              
              ServidoresAtivos candidato = eleicao.selcionarLider();
             if(candidato != null) {
                  int portaDestinoUDP = (candidato).obterPortaUDP();
                  int portaDestinoTCP = (candidato).obterPortaTCP();
                  String enderecoIp = (candidato).obterEnderecoIP();
                  APDU apdu = new APDU("TIMESTAMPTEST", portaDestinoTCP, portaDestinoUDP, enderecoIp); //definirmos uma apdu para testar o timestamp do candidato, se estiver desatualizada, escolheremos o proximo maior
                  String resposta = null;//resposta do servidor em que sera estabelecido, retornando o valor da atualizacao mais recente aplicado a estrutura de gerenciamento de dados
                  resposta = servidorTCP.sendTCP(apdu, enderecoIp, portaDestinoTCP);
                  
                  testeConexao = resposta != null;
                  if(resposta != null) testeTimeStamp = Integer.parseInt(resposta) >= TIMESTAMPATUAL;
                  if(!testeConexao) {
                      registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + "  nao confirmou a requisicao de lider escolhendo proximo no");
                      eleicao.removerNo(candidato);
                  }  
                  if(testeConexao && !testeTimeStamp) {
                       registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " confirmou a requisicao com o TIMESTAMP desatualizado escolhendo proximo no");
                       eleicao.removerNo(candidato);
                  } else if(testeConexao) {//nesse caso, somente !testeTimeStamp eh falso, portanto !testeTimeStamp => testeTimeStamp
                     registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " confirmou a requisicao com o TIMESTAMP atualizado No promovido a lider");
                      Lider =  candidato;
                      if(sevidorLider()) {
                          aletarTodosClientes();
                      }
                  }
              }
            } while((!testeConexao || !testeTimeStamp) &&  !eleicao.SemCandidato());
            
   }
   
   public void setEstruturaDados(EstruturaDados estruturaDados) {
          this.estruturaDados = estruturaDados;
   }

   public boolean sevidorLider() {
         return Lider != null && (Lider.obterPortaUDP() == portaUDPLocal && Lider.obterPortaTCP() == portaTCPLocal && enderecoIPLocal.equals(Lider.obterEnderecoIP()));
   }
   
   public static void incrementarTIMEStamp() {
          TIMESTAMPATUAL++;//alterar para ATOMIC posteriormente
   }
}
