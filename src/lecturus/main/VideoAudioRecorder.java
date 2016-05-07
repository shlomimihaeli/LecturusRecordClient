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
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.ferry.IBuffer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.video.ConverterFactory;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import static lecturus.main.RecordSessionController.videoId;

public class VideoAudioRecorder{

    IMediaWriter imw;
    IStreamCoder isc;
    BufferedImage bgr;
    int vidRate = 30;
    long sTime;
    long fTime;

    //Capture cam;

    final int audioStreamIndex = 1;
    final int audioStreamId = 1;
    final int channelCount = 2;
    int sampleRate;

    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    TargetDataLine aline;
    AudioFormat targetType;

    byte[] audioBuf;
    int audionumber;

    int widthCapture=640;
    int heightCapture=480;
    
    Webcam webcam;

    private boolean recording;
    
    File file;

    void setup() {
      //frameRate(30);
      //size(widthCapture, heightCapture, JAVA2D);
      
      avSetup();
    }

    public VideoAudioRecorder() {
        
       
        
        setupCam();
    }
    
    private void setupCam(){
        
        
		      Dimension size = WebcamResolution.QVGA.getSize();

                      //writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, 640, 360);

		      webcam = Webcam.getDefault();
		webcam.setViewSize(size);
		webcam.open(true);
    }
    
    BufferedImage draw() {


        if (recording) {
          if (imw.isOpen()) {
            //video recording stuff
            long cTime = System.nanoTime()-fTime;
            if (cTime >= (double)1000/vidRate) {
              /*bgr.getGraphics().drawImage(cam.getImage(), 0, 0,
              new ImageObserver() {
                public boolean imageUpdate(Image i, int a, int b, int c, int d, int e) {
                  return true;
                }
              }
              );*/
              bgr = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
              imw.encodeVideo(0, bgr, System.nanoTime()-sTime, TimeUnit.NANOSECONDS);
              //audio recording stuff
              if (aline.available() == 88200) {
                int nBytesRead = aline.read(audioBuf, 0, aline.available());//audioBuf.length);//aline.available());
                if (nBytesRead>0) {
                    IBuffer iBuf = IBuffer.make(null, audioBuf, 0, nBytesRead);
                  IAudioSamples smp = IAudioSamples.make(iBuf, channelCount, IAudioSamples.Format.FMT_S16);

                  if (smp!=null) {
                    long numSample = nBytesRead/smp.getSampleSize();
                    smp.setComplete(true, numSample, (int) audioFormat.getSampleRate(), audioFormat.getChannels(), IAudioSamples.Format.FMT_S16, (System.nanoTime()-sTime) / 1000);
                    smp.put(audioBuf, 1, 0, aline.available());
                    try {
                      imw.encodeAudio(audionumber, smp);
                    }
                    catch(Exception e) {
                     // println("EXCEPTION: " + e);
                    }
                  }
                }
              }
              fTime = System.nanoTime();
              
              return bgr;
            }
          }
          
        }else{
            
            return ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
        }
    
      
      return null;
    }
    
    public int getVideoLength(){
        
        return (int) (fTime - sTime / 1000000000.0);
    }

    /*public void toggleRecord(){
        if (!recording) {
          startRecording();
          
        }else{
            //println("saving");
          stopRecording();
        }
    }*/
    
    public boolean isRecording(){
        
        return recording;
    }
    
    public void startRecording(File videoFile){
        
         this.file = videoFile;
        
        setup();
          avRecorderSetup();
          recording = true;
    }
    
    public void stopRecording(){
        
        recording = false;
          //imw.flush();
          if(imw != null) imw.close();
          if(webcam != null) webcam.close();
    }
    
   /* public void keyPressed() {
      if (key == 'r') {
        if (!recording) {
          //println("recording");
          avRecorderSetup();
          recording = true;
        }
      }
      if (key == 's') {
        if (recording) {
          //println("saving");
          imw.flush();
          imw.close();
          recording = false;
        }
      }
    }*/

    void avSetup() {
      audioFormat = new AudioFormat(44100.0F, 16, channelCount, true, false);
      sampleRate = (int) audioFormat.getSampleRate();
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
          System.exit(0);
      }
      int bufferSize = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize();
      audioBuf = new byte[bufferSize];
      targetType = aline.getFormat();
      audioInputStream = new AudioInputStream(aline);
    }

    void avRecorderSetup() {
        //File file = new File(String.valueOf(videoId)+".mp4");

		    
      imw = ToolFactory.makeWriter(file.getAbsolutePath());//or "output.avi" or "output.mov"
      imw.open();
      imw.setForceInterleave(true);
      imw.addVideoStream(0, 0, IRational.make((double)vidRate), widthCapture, heightCapture);
      audionumber = imw.addAudioStream(audioStreamIndex, audioStreamId, channelCount, sampleRate);
      isc = imw.getContainer().getStream(0).getStreamCoder();
      bgr = new BufferedImage(widthCapture, heightCapture, BufferedImage.TYPE_3BYTE_BGR);
      sTime = fTime = System.nanoTime();
    }

}