/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;
import lecturus.model.College;
import lecturus.model.Course;
import lecturus.model.RestModelQueryResponse;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class NewVideoController implements Initializable, ControlledScreen {

    ScreensController screenPage;
    String token;
    
    @FXML
    TextField lectureTitle;
    
    @FXML
    ComboBox collegeCb;
    
    @FXML
    ComboBox<Course> courseCb;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void onResume() {
       
        // check user token
       try{
           
           token = UserSession.getSession().getToken();
           
           screenPage.showLoader(true, "Loading colleges");
           
           /**
            * clear inputs
            */
           lectureTitle.setText("");
           
           collegeCb.getSelectionModel().clearSelection();
            collegeCb.getItems().clear();
            
            courseCb.getSelectionModel().clearSelection();
                       courseCb.getItems().clear();
           
           // get list of colleges
           College.list(new RestModelQueryResponse() {
               @Override
               public void done(List models) {
                   
                    collegeCb.getItems().addAll(models);
                    screenPage.showLoader(false, "done loading colleges");
               }

               @Override
               public void failed() {
                  screenPage.showLoader(false, "failed loading colleges");
               }
           });
           
           
          collegeCb.valueProperty().addListener(new ChangeListener() {
               @Override
               public void changed(ObservableValue ov, Object t, Object t1) {
                  
                   try{
                        screenPage.showLoader(true, "Loading courses");
                        
                       Course.list(((College) collegeCb.getValue()).getID(), new RestModelQueryResponse() {
                            @Override
                            public void done(List models) {
                                
                                courseCb.getSelectionModel().clearSelection();
                                courseCb.getItems().clear();

                                 courseCb.getItems().addAll(models);

                                 courseCb.setDisable(false);

                                 screenPage.showLoader(false);
                            }

                            @Override
                            public void failed() {
                                screenPage.showLoader(false, "failed loading courses");
                            }
                        });
                       
                        
                   }catch(Exception e){
                       
                   }
               }
           });
           
       }catch(Exception e){
           
           e.printStackTrace();
           screenPage.setScreen(ScreensController.WELCOME_SCREEN);
       }
        
    }

    @Override
    public boolean onStop() {
       return true;
    }
    
    
    
    
    @FXML
    public void createNewVideo(){
        
        screenPage.showLoader(true, "Creating new video session");
        
        try{
            JSONObject res = HttpUtills.restGetAction("http://1e3d0ffd.ngrok.io/video/new?title="+lectureTitle.getText()+"&course_id="+courseCb.getValue().getID()+"&token="+token);
            if(res.get("status").equals("success")){
                
                JSONObject data = (JSONObject) res.get("data");
                RecordSessionController.videoId = ((Long) data.get("id")).intValue();
                this.screenPage.setScreen(ScreensController.RECORD_SCREEN);
            }
        }catch(Exception e){
            
            e.printStackTrace();
        }
        
        screenPage.showLoader(false);
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.screenPage = screenPage;
    }
    
    
}
