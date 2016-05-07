/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.ferry.IBuffer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;
import javax.activation.DataSource;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.format.VideoFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFileChooser;
import lecturus.asyncvideouploader.VideoUploaderCallback;
import lecturus.controllers.ScreensController;
import lecturus.interfaces.ControlledScreen;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author shnizle
 */
public class RecordSessionController implements Initializable, ControlledScreen {

    static int videoId;
    
    ScreensController myController; 
    
    @FXML
    Button recordBtn;
    @FXML
    private ImageView videoDisplayImageView;
    @FXML
    private Hyperlink videoEditLink;
    
    private Thread recordThead;
    private int frameIndex = 0;
    private IMediaWriter writer;
    private Webcam webcam;
    private File file;
    private boolean recording = false;
    private boolean displaying = true;
    final int audioStreamIndex = 1;
    final int audioStreamId = 1;
    int channelCount = 2;
    int sampleRate;
    AudioInputStream audioInputStream;
    TargetDataLine aline;
    AudioFormat targetType;
    byte[] audioBuf;
    int audionumber;
    private String remoteVideoURL;
    VideoAudioRecorder var;
    boolean recordingSessionNotStarted = true;
    boolean isDisplaying = false;
    VideoUploader videoUploader;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Media media = new Media("http://localhost:3333");
        //final MediaPlayer player = new MediaPlayer(media);
        
        
        
    }    

    @Override
    public void onResume() {
      
        isDisplaying = true;
        
        videoUploader = new VideoUploader(new VideoUploaderCallback() {
            @Override
            public void onFinish() {
                System.out.println("bg upload finished");
                
                // upload is done, send end video session request and retrive
            }

            @Override
            public void onFailed() {
                System.err.println("bg upload failed");
            }

            @Override
            public void onChunkUploaded(int chunkIndex) {
                System.out.println("uploaded chunk "+chunkIndex);
            }
        });
        
        var = initVideoRecorder();
        
        
       //mPlayer.setMediaPlayer(player);
       
       new Thread(){



                        @Override
                        public void run() {

                           while(isDisplaying){

                               BufferedImage bi = var.draw();
                               
                          
                               // update screen display
                               try{
                                   
                                   Image displayImage = SwingFXUtils.toFXImage(bi, null);
                                    videoDisplayImageView.setImage(displayImage);
                               }catch(Exception e){
                                   
                               }

                               try{
                                    // 10 FPS
                                    Thread.sleep(41);
                                }catch(Exception e){

                                }
                           } 
                        }

                 }.start();
       
        
    }

    @Override
    public void onStop() {
       
        videoUploader = null;
        isDisplaying = false;
        if(var != null) {
            
            try{
                var.stopRecording();
                var = null;
            }catch(Exception e){
                
            }
            
        }
    }
    
    
    
    private VideoAudioRecorder initVideoRecorder(){
 
            
            return new VideoAudioRecorder();
        
    }
    
    private String getRecordCommand(){
        
        /**
         * vlc dshow:// :v4l2-width=320 :v4l2-height=240 :display :sout-keep --sout "#transcode{vcodec=theora,acodec=vorbis,vb=800,ab=128} :display :standard{access=file,dst=C:\Users\shnizle\Documents\Lecturus\capture.ogg}"
         */
        //return "\"c:/Program Files (x86)/VideoLAN/VLC/vlc.exe\" -vvv dshow:// --sout=#transcode{vcodec=mp2v,fps=60,width=1080,acodec=mp2a,scale=1,channels=2,deinterlace,audio-sync}:standard{access-file,mux=ps,dst=\""+getLecturusStorageFolder()+"/123344.avi\"}";
        //return "\"c:/Program Files (x86)/VideoLAN/VLC/vlc.exe\" dshow:// --sout=#transcode{vcodec=h264,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=http{dst=:3333/http://localhost},dst=\""+getLecturusStorageFolder()+"/test8.mpg\"} :sout-keep";
        //return "\"c:/Program Files (x86)/VideoLAN/VLC/vlc.exe\" dshow:// --sout=#transcode{vcodec=h264,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=\""+getLecturusStorageFolder()+"/test8.avi\"}";
        return "\"c:/Program Files (x86)/VideoLAN/VLC/vlc.exe\" dshow:// --sout=#transcode{vcodec=mp2v,vb=1024,fps=30,width=320,acodec=mp2a,ab=128,scale=1,channels=2,deinterlace,audio-sync}:standard{access=file,mux=ps,dst=\""+getLecturusStorageFolder()+"\\Output.mpg\"}";
    }
    
    @FXML
    private void openVideoEditWebApplication(){
        
        try{
         java.awt.Desktop.getDesktop().browse(new URI(remoteVideoURL));
        }catch(Exception e){
            myController.alert("failed opening link");
        }
    }
    
    @FXML
    private void startRecordingBtnAction(ActionEvent evt){
        
        try{
          
            //var.toggleRecord();

            if(recordingSessionNotStarted){
                
                // only one record per session
                recordingSessionNotStarted = false;
                
                // create new video file
                file = new File(getLecturusStorageFolder()+"/"+String.valueOf(videoId)+".mp4");
                
                videoUploader.setFile(file);
                
                // start recording to file
                var.startRecording(file);
                
                recordBtn.setText("Stop");
                
                // start bg uploading
                videoUploader.start();
                
                Runtime.getRuntime().addShutdownHook(new Thread(){
                    @Override
                    public void run() {
                        var.stopRecording();
                        stopRecording();
                    }
                    
                });
                
            }else{
                
                var.stopRecording();
                stopRecording();
                
                videoUploader.setFileHasBeenClosed();
                
                // save file and show link, stop recording
                recordBtn.setVisible(false);
            }
            
            
        }catch(Exception e){
            
            videoUploader.uploadStopped();
            if(var != null) var.stopRecording();
        }
    }
    
    public static String getLecturusStorageFolder(){
        
        return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/LecturusVideos";
    }
    
    private boolean validateLocalStorage(String storage){
        
        File theDir = new File(storage);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + storage);

            try{
                theDir.mkdir();
                return true;
            } 
            catch(SecurityException se){
                //handle it
            }    
        }else{
            return true;
      
        }
        
        return false;
    }
    
    public void closeVideoSession(){
        
        try{
                // close video and prepare it for editing
                JSONObject prepareForEditRes = HttpUtills.restGetAction("http://lecturus2.herokuapp.com/video/end?id="+String.valueOf(videoId)+"&length="+String.valueOf(var.getVideoLength()));
                if(prepareForEditRes.get("status").equals("success")){
                    
                    videoEditLink.setVisible(true);
                    videoEditLink.setText(remoteVideoURL);
                    
                    myController.alert("video uploaded and prepared for edit successfully");
                    
                    // show edit button
                    
                }
                
            }catch(Exception e){
                
                myController.alert("video preparation failed");
                e.printStackTrace();
            }
    }
    
    private void stopRecording(){
        
        System.out.println("Video recorded in file: " + file.getAbsolutePath());
        
        /*File vFile = new File("C:\\Users\\shnizle\\Documents\\NetBeansProjects\\Lec.2.0\\"+String.valueOf(videoId)+".mp4");
        JSONObject saveRes = VideoUploader.uploadFile("http://lecturus2.herokuapp.com/video/upload?id="+String.valueOf(videoId), "video", vFile);
        if(saveRes != null && saveRes.get("status").equals("success")){
            
            JSONObject jData = (JSONObject) saveRes.get("data");
            remoteVideoURL = jData.get("videoUrl").toString();
            
            try{
                // close video and prepare it for editing
                JSONObject prepareForEditRes = HttpUtills.restGetAction("http://lecturus2.herokuapp.com/video/end?id="+String.valueOf(videoId)+"&length="+String.valueOf(vFile.length()));
                if(prepareForEditRes.get("status").equals("success")){
                    
                    videoEditLink.setVisible(true);
                    videoEditLink.setText(remoteVideoURL);
                    
                    myController.alert("video uploaded and prepared for edit successfully");
                    
                    // show edit button
                    
                }
                
            }catch(Exception e){
                myController.alert("video preparation failed");
                e.printStackTrace();
            }
        }else{
            
            myController.alert("video upload failed");
        }*/
        
        
        
    }
    
    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
