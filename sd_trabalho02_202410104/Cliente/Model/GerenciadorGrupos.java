package Model;

import java.util.concurrent.Semaphore;
import Controller.Registro;
import Protocol.APDU;
import java.util.Hashtable;

public class GerenciadorGrupos {
       private ListaDupla<Grupo> listaGruposConectados;
       private Semaphore mutex;
       private Semaphore cheio;
       private Grupo grupoAtual; // variavel que registra qual o grupo atual do usuario 
       private TabelaMapeamento<FuncaoAltaOrdem<APDU>, APDU> tabelaFuncional;
       private Fila<APDU> filaCompartilhada;
       private Controller.Registro registro;
       private Cliente cliente;
       private Hashtable<String, Controller.VisualizadorMensagem> mensagemPendentes;

       public GerenciadorGrupos(Fila<APDU> filaCompartilhada, Semaphore mutex, Semaphore cheio, Cliente cliente) {
            this.filaCompartilhada = filaCompartilhada;
            this.mutex = mutex;
            this.cheio = cheio;
            this.cliente = cliente;
            this.listaGruposConectados = new ListaDupla();
            this.mensagemPendentes = new Hashtable<String, Controller.VisualizadorMensagem>();
            this.tabelaFuncional = new TabelaMapeamento<FuncaoAltaOrdem<APDU>, APDU>();
            processarAPDUs();
       }
       
        public void gerenciarMensagens() {
          Thread processoGerenciadorGrupos = new Thread(() -> {
             while(true) {
                  down(cheio);
                  down(mutex);
                  try {
                      APDU apdu = filaCompartilhada.desenfileirar();
                      tabelaFuncional.obterItem(new TraduzirAPDU().obterTipoAPDU(apdu.getOperacao()).tipoAtual).funcaoAltaOrdem(apdu);
                  } catch(Exception ex) {
                      ex.printStackTrace();
                  } finally {                   
                      up(mutex);
                 }   
             }
          }); 
          processoGerenciadorGrupos.setDaemon(true);
          processoGerenciadorGrupos.start();
       }
       
