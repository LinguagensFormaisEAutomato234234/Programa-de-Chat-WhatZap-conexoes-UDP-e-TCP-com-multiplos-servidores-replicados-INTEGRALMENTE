/* ***************************************************************
* Autor............: Gustavo Henrique Oliveira Fernandes
* Matricula........: 202410104
* Inicio...........: 09/10/2026
* Ultima alteracao.: 09/10/2026
* Nome.............: Descobridor
* Funcao...........: Varrer todas as interfaces de rede do computador para descobrir o IP do servidor
*******************************************************************/

package Model;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;


public class PropagarRequisicaoComunicacaoBroadcast {
     private String EnderecoIP;
     private int portaServidorTCP;
     private int portaServidorUDP;
     private final int portaBroadcast = 8888;
     private Semaphore semaforoMutex;
     private final int tempoLimiteHostRemoto = 3000; //3 segundos para aguardar resposta do servidor 
     private final int tempoLimiteHostExterno = 1500;//1,5 segundos para aguardar resposta do servidor remoto
     private final int tempoLimiteRespostaServidor = 50;
     private ListaDupla<InetAddress> listaServidores;
     private ListaDupla<ServidoresAtivos> listaServidoresAtivos;
     private Hashtable<String, Hashtable<Integer, ServidoresAtivos>> tabelaServidoresAtivos;
     public PropagarRequisicaoComunicacaoBroadcast(ListaDupla<ServidoresAtivos> listaServidoresAtivos, Semaphore semaforo, Hashtable<String, Hashtable<Integer, ServidoresAtivos>> tabelaServidoresAtivos) {
          this.listaServidoresAtivos = listaServidoresAtivos;
          this.tabelaServidoresAtivos = tabelaServidoresAtivos;
          this.semaforoMutex = semaforo;
     }
     
   /********************************************************************
   * Metodo: descobrirServidor
   * Funcao: Envia broadcast UDP varrendo todas as interfaces de rede locais
   * ativas
   * Parametros: nenhum
   * Retorno: String com o IP encontrado ou null
   ***********************************************************************/
  public void descobrirServidor() {
       ListaDupla<InetAddress> broadcasts = coletarBroadcasts();
       encontrarPortaTCPUDPHostExterno(broadcasts);
       try {
          broadcasts.inserirFim(InetAddress.getByName("255.255.255.255"));
       } catch (Exception e) {
         System.err.println("CLIENTE - AVISO: Nao foi possivel adicionar broadcast padrao: " + e.getMessage());
       } // fim do try-catch
  } // fim do metodo descobrirServidor
  
  
  private void encontrarPortaTCPUDPHostExterno(ListaDupla<InetAddress> broadcasts) {
     try {
         DatagramSocket socket = new DatagramSocket();
         socket.setBroadcast(true);
          byte[] pedido = "SERVIDOR_IP_NO_ATIVO".getBytes();
          byte[] buffer = new byte[256];
         // Envia o pedido para todos os enderecos de broadcast encontrados
        for (ListaDupla.Element ptr = broadcasts.getHead(); ptr != null; ptr = ptr.getNext()) {
            try {
                 System.out.println("CLIENTE - Tentando broadcast em: " + ((InetAddress)ptr.getValor()).getHostAddress());
                 DatagramPacket pacoteRequisicaoComunicacao = new DatagramPacket(pedido, pedido.length, ((InetAddress)ptr.getValor()), portaBroadcast);
                 socket.send(pacoteRequisicaoComunicacao);
            } catch (Exception e) {
                 System.err.println("CLIENTE - AVISO: broadcast falhou para " + ((InetAddress)ptr.getValor()).getHostAddress() + ": " + e.getMessage());
            } // fim do try-catch
        } // fim do for
        processarResposta(buffer, socket); //delegamos um processo para escutar a resposta dos host remotos
        processarRespostaLocalHost();//delegamos entao um segundo processo 
      } catch (Exception e) {
           System.out.println("CLIENTE - Erro na rede: " + e.getMessage());
      } // fim do try-catch
         
  } 

