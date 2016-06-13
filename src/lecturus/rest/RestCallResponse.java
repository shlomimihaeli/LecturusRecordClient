/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.rest;

import org.json.simple.JSONObject;

/**
 *
 * @author shnizle
 */
public abstract class RestCallResponse {
    
    public abstract void done(JSONObject jo);
    public abstract void failed();
}
