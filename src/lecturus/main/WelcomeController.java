/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class WelcomeController implements Initializable, ControlledScreen {

    ScreensController myController; 
    
    @FXML
    TextField usernameText;
    
    @FXML
    PasswordField passwordText;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void loginBtnAction(ActionEvent evt){
        
        try{
        
            UserSession s = UserSession.login(usernameText.getText(), passwordText.getText());
            
            if(s.getToken().length() > 0){
                myController.setScreen(ScreensController.NEW_VIDEO_SCREEN);
            }else{
                throw new InvalidLogin();
            }
            
        }catch(InvalidLogin e){
            
            // invalid login
            myController.alert("Invalid login details");
        }
        catch(Exception e){
            
            // connection error
            myController.alert("connection error, please try again");
        }
    }
    
    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void onResume() {
        
    }

    @Override
    public boolean onStop() {
        return true;
    }
    
    
    
    
}
