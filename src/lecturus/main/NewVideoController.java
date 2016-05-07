/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class NewVideoController implements Initializable, ControlledScreen {

    ScreensController screenPage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void onResume() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStop() {
       
    }
    
    
    
    
    @FXML
    public void createNewVideo(){
        RecordSessionController.videoId = 20;//(Long) data.get("video_id")).intValue();
                this.screenPage.setScreen(Lec20.RECORD_SCREEN);return;
        /*try{
            JSONObject res = HttpUtills.restGetAction("http://lecturus2.herokuapp.com/video/new?title=asd&course_id=1&master_id=2");
            if(res.get("status").equals("success")){
                
                JSONObject data = (JSONObject) res.get("data");
                RecordSessionController.videoId = ((Long) data.get("video_id")).intValue();
                this.screenPage.setScreen(Lec20.RECORD_SCREEN);
            }
        }catch(Exception e){
            
            e.printStackTrace();
        }*/
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.screenPage = screenPage;
    }
    
    
}
