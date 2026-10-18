/************************************************************************************************
*Autor............: Gustavo Henrique Oliveira Fernandes
*Matricula........: 202410104
*Inicio...........: 10/06/2026
*Ultima alteracao.: 10/06/2025 //descolhecido
*Nome.............: Principal
*Funcao...........: instanciar a classe Janela e o Controler. Fundamentais para, respectivamente, 
                      - construir a interface de iteracao com o usuario
                      - Comunicar a classe do diretorio View com o diretorio Model
                    Essa classe representa o processo cliente da camada de aplicacao da rede
                    Sendo reposnsavel por instanciar/inicializar os compontes essenciais do sistema 
**************************************************************************************************/

import javafx.application.Application;
import javafx.stage.Stage;

public class Principal extends Application {
    @Override
    public void start(Stage primaryStage) {
          new Controller.Controller(new View.Janela(primaryStage)).iniciar();
    }
    
    public static void main(String[] args) {
        launch(args);        
    }
}
