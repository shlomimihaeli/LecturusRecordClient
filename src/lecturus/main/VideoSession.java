/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

/**
 *
 * @author shnizle
 */
public class VideoSession {

    private static VideoSession currSession;
    
    private int videoId;
    
    private VideoSession(int videoId) {
    
        this.videoId = videoId;
    }
    
    public static VideoSession startNewSession(int videoId){
        
        return currSession = new VideoSession(videoId);
    }
    
    public static VideoSession getCurrentSession() throws Exception{
        
        if(currSession == null) throw new Exception("video session has not started");
        return currSession;
    }
}
