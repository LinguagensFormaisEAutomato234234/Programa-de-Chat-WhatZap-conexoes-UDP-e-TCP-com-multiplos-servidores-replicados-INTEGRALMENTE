package Controller;
import View.Janela;
public class Registro {
      private Janela janela;
      private String registroLog = "";
      public Registro(Janela janela) {
           this.janela = janela;
      }
      public void registrarMensagem(String mensagem) {
           if(registroLog.equals("")) {
                registroLog = mensagem;
           } else {
                registroLog = registroLog + "\n" + "-----------------------------------------------------------------------------------" + "\n" +  mensagem;
           }
         
           janela.alocarMensagem(registroLog);
      }
}
