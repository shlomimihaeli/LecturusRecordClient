/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lecturus.interfaces.ControlledScreen;

/**
 *
 * @author shnizle
 */

public class ScreensController extends StackPane{ 
    
    private HashMap<String, Node> screens = new HashMap<>();
    private HashMap<String, ControlledScreen> screenControllers = new HashMap<>();
    
    private ControlledScreen currentController;
    
    public void addScreen(String name, Node screen, ControlledScreen controlledScreen) { 
          screens.put(name, screen); 
          screenControllers.put(name, controlledScreen);
    } 
     //any required method here
    
    public boolean loadScreen(String name, String resource) {
     try { 
         FXMLLoader myLoader = new 
               FXMLLoader(getClass().getResource(resource));
       Parent loadScreen = (Parent) myLoader.load(); 
       ControlledScreen myScreenControler = 
              ((ControlledScreen) myLoader.getController());
       myScreenControler.setScreenParent(this); 
       addScreen(name, loadScreen, myScreenControler); 
       return true; 
     }catch(Exception e) { 
       System.out.println(e.getMessage()); 
       return false; 
     } 
   } 
    
    public boolean setScreen(final String name) { 

     if(screens.get(name) != null) { //screen loaded 
         
         // call resume
         currentController = screenControllers.get(name);
         currentController.onResume();
         
       final DoubleProperty opacity = opacityProperty(); 

       //Is there is more than one screen 
       if(!getChildren().isEmpty()){ 
           Timeline fade = new Timeline( 
           new KeyFrame(Duration.ZERO, 
                        new KeyValue(opacity,1.0)), 
           new KeyFrame(new Duration(200), 

               new EventHandler<ActionEvent>() { 

                 @Override 
                 public void handle(ActionEvent t) { 
                   //remove displayed screen 
                   getChildren().remove(0); 
                   //add new screen 
                   getChildren().add(0, screens.get(name)); 
                   Timeline fadeIn = new Timeline( 
                       new KeyFrame(Duration.ZERO, 
                              new KeyValue(opacity, 0.0)), 
                       new KeyFrame(new Duration(200), 
                              new KeyValue(opacity, 1.0))); 
                   fadeIn.play(); 
                 } 
               }, new KeyValue(opacity, 0.0))); 
         fade.play(); 
       } else { 
         //no one else been displayed, then just show 
         setOpacity(0.0); 
         getChildren().add(screens.get(name)); 
         Timeline fadeIn = new Timeline( 
             new KeyFrame(Duration.ZERO, 
                          new KeyValue(opacity, 0.0)), 
             new KeyFrame(new Duration(500), 
                          new KeyValue(opacity, 1.0))); 
         fadeIn.play(); 
       } 
       return true; 
     } else { 
         System.out.println("screen hasn't been loaded!\n");
         return false; 
    } 
 } 
    
    public void onCloseRequest(WindowEvent t){
        
       currentController.onStop();
    }
    
    public void alert(String msg){
        
        Stage dialogStage = new Stage();
dialogStage.initModality(Modality.WINDOW_MODAL);
dialogStage.setScene(new Scene(VBoxBuilder.create().
    children(new Text(msg), new Button("Ok.")).
    alignment(Pos.CENTER).padding(new Insets(20)).build()));
dialogStage.show();
    }
}
