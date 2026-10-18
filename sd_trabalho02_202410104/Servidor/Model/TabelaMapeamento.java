package Model;

public class TabelaMapeamento<T, K> {
      private static final int tamanho = 200;
      private FuncaoAltaOrdem<K>[] tabela; 
      public TabelaMapeamento() {
            this.tabela = new FuncaoAltaOrdem[tamanho];        
      }

      public void inserirDado(int indice, FuncaoAltaOrdem<K> funcao) {
             if(indice >= tamanho) throw new RuntimeException("indice fora dos limites");
             if(tabela[indice] != null) throw new RuntimeException("colisao indice");
             tabela[indice] = funcao;    
      }

      public FuncaoAltaOrdem<K> obterItem(int indice) {
              return tabela[indice];
      }
}