        private void processarAPDUs() {
              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SEND, (apdu) -> {
                     Grupo grupo = obterGrupo(apdu.getNomeGrupo());
                     String Usuario = apdu.getNomeUsuario();
                     if(grupo != null) {
                        String mensagem = apdu.getTextoMensagem();
                        grupo.registrarMensagemRecebida(mensagem, Usuario, apdu);
                        cliente.confirmacaoMensagem(Usuario, 2, apdu, apdu.getNomeGrupo());
                        if(grupo == grupoAtual && registro != null) {//basicamente se o grupo
                              registro.alocarTexto(grupo.obterTextoHistoricoGrupo());
                              confirmarMensagem(grupo);
                        }
                     }
              });      
              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.Block, (apdu) -> {
                      registro.emitirAviso(apdu.getTextoMensagem());
              });    
              
              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SENDUV, (apdu) -> {
                      Grupo grupo = obterGrupo(apdu.getNomeGrupo());
                      String Usuario = apdu.getNomeUsuario();
                      if(grupo != null) {
                         String mensagem = apdu.getTextoMensagem();
                         grupo.registrarMensagemUnicaRecebida(mensagem, Usuario, apdu);
                         cliente.confirmacaoMensagem(Usuario, 2, apdu, apdu.getNomeGrupo());
                         if(grupo == grupoAtual && registro != null) {
                               registro.alocarTexto(grupo.obterTextoHistoricoGrupo()); //poderiamos utilizar uma parametrizacaao com funcao de alta ordem como parametro, para constrirmos algo elgentemente reutilizavel, mas ia dar trabalho.
                               confirmarMensagem(grupo);
                         }
                      }
              });

              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.CONFIRM, (apdu) -> {
                      if(apdu.getStatusRecebido() == 1) {
                           mensagemPendentes.get(apdu.getIdMensagem()).adicionarImagem("img/Tick1.png");
                      } else if(apdu.getStatusRecebido() == 2) {
                           mensagemPendentes.get(apdu.getIdMensagem()).adicionarImagem("img/tick3.png");
                      } else if(apdu.getStatusRecebido() == 3) {
                           mensagemPendentes.get(apdu.getIdMensagem()).adicionarImagem("img/tick2.png");
                      }
              });
              
              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.SENDPVT, (apdu) -> {//assim termos de que, dado o recebimento de uma apdu do tipo SendPVT, teremos que o procedimento de processamento da mensagem se dara de maneira similar ao processamento de uma mensagem do tipo SEND comum
                    Grupo UsuarioPrivado = obterGrupo(apdu.getDestinatario()); //o usuario privado tem a mesma semantica de grupo
                    String Usuario = apdu.getNomeUsuario();
                    if(UsuarioPrivado != null) {
                        String mensagem = apdu.getTextoMensagem();
                        UsuarioPrivado.registrarMensagemRecebida(mensagem, Usuario, apdu);
                        cliente.confirmacaoMensagem(Usuario, 2, apdu, apdu.getDestinatario());
                        if(UsuarioPrivado == grupoAtual && registro != null) {//basicamente se o grupo
                              registro.alocarTexto(UsuarioPrivado.obterTextoHistoricoGrupo());
                              confirmarMensagem(UsuarioPrivado);
                        }
                    } else {//para a possibilidade de nao haver nenhum usuario privado registrado com o referido nome associado, temos de que esse sera inserido na estrutura de controle
                       inserir(apdu.getDestinatario());
                       String mensagem = apdu.getTextoMensagem();
                       Grupo NovoUsuario = obterGrupo(apdu.getDestinatario());
                       registro.registrarNovoUsuario(apdu.getDestinatario());
                       if(NovoUsuario == grupoAtual && registro != null) {
                           registro.alocarTexto(UsuarioPrivado.obterTextoHistoricoGrupo());
                           confirmarMensagem(UsuarioPrivado);
                       }
                    }
              });
              
              tabelaFuncional.inserirDado(TraduzirAPDU.TipoAPDU.NEWMISTER, (apdu) -> {//assim termos de que, dado o recebimento de uma apdu do tipo SendPVT, teremos que o procedimento de processamento da mensagem se dara de maneira similar ao processamento de uma mensagem do tipo SEND comum
                    System.out.println("Novo lider eleito: " + apdu.obterPortaTCP() + " ," + apdu.getPortaClienteUDP() + " ," + apdu.obterEnderecoIp());
                    //cliente.alterarPortaTCP(apdu.obterPortaTCP());
                   // cliente.alterarPortaUDP(apdu.getPortaClienteUDP());
                   // cliente.alterarEnderecoIP(apdu.obterEnderecoIp());
                    cliente.novaRequisicaoBroadCast();
              });
        }
       
       public void inserir(String nomeGrupo) {
            //  System.out.println("O grupo inserido eh: " + nomeGrupo);
              Grupo grupo = new Grupo(nomeGrupo);
              listaGruposConectados.inserirFim(grupo);
       }

       public void excluir(String nomeGrupo) {
             Grupo grupo = obterGrupo(nomeGrupo);
             listaGruposConectados.remover(grupo);
       }

       public Boolean verificarConsistencia(String nomeGrupo) {
             for(ListaDupla<Grupo>.Element ptr = listaGruposConectados.getHead(); ptr != null; ptr = ptr.getNext()) {
                 
                 String nome = ((Grupo)ptr.getValor()).obterNomeGrupo();
                 
                 if(nome.equals(nomeGrupo)) {
                     return true;
                 }
             }
             return false;
      }
      
      public void estabelecerGrupoAtual(String nome) {
            Grupo grupo = obterGrupo(nome);
            if(grupo == null) {
                 throw new RuntimeException("inconsistencia nos dados");
            }
            grupoAtual = grupo;
      }

      public void excluirGrupo(String nome) {
          Grupo grupo = obterGrupo(nome);
          if(grupo != null) {
              listaGruposConectados.remover(grupo);
          }
      }
      
      public Grupo obterGrupo(String nomeGrupo) {
             for(ListaDupla<Grupo>.Element ptr = listaGruposConectados.getHead(); ptr != null; ptr = ptr.getNext()) {
                 String nome = ((Grupo)ptr.getValor()).obterNomeGrupo();
                 if(nome.equals(nomeGrupo)) {
                     return (Grupo)ptr.getValor();
                 }
             }
             return null;
      }
      
      public void registrarMensagem(String mensagem) {
                grupoAtual.registrarMensagem(mensagem);
      }
      
      public void alocarMensagemPendente(APDU apdu) {
            mensagemPendentes.put(apdu.getIdMensagem(), grupoAtual.obterMensagemRotuladaAtual()); //assim, associamos o id da mensafgem com o seu rotulo em que esta associada
      }

      public void registrarMensagemUnica(String mensagem) {
                grupoAtual.registrarMensagemUnica(mensagem);
      }
      
      public Grupo obterGrupoAtual() {
               return grupoAtual;
      }
      
      public void estabelecerRegistro(Controller.Registro registro) {
            this.registro = registro;
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

     public void confirmarMensagem(Grupo grupo) {
        if(grupo != null) {
            for(APDU ptr = grupo.obterProximaMensagemPendente(); ptr != null; ptr = grupo.obterProximaMensagemPendente()) {
                 //System.out.println("confirmando");
                 cliente.confirmacaoMensagem(ptr.getNomeUsuario(), 3, ptr, grupo.obterNomeGrupo());
            }
        }    
     }    
}
