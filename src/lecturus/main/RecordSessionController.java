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
    
    public final String localStorage = "c:/Documents/Lecturus/";
    private Thread recordThead;
    private int frameIndex = 0;
    private IMediaWriter writer;
    private Webcam webcam;
    private File file;
    private boolean recording = false;
    private boolean displaying = true;
    private AudioFormat audioFormat;
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
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        if(validateLocalStorage(getLecturusStorageFolder())){
            
            /**try{
             
                Process p = Runtime.getRuntime().exec(getRecordCommand());

            }catch(IOException e){
                e.printStackTrace();
            }*/
            
            //startDisplaying();
        }
        
        //Media media = new Media("http://localhost:3333");
        //final MediaPlayer player = new MediaPlayer(media);
        
        
        
       //mPlayer.setMediaPlayer(player);
       
       var = new VideoAudioRecorder();
        
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
        var.toggleRecord();
        
        if(var.recording){
             
             new Thread(){
                    
                    
                    
                    @Override
                    public void run() {
                        
                       while(var.recording){
                           
                           var.draw();
                           
                           try{
                                // 10 FPS
                                Thread.sleep(41);
                            }catch(Exception e){

                            }
                       } 
                    }
                    
             }.start();
           // recordBtn.setText("Stop");
        }else{
            //stopRecording();
            //recordBtn.setVisible(false);
        }
    }
    
    private String getLecturusStorageFolder(){
        
        return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Lecturus";
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
    
    private void setupCam(){
        
        
		      Dimension size = WebcamResolution.QVGA.getSize();

                      //writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, 640, 360);

		      webcam = Webcam.getDefault();
		webcam.setViewSize(size);
		webcam.open(true);
    }
    
    private void setupVideoFile(){
        avSetup();
        file = new File(String.valueOf(videoId)+".mp4");

		      writer = ToolFactory.makeWriter(file.getName());
		      Dimension size = WebcamResolution.QVGA.getSize();

                      writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
                       audionumber = writer.addAudioStream(audioStreamIndex, 0, channelCount, sampleRate);
    }
    
    public void startDisplaying(){
        
        setupCam();
        
        
        frameIndex = 0;
        

		final long start = System.nanoTime();

                recordThead = new Thread(){
                    
                    
                    
                    @Override
                    public void run() {
                        
                        while(displaying || recording){
                            
                            /**
                             * capture camera
                             */
                            

                                             BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);

                                       Image displayImage = SwingFXUtils.toFXImage(image, null);
                            videoDisplayImageView.setImage(displayImage);
                            
                            if(!recording) continue;

                            System.out.println("Capture frame " + frameIndex);
                            System.out.println("file size:"+file.length());
                            
                                             IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

                                             IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
                            frame.setKeyFrame(frameIndex == 0);
                            frame.setQuality(0);

                            //writer.encodeVideo(0, frame);//image, (System.nanoTime()-start) / 1000,  TimeUnit.NANOSECONDS);
                            writer.encodeVideo(0,image, (System.nanoTime()-start) ,  TimeUnit.NANOSECONDS);

                             
                            
                            //audio recording stuff
                            if (aline.available() == 88200) {
                                byte[] audioBytes = new byte[ aline.getBufferSize() / 2 ]; // best size?
                                int nBytesRead = 0;
                              nBytesRead = aline.read(audioBuf, 0, aline.available());//audioBuf.length);//aline.available());
                              if (nBytesRead>0) {
                                  
                                  int numSamplesRead = nBytesRead / 2 +2;
                                    short[] audioSamples = new short[ numSamplesRead ];
                                    if (audioFormat.isBigEndian()) {
                                        for (int i = 0; i < numSamplesRead; i++) {
                                            audioSamples[i] = (short)((audioBytes[2*i] << 8) | audioBytes[2*i + 1]);
                                        }
                                    }
                                    else {
                                        for (int i = 0; i < numSamplesRead; i++) {
                                            try{
                                                audioSamples[i] = (short)((audioBytes[2*i + 1] << 8) | audioBytes[2*i]);
                                            }catch(Exception e){
                                                
                                            }
                                        }
                                    }
                                  
                                  IBuffer iBuf = IBuffer.make(null, audioBuf, 0, nBytesRead);
                                  IAudioSamples smp = IAudioSamples.make(iBuf, channelCount, IAudioSamples.Format.FMT_S16);

                                if (smp!=null) {
                                  long numSample = nBytesRead/smp.getSampleSize();
                                  smp.setComplete(true, numSample, (int) audioFormat.getSampleRate(), audioFormat.getChannels(), IAudioSamples.Format.FMT_S16, (System.nanoTime()-start));
                                  smp.put(audioBuf, 1, 0, aline.available());
                                  try {
                                    //writer.encodeAudio(audionumber, smp);
                                    writer.encodeAudio(audionumber, audioSamples, (System.nanoTime()-start),  TimeUnit.NANOSECONDS );
                                  }
                                  catch(Exception e) {
                                    System.out.println("EXCEPTION: " + e);
                                  }
                                }
                              }
                            }

                            frameIndex++;

                            try{
                                // 10 FPS
                                Thread.sleep(41);
                            }catch(Exception e){

                            }
                        }
                    }

                    
                };
                
                recordThead.start();
              
    }
    
    void avSetup() {
        audioFormat = new AudioFormat(44100.0F, 16, channelCount, true, false);
        sampleRate = (int) audioFormat.getSampleRate();
        channelCount = audioFormat.getChannels();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
          aline = (TargetDataLine) AudioSystem.getLine(info);
          aline.open(audioFormat);
          aline.start();
          //println("audio line");
        }
        catch (LineUnavailableException e)
        {
          //println("unable to get a recording line");
          e.printStackTrace();
          //exit();
        }
        int bufferSize = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize();
        audioBuf = new byte[bufferSize];
        targetType = aline.getFormat();
        audioInputStream = new AudioInputStream(aline);
        
       
        
      }
    
    private void stopRecording(){
        
        recording = false;
        writer.close();
        webcam.close();
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
    
    private void rec2(){
        
        BufferedImage s1 = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);//genImage();
        //writer = ToolFactory.makeWriter("temp/" + sermon.getFile().getName() + ".flv");

        File f = new File(String.valueOf(videoId)+".flv");

		      writer = ToolFactory.makeWriter(file.getName());
		      Dimension size = WebcamResolution.QVGA.getSize();

                      //writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
                       //audionumber = writer.addAudioStream(audioStreamIndex, 0, channelCount, sampleRate);
        
        String filename = f.getAbsolutePath();
        IContainer container = IContainer.make();

        if (container.open(filename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: " + filename);
        }
        int numStreams = container.getNumStreams();

        int audioStreamId = -1;
        IStreamCoder audioCoder = null;
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                audioStreamId = i;
                audioCoder = coder;
                break;
            }
        }
        if (audioStreamId == -1) {
            throw new RuntimeException("could not find audio stream in container: " + filename);
        }

        if (audioCoder.open() < 0) {
            throw new RuntimeException("could not open audio decoder for container: " + filename);
        }
        writer.addAudioStream(0, 0, audioCoder.getChannels(), audioCoder.getSampleRate());
        writer.addVideoStream(1, 1, size.width, size.height);
        IPacket packet = IPacket.make();
        int n = 0;
        while (container.readNextPacket(packet) >= 0) {
            n++;

            if (packet.getStreamIndex() == audioStreamId) {
                IAudioSamples samples = IAudioSamples.make(2048, audioCoder.getChannels());
                int offset = 0;
                while (offset < packet.getSize()) {
                    try {
                        int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                        if (bytesDecoded < 0) {
                            //throw new RuntimeException("got error decoding audio in: " + filename);
                            break;
                        }
                        offset += bytesDecoded;

                        if (samples.isComplete()) {
                            if (n % 1000 == 0) {
                                writer.flush();
                                System.out.println(n);
                                System.gc();
                            }
                            writer.encodeAudio(0, samples);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            } else {
                do {
                } while (false);
            }
        }
        for (int i = 0; i < container.getDuration() / 1000000; i++) {
            writer.encodeVideo(1, s1, i, TimeUnit.SECONDS);
        }

        writer.close();

        if (audioCoder != null) {
            audioCoder.close();
            audioCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
        //return "temp/" + sermon.getFile().getName() + ".flv";
    
    }
    
    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
