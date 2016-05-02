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
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class WelcomeController implements Initializable, ControlledScreen {

    ScreensController myController; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void loginBtnAction(ActionEvent evt){
        
        myController.setScreen(Lec20.NEW_VIDEO_SCREEN);
    }
    
    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
    
    
}
