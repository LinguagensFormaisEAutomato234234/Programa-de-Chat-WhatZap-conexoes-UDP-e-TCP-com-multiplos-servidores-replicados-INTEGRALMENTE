package Model;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import Controller.Registro;
import java.net.InetAddress;

public class ServidorBroadCast {
    private final int PORTA = 8888;
    private final int portaUDPServidor = 7777;
    private final int portaTCPServidor = 6789;
    private Registro registroServidor;
    private String enderecoIpLocal;
    private Servidor servidor;
    public ServidorBroadCast(Registro registroServidor, Servidor servidor) {
           this.registroServidor = registroServidor;
           this.servidor = servidor;
           this.enderecoIpLocal = associarEnderecoIP();
    }
    
    public void gerenciarConexoes() {
         Thread ProcessoBroadcast = new Thread(() -> {
             registroServidor.registrarMensagem("Servidor escutando por BroadCast na porta 8888");
             gerenciarRequisicaoConexao();
         });
         ProcessoBroadcast.start();
    }
    
    private void gerenciarRequisicaoConexao() {
          try(DatagramSocket socket = new DatagramSocket(PORTA)) {
            byte[] buffer = new byte[256];
            while(true) {
               DatagramPacket pacoteRecebido = new DatagramPacket(buffer, buffer.length); //fica em espera bloqueante por uma requisicao da rede de um cliente escutando no meio de comunicacao  
               socket.receive(pacoteRecebido);
               String mensagem = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());//toma a mensagem recebida e a transforma num fluxo de String's
               if (mensagem.equals("SERVIDOR_IP") && servidor.esteServidorEhlider()) {//caso a mensagem for igual a servidor IP
                   byte[] resposta = (String.valueOf(portaUDPServidor) + "," + String.valueOf(portaTCPServidor)+ "," + enderecoIpLocal).getBytes();
                   DatagramPacket pacoteEnvio = new DatagramPacket(
                     resposta, resposta.length,
                     pacoteRecebido.getAddress(), pacoteRecebido.getPort()
                   );
                 socket.send(pacoteEnvio);
              } else if(mensagem.equals("SERVIDOR_IP_NO_ATIVO")) {
                  System.out.println("o IP eh: " + String.valueOf(portaUDPServidor) + "," + String.valueOf(portaTCPServidor)+ "," + enderecoIpLocal);
                  byte[] resposta = (String.valueOf(portaUDPServidor) + "," + String.valueOf(portaTCPServidor)+ "," + enderecoIpLocal).getBytes();
                   DatagramPacket pacoteEnvio = new DatagramPacket(
                     resposta, resposta.length,
                     pacoteRecebido.getAddress(), pacoteRecebido.getPort()
                   );
                 socket.send(pacoteEnvio);
              }
           }
        } catch(java.net.UnknownHostException ex) {
           ex.printStackTrace();
        } catch(Exception e) {
           e.printStackTrace(); 
        }   
    }
    
    private String associarEnderecoIP() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ipReal = socket.getLocalAddress().getHostAddress();
            return ipReal;
       } catch(java.net.SocketException ex) {
           ex.printStackTrace();
       } catch(java.net.UnknownHostException e) {
           e.printStackTrace();
       }
       return "ERRO";
       
    }
    
    public String obterEnderecoIpLocal() {
        return enderecoIpLocal;
    }
}
