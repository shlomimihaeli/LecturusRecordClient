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
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

/**
 *
 * @author shnizle
 */
public class StatusBar implements Initializable{

    @FXML
    Text statusBarText;
    
    @FXML
    Button retryButton;
    
    @FXML
    ProgressIndicator spinner;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        retryButton.setVisible(false);
        spinner.setVisible(false);
        statusBarText.setText("");
    }
    
    public void showLoader(boolean show, String status){
        
        statusBarText.setText(status);
        spinner.setVisible(show);
        
    }
    
     public void retryMessage(boolean show, String status){
        
        statusBarText.setText(status);
        spinner.setVisible(show);
        
    }
    
    public void showLoader(boolean show){
        
        showLoader(show, "");
        
    }
    
}
