package View;

import javafx.scene.control.Button;

public class ConfigurarButao {
     private Button butao;
     private double x, y;
     private String titulo;
     private double largura, altura;
     public ConfigurarButao(Button butao, String titulo, double x, double y, double largura, double altura) {
            this.butao = butao;
            this.titulo = titulo;
            this.x = x;
            this.y = y;
            this.largura = largura;
            this.altura = altura;
     }
     
     public ConfigurarButao(Button butao) {
            this.butao = butao;
     }
     
     public void setTitulo(String titulo) {
           this.titulo = titulo;
     }
     
     public void setPoiscaoX(double x) {
         this.x = x;
     }
     
     public void setDimensao(double largura, double altura) {
          this.largura = largura;
          this.altura = altura;
     }
     
     public void setPoiscaoY(double y) {
         this.y = y;
     }
     
     public void editarconfigurarButao() {
          configurarButaoEntrar(x, y, largura, altura, butao, titulo);
     }
     
     private void configurarButaoEntrar(double x, double y, double largura, double altura, Button ponteiro, String titulo) {
              String cor = "#C0C0C0";
              String cor_fundo = "black";
              String tamanho_fonte = "12";
              String fonte = "'System'";
              String radius = "6";
              double posicaoX = x;
              double posicaoY =  y;
              String title = titulo;
              setAcaoButao(ponteiro);
              configurarButaoEnvio(cor, cor_fundo, tamanho_fonte, fonte, radius, posicaoX, posicaoY, largura, altura, title, ponteiro);
      } 
      
    /********************************************************************************************
    * Metodo: setAcaoButao
    * Funcao: define comportamento do botao principal com base em uma funcao de alta ordem
    * Parametros: funcao - acao a ser executada
    * Retorno: vazio
    ********************************************************************************************/
    public void setAcaoButao(Button parametro) {
            parametro.setOnMouseEntered(e -> {
                   costumizar("lightblue", "black", "12", "Arial", "6", parametro);
            }); 
            parametro.setOnMouseExited(e -> {
                    costumizar("#C0C0C0", "black", "12", "ARIAL", "6", parametro);
            });
            parametro.setOnMousePressed(e -> {
                   costumizar("#C0C0C0", "black", "12", "ARIAL", "6", parametro);    
            });
    } // fim do metodo setAcaoButao
    
    /********************************************************************************************
     * Metodo: costumizar
     * Funcao: aplica estilo customizado a um botao
     * Parametros: propriedades visuais e botao alvo
     * Retorno: vazio

     ********************************************************************************************/     
    private void costumizar(String x, String y, String z, String w, String r, Button k) {
             k.setStyle(
                      "-fx-background-color: " + x + ";" +
                      "-fx-text-fill: " +  y +  ";" +
                      "-fx-font-size: " + z + ";" +
                      "-fx-font-family: " + w + ";" +
                      "-fx-background-radius: " + r + ";"  
            );
    }//fim do metodo costumizar
    
    private void configurarButaoEnvio(String cor, String cor_fundo, String tamanho_fonte, String fonte, String radius, double x, double y, double largura, double altura, String title, javafx.scene.control.Button ponteiro) {
          ponteiro.setLayoutX(x); ponteiro.setLayoutY(y);
          ponteiro.setPrefSize(largura, altura);
          ponteiro.setStyle(
               "-fx-background-color: " + cor + ";" +
               "-fx-text-fill: " +  cor_fundo +  ";" +
               "-fx-font-size: " + tamanho_fonte + ";" +
               "-fx-font-family: " + fonte + ";" +
               "-fx-background-radius: " + radius + ";"  
          );
          ponteiro.setText(title);
    }//fim do metodo configurarButaoEnvio 
}
