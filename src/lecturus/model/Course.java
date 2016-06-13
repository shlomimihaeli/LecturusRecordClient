/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.model;

import java.util.ArrayList;
import java.util.List;
import lecturus.interfaces.ComboBoxItem;
import lecturus.main.HttpUtills;
import lecturus.rest.RestCallResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author shnizle
 */
public class Course extends DBModel<Course> implements ComboBoxItem{
    
    private static String model = "course";
    
  private final int id;
  private final String name;
  
  public Course(JSONObject jo) {
      
      super(jo);

        this.id = ((Long) jo.get("id")).intValue();
        this.name = (String) jo.get("name");
  }

  public int getID() {
    return id;
  }

  public String toString() {
    return name;
  }
 
  public static List list(int collegeId) throws Exception{
        
        List<Course> list = new ArrayList<Course>();
        JSONArray data = HttpUtills.queryDataList(model, "get", "college_id="+String.valueOf(collegeId));
        for(int i=0; i < data.size(); i++){
            
            list.add(new Course((JSONObject) data.get(i)));
        }
        
        return list;
    }
  
  public static List list(int collegeId, final RestModelQueryResponse callResponse) throws Exception{
        
        final List<Course> list = new ArrayList<Course>();
        HttpUtills.asyncQueryDataList(model, "get","college_id="+String.valueOf(collegeId), new RestCallResponse() {
            @Override
            public void done(JSONObject jo) {
                
                JSONArray data = (JSONArray) jo.get("data");
                
                for(int i=0; i < data.size(); i++){

                    list.add(new Course((JSONObject) data.get(i)));
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
