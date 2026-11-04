package Model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import Protocol.APDU;

public class SaidaAPDU {
     private APDU mensagem;
     private ObjectOutputStream saida;
     public SaidaAPDU(APDU mensagem) {
          this.mensagem = mensagem;
     }
     public SaidaAPDU(APDU mensagem, ObjectOutputStream saida) {
            this.mensagem = mensagem;
            this.saida = saida;
     }
     public ObjectOutputStream obterSaida() {
         return saida;
     }
     
     public APDU obterMensagem() {
         return mensagem;
     }
}
