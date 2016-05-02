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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author shnizle
 */
public class HttpUtills {
    
    public static JSONObject restGetAction(String url) throws ParseException, Exception{
        
        JSONParser jo = new JSONParser();
        return (JSONObject) (jo.parse(HttpUtills.get(url)));
    }
    public static String get(String url) throws Exception{
        
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