  /********************************************************************
   * Metodo: processarResposta
   * Funcao: aguarda um resposta dos servidores listados durante um intervalo de tempo e verifica se estes sao nos ativos da rede
   * Parametros: nenhum
   * Retorno: String com o IP encontrado ou null
   ***********************************************************************/
  private void processarResposta(byte[] buffer, DatagramSocket socket) {
    Thread ProcessoRemoto = new Thread(() -> {
           while(true) {
              DatagramPacket pacoteResposta = new DatagramPacket(buffer, buffer.length);
              try {
                  socket.receive(pacoteResposta);
                  String resposta = new String(pacoteResposta.getData(), 0, pacoteResposta.getLength()).trim();
                  String[] PortasUDPTCPEnderecoIp = resposta.split(",");//Chamaremos a subrotina split(String: variavel) para quabrar a mensagem que vem do formato mensagem = UDP , TCP, endereco => split(') = {UDP, TCP, endereco}
                 dowmMutex();//obtem lock do semforo mutex para o caso de acesso concorrente de recursos
                  portaServidorUDP = Integer.parseInt(PortasUDPTCPEnderecoIp[0]); //o servidor deve informar a porta UDP primeiramente
                  portaServidorTCP = Integer.parseInt(PortasUDPTCPEnderecoIp[1]); 
                  EnderecoIP = PortasUDPTCPEnderecoIp[2];
                  System.out.println("Resposta recebida: " + resposta);
                  if(tabelaServidoresAtivos.get(EnderecoIP) != null && (tabelaServidoresAtivos.get(EnderecoIP)).get(portaServidorUDP) == null) {  
                     ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaServidorUDP, portaServidorTCP, EnderecoIP);
                     listaServidoresAtivos.inserirFim(servidorAtivo);
                     (tabelaServidoresAtivos.get(EnderecoIP)).put(portaServidorUDP, servidorAtivo); 
                  }

                  if(tabelaServidoresAtivos.get(EnderecoIP) == null) {
                     ServidoresAtivos servidorAtivo = new ServidoresAtivos(portaServidorUDP, portaServidorTCP, EnderecoIP);
                     listaServidoresAtivos.inserirFim(servidorAtivo);
                     tabelaServidoresAtivos.put(EnderecoIP, criarRelacao(portaServidorUDP, servidorAtivo));
                  }
                 upMutex();//liberacao do semaforo mutex
              } catch(SocketTimeoutException e) {
                 System.out.println("CLIENTE - Nenhum servidor respondeu ao broadcast.");
              } catch(Exception ex) {
                 ex.printStackTrace();
              }
           }
    });
    ProcessoRemoto.start();
  }

  private Hashtable<Integer, ServidoresAtivos> criarRelacao(int porta, ServidoresAtivos servidor) {
         Hashtable<Integer, ServidoresAtivos> tabela = new Hashtable<>();
         tabela.put(porta, servidor);
         return tabela;
  }
  
  private void processarRespostaLocalHost() {
      //  System.out.println("CLIENTE - Tentando conexao com servidor local. - Log na classe PropagarRequisicaoComunicacaoBroadcast");
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] pedido = "SERVIDOR_IP_NO_ATIVO".getBytes();  
            byte[] bufferResposta = new byte[256];
            for(int porta = 5000; porta < 10000; porta++) {   
               if(porta != portaBroadcast) {          
                 DatagramPacket pacoteRequisicaoLocal = new DatagramPacket(pedido, pedido.length, InetAddress.getByName("127.0.0.1"), porta);
                 socket.send(pacoteRequisicaoLocal);
               }
            }
            processarResposta(bufferResposta, socket);
        } catch(Exception e) {
            System.out.println("CLIENTE - Servidor nao encontrado em localhost.");
        } 
  }
     
  /*******************************************************
   * Metodo: coletarBroadcasts
   * Funcao: Vasculha a placa de rede do computador para encontrar o IP de
   * broadcast correto (ex: 192.168.1.255)
   * Parametros: nenhum
   * Retorno: Lista de InetAddress
   ********************************************************/
  private ListaDupla<InetAddress> coletarBroadcasts() {
    ListaDupla<InetAddress> lista = new ListaDupla<InetAddress>();
    try {
      java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface interfaceSaida = interfaces.nextElement();
        if (interfaceSaida.isUp() && !interfaceSaida.isLoopback() && !interfaceSaida.isVirtual()) {
             for (InterfaceAddress ia : interfaceSaida.getInterfaceAddresses()) {
                InetAddress broadcast = ia.getBroadcast();
                if (broadcast != null) {
                  lista.inserirFim(broadcast);
                } // fim do if
             } // fim do for
        }// fim do while
      }  
    } catch (Exception e) {
      System.err.println("CLIENTE - AVISO: Falha ao listar interfaces de rede: " + e.getMessage());
    } // fim do try-catch
    return lista;
  } // fim do metodo coletarBroadCasts
  
  public String obterEnderecoIP() {
      return this.EnderecoIP;
  }
  
  public int obterPortaUDP() {
      return this.portaServidorUDP;
  }
  
  public int obterPortaTCP() {
     return this.portaServidorTCP;
  }

  private void dowmMutex() {
       try {
          semaforoMutex.acquire();
       } catch(Exception ex) {
          ex.printStackTrace();
       } 
  }
  
  private void upMutex() {
      semaforoMutex.release();
  } 

  private void escutarMeio(int intervalotempo) {
    try { 
       Thread.sleep(intervalotempo);
    } catch(Exception ex) {
       ex.printStackTrace();
    }
  }  
}
