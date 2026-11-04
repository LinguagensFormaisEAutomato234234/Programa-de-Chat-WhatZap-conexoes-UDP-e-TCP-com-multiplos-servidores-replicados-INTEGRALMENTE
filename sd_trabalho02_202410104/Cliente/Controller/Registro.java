package Controller;
import View.Janela;
import View.Aviso;
import javafx.application.Platform;
import javafx.scene.control.Label;
public class Registro {
     private Controller controler;
     public Registro(Controller controler) {
          this.controler = controler;
     }
     
     public void alocarTexto(HitoricoGrupo historico) {
          controler.editarCampoTexto(historico);
     }
     
     public void registrarNovoUsuario(String nomeUsuarioPrivado) {
          controler.adicionarConversaPrivadaInterface(nomeUsuarioPrivado);
     }
     

    public void emitirAviso(String mensagem) {
      Platform.runLater(() -> {
           Aviso aviso = new Aviso();
           aviso.mostrarAlerta(mensagem);
      });
    }
}

