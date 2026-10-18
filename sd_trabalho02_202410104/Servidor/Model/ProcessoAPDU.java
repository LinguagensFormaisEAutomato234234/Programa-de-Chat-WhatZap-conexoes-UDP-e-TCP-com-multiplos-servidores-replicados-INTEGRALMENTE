/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 01/07/2026
*Ultima alteracao.: 01/07/2026 
*Nome.............: ProcessoAPDU
*Funcao...........: define os comportamentos para cada tipo de unidade de dados de aplicação, como juntar grupo, deixar grupo e bloquear usuario 
**************************************************************************************************/

package Model;
import Protocol.APDU;

public class ProcessoAPDU {
   private EstruturaControle estruturaControle;
   private AdministrarMensagensPendentes administrarMensagensPendentes;
   public ProcessoAPDU(EstruturaControle estruturaControle, AdministrarMensagensPendentes administrarMensagensPendentes) {
       this.estruturaControle = estruturaControle;
       this.administrarMensagensPendentes = administrarMensagensPendentes; 
   }
   
   /********************************************************************************************
   * Metodo: processoJuntar
   * Funcao: definrir os procedimentos necessarios para incluir um novo usuario num grupo
   * Parametros: Vazio
   * Retorno: Vazio
   ********************************************************************************************/
   public void processoJuntar(Grupos grupo, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos) {     
                     Usuario usuario = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getNomeUsuario());
                    //System.out.println(apdusaida.obterMensagem().getNomeUsuario());
                     if(grupo != null) {
                            grupo.inserirUsuario(usuario); //se o usuario ja existir, essa insercao eh ignorada. 
                            enviarMensagemTCP(apdusaida, "Usuario ja existente no grupo");
                     } else {
                             grupo = new Grupos(apdusaida.obterMensagem().getNomeGrupo());
                             listaGrupos.inserirFim(grupo);
                             grupo.inserirUsuario(usuario);
                             enviarMensagemTCP(apdusaida, "Novo grupo criado");
                     }
                     
                     String registroLog = "Usuario " + usuario.obterNome() + " se juntou ao grupo " + grupo.obterNomeGrupo() + " endereco IP: " + usuario.obterEnderecoIp();
                     
