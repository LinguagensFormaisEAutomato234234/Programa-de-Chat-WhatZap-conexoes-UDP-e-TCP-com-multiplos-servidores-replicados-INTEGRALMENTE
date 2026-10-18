package Model;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;
import Protocol.APDU;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.io.EOFException;

public class ServidorTCP {
      private static final int portaLocal = 6789;
      private ServerSocket servidor;
      private Semaphore mutex, mutexFilaServidores;
      private Semaphore cheio, cheioFilaServidores;
      private Fila<SaidaAPDU> filaCompartilhada, filaGerenciamentoServidores;
      private Hashtable<String, Fila<SaidaAPDU>> tabelaFuncionalFilas;
      private EstruturaControle estruturaControle;
      private ObjectInputStream entrada;
      private ObjectOutputStream saida;
      public ServidorTCP(Fila<SaidaAPDU> filaCompartilhada, Fila<SaidaAPDU> filaGerenciamentoServidores, Semaphore mutex, Semaphore cheio, Semaphore mutexFilaServidores, Semaphore cheioFilaServidores, EstruturaControle estruturaControle) {
             this.tabelaFuncionalFilas = new Hashtable<>();
             this.filaCompartilhada = filaCompartilhada; this.filaGerenciamentoServidores = filaGerenciamentoServidores;
             this.estruturaControle = estruturaControle; 
             this.mutex = mutex; this.mutexFilaServidores = mutexFilaServidores;
             this.cheio = cheio; this.cheioFilaServidores = cheioFilaServidores;
             associarFilaPorOperacao();
      }
   
    /********************************************************************************************
     * Metodo: gerenciarConexoes
     * Funcao: receber mensagens via o protocolo TCP da camada de transporte.
     * Parametros: vazio
     * Retorno: vazio
     ********************************************************************************************/
    public void gerenciarConexoes() {
        try {  
              servidor = new ServerSocket(portaLocal);
        } catch(Exception ex) {
               ex.printStackTrace();
        }

         Thread ProcessoReceberMensagem = new Thread(() -> {
             try {
               while(true) {
                 Socket conexao = servidor.accept(); //entra em estado de listem, para esatabelecer uma conexao
                 
                 String enderecoIP = conexao.getInetAddress().getHostAddress();
                 Thread ProcessoReceber = new Thread(() -> {
                      try {
                          ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());
                          ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());
                         while(true) {
                           
                            APDU mensagemRecebida = (APDU)entrada.readObject();
                            int porta = mensagemRecebida.getPortaClienteUDP();
                            if(mensagemRecebida.getOperacao().equals("REGISTER")) {
                               if(!estruturaControle.verificarRedundanciaUsuario(mensagemRecebida.getNomeUsuario(), enderecoIP)) {
                                  estruturaControle.registrarNome(mensagemRecebida.getNomeUsuario(), enderecoIP, porta);
                                  saida.writeObject("OK: registrado");
                               } else {
                                  conexao.close();
                               }
                            } else {
                               System.out.println(mensagemRecebida.getOperacao());
                               Fila<SaidaAPDU> fila = tabelaFuncionalFilas.get(mensagemRecebida.getOperacao());
                               if(fila == filaCompartilhada) alocarMensagemFila(new SaidaAPDU(mensagemRecebida, saida), fila, mutex, cheio);
                               if(fila == filaGerenciamentoServidores) alocarMensagemFila(new SaidaAPDU(mensagemRecebida, saida), fila, mutexFilaServidores, cheioFilaServidores);
                            }
                         } 
                      } catch(EOFException ex) {
                      } catch(Exception ex) {
                         ex.printStackTrace();
                      }
                 }); 
                 ProcessoReceber.start();
               }
             } catch(Exception ex) {
               ex.printStackTrace();
             }  
         });
         ProcessoReceberMensagem.start();
    }
    
    public String sendTCP(APDU mensagem, String enderecoIP, int portaServidorTCP) {
       
        try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(enderecoIP, portaServidorTCP),1000);
            socket.setSoTimeout(1000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            out.writeObject(mensagem);
            out.flush();
            String resposta = (String)in.readObject();
            return resposta;
        } catch(Exception e) {
             return null;
        }//fim do try-catch  
        
    }

    private void alocarMensagemFila(SaidaAPDU saidaApdu, Fila<SaidaAPDU> filaCompartilhadaParametro,  Semaphore mutexParametro, Semaphore cheioParametro) {
          downMutex(mutexParametro);
            try {
                if(!filaCompartilhadaParametro.cheia()) {
                    filaCompartilhadaParametro.enfileirar(saidaApdu);
                    upMutex(cheioParametro);
                } 
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
               upMutex(mutexParametro);
            }
          
    }
    
    private void associarFilaPorOperacao() {
        this.tabelaFuncionalFilas = new Hashtable<String, Fila<SaidaAPDU>>();
        tabelaFuncionalFilas.put("SEND", filaCompartilhada);
        tabelaFuncionalFilas.put("LEAVE", filaCompartilhada);
        tabelaFuncionalFilas.put("JOIN", filaCompartilhada);
        tabelaFuncionalFilas.put("BLOCK", filaCompartilhada);
        tabelaFuncionalFilas.put("SENDVU", filaCompartilhada);
        tabelaFuncionalFilas.put("UNBLOCK", filaCompartilhada);
        tabelaFuncionalFilas.put("CONFIRM", filaCompartilhada);
        tabelaFuncionalFilas.put("SENDPVT", filaCompartilhada); 
        tabelaFuncionalFilas.put("LIST", filaCompartilhada);
        tabelaFuncionalFilas.put("SinalizarExstencia", filaGerenciamentoServidores);
        tabelaFuncionalFilas.put("RequestMaster", filaGerenciamentoServidores);
        tabelaFuncionalFilas.put("TIMESTAMPTEST", filaGerenciamentoServidores);
        tabelaFuncionalFilas.put("UPDATEDATA", filaCompartilhada);
        tabelaFuncionalFilas.put("CONNECTIONREQUEST", filaGerenciamentoServidores);
    }

    private void downMutex(Semaphore semaforo) {
            try {
                 semaforo.acquire();
            } catch(Exception ex) {
                 ex.printStackTrace();
            }
    }

    private void upMutex(Semaphore semaforo) {
           semaforo.release();
    }
    
    public int obterPortaTCP() {
        return portaLocal;
    }
}
