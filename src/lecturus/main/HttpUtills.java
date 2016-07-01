/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import com.sun.javafx.css.Stylesheet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javafx.application.Platform;
import lecturus.rest.RestCallResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author shnizle
 */
public class HttpUtills {
    
    private static final boolean debug = true;
    
    public static Thread asyncQueryDataList(String model, String action, String filters, RestCallResponse callResponse) throws NoUserSessionException{
        
        return asyncRestGetAction(Lec20.serverURL+"/"+model+"/"+action+"?"+((!debug) ? "token="+UserSession.getSession().getToken() : "debug=true")+"&"+filters, callResponse);
        
    }
    
    public static JSONArray queryDataList(String model, String action, String filters) throws ParseException, Exception{
        
        return restGetActionForDataList(Lec20.serverURL+"/"+model+"/"+action+"?"+((!debug) ? "token="+UserSession.getSession().getToken() : "debug=true")+"&"+filters);
        
    }
    
    public static JSONArray queryDataList(String model, String action) throws ParseException, Exception{
        
        return restGetActionForDataList(Lec20.serverURL+"/"+model+"/"+action+"?"+((!debug) ? "token="+UserSession.getSession().getToken() : "debug=true"));
        
    }
    
     public static JSONObject queryData(String model, String action) throws ParseException, Exception{
        
        return restGetActionForData(Lec20.serverURL+"/"+model+"/"+action+"?"+((!debug) ? "token="+UserSession.getSession().getToken() : "debug=true"));
        
    }
    
    public static JSONObject queryData(String model, String action, String getParams) throws ParseException, Exception{
        
        return restGetActionForData(Lec20.serverURL+"/"+model+"/"+action+"?"+getParams+"&"+((!debug) ? "token="+UserSession.getSession().getToken() : "debug=true"));
        
    }
    
    public static JSONArray restGetActionForDataList(String url) throws ParseException, NoUserSessionException, Exception{
        
        JSONParser jo = new JSONParser();
        JSONObject result = (JSONObject) (jo.parse(HttpUtills.get(url)));
        //check is success  
        if(result.get("status").equals("success")){
            return (JSONArray) result.get("data");
        }else{
            throw new Exception(result.get("msg").toString());
        }
    }
    
    public static JSONObject restGetActionForData(String url) throws ParseException, Exception{
        
        JSONParser jo = new JSONParser();
        JSONObject result = (JSONObject) (jo.parse(HttpUtills.get(url)));
        //check is success  
        if(result.get("status").equals("success")){
            return (JSONObject) result.get("data");
        }else{
            throw new Exception(result.get("msg").toString());
        }
    }
    
    public static Thread asyncRestGetAction(final String url, final RestCallResponse callResponse){
        
        Thread t = new Thread(){
            
            @Override
            public void run() {
                
                try{
                    JSONParser jo = new JSONParser();
                    final JSONObject d = (JSONObject) (jo.parse(HttpUtills.get(url)));
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                           callResponse.done(d);
                        }
                    });

                }catch(Exception e){
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                           callResponse.failed();
                        }
                    });
                }
                
            }
          
        };
        
        t.start();
        
        return t;
        
    }
    
    public static JSONObject restGetAction(String url) throws ParseException, Exception{
        
        JSONParser jo = new JSONParser();
        return (JSONObject) (jo.parse(HttpUtills.get(url)));
    }
    public static String get(String url) throws Exception{
        
        //url = URLEncoder.encode(url, "UTF-8");
        url = url.replace(" ", "%20");
        
        //String url = "http://www.google.com/search?q=mkyong";
		
		      URL obj = new URL(url);
		      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		//con.setRequestProperty("User-Agent", Stylesheet.Origin.USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		      BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
                return response.toString();
    }
}