                     registro.registrarMensagem(registroLog);
   }//fim do metodo processoJuntar
   
   
    /********************************************************************************************
     * Metodo: bloquearUsuario
     * Funcao: definrir os procedimentos necessarios para bloquear um usuario num grupo
     * Parametros: Vazio
     * Retorno: Vazio
      ********************************************************************************************/
   public void bloquearUsuario(EstruturaControle estruturaControle, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos, Servidor servidor) {
                             Usuario usuarioBloqueado = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getDestinatario());
                             Usuario usuarioSolicitente = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getNomeUsuario());
                             String registroLog = "";
                             if(usuarioBloqueado == null) {
                                  registroLog =  "Usuario a ser bloqueado inexistente";
                                  enviarMensagemTCP(apdusaida, "Usuario nao encontrado");
                            } else if(usuarioSolicitente == null) {
                                  registroLog =  "Usuario solicitante inexistente";
                                  enviarMensagemTCP(apdusaida, "Usuario solicitante inexistente");
                            } else if(usuarioBloqueado.obterNome().equals(usuarioSolicitente.obterNome())) {
                                enviarMensagemTCP(apdusaida, "Nao pode bloquear a si mesmo");
                                registroLog =  "Usuario " + usuarioBloqueado.obterNome() + " tentou bloquear a si mesmo, kkkkk";
                             } else {
                                usuarioSolicitente.bloquearUsuario(usuarioBloqueado);
                                registroLog =  "Usuario " + usuarioBloqueado.obterNome() + " bloqueado pelo usuario " + usuarioSolicitente.obterNome();
                                enviarMensagemTCP(apdusaida, "usuario bloqueado");
                             }
                             
                             System.out.println("Bloqueando usuario: " + usuarioBloqueado.obterNome() + " Solicitado por: " + usuarioSolicitente.obterNome()); 
                             registro.registrarMensagem(registroLog);
                
  }//fim do metodo bloquear usuaario
  
  /********************************************************************************************
  * Metodo: verificacaoLider
  * Funcao: permite a verificacao de que se este servidor eh o lider dos N processos servidores da camada de aplicacao
  * Parametros: Registro : registro, Servidor : servidor, SaidaAPDU : apdusaida
  * Retorno: Vazio
  ********************************************************************************************/
  public void verificacaoLider(Controller.Registro registro, Servidor servidor, SaidaAPDU apdusaida) {
  
  }
  
  /********************************************************************************************
  * Metodo: desbloquearUsuario
  * Funcao: definrir os procedimentos necessarios para desbloquear um usuario num grupo
  * Parametros: Vazio
  * Retorno: Vazio
  ********************************************************************************************/
  public void desbloquearUsuario(EstruturaControle estruturaControle, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos, Servidor servidor) {
                             Usuario usuarioBloqueado = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getDestinatario());
                             Usuario usuarioSolicitente = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getNomeUsuario());
                             String registroLog = "";
                             if(usuarioBloqueado == null) {
                                  registroLog =  "Usuario a ser desbloqueado inexistente";
                                  enviarMensagemTCP(apdusaida, "Usuario nao encontrado");
                            } else if(usuarioSolicitente == null) {
                                  registroLog =  "Usuario solicitante inexistente";
                                  enviarMensagemTCP(apdusaida, "Usuario solicitante inexistente");
                            } else if(usuarioBloqueado.obterNome().equals(usuarioSolicitente.obterNome())) {
                                enviarMensagemTCP(apdusaida, "Nao pode desbloquear a si mesmo");
                                registroLog =  "Usuario " + usuarioBloqueado.obterNome() + " tentou bloquear a si mesmo, kkkkk";
                             } else {
                                usuarioSolicitente.desbloquearUsuario(usuarioBloqueado);
                                registroLog =  "Usuario " + usuarioBloqueado.obterNome() + " desbloqueado pelo usuario " + usuarioSolicitente.obterNome();
                                enviarMensagemTCP(apdusaida, "usuario bloqueado");
                             }
                             
                             System.out.println("Bloqueando usuario: " + usuarioBloqueado.obterNome() + " Solicitado por: " + usuarioSolicitente.obterNome()); 
                             registro.registrarMensagem(registroLog);
                          
  }//fim do metodo desbloquearUsuario
  
  
   /********************************************************************************************
  * Metodo: deixarGrupo
  * Funcao: definrir os procedimentos necessarios para um usuario deixar um grupo
  * Parametros: Grupo : grupo, APDU : apdu, Controller.Registro : registro, ListaDupla<Grupos> : listaGrupos
  * Retorno: Vazio
  ********************************************************************************************/
  public void deixarGrupo(Grupos grupo, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos) {
                         if(grupo != null) {
                             Usuario usuario = grupo.obterUsuario(apdusaida.obterMensagem().getNomeUsuario());
                             grupo.remover(usuario);
                             String registroLog =  "Usuario " + usuario.obterNome() + " solicitou remocao do grupo " + grupo.obterNomeGrupo() + " endereco IP do usuario: " + usuario.obterEnderecoIp();
                             enviarMensagemTCP(apdusaida, "Voce saiu do grupo " + grupo.obterNomeGrupo());
                             registro.registrarMensagem(registroLog);
                         }
   }//fim do metodo deixarGrupo

   
  /********************************************************************************************
  * Metodo: REGISTRO
  * Funcao: definrir os procedimentos necessarios para um usuario deixar um grupo
  * Parametros: Grupo : grupo, APDU : apdu, Controller.Registro : registro, ListaDupla<Grupos> : listaGrupos
  * Retorno: Vazio
  ********************************************************************************************/
  public void REGISTRO(SaidaAPDU apdusaida, Controller.Registro registro) {
              String nomeUsuario = apdusaida.obterMensagem().getNomeUsuario();
              String registroLog =  "Usuario " + nomeUsuario + " adicionado na estrutura de controle geral";
              registro.registrarMensagem(registroLog);
   }//fim do metodo deixarGrupo
  
  /********************************************************************************************
  * Metodo: enviarMensagem
  * Funcao: definrir os procedimentos necessarios para um usuario deixar um grupo
  * Parametros: (Grupo : grupo), (APDU : apdu), (Controller.Registro : registro), (ListaDupla<Grupos> : listaGrupos), (Servidor : servidor)
  * Retorno: Vazio
  ********************************************************************************************/
  public void enviarMensagem(Grupos grupo, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos,  Servidor servidor) {
                     if(grupo != null) {
                        ListaDupla<Usuario> listaUsuarios = grupo.obterListaUsuarios();
                        Usuario usuario = grupo.obterUsuario(apdusaida.obterMensagem().getNomeUsuario()); //obtem o usuario de origem
                        grupo.registrarMensagemPendente(apdusaida.obterMensagem().getTextoMensagem());
                        APDU Confirm = new APDU("CONFIRM", apdusaida.obterMensagem().getIdMensagem(), 1, "Servidor", grupo.obterNomeGrupo(), usuario.obterNome());//enviamos a apdu de confirmacao de mensagem de volta para o destinatario
                        servidor.enviarMensagemSend(Confirm, usuario.obterEnderecoIp(), usuario.ObterPortaUDP());
                        administrarMensagensPendentes.adicionarMensagemPendente(apdusaida.obterMensagem().getIdMensagem(), grupo.obterListaUsuarios());//registra a mensagem na lista pendente de 
                        if(grupo.quantidadeMembros() == 1) {
                                 servidor.enviarMensagemSend(new APDU("CONFIRM", apdusaida.obterMensagem().getIdMensagem(), 3, "Servidor", grupo.obterNomeGrupo(), usuario.obterNome()), usuario.obterEnderecoIp(), usuario.ObterPortaUDP()); //se existir somente esse usuario no grupo, retornamos imediatamente o id do usuario para o grupo original        
                                 
                        }
                        for(ListaDupla.Element ptr = listaUsuarios.getHead(); ptr != null && usuario != null; ptr = ptr.getNext()) {//propagacao por inundacao
                                  String endereco = ((Usuario)ptr.getValor()).obterEnderecoIp();
                                  int portaOrigem = ((Usuario)ptr.getValor()).ObterPortaUDP();

                                  if(!usuario.verificarSeEhIgual(((Usuario)ptr.getValor()).obterNome()) && !(usuario.verificarUsuarioBloqueado(((Usuario)ptr.getValor()))  || ((Usuario)ptr.getValor()).verificarUsuarioBloqueado(usuario))) {
                                         //  System.out.println("enviando mensagem para " + ((Usuario)ptr.getValor()).ObterPortaUDP());
                                           servidor.enviarMensagemSend(apdusaida.obterMensagem(), endereco, portaOrigem);
                                  }  
                        }

                        if(usuario != null) {
                          String registroLog = "Usuario " + usuario.obterNome() + " enviou mensagem ao grupo " + grupo.obterNomeGrupo() + " endereco IP do usuario: " + usuario.obterEnderecoIp() + " Mensagem: " + apdusaida.obterMensagem().getTextoMensagem();
                           registro.registrarMensagem(registroLog);
                        }
                     }  
   }//fim do metodo enviarMensagem
   
   public void enviarMensagemSend(Grupos grupo, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos,  Servidor servidor) {
          enviarMensagem(grupo, apdusaida, registro, listaGrupos, servidor);
   }
   

  /********************************************************************************************
  * Metodo: enviarMensagemVizualizacaoUnica
  * Funcao: enviar mensagem com a flag ou apdu de sinzalizacao sendUV, o que significa mensagem de visualizacao unica.  
  * Parametros: (Grupos : grupo), (APDU : apdu), (Controller.Registro : registro), (ListaDupla<Grupos> : listaGrupos), (Servidor : servidor)
  * Retorno: Vazio
  ********************************************************************************************/
   public void enviarMensagemVizualizacaoUnica(Grupos grupo, SaidaAPDU apdusaida, Controller.Registro registro, ListaDupla<Grupos> listaGrupos,  Servidor servidor) {
           enviarMensagem(grupo, apdusaida, registro, listaGrupos, servidor);     
   }//fim do metodo enviarMensagemVizualizacaoUnica
   
   
  /********************************************************************************************
  * Metodo: enviarMensagemVizualizacaoUnica
  * Funcao: enviar mensagem com a flag ou apdu de sinzalizacao sendUV, o que significa mensagem de visualizacao unica.  
  * Parametros: (Grupos : grupo), (APDU : apdu), (Controller.Registro : registro), (ListaDupla<Grupos> : listaGrupos), (Servidor : servidor)
  * Retorno: Vazio
  ********************************************************************************************/
   public void enviarMensagemVizualizacaoUnicaPrivada(SaidaAPDU apdusaida, EstruturaControle estruturaControle, Controller.Registro registro, Servidor servidor) {
               processarAPDUEnvioMensagemPrivada(apdusaida, estruturaControle, registro, servidor);
   }//fim do metodo enviarMensagemVizualizacaoUnica
   

   public void processarAPDUConfirmacao(Grupos grupo, Controller.Registro registro, Servidor servidor, SaidaAPDU apdusaida) {
              if(grupo != null) {
                  Usuario usuarioOrigem = grupo.obterUsuario(apdusaida.obterMensagem().getDonoDaMensagem());
                  int portaOrigem =  usuarioOrigem.ObterPortaUDP();
                  String endereco = usuarioOrigem.obterEnderecoIp();
                  if(apdusaida.obterMensagem().getStatusRecebido() == 2) { 
                    if(administrarMensagensPendentes.acumularMensagemPendenteEnvio(apdusaida.obterMensagem().getIdMensagem())) servidor.enviarMensagemSend(apdusaida.obterMensagem(), endereco, portaOrigem);
                  } else if(apdusaida.obterMensagem().getStatusRecebido() == 3) {
                    if(administrarMensagensPendentes.acumularMensagemPendenteLeitura(apdusaida.obterMensagem().getIdMensagem())) servidor.enviarMensagemSend(apdusaida.obterMensagem(), endereco, portaOrigem);
                  }
              }
            enviarMensagemTCP(apdusaida, "mensagem recebida");
   }

   public void processarAPDUEnvioMensagemPrivada(SaidaAPDU apdusaida, EstruturaControle estruturaControle, Controller.Registro registro, Servidor servidor) {
          Usuario Remetente = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getNomeUsuario());
          Usuario Destinatario = estruturaControle.procurarUsuario(apdusaida.obterMensagem().getDestinatario());
          String registroLog = "";
          if(Remetente.verificarUsuarioBloqueado(Destinatario)) {
               registroLog = "[SENDPVT-BLOQUEADO] " + apdusaida.obterMensagem().getDestinatario() + " esta bloqueado por " + apdusaida.obterMensagem().getNomeUsuario();
          } else if(Destinatario.verificarUsuarioBloqueado(Remetente)) {
                registroLog = "[SENDPVT-BLOQUEADO] " + apdusaida.obterMensagem().getNomeUsuario() + " esta bloqueado por " + apdusaida.obterMensagem().getDestinatario();
          } else {
                 APDU confirm = new APDU("CONFIRM", apdusaida.obterMensagem().getIdMensagem(), 1, "Servidor", apdusaida.obterMensagem().getNomeGrupo(), apdusaida.obterMensagem().getDestinatario());
                 String endereco = Destinatario.obterEnderecoIp();
                 int portaOrigem = Destinatario.ObterPortaUDP();
                 servidor.enviarMensagemSend(confirm, endereco, portaOrigem);
                 servidor.enviarMensagemSend(apdusaida.obterMensagem(), endereco, portaOrigem);
                 registroLog = "[SENDPVT] " + apdusaida.obterMensagem().getNomeUsuario() + " -> " + apdusaida.obterMensagem().getDestinatario();
          }
           registro.registrarMensagem(registroLog);
           
   }
   
   

   public void processarApduList(ListaDupla<Grupos> listaGrupos, Controller.Registro registro, Servidor servidor, SaidaAPDU apdusaida) {
           ListaDupla.Element ptr = listaGrupos.getHead();
           String gruposListados = "OK: ";
           if(ptr != null) {
              gruposListados =  ((Grupos)(ptr.getValor())).obterNomeGrupo() + ",";
              for(ptr = ptr.getNext(); ptr != null; ptr = ptr.getNext()) {
                 gruposListados = gruposListados + ((Grupos)(ptr.getValor())).obterNomeGrupo() + ",";
              }
           }
            
            String nomeUsuario = (apdusaida.obterMensagem()).getNomeUsuario();
            String registroLog = "Usuario " + nomeUsuario + " solicitou listagem de grupos existentes";
            registro.registrarMensagem(registroLog);
           
            enviarMensagemTCP(apdusaida, gruposListados);
   }

   public void processarApduUsers(Controller.Registro registro, Servidor servidor, SaidaAPDU apdusaida) {
            ListaDupla.Element ptr = estruturaControle.obterListaUsuario().getHead();
            String usuariosOnline = "OK: " + ((Usuario)(ptr.getValor())).obterNome() + ",";
            for(ptr = ptr.getNext(); ptr != null; ptr = ptr.getNext()) {
                usuariosOnline = usuariosOnline + ((Usuario)(ptr.getValor())).obterNome() + ",";
            }
            String nomeUsuario = (apdusaida.obterMensagem()).getNomeUsuario();
            String registroLog = "Usuario " + nomeUsuario + " solicitou listagem de usuarios existentes";
            registro.registrarMensagem(registroLog);
           
            enviarMensagemTCP(apdusaida, usuariosOnline);
   }
   
   public void enviarMensagemTCP(SaidaAPDU apdusaida, String Mensagem) {
        try {
           apdusaida.obterSaida().writeObject(Mensagem);
        } catch(Exception ex) {
           ex.printStackTrace();
        }
   }
 }   
