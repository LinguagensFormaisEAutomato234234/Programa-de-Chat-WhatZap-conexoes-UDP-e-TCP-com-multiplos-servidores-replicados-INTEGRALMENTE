package Model;

public class EleicaoLider {
    private ListaDupla<ServidoresAtivos> listaServidoresAtivos; 
    public EleicaoLider(ListaDupla<ServidoresAtivos> listaServidoresAtivos) {
        this.listaServidoresAtivos = copy(listaServidoresAtivos);
    }
    
    public ServidoresAtivos selcionarLider() {
        long maior = 0;
        ServidoresAtivos lider = null;
        for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
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
   
   public void removerNo(ServidoresAtivos LiderCandidato) {
        for(ListaDupla.Element ptr = listaServidoresAtivos.getHead(); ptr != null; ptr = ptr.getNext()) {
           ServidoresAtivos valor = ((ServidoresAtivos)ptr.getValor());
           if(valor.obterPortaUDP() == LiderCandidato.obterPortaUDP() && valor.obterPortaTCP() == LiderCandidato.obterPortaTCP() && valor.obterEnderecoIP().equals(LiderCandidato.obterEnderecoIP())) {
             listaServidoresAtivos.remover(valor); 
           }
        }
   }
   
   public boolean SemCandidato() {
       return listaServidoresAtivos.getComprimentoLista() == 0;
   }
}
