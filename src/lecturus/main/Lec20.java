/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lecturus.controllers.ScreensController;

/**
 *
 * @author shnizle
 */
public class Lec20 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        final ScreensController mainContainer = new ScreensController(stage); 
       mainContainer.loadScreen(ScreensController.NEW_VIDEO_SCREEN, 
                            "/lecturus/main/"+ScreensController.NEW_VIDEO_SCREEN); 
       mainContainer.loadScreen(ScreensController.WELCOME_SCREEN, 
                           "/lecturus/views/"+ScreensController.WELCOME_SCREEN);
       mainContainer.loadScreen(ScreensController.RECORD_SCREEN, 
                           "/lecturus/main/"+ScreensController.RECORD_SCREEN);
       
       mainContainer.setScreen(ScreensController.NEW_VIDEO_SCREEN); 

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
