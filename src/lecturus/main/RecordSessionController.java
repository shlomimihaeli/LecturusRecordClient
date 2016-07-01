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
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
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
    @FXML
    private Text recordTimer;
    @FXML
    Button editVideoOnlineBtn;
    @FXML
    Pane recordStatusPane;
    
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
    UserSession session;
    
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
        recordBtn.setVisible(false);
        recordStatusPane.setVisible(false);
        editVideoOnlineBtn.setVisible(false);
        
        try{
            
            session = UserSession.getSession();
            recordingSessionNotStarted = true;
            
            
        }catch(Exception e){
            
            myController.setScreen(ScreensController.WELCOME_SCREEN);
            return;
        }
        
        myController.showLoader(true, "setup video background uploader thread");
        
        videoUploader = new VideoUploader(session.getToken(), new VideoUploaderCallback() {
            @Override
            public void onFinish(final int numOfChunks) {
                System.out.println("bg upload finished");
                
                Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            myController.showLoader(true, "Video Uploaded successfully, preparing for edit");
                           // call video end
                            closeVideoSession(numOfChunks);
                        }
                });
               
                
            }

            @Override
            public void onFailed() {
                Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            myController.showLoader(false, "Video upload failed");
                           
                        }
                });
            }

            @Override
            public void onChunkUploaded(int chunkIndex) {
                System.out.println("uploaded chunk "+chunkIndex);
            }
        });
        
        myController.showLoader(true, "setup camera and sound devices");
      
        
       //mPlayer.setMediaPlayer(player);
       
       new Thread(){



                        @Override
                        public void run() {

                            try{
                            var = initVideoRecorder();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            
                             Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                  myController.showLoader(false);
                                  recordBtn.setVisible(true);
                                  recordStatusPane.setVisible(true);
                                    recordBtn.setText("start recording");
                                }
                            });
                             
                            
                           while(isDisplaying){

                               BufferedImage bi = var.draw();
                               
                          
                               // update screen display
                               try{
                                   
                                   Image displayImage = SwingFXUtils.toFXImage(bi, null);
                                    videoDisplayImageView.setImage(displayImage);
                                    
                                    //update 
                                    recordTimer.setText(var.getVideoLengthTimestring());
                                    
                               }catch(Exception e){
                                   
                                   e.printStackTrace();
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
    public boolean onStop() {
       
        if(var.isRecording()){
            
            
            //show in middle of recording
            return false;
            
        }else{
            
            videoUploader = null;
            isDisplaying = false;
            if(var != null) {

                try{
                    var.stopRecording();
                    var = null;
                }catch(Exception e){

                }

            }
            
            return true;
        }
    }
    
    
    
    private VideoAudioRecorder initVideoRecorder(){
 
            
            return new VideoAudioRecorder();
        
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
    private void gotoNewVideoScreen(){
        
        myController.setScreen(ScreensController.NEW_VIDEO_SCREEN);
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
                recordStatusPane.setVisible(false);
                
                myController.showLoader(true, "Finishing upload");
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
    
    public void closeVideoSession(int numOfChunks){
        
        try{
                // close video and prepare it for editing
                JSONObject prepareForEditRes = HttpUtills.restGetAction(Lec20.serverURL+"/video/end?id="+String.valueOf(videoId)+"&parts="+numOfChunks+"&length="+String.valueOf(var.getVideoLength())+"&token="+session.getToken());
                if(prepareForEditRes.get("status").equals("success")){
                    
                    JSONObject data = (JSONObject) prepareForEditRes.get("data");
                    
                    remoteVideoURL = (String) data.get("videoUrl");
                    
                    editVideoOnlineBtn.setVisible(true);
                    editVideoOnlineBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent t) {
                           openVideoEditWebApplication();
                        }
                    });
                    
                    myController.showLoader(false);
                    
                    return;
                }
                
            }catch(Exception e){
                
                    e.printStackTrace();
            }
        
        myController.showLoader(false,"video preparation failed");
            
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
