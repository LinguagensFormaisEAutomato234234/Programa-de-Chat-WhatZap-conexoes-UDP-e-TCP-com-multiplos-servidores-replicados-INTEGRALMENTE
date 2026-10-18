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

public class PropagarRequisicaoComunicacaoBroadcast {
     private String EnderecoIP;
     private int portaServidorTCP;
     private int portaServidorUDP;
     private final int tempoLimiteHostRemoto = 16; //3 segundos para aguardar resposta do servidor 
     private final int tempoLimiteHostExterno = 3000;//1,5 segundos para aguardar resposta do servidor remoto
     private ListaDupla<InetAddress> listaServidores;
     public PropagarRequisicaoComunicacaoBroadcast() {
     
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
       try {
          broadcasts.inserirFim(InetAddress.getByName("255.255.255.255"));
       } catch (Exception e) {
         System.err.println("CLIENTE - AVISO: Nao foi possivel adicionar broadcast padrao: " + e.getMessage());
       } // fim do try-catch
       if(!encontrarPortaTCPUDPHostExterno(broadcasts)) encontrarEndederecoHostLocal();
  } // fim do metodo descobrirServidor
  
  
  private boolean encontrarPortaTCPUDPHostExterno(ListaDupla<InetAddress> broadcasts) {
     try (DatagramSocket socket = new DatagramSocket()) {
         socket.setBroadcast(true);
         socket.setSoTimeout(tempoLimiteHostExterno); // 3 segundos de limite
          byte[] pedido = "SERVIDOR_IP".getBytes();
          byte[] buffer = new byte[256];
         // Envia o pedido para todos os enderecos de broadcast encontrados
        for (ListaDupla.Element ptr = broadcasts.getHead(); ptr != null; ptr = ptr.getNext()) {
            try {
                 DatagramPacket pacoteRequisicaoComunicacao = new DatagramPacket(pedido, pedido.length, ((InetAddress)ptr.getValor()), 8888);
                 socket.send(pacoteRequisicaoComunicacao);
            } catch (Exception e) {
                 System.err.println("CLIENTE - AVISO: broadcast falhou para " + ((InetAddress)ptr.getValor()).getHostAddress() + ": " + e.getMessage());
            } // fim do try-catch
        } // fim do for
          //colocar um laco posteriormente:
          DatagramPacket pacoteResposta = new DatagramPacket(buffer, buffer.length);
          try {
               socket.receive(pacoteResposta);
               String resposta = new String(pacoteResposta.getData(), 0, pacoteResposta.getLength()).trim();
               String[] PortasUDPTCP = resposta.split(",");
               portaServidorUDP = Integer.parseInt(PortasUDPTCP[0]); //o servidor deve informar a porta UDP primeiramente
               portaServidorTCP = Integer.parseInt(PortasUDPTCP[1]); 
               EnderecoIP = pacoteResposta.getAddress().getHostAddress();
               System.out.println("CLIENTE - Servidor encontrado no IP: " + EnderecoIP);
               return true;
          } catch (SocketTimeoutException e) {
                System.out.println("CLIENTE - Nenhum servidor respondeu ao broadcast.");
          } // fim do try-catch
      } catch (Exception e) {
           System.out.println("CLIENTE - Erro na rede: " + e.getMessage());
      } // fim do try-catch
       return false;  
  } 
  
  private void encontrarEndederecoHostLocal() {
        System.out.println("CLIENTE - Tentando conexao com servidor local. - Log na classe PropagarRequisicaoComunicacaoBroadcast");
       for(int porta = 8888; porta < 8898; porta++) {
        try(DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(tempoLimiteHostRemoto);
            byte[] pedido = "SERVIDOR_IP".getBytes();
            DatagramPacket pacoteRequisicaoLocal = new DatagramPacket(pedido, pedido.length, InetAddress.getByName("127.0.0.1"), porta);
            socket.send(pacoteRequisicaoLocal);
            byte[] buffer = new byte[256];
            DatagramPacket pacoteResposta = new DatagramPacket(buffer, buffer.length);
            socket.receive(pacoteResposta);
            String resposta = new String(pacoteResposta.getData(), 0, pacoteResposta.getLength()).trim();
            String[] PortasUDPTCP = resposta.split(",");
            portaServidorUDP = Integer.parseInt(PortasUDPTCP[0]); //o servidor deve informar a porta UDP primeiramente
            portaServidorTCP = Integer.parseInt(PortasUDPTCP[1]); 
            EnderecoIP = pacoteResposta.getAddress().getHostAddress();
            System.out.println("CLIENTE - Servidor encontrado no IP: " + EnderecoIP);
         
        } catch(Exception e) {
            System.out.println("CLIENTE - Servidor nao encontrado tentando a proxima porta: " + porta);
        } 
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
}
