/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.model;

import java.util.ArrayList;
import java.util.List;
import lecturus.main.HttpUtills;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author shnizle
 */
public abstract class DBModel<ModelClass> {
    
    protected String model;
     ModelClass mc;

    public DBModel(){
        
        ModelClass mc;
    }
    
    public DBModel(JSONObject jo) {
    }
    
    
    
}
