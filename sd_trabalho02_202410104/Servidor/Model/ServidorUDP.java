package Model;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import Protocol.APDU;

public class ServidorUDP {
    private DatagramSocket conexaoServidor;  //objeto socket UDP, permitindo um estabelecimento de conexao com quaisquer hosts na rede em que solicita os servicos de comunicacao. Nesse caso, estamos usando os servicos de transporte conforme o protocolo UDP para o estabelecimento de conexoes. 
    private DatagramPacket pacoteRecebido; //representa o pacote recebido pelo servico utilizado pela entidade de transporte que implementa o procolo UDP 
    private static final int portaServidor = 7777; //define a porta para o qual esta associada esse processo de aplicacao em que utilizam os servicos da entidade de transporte
    private String MensagemRecebida = null;
    private Fila<SaidaAPDU> filaCompartilhada, filaGerenciamentoServidores;
    private Semaphore mutex, mutexFilaServidores;
    private Semaphore cheio, cheioFilaServidores;
    private Hashtable<String, Fila<SaidaAPDU>> tabelaFuncionalFilas;
    public ServidorUDP(Fila<SaidaAPDU> filaCompartilhada, Fila<SaidaAPDU> filaGerenciamentoServidores, Semaphore mutex, Semaphore cheio, Semaphore mutexFilaServidores, Semaphore cheioFilaServidores) {
           this.filaCompartilhada = filaCompartilhada; this.filaGerenciamentoServidores = filaGerenciamentoServidores;
           this.tabelaFuncionalFilas = new Hashtable<>();
           this.mutex = mutex; this.mutexFilaServidores = mutexFilaServidores;
           this.cheio = cheio; this.cheioFilaServidores = cheioFilaServidores;
           associarFilaPorOperacao();
    }
    /********************************************************************************************
    * Metodo: gerenciarConexoes
    * Funcao: 
    * Parametros:
    *      - W: função de alta ordem responsável por tratar a edição da sub-rede.
    * Retorno: vazio

    ********************************************************************************************/
    public void gerenciarConexoes() {
     Thread ProcessoReceive = new Thread(() -> {
                               try {
                                    conexaoServidor = new DatagramSocket(portaServidor); //cria um soquete para o processo em que esta vinculado pela porta associado, mantendo um estrutura de dados de controle para garantir essa associacao
                                    byte[] dadosEntrada = new byte[8192]; //define o tamanho da mensagem em que sera recebida
                                    while(true) {  
                                         pacoteRecebido = new DatagramPacket(dadosEntrada, dadosEntrada.length);
                                         conexaoServidor.receive(pacoteRecebido); 
                                         String resposta = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength()).trim();
         
                                         if(!resposta.equals("SERVIDOR_IP") && !resposta.equals("SERVIDOR_IP_NO_ATIVO")) {//evita receber mensagens que nao esteja na formatacao serializada apdu
                                            ByteArrayInputStream dadoEntrada =  new ByteArrayInputStream(pacoteRecebido.getData());
                                            ObjectInputStream entrada = new ObjectInputStream(dadoEntrada);
                                            APDU apdu = (APDU) entrada.readObject();
                                            int porta = pacoteRecebido.getPort();
                                            String enderecoIP = pacoteRecebido.getAddress().getHostAddress();
                                            Fila<SaidaAPDU> fila = tabelaFuncionalFilas.get(apdu.getOperacao());
                                            if(fila == filaCompartilhada) alocarMensagemFila(apdu, fila, mutex, cheio);
                                            if(fila == filaGerenciamentoServidores) alocarMensagemFila(apdu, fila, mutexFilaServidores, cheioFilaServidores);
                                         }
                                    }   
                               } catch(Exception ex) {
                                     ex.printStackTrace();
                               } 
                          }); 
      ProcessoReceive.start();                     
    }
    
    public void enviarMensagens(APDU mensagems, String ipDestino, int portaDestino) {
         try {
             ByteArrayOutputStream dados = new ByteArrayOutputStream();
             ObjectOutputStream saida = new ObjectOutputStream(dados);
             saida.writeObject(mensagems);
             saida.flush();
             byte[] dadosSaida = dados.toByteArray();
             InetAddress endereco = InetAddress.getByName(ipDestino);
             DatagramPacket pacote = new DatagramPacket(dadosSaida, dadosSaida.length, InetAddress.getByName(ipDestino), portaDestino);
             conexaoServidor.send(pacote);
         } catch(Exception ex) {
            ex.printStackTrace(); 
         }
    }
    
    private void associarFilaPorOperacao() {
        this.tabelaFuncionalFilas = new Hashtable<String, Fila<SaidaAPDU>>();
        tabelaFuncionalFilas.put("SEND", filaCompartilhada);
        tabelaFuncionalFilas.put("LEAVE", filaCompartilhada);
        tabelaFuncionalFilas.put("JOIN", filaCompartilhada);
        tabelaFuncionalFilas.put("Block", filaCompartilhada);
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
    
     private void alocarMensagemFila(APDU apdu, Fila<SaidaAPDU> filaCompartilhadaParametro,  Semaphore mutexParametro, Semaphore cheioParametro) {
          downMutex(mutexParametro);
            try {
                if(!filaCompartilhadaParametro.cheia()) {
                    filaCompartilhadaParametro.enfileirar(new SaidaAPDU(apdu));
                    upMutex(cheioParametro);
                } 
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
               upMutex(mutexParametro);
            }
          
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
    
    public int obterPortaUDP() {
        return portaServidor;
    }
}
