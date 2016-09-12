/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;
import lecturus.main.HttpUtills;
import lecturus.main.Lec20;
import lecturus.main.UserSession;
import lecturus.model.College;
import lecturus.model.Course;
import lecturus.model.RestModelQueryResponse;
import lecturus.rest.RestCallResponse;
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
    
    @FXML
    Button newViewBtn;
    
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
           
           // disable button
           newViewBtn.setDisable(true);
           
           /**
            * clear inputs
            */
           lectureTitle.setText("");
           
           collegeCb.getSelectionModel().clearSelection();
            collegeCb.getItems().clear();
            
            courseCb.getSelectionModel().clearSelection();
                       courseCb.getItems().clear();
           
           // get list of colleges
           loadCollegeList();
           
           
          collegeCb.valueProperty().addListener(new ChangeListener() {
               @Override
               public void changed(ObservableValue ov, Object t, Object t1) {
                  
                   loadCourseList();
               }
           });
           
       }catch(Exception e){
           
           e.printStackTrace();
           screenPage.setScreen(ScreensController.WELCOME_SCREEN);
       }
        
    }
    
    private void loadCollegeList(){
        
        screenPage.showLoader(true, "Loading colleges");
        
        try{
        
            College.list(new RestModelQueryResponse() {
                   @Override
                   public void done(List models) {

                        collegeCb.getItems().addAll(models);
                        screenPage.showLoader(false, "done loading colleges");
                   }

                   @Override
                   public void failed() {
                      screenPage.getStatusBar().retryMessage(false, "failed loading colleges", new EventHandler<Event>() {
                          @Override
                          public void handle(Event t) {
                                loadCollegeList();
                          }
                      }
                      );
                   }
               });
            
        }catch(Exception e){
            
            screenPage.setScreen(ScreensController.WELCOME_SCREEN);
        }
    }
    
    private void loadCourseList(){
        
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
                                 
                                 courseCb.valueProperty().addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ObservableValue ov, Object t, Object t1) {

                                        newViewBtn.setDisable(false);
                                    }
                                });
                            }

                            @Override
                            public void failed() {
                                screenPage.getStatusBar().retryMessage(false, "failed loading courses",  new EventHandler<Event>() {
                                    @Override
                                    public void handle(Event t) {
                                        loadCourseList();
                                    }
                                });
                            }
                        });
                       
                        
                   }catch(Exception e){
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
        
        // validate input
        if(lectureTitle.getText().length() < 5){
            
            screenPage.showLoader(false, "please insert a title of at least 5 characters");
            return;
        }else if(courseCb.getValue() == null){
            
            screenPage.showLoader(false, "please select a course");
            return;
        }
        
        try{
            
            screenPage.showLoader(true, "creating new video session");
            
            HttpUtills.asyncRestGetAction(Lec20.serverURL+"/video/new?title="+lectureTitle.getText()+"&course_id="+courseCb.getValue().getID()+"&token="+token,
                    
                    new RestCallResponse() {
                @Override
                public void done(JSONObject res) {
                    
                    screenPage.showLoader(false);
                    
                   if(res.get("status").equals("success")){

                        JSONObject data = (JSONObject) res.get("data");
                        RecordSessionController.videoId = ((Long) data.get("id")).intValue();
                        screenPage.setScreen(ScreensController.RECORD_SCREEN);
                    }
                }

                @Override
                public void failed() {
                    
                    screenPage.showLoader(false,"failed creating new video session, please try again");
                }
            });
            
        }catch(Exception e){
            
            e.printStackTrace();
        }
        
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.screenPage = screenPage;
    }
    
    
}
