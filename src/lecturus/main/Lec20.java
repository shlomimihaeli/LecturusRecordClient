/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
        ScreensController mainContainer = new ScreensController(); 
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
       
       //final Process p = Runtime.getRuntime().exec("\"c:/Program Files (x86)/VideoLAN/VLC/vlc.exe\" dshow:// :v4l2-width=320 :v4l2-height=240 :display :sout-keep --sout \"#transcode{vcodec=theora,acodec=vorbis,vb=800,ab=128} :display :standard{access=file,dst=C:\\Users\\shnizle\\Documents\\Lecturus\\capture.ogg}\"");
        
       new Thread(){
            @Override
            public void run() {
                
                try{
                    
                    sleep(25000);
                }catch(Exception e){
                    
                }
                
                 //p.destroy();
            }
           
           
       };//.start();
      
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
