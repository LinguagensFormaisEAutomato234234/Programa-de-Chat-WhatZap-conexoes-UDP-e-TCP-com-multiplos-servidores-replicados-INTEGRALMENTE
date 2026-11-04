package Model;

public class ServidoresAtivos {
      private int portaUDP, portaTCP;
      private String enderecoIP;
      private boolean Lider = false;
      public ServidoresAtivos(int portaUDP, int portaTCP, String enderecoIP) {
             this.portaUDP = portaUDP;
             this.portaTCP = portaTCP;
             this.enderecoIP = enderecoIP;
      }
      
      public void promoverHostComoLider() {
           Lider = true;
      }
      public void definirPortaUDP(int portaUDP) {
           this.portaUDP = portaUDP;
      }

      public void definirPortaTCP(int portaTCP) {
            this.portaTCP = portaTCP;
      }

      public int obterPortaUDP() {
           return portaUDP;
      }

      public int obterPortaTCP() {
          return portaTCP;
      }

      public String obterEnderecoIP() {
          return enderecoIP; 
      }
      
      public boolean ehLider() {
          return Lider;
      }
}
