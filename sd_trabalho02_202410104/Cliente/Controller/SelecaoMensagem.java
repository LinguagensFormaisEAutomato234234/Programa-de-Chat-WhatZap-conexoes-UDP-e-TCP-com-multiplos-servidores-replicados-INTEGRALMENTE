package Controller;

public class SelecaoMensagem {
      public int MENSAGEMPUBLICA = 0;
      public int MENSAGEMPRIVADA = 1;  
      private javafx.scene.control.TextField textoUsuarioSelecionado;
      private int tipoAtual;     
      public SelecaoMensagem() {
         
      }

      public void definirTipoAtual(int tipoAtual) {
              this.tipoAtual = tipoAtual;
      }
   
      public int retornarTipoAtual() {
           return tipoAtual;
      }   

      public javafx.scene.control.TextField retornarTextoUsuarioSelecionado() {
          return textoUsuarioSelecionado;
      }
}