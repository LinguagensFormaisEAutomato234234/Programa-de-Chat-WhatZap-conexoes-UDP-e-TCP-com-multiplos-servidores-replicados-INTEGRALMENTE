/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 01/07/2026
*Ultima alteracao.: 01/07/2026 
*Nome.............: GerenciadorGrupos
*Funcao...........: processar cada APDU recebeida pela rede de tal forma que administra diferentes grupos na estrutura de dados representada em ListaDupla<GRUPOS>, uma lista de grupo cada obtendo uma lista de usuario
**************************************************************************************************/


package Model;
import java.util.concurrent.Semaphore;
import Controller.Registro;
import Protocol.APDU;
import Protocol.APDUDATA;

public class GerenciadorGrupos {
     private ListaDupla<Grupos> listaGrupos; //eu poderia ter usado uma tabela hash, mas estou com preguica de implementar
     //definir uma fila de eventos para chegada 
     private Fila<SaidaAPDU> filaCompartilhada;
     private ListaDupla<ServidoresAtivos> listaServidoresAtivos;
     private Semaphore mutex;
     private Semaphore cheio;
     private Semaphore mutexListaServidoresAtivos;
     private TabelaMapeamento<FuncaoAltaOrdem<SaidaAPDU>, SaidaAPDU> tabelaFuncional;
     private Registro registro;
     private Servidor servidor; //oferece serviços de rede
     private EstruturaControle estruturaControle;
     private AdministrarMensagensPendentes administrarMensagensPendentes;
     private ProcessoAPDU processoAPDU;
     private EstruturaDados estruturaDados;
     public GerenciadorGrupos(Fila<SaidaAPDU> filaCompartilhada, Semaphore mutex, Semaphore cheio, Registro registro, Servidor servidor, EstruturaControle estruturaControle, Semaphore mutexListaServidoresAtivos, ListaDupla<ServidoresAtivos> listaServidoresAtivos) {
            this.filaCompartilhada = filaCompartilhada;
            this.mutex = mutex;
            this.cheio = cheio;
            this.servidor = servidor;
            this.registro = registro;
            this.mutexListaServidoresAtivos = mutexListaServidoresAtivos;
            this.listaServidoresAtivos = listaServidoresAtivos;
            this.estruturaControle = estruturaControle;
            this.administrarMensagensPendentes = new AdministrarMensagensPendentes();
            listaGrupos = new ListaDupla<Grupos>();
            this.processoAPDU = new ProcessoAPDU(servidor.obterEstruturaControle(), administrarMensagensPendentes);
            this.tabelaFuncional = new TabelaMapeamento<FuncaoAltaOrdem<SaidaAPDU>, SaidaAPDU>();
            estruturaDados = new EstruturaDados(administrarMensagensPendentes, listaGrupos, estruturaControle);
            processarAPDUs();
     }   
     
     
     /********************************************************************************************
     * Metodo: gerenciarMensagens
     * Funcao: definir um processo consumidor em que reaja a chegada de diferentes mensagens requeridas pela rede com base numa apdu, um protocolo de comunicacao padronizada. 
     * Parametros: Vazio
     * Retorno: Vazio
      ********************************************************************************************/
     public void gerenciarMensagens() {
          Thread processoGerenciadorGrupos = new Thread(() -> {
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
          }); 
          processoGerenciadorGrupos.start();
     }//fim do metodo gerenciarMensagens
     
