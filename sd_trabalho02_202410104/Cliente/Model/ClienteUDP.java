/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 06/09/2026
*Ultima alteracao.: 06/09/2026 
*Nome.............: ClienteUDP
*Funcao...........: processar o envio de mensagens bem como a recepcao de mensagem utilizando o protocolo de comunicacao UDP, veja que o processo UDP temos somente um caso de chamada bloqueante, que eh justamente o processo responsavel para gerenciar o processo de entrada
**************************************************************************************************/

package Model;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;//declarmos uma variavel semaforo para controlar concorrencia
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import Protocol.APDU;
import java.util.Random;

public class ClienteUDP {
     private int portaServidor;
     private static final int portaProcesso = 25641;      
     private static final int TAMANHO_DADOS_SAIDA = 1024;
     private String enderecoIP;
     private DatagramSocket soqueteConexaoCliente;
     private DatagramPacket pacoteEnviado;
     private DatagramPacket pacoteRecebido;
     private InetAddress enderecoIPServidor;
     private Semaphore mutex;
     private Semaphore cheio;
     private Fila<APDU> filaCompartilhada;
      
     public ClienteUDP(Semaphore mutex, Semaphore cheio, Fila<APDU> filaCompartilhada) {
           System.out.println(portaProcesso);
           this.mutex = mutex;
           this.cheio = cheio;
           this.filaCompartilhada = filaCompartilhada;
     }
     

     /********************************************************************************************
     * Metodo: gerenciarConexao
     * Funcao: inicializar a abstracao soquete associando-a a porta que identifica este processo de aplicacao, bem como associarmos o endereco IP do servidor a abstracao da classe InetAddress
     * Parametros: Vazio
     * Retorno: Vazio
      ********************************************************************************************/
     public void gerenciarConexao(int portaServidor, String enderecoIP) {
         try {
            soqueteConexaoCliente = new DatagramSocket(portaProcesso);
            enderecoIPServidor = InetAddress.getByName(enderecoIP);
            this.portaServidor =  portaServidor;
            this.enderecoIP = enderecoIP;
         } catch(Exception ex) {
            ex.printStackTrace();         
         }
     }//fim do metodo gerenciarConexao
     

      /********************************************************************************************
     * Metodo: SEND
     * Funcao: tranformar a mensagem String em um fluxo de byte, e estrutramos numa abstracao representativa dos campos de estrutura do segemento UDP, como a porta do processo de aplicacao de origem, destino, e endereco IP do servidor, representado pela classe  DatagramPacket
     * Parametros: (String : mensagem)
     * Retorno: Vazio
      ********************************************************************************************/
      public void SEND(APDU mensagem, String enderecoIPparametro, int portaServidorparametro) {
       try {
           ByteArrayOutputStream dados = new ByteArrayOutputStream();//toma a informacao e inscerve em um vetor de bytes na memoria
           ObjectOutputStream saida = new ObjectOutputStream(dados); //permite realizar o procedimento de tranformar um objeto serializado em vetor de bytes na memoria 
           saida.writeObject(mensagem); //permite tomar o objeto de classe apdu e tranformar num fluxo de bits na memoria
           saida.flush(); //forca com que toda a informacao armazenada no buffer interna seja direcionada para dados
           byte[] dadosSaida = dados.toByteArray();//efetivamente, transformamos o objeto serializado num fluxo de bytes
           DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length,  InetAddress.getByName(enderecoIPparametro), portaServidorparametro);
           soqueteConexaoCliente.send(pacoteEnviado); //a mensagem eh enviada mediante as pilhas de protocolos da rede
        } catch(Exception ex) {
           ex.printStackTrace();   
        }
     }//fim do metodo SEND
     

      /********************************************************************************************
     * Metodo: gerenciarFluxoEntrada
     * Funcao: tranformar a mensagem String em um fluxo de byte, e estrutramos numa abstracao representativa dos campos de estrutura do segemento UDP, como a porta do processo de aplicacao de origem, destino, e endereco IP do servidor, representado pela classe  DatagramPacket. Para tanto, desenvolvemos um processo em que aguarda em um loop bloqueante a chegada de mensagem na rede e a aloca na fila de apdu's na qual dispara um evento a ser tratado. Permitindo que haja uma comunicacao entre cliente e servidor atravez do tratamento do evento gerado
     * Parametros: vazio
     * Retorno: Vazio
      ********************************************************************************************/
     public void gerenciarFluxoEntrada() {
          Thread ProcessoReceive = new Thread(() -> {
              try {
                 //  soqueteConexaoCliente = new DatagramSocket(portaProcesso);
                   
                   while(true) {
                        byte[] dadosEntrada = new byte[1024];
                        pacoteRecebido = new DatagramPacket(dadosEntrada, dadosEntrada.length);
                        soqueteConexaoCliente.receive(pacoteRecebido);//aqui temos a chamada bloqueante do sistema 
                        ByteArrayInputStream dados = new ByteArrayInputStream(pacoteRecebido.getData(),  pacoteRecebido.getOffset(), pacoteRecebido.getLength()); //toma a informacao em fluxo de bytes e aloca num array de bytes na memoria
                      //  System.out.println(portaProcesso);
                        ObjectInputStream entrada = new ObjectInputStream(dados);//permite realizar o processo de deserializacao do objeto
                        APDU mensagemRecebida = (APDU) entrada.readObject(); //a mensagem recebida eh tranformada no objeto original
                        String enderecoIp = pacoteRecebido.getAddress().getHostAddress();
                        //System.out.println(portaProcesso);									
                        System.out.println(mensagemRecebida);
                        new Thread(() -> {
                             alocarMensagemFila(mensagemRecebida);
                        }).start();
                   }
              } catch(Exception ex) {
                 ex.printStackTrace();
              }
           });
           ProcessoReceive.setDaemon(true);
           ProcessoReceive.start();
     }//fim do metodo gerenciarFluxoEntrada
     
     private void alocarMensagemFila(APDU apdu) {
          downMutex(mutex);
            try {
                if(!filaCompartilhada.cheia()) {
                    filaCompartilhada.enfileirar(apdu);
                    upMutex(cheio);
                } 
                 
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
               
               upMutex(mutex);
            }
    }
    
     private void downMutex(Semaphore mutex) {
            try {
                 mutex.acquire();
            } catch(Exception ex) {
                 ex.printStackTrace();
            }
    }

    private void upMutex(Semaphore mutex) {
           mutex.release();
    }
    
    public int portaUDP() {
         return portaProcesso;
    }
}
//10.0.39.254
