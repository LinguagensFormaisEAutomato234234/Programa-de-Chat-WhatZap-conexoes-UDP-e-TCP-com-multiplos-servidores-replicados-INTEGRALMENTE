package Model;

public class TraduzirAPDU {
     static class TipoAPDU  {
          public static final int SEND = 0, LEAVE = 1, JOIN = 2, Block = 3, SENDUV = 4, UNBLOCK = 5, CONFIRM = 6, REGISTER = 7, LIST = 8, SENDPVT = 9, Users = 10, RequestMaster = 11, SinalizarExstencia = 12, TIMESTAMPTEST = 13, UPDATEDATA = 14, CONNECTIONREQUEST = 15;
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
                 case "BLOCK":
                      return new TipoAPDU(TipoAPDU.Block);
                 case "SENDVU":
                       return new TipoAPDU(TipoAPDU.SENDUV);
                 case "UNBLOCK":
                       return  new TipoAPDU(TipoAPDU.UNBLOCK);
                 case "CONFIRM":
                       return new TipoAPDU(TipoAPDU.CONFIRM);
                 case "REGISTER":
                      return new TipoAPDU(TipoAPDU.REGISTER);
                 case "LIST":
                      return new TipoAPDU(TipoAPDU.LIST);
                 case "SENDPVT":
                      return new TipoAPDU(TipoAPDU.SENDPVT);
                 case "USERS":
                      return new TipoAPDU(TipoAPDU.Users);
                 case "RequestMaster":
                      return new TipoAPDU(TipoAPDU.RequestMaster);
                 case "SinalizarExstencia":
                      return new TipoAPDU(TipoAPDU.SinalizarExstencia);
                 case "TIMESTAMPTEST":
                      return new TipoAPDU(TipoAPDU.TIMESTAMPTEST); 
                 case "UPDATEDATA":
                      return new TipoAPDU(TipoAPDU.UPDATEDATA);
                 case "CONNECTIONREQUEST":
                     return new TipoAPDU(TipoAPDU.CONNECTIONREQUEST);
            }
            return null;
     }
     
     public String TraduzirAPDU(TipoAPDU tipoADU) {
            switch(tipoADU.tipoAtual) {
                case  TipoAPDU.SinalizarExstencia:
                return "SinalizarExstencia";
            }
            return null;
     }
}
