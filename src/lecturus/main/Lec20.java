/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lecturus.controllers.ScreensController;

/**
 *
 * @author shnizle
 */
public class Lec20 extends Application {
    
    public static final String NEW_VIDEO_SCREEN = "newVideo.fxml"; 
    public static final String WELCOME_SCREEN = "welcome.fxml"; 
    public static final String RECORD_SCREEN = "recordSession.fxml"; 
    
    @Override
    public void start(Stage stage) throws Exception {
        final ScreensController mainContainer = new ScreensController(); 
       mainContainer.loadScreen(Lec20.NEW_VIDEO_SCREEN, 
                            "/lecturus/main/"+Lec20.NEW_VIDEO_SCREEN); 
       mainContainer.loadScreen(Lec20.WELCOME_SCREEN, 
                           "/lecturus/main/"+Lec20.WELCOME_SCREEN);
       mainContainer.loadScreen(RECORD_SCREEN, 
                           "/lecturus/main/"+RECORD_SCREEN);
       
       mainContainer.setScreen(NEW_VIDEO_SCREEN); 

        Group root = new Group(); 
       root.getChildren().addAll(mainContainer); 
       Scene scene = new Scene(root); 
       stage.setScene(scene); 
       stage.show(); 
       
       stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                
                mainContainer.onCloseRequest(t);
                System.out.println("window has been closed");
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
