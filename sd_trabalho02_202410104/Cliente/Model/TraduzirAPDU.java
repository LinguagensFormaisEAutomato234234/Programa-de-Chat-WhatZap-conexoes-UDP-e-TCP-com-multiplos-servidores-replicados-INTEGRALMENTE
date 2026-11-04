package Model;

public class TraduzirAPDU {
     static class TipoAPDU  {
           public static final int SEND = 0, LEAVE = 1, JOIN = 2, Block = 3, SENDUV = 4, UNBLOCK = 5, CONFIRM = 6, REGISTER = 7, LIST = 8, SENDPVT = 9, Users = 10, NEWMISTER = 11;
          public int tipoAtual;
          public TipoAPDU(int tipoAtual) {
               this.tipoAtual = tipoAtual;
          }
     }
     private String Operacao;
     
     public TraduzirAPDU() {
     }
     public TipoAPDU obterTipoAPDU(String Operacao) {
            switch(Operacao) {
                 case "SEND":
                     return new TipoAPDU(TipoAPDU.SEND);
                 case "LEAVE":
                     return new TipoAPDU(TipoAPDU.LEAVE);
                 case "JOIN":
                      return new TipoAPDU(TipoAPDU.JOIN);
                 case "Block":
                      return new TipoAPDU(TipoAPDU.Block);
                 case "SENDVU":
                       return new TipoAPDU(TipoAPDU.SENDUV);
                 case "UNBLOCK":
                       return  new TipoAPDU(TipoAPDU.UNBLOCK);
                 case "CONFIRM":
                       return new TipoAPDU(TipoAPDU.CONFIRM);
                 case "REGISTER":
                      return new TipoAPDU(TipoAPDU.REGISTER);
                 case "List":
                      return new TipoAPDU(TipoAPDU.LIST);
                 case "Users":
                      return new TipoAPDU(TipoAPDU.Users);  
                 case "SENDPVT":
                      return new TipoAPDU(TipoAPDU.SENDPVT);
                 case "NEWMISTER":
                      return new TipoAPDU(TipoAPDU.NEWMISTER);
            }
            return null;
     }
     
     public String obterTipoAPDU(TraduzirAPDU.TipoAPDU tipo) {
            switch(tipo.tipoAtual) {
                 case TipoAPDU.SEND:
                     return "SEND";
                 case TipoAPDU.LEAVE:
                     return "LEAVE";
                 case TipoAPDU.JOIN:
                      return "JOIN";
                 case TipoAPDU.Block:
                      return "BLOCK";
                 case TipoAPDU.SENDUV:
                      return "SENDVU";
                 case TipoAPDU.UNBLOCK:
                       return  "UNBLOCK";
                 case TipoAPDU.CONFIRM:
                       return "CONFIRM";
                 case TipoAPDU.REGISTER:
                      return "REGISTER";
                 case TipoAPDU.LIST:
                      return "LIST";
                 case TipoAPDU.Users:
                     return "USERS";
                 case TipoAPDU.SENDPVT:
                     return "SENDPVT";
                 case TipoAPDU.NEWMISTER:
                     return "NEWMISTER";
            }
            return null;
     }
}
