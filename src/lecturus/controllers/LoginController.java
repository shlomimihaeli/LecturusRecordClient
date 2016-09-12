/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lecturus.interfaces.ControlledScreen;
import lecturus.main.UserSession;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class LoginController implements Initializable, ControlledScreen {

    ScreensController main;
    
    @FXML
    TextField loginUsername;
    
    @FXML
    PasswordField loginPassword;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void login(){
        
        if(loginUsername.getText().length() > 0 && loginPassword.getText().length() > 0){
            
            try{
            
                UserSession.login(loginUsername.getText(), loginPassword.getText());
                // redirect to home
                main.setScreen(ScreensController.NEW_VIDEO_SCREEN);
                
            }catch(Exception e){
                
                main.showLoader(false, "Login error");
            }
        }else{
            main.showLoader(false, "Please enter username and password");
        }
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        main = screenPage;
    }

    @Override
    public boolean onStop() {
        return true;
    }

    @Override
    public void onResume() {
        
    }
    
    
    
}
