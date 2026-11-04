package Controller;
import Model.Servidor;
import View.Janela;
public class Controller {
      private Servidor servidor;
      private Janela janela;
      private Registro registro;
      public Controller(Janela janela) {
           this.janela = janela;
           this.registro = new Registro(janela);
           this.servidor = new Servidor(registro);
           
      }
      
      public void iniciar() {
            servidor.gerenciarServidor(); 
      }
      
}
