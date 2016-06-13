/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.asyncvideouploader;

/**
 *
 * @author shnizle
 */
public abstract class VideoUploaderCallback {
    
    public abstract void onFinish(int numOfChunks);
    public abstract void onFailed();
    public abstract void onChunkUploaded(int chunkIndex);
}
