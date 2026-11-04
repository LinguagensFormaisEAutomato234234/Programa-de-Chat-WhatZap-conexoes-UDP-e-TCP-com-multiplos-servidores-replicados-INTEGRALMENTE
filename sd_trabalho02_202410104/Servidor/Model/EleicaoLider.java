package Model;

import Controller.Registro;
import Protocol.APDU;

public class EleicaoLider { 
    private ServidorTCP servidorTCP;
    private ServidorUDP servidorUDP;
    private Registro registro;
    public EleicaoLider(ServidorTCP servidorTCP, ServidorUDP servidorUDP, Registro registro) {
        this.servidorTCP = servidorTCP;
        this.servidorUDP = servidorUDP;
        this.registro = registro;
    }
    
    public ServidoresAtivos protocoloSelecao(ListaDupla<ServidoresAtivos> listaServidoresAtivos) {
            boolean testeConexao = false;
            boolean testeTimeStamp = false;
            ListaDupla<ServidoresAtivos> copiaLista = copy(listaServidoresAtivos);
            do {
              ServidoresAtivos candidato = selcionarLider(copiaLista);
              
              if(candidato != null) {
                  int portaDestinoUDP = (candidato).obterPortaUDP();
                  int portaDestinoTCP = (candidato).obterPortaTCP();
                  String enderecoIp = (candidato).obterEnderecoIP();
                  APDU apdu = new APDU("TIMESTAMPTEST", portaDestinoTCP, portaDestinoUDP, enderecoIp); //definirmos uma apdu para testar o timestamp do candidato, se estiver desatualizada, escolheremos o proximo maior
                  String resposta = null;//resposta do servidor em que sera estabelecido, retornando o valor da atualizacao mais recente aplicado a estrutura de gerenciamento de dados
                  resposta = servidorTCP.sendTCP(apdu, enderecoIp, portaDestinoTCP);
                  testeConexao = resposta != null;
                  if(resposta != null) testeTimeStamp = Integer.parseInt(resposta) >= GerenciadorServidores.obterTimeStamp();
                  if(!testeConexao) {
                      registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + "  nao confirmou a requisicao de lider escolhendo proximo no");
                      
                      removerNo(candidato, copiaLista);
                  }  
                  if(testeConexao && !testeTimeStamp) {
                       registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " confirmou a requisicao com o TIMESTAMP desatualizado escolhendo proximo no");
                       removerNo(candidato, copiaLista);
                  } else if(testeConexao) {//nesse caso, somente !testeTimeStamp eh falso, portanto !testeTimeStamp => testeTimeStamp
                      registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " confirmou a requisicao com o TIMESTAMP atualizado No promovido a lider");
                      ServidoresAtivos Lider =  candidato;
                      return Lider;
                  }
              }
            } while(!SemCandidato(copiaLista));
            return null;
   }
   
   
   public ServidoresAtivos procurarExistenciaLiderSelecionado(int portaUDPLocal, int portaTCPLocal, String enderecoIPLocal, ListaDupla<ServidoresAtivos> lista) {
       for(ListaDupla.Element ptr = lista.getHead(); ptr != null; ptr = ptr.getNext()) {
           
            int portaDestinoUDP = ((ServidoresAtivos)ptr.getValor()).obterPortaUDP();
            int portaDestinoTCP = ((ServidoresAtivos)ptr.getValor()).obterPortaTCP();
            String enderecoIp = ((ServidoresAtivos)ptr.getValor()).obterEnderecoIP(); 
            APDU apdu = new APDU("PROCURARLIDER", portaTCPLocal, portaUDPLocal, enderecoIPLocal);
            String resposta = servidorTCP.sendTCP(apdu, enderecoIp, portaDestinoTCP);
            if(resposta != null) {
               if(resposta.equals("SIM")) {//selecionamos o servidor que tiver o maior time-Stamp, maior timestamp significa estrutura de dados mais consistente 
                  registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " respondeu ser o lider");
                  return ((ServidoresAtivos)ptr.getValor());
               } else {
                  registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " confirmou negativamente ser o lider");
               }
            } else {
               registro.registrarMensagem("Servidor (IP, portaTCP, portaUDP): " + enderecoIp + " ," + portaDestinoTCP + " ," + portaDestinoUDP + " nao respondeu a pergunta se eh lider");
            }
        }
        
        return null;
  }
  
  public ServidoresAtivos selcionarLider(ListaDupla<ServidoresAtivos> lista) {
        long maior = 0;
        ServidoresAtivos lider = null;
        for(ListaDupla.Element ptr = lista.getHead(); ptr != null; ptr = ptr.getNext()) {
            ServidoresAtivos candidato = ((ServidoresAtivos)ptr.getValor());
            int porta = candidato.obterPortaUDP();
            String endereco = candidato.obterEnderecoIP();
            long valor = funcaoAvaliacao(porta, endereco);
            if(valor > maior) {
               lider = candidato;
               maior = funcaoAvaliacao(porta, endereco);
            }
        }
        return lider;
  }
    
    public long funcaoAvaliacao(int porta, String enderecoIP) {
        String[] Octeto = enderecoIP.split("\\.");
        return ((((long)Integer.parseInt(Octeto[3]) << 24) | ((long)Integer.parseInt(Octeto[2])) << 16) | ((long)Integer.parseInt(Octeto[1])) << 8 | ((long)Integer.parseInt(Octeto[0]))) << 16 | porta;
    }
    
    
   public ListaDupla<ServidoresAtivos> copy(ListaDupla<ServidoresAtivos> lista) {
      ListaDupla<ServidoresAtivos> copia = new ListaDupla<>();
      for(ListaDupla.Element ptr = lista.getHead(); ptr != null; ptr = ptr.getNext()) {
         ServidoresAtivos valor = ((ServidoresAtivos)ptr.getValor());
         copia.inserirFim(new ServidoresAtivos(valor.obterPortaUDP(), valor.obterPortaTCP(), new String(valor.obterEnderecoIP())));
      }
      return copia;
   }
   
   public void removerNo(ServidoresAtivos LiderCandidato, ListaDupla<ServidoresAtivos> lista) {
        for(ListaDupla.Element ptr = lista.getHead(); ptr != null; ptr = ptr.getNext()) {
           ServidoresAtivos valor = ((ServidoresAtivos)ptr.getValor());
           if(valor.obterPortaUDP() == LiderCandidato.obterPortaUDP() && valor.obterPortaTCP() == LiderCandidato.obterPortaTCP() && valor.obterEnderecoIP().equals(LiderCandidato.obterEnderecoIP())) {
             lista.remover(valor); 
           }
        }
   }
   
   public boolean SemCandidato(ListaDupla<ServidoresAtivos> lista) {
       
       return lista.getComprimentoLista() == 0;
   }
}
