/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.model;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import lecturus.interfaces.ComboBoxItem;
import lecturus.main.HttpUtills;
import lecturus.rest.RestCallResponse;
import org.json.simple.JSONArray;

/**
 *
 * @author shnizle
 */
public class College extends DBModel<College> implements ComboBoxItem{

    private static String model = "college";
    
    private int id;
    private String name;

    public College(JSONObject jo) {
      
      super(jo);

        this.id = ((Long) jo.get("id")).intValue();
        this.name = (String) jo.get("name");
  }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    
    public static List list(final RestModelQueryResponse callResponse) throws Exception{
        
        final List<College> list = new ArrayList<College>();
        HttpUtills.asyncQueryDataList(model, "get","", new RestCallResponse() {
            @Override
            public void done(JSONObject jo) {
                
                JSONArray data = (JSONArray) jo.get("data");
                
                for(int i=0; i < data.size(); i++){

                    list.add(new College((JSONObject) data.get(i)));
                }
                
                callResponse.done(list);
            }

            @Override
            public void failed() {
                callResponse.failed();
            }
        });
        
        
        return list;
    }
    
    
}
