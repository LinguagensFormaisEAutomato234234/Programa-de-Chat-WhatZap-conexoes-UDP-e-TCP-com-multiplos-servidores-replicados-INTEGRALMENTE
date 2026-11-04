package Model;

import java.util.concurrent.Semaphore;
import Controller.Registro;
import Protocol.APDU;

public class Servidor {
    private ServidorUDP servidorUDP;
    private ServidorTCP servidorTCP;
    private ServidorBroadCast servidorBroadCast;
    private Semaphore mutex, mutexFilaServidores, mutexListaServidoresAtivos; 
    private GerenciadorGrupos gerenciadorGrupos; //mantem uma estrutura de dados para gerenciar grupos, como uma listas de listas. Neste caso, optei por uma tabela hash de listas 
    private Semaphore cheio, cheioFilaServidores;
    private Registro registro;
    private EstruturaControle estruturaControle;
    private ListaDupla<ServidoresAtivos> listaServidoresAtivos;
    private boolean ServidorLider; //determina se este servidor eh o lider entre o conjunto de servidores 
    private Fila<SaidaAPDU> filaCompartilhada, filaGerenciamentoServidores; //estrutura de dados em que recebe as APDUS
    private GerenciadorServidores gerenciadorServidor;
    public Servidor(Registro registro) {//o registro eh somente um ponteiro que permite editar na interface grafica de log
         this.mutex = new Semaphore(1); this.mutexFilaServidores = new Semaphore(1); this.mutexListaServidoresAtivos = new Semaphore(1);
         this.cheio = new Semaphore(0); this.cheioFilaServidores = new Semaphore(0);
         this.filaCompartilhada = new Fila<>();
         this.listaServidoresAtivos = new ListaDupla<>();
         this.filaGerenciamentoServidores = new Fila<>();
         this.estruturaControle = new EstruturaControle();
         this.ServidorLider = false;
         this.servidorUDP = new ServidorUDP(filaCompartilhada, filaGerenciamentoServidores, mutex, cheio, mutexFilaServidores, cheioFilaServidores);//instanciamos a classe em que gerencia os processos em que usam a aplicacao servidorUDP
         this.registro = registro;
         this.servidorBroadCast = new ServidorBroadCast(registro, this);
         this.servidorTCP = new ServidorTCP(filaCompartilhada, filaGerenciamentoServidores, mutex, cheio, mutexFilaServidores, cheioFilaServidores, estruturaControle);
         this.gerenciadorServidor = new GerenciadorServidores(filaGerenciamentoServidores, servidorTCP, servidorUDP, mutexFilaServidores, cheioFilaServidores, registro, servidorBroadCast.obterEnderecoIpLocal(), listaServidoresAtivos, mutexListaServidoresAtivos);
         this.gerenciadorServidor.setEstruturaControler(estruturaControle);
    }
    /**
     *  funcao : inicializa processos que usam servicos TCP e UDP, dependendo da forma em que foi especificada na APDU.  
     *  parametros: vazio
     *  retorno: vazio
     **/
     public void gerenciarServidor() {
           gerenciadorGrupos = new GerenciadorGrupos(filaCompartilhada, mutex, cheio, registro, this, estruturaControle, mutexListaServidoresAtivos, listaServidoresAtivos);
           gerenciadorGrupos.gerenciarMensagens();
           servidorUDP.gerenciarConexoes();
           servidorTCP.gerenciarConexoes();
           servidorBroadCast.gerenciarConexoes();
           gerenciadorServidor.setEstruturaDados(gerenciadorGrupos.obterEstruturaDados());
           gerenciadorServidor.start();//inicializa o processo responsavel por processar requisicoes de processos da camada de aplicacao
     }
     
     /**
     *  funcao : envia mensagens para os cliente via processos UDP e TCP da camada de transporte  
     *  parametros: vazio
     *  retorno: vazio
     **/
     public void enviarMensagemSend(APDU mensagem, String enderecoIP, int porta) {
        servidorUDP.enviarMensagens(mensagem, enderecoIP, porta);
     }
     
     public String enviarmensagemTCP(APDU mensagem, String enderecoIP, int porta) {
         return servidorTCP.sendTCP(mensagem, enderecoIP, porta);
     }
     
     
     public void enviarMensagemBlockUnblock(String enderecoIP, int porta, String mensagem) {
          APDU mensagemEnviar = new APDU("BLOCK", null, mensagem, null, porta);
          servidorUDP.enviarMensagens(mensagemEnviar, enderecoIP, porta);
     }
     
     public EstruturaControle obterEstruturaControle() {
         return estruturaControle;
     }
     
     public String obterEnderecoIpLocal() {
         return servidorBroadCast.obterEnderecoIpLocal();
     }
     
     public int obterPortaTCPLocal() {
        return servidorTCP.obterPortaTCP();
     }
     
     public int obterPortaUDPLocal() {
        return servidorUDP.obterPortaUDP();
     }
     
     public boolean esteServidorEhlider() {
            return gerenciadorServidor.sevidorLider();
     }
}