     /********************************************************************************************
     * Metodo: processarAPDUs
     * Funcao: definir, para cada mensagem protocolada em que chegou pela rede, uma acao associado a ela. Como acao de juntar ao grupo, sair bloquear e desbloquear, simobolizando-os por TipoAPDU, em que atibui uma indexacao numerica para tratar cada mensagem protocolizada associando a uma funcao de alta ordem em que define o comportamente de cada apdu
     * Parametros: Vazio
     * Retorno: Vazio
      ********************************************************************************************/ 
    private void processarAPDUs() {
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.JOIN, (apdusaida) -> {
                 processoAPDU.processoJuntar(procurar(apdusaida.obterMensagem().getNomeGrupo()), apdusaida, registro, listaGrupos);
                 replicardados();
            });
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SEND, (apdusaida) -> {
                  processoAPDU.enviarMensagemSend(procurar(apdusaida.obterMensagem().getNomeGrupo()), apdusaida, registro, listaGrupos, servidor);
                  replicardados();
            });  
              
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.LEAVE, (apdusaida) -> {
                  processoAPDU.deixarGrupo(procurar(apdusaida.obterMensagem().getNomeGrupo()), apdusaida, registro, listaGrupos);
                  replicardados();
            });
              
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.Block, (apdusaida) -> {
                  processoAPDU.bloquearUsuario(estruturaControle, apdusaida, registro, listaGrupos, servidor);
                  replicardados();
            });
              
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.UNBLOCK, (apdusaida) -> {
                  processoAPDU.desbloquearUsuario(estruturaControle, apdusaida, registro, listaGrupos, servidor);
                  replicardados();
            });
            
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SENDUV, (apdusaida) -> {
                  
                  processoAPDU.enviarMensagemVizualizacaoUnica(procurar(apdusaida.obterMensagem().getNomeGrupo()), apdusaida, registro, listaGrupos, servidor);
                  replicardados();
            });
            
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.CONFIRM, (apdusaida) -> {
                processoAPDU.processarAPDUConfirmacao(procurar(apdusaida.obterMensagem().getNomeGrupo()), registro, servidor, apdusaida);
                replicardados();
            });

            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.REGISTER, (apdusaida) -> {
                  processoAPDU.REGISTRO(apdusaida, registro);
                  replicardados();
            });

            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.LIST, (apdusaida) -> {
                  processoAPDU.processarApduList(listaGrupos, registro, servidor, apdusaida);
            });

            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SENDPVT, (apdusaida) -> {
                  processoAPDU.processarAPDUEnvioMensagemPrivada(apdusaida, estruturaControle, registro, servidor);
                  replicardados();
            });

            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.Users, (apdusaida) -> {
                  processoAPDU.processarApduUsers(registro, servidor, apdusaida);
            });
            
            tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.UPDATEDATA, (apdusaida) -> {
                 estruturaControle.setListaUsuario(((APDUDATA)apdusaida.obterMensagem()).obterEstruturaControle().obterListaUsuario());
                 listaGrupos = ((APDUDATA)apdusaida.obterMensagem()).obterGrupo();
                 administrarMensagensPendentes = ((APDUDATA)apdusaida.obterMensagem()).obterAdminstradorMensagens();
                 registro.registrarMensagem("Estrutura de dados : atualizada e replicada");
                 processoAPDU.enviarMensagemTCP(apdusaida, "Ok, recebido");
            });
    }

    /********************************************************************************************
     * Metodo: imprimirGrupos
     * Funcao: imprimirGrupos 
     * Parametros: Vazio
     * Retorno: Vazio
      ********************************************************************************************/ 
    private void imprimirGrupos() {
           String res = "";
           for(ListaDupla.Element ptr = listaGrupos.getHead(); ptr != null; ptr = ptr.getNext()) {
                 res = ((Grupos)ptr.getValor()).obterNomeGrupo();
                 res = res + " : " + ((Grupos)ptr.getValor()).imprimirUsuarios();
           }
            
           System.out.println(res);  
    }//fim da funcao imprimirGrupos
    
     /********************************************************************************************
     * Metodo: procurar
     * Funcao: retornar um grupo dada a expecificação de um nome 
     * Parametros: STRING : nomeGRUPO
     * Retorno: GRUPO
      ********************************************************************************************/ 
    private Grupos procurar(String nomeGrupo) {
           for(ListaDupla.Element ptr = listaGrupos.getHead(); ptr != null; ptr = ptr.getNext()) {
                   if(((Grupos)ptr.getValor()).obterNomeGrupo().equals(nomeGrupo)) {
                       return (Grupos)ptr.getValor();
                   }
           }          
           return null; 
    } // fim do metodo procurar
    
    private void replicardados() {
      down(mutexListaServidoresAtivos);
        estruturaDados.setListaGrupos(listaGrupos);
        estruturaDados.setEstruturaControle(estruturaControle);
        estruturaDados.setAdministrarMensagensPendentes(administrarMensagensPendentes); 
        for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
             int portaTCP = ((ServidoresAtivos)ptr.getValor()).obterPortaTCP();
             int portaUDP = ((ServidoresAtivos)ptr.getValor()).obterPortaUDP();
             String enderecoIP = ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP();
             registro.registrarMensagem("Evindando replica de dados para o servidor ( PortaTCP: " + portaTCP + "," + " PortaUDP: " + portaUDP + " enderecoIP: " + enderecoIP + ")");
             if(portaTCP != servidor.obterPortaTCPLocal() || portaUDP != servidor.obterPortaUDPLocal() || !enderecoIP.equals(servidor.obterEnderecoIpLocal())) {
               APDU mensagem = new APDUDATA("UPDATEDATA", portaTCP, portaUDP, enderecoIP, listaGrupos , estruturaControle, administrarMensagensPendentes);
               if(servidor.enviarmensagemTCP(mensagem, enderecoIP, portaTCP) == null) registro.registrarMensagem("Servidor nao esta ativo na rede");
             }
        }
      up(mutexListaServidoresAtivos);   
    }

    private void down(Semaphore semaforo) {
            try {
                 semaforo.acquire();
            } catch(Exception ex) {
                 ex.printStackTrace();
            }
    }

    private void up(Semaphore semaforo) {
           semaforo.release();
    }

    public EstruturaDados obterEstruturaDados() {
           return estruturaDados;
    }
}
