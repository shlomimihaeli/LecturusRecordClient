/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lecturus.interfaces.ControlledScreen;

/**
 *
 * @author shnizle
 */

public class ScreensController extends BorderPane{ 
    
    public static final String NEW_VIDEO_SCREEN = "newVideo.fxml"; 
    public static final String WELCOME_SCREEN = "welcome.fxml"; 
    public static final String RECORD_SCREEN = "recordSession.fxml"; 
    
    private HashMap<String, Node> screens = new HashMap<>();
    private HashMap<String, ControlledScreen> screenControllers = new HashMap<>();
    
    private ControlledScreen currentController;
    private Stage loaderWindow;
    private Stage mainWindow;
    private StackPane mainView;
    Text statusBarText;
    StatusBar sBar;
    
    @FXML
    ProgressIndicator spinner;
     
    Parent statusBarView;
    
    public ScreensController(Stage mainWindow) {
        
        loaderWindow = setupLoaderStage();
        
        this.mainWindow = mainWindow;
        
        mainView = new StackPane();
        
        mainView.getStylesheets().add("/style/main.css");
        
        statusBarText = new Text();
        
        spinner = new ProgressIndicator();
        spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        spinner.setVisible(false);
        
        try{
            // load status bar
            FXMLLoader myLoader = new 
                   FXMLLoader(getClass().getResource("/lecturus/views/statusBar.fxml"));
           statusBarView = (Parent) myLoader.load(); 
           
           sBar = 
              ((StatusBar) myLoader.getController());
           
        }catch(IOException e){}
        
        
        /**
         * build status Bar
         */
        /*BorderPane statusBar = new BorderPane();
        statusBar.setPadding(new Insets(10));
        statusBar.setLeft(statusBarText);
        statusBar.setRight(spinner);
        */
        
        setCenter(mainView);
        setBottom(statusBarView);
        
    }
    
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
         
         
       final DoubleProperty opacity = opacityProperty(); 

       //Is there is more than one screen 
       if(!mainView.getChildren().isEmpty()){ 
           Timeline fade = new Timeline( 
           new KeyFrame(Duration.ZERO, 
                        new KeyValue(opacity,1.0)), 
           new KeyFrame(new Duration(200), 

               new EventHandler<ActionEvent>() { 

                 @Override 
                 public void handle(ActionEvent t) { 
                   //remove displayed screen 
                   mainView.getChildren().remove(0); 
                   //add new screen 
                   mainView.getChildren().add(0, screens.get(name)); 
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
         mainView.getChildren().add(screens.get(name)); 
         Timeline fadeIn = new Timeline( 
             new KeyFrame(Duration.ZERO, 
                          new KeyValue(opacity, 0.0)), 
             new KeyFrame(new Duration(500), 
                          new KeyValue(opacity, 1.0))); 
         fadeIn.play(); 
       } 
       
       currentController.onResume();
       
       return true; 
     } else { 
         System.out.println("screen hasn't been loaded!\n");
         return false; 
    } 
 } 
    
    public void onCloseRequest(WindowEvent t){
        
        try{
            if(!currentController.onStop()){
                t.consume();
            }
        }catch(Exception e){}
    }
    
    public void alert(String msg){
        
        final Stage dialogStage = new Stage();
dialogStage.initModality(Modality.WINDOW_MODAL);

    Button b = new Button("Ok");
        b.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                dialogStage.close();
            }
        });

        dialogStage.setScene(new Scene(VBoxBuilder.create().
    children(new Text(msg), b).
    alignment(Pos.CENTER).padding(new Insets(20)).build()));
        
dialogStage.show();
    }
    
    private Stage setupLoaderStage(){
        
        final Stage dialogStage = new Stage();
dialogStage.initModality(Modality.WINDOW_MODAL);

    Button b = new Button("Ok");
        b.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                dialogStage.close();
            }
        });

        ProgressIndicator pi = new ProgressIndicator();
        pi.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        
        dialogStage.setScene(new Scene(VBoxBuilder.create().
    children(pi).
    alignment(Pos.CENTER).padding(new Insets(20)).build()));
        
return dialogStage;
    }
    
    public void showLoader(boolean show, String status){
        
        sBar.showLoader(show, status);
        
    }
    
     public void retryMessage(boolean show, String status){
        
       sBar.showLoader(show, status);
        
    }
    
    public void showLoader(boolean show){
        
        sBar.showLoader(show, "");
        
    }
    
    public StatusBar getStatusBar(){
        
        return sBar;
    }
    
    public void close(){
        
        mainWindow.close();
    }
    
    
}
