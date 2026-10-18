package Model;

import java.net.Socket;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.io.ObjectInputStream;
import Protocol.APDU;
import java.util.concurrent.Semaphore;//declarmos uma variavel semaforo para controlar concorrencia
import java.util.Random;

public class ClienteTCP {
     private int portaServidor; //define a porta do processo da aplicacao
     private static final int portaProcesso = 25641;
     private String enderecoIP; //define o endereco IP do host conectado
     private Socket socketConexaoCliente; 
     private ObjectOutputStream saidaSerializda;     
     private ObjectInputStream entrada;
     private Semaphore mutex;
     private Semaphore cheio;
     private Fila<APDU> filaCompartilhada;
     
     public ClienteTCP(Semaphore mutex, Semaphore cheio, Fila<APDU> filaCompartilhada) {
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
     public void iniciarConexaoClienteTCP(int portaServidor, String enderecoIP) {
           this.portaServidor = portaServidor;
           this.enderecoIP = enderecoIP;
           this.mutex = mutex;
           this.cheio = cheio;
           this.filaCompartilhada = filaCompartilhada;
     }
     
   /**  
     public void iniciarConexao(String nome) {
       Thread Processo = new Thread(() -> {
            try {
               socketConexaoCliente = new Socket();
               socketConexaoCliente.bind(new InetSocketAddress(portaProcesso));
               socketConexaoCliente.connect(new InetSocketAddress(enderecoIP, portaServidor));
               OutputStream saida = socketConexaoCliente.getOutputStream();
               saidaSerializda = new ObjectOutputStream(saida);
               entrada = new ObjectInputStream(socketConexaoCliente.getInputStream());   
               receberMensagem();
               SEND(new APDU("REGISTER", null, nome, null, portaProcesso));
            } catch(Exception ex) {
               ex.printStackTrace();
           }
       });
       Processo.setDaemon(true);
       Processo.start();
     }**/
  /********************************************************************************************
  * Metodo: inciarConexao
  * Funcao: iniciar uma conexao com o servidor usando soquetes, permitindo alocar uma espaco na memoria para este processo de aplicao
  * Parametros: vazio
  * Retorno: vazio
  ********************************************************************************************/   
  public boolean iniciarConexao(String nome) {
     //Tenta conectar no IP e porta fornecidos
        try(Socket socket = new Socket(enderecoIP, portaServidor)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            APDU mensagem  = new APDU("REGISTER", null, nome, null, portaProcesso);
            out.writeObject(mensagem);
            out.flush();
            String resposta = (String)in.readObject(); 
            System.out.println(resposta);  
            return true;
        } catch(Exception e) {
             System.err.println("Erro na comunicacao com o Servidor: " + e.getMessage());
             return false;
        }//fim do try-catch 
 }    
     
 public void receberMensagem() {
    Thread processoReceive = new Thread(() -> {
        try {
            // Cria primeiro o fluxo de saída
            ObjectOutputStream saida =  new ObjectOutputStream(socketConexaoCliente.getOutputStream());
            saida.flush();
            // Depois cria o fluxo de entrada
            ObjectInputStream entrada = new ObjectInputStream(socketConexaoCliente.getInputStream());
            while (true) {
                APDU mensagemRecebida = (APDU) entrada.readObject();
                System.out.println("Mensagem recebida");
                alocarMensagemFila(mensagemRecebida);
                saida.writeObject("Mensagem Recebida");
                saida.flush();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    processoReceive.setDaemon(true);
    processoReceive.start();
}
     
     public boolean SEND(APDU mensagem) {
         //Tenta conectar no IP e porta fornecidos
         System.out.println(enderecoIP);
         System.out.println(portaServidor);
        try(Socket socket = new Socket(enderecoIP, portaServidor)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(mensagem);
            out.flush();
            String resposta = (String)in.readObject();
            System.out.println(resposta);  
            return true;
        } catch(Exception e) {
             e.printStackTrace();
             return false;
        }//fim do try-catch  
       
   }
   
   public String enviarMensagemComRespostaServidor(APDU mensagem) {
      //Tenta conectar no IP e porta fornecidos
        try(Socket socket = new Socket(enderecoIP, portaServidor)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(mensagem);
            out.flush();
            String resposta = (String)in.readObject(); 
            return resposta;
        } catch(Exception e) {
             System.err.println("Erro na comunicacao com o Servidor: " + e.getMessage());
             return null;
        }//fim do try-catch 
   }
   
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
    
     public int portaTCP() {
         return portaProcesso;
     }
     
     
}
