/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import lecturus.asyncvideouploader.VideoUploaderCallback;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.net.www.http.HttpClient;

/**
 *
 * @author shnizle
 */
public class VideoUploader extends Thread{
    
    public static final int CHUNK_SIZE = 1*500*1024;
    private static final int MAX_RETRIES = 10;
    
    private boolean fileCloded = false;
    private boolean uploadDone = false;
    private File fileToUpload;
    private boolean uploadStopped = false;
    private VideoUploaderCallback callback;

    public VideoUploader(VideoUploaderCallback callback) {
    
        this.callback = callback;
    }
    
    
    
    
    public void fileClosed(){
        
        
    }
    
    public VideoUploader setFile(File fileToUpload){
        
        this.fileToUpload = fileToUpload;
        return this;
    }
    
    /**
     * this thread will
     */
    public void run(){
        
        int retry = 0;
        int chunkIndex = 0;
        
        if(fileToUpload == null){
            return;
        }
        
        /**
         * while not passed max upload retries
         * while fileClosed flag was not triggered meaning the file is still being recorded, keep trying
         * 
         */
        while(retry < MAX_RETRIES && !uploadDone && !uploadStopped){
            
            // check if mroe data is left to upload
            // check if at least chunk size available to upload OR the file has been closed upload left overs
            long fileLength = fileToUpload.length();
            if(chunkIndex * CHUNK_SIZE < fileLength && ((chunkIndex+1)*CHUNK_SIZE < fileLength || fileCloded)){
                
                    try{
                        JSONObject saveRes = VideoUploader.uploadFileChunk("http://lecturus2.herokuapp.com/video/upload?id=20&index="+chunkIndex, "video", fileToUpload, chunkIndex*CHUNK_SIZE , CHUNK_SIZE);
                        if(saveRes.get("status").equals("success")){

                            callback.onChunkUploaded(chunkIndex);
                            System.out.println("chunk "+chunkIndex+" uploaded");
                            chunkIndex++;
                        }
                        System.out.println(saveRes.toJSONString());
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                
            }else if(fileCloded && chunkIndex * CHUNK_SIZE >= fileToUpload.length()){
                
                // check if file has been closed
                uploadDone = true;
            }
            
            try{
                /**
                 * if file stopped recording pick up the pase no need to wait for buffer
                 */
                sleep((fileCloded) ? 1 : 300);
                
            }catch(Exception e){
                
            }
        }
        
        if(retry < MAX_RETRIES && !uploadStopped){
            
            callback.onFinish();
            System.out.println("file chunks upload done\n"+chunkIndex+" chunks uploaded");
        }else{
            callback.onFailed();
            System.out.println("file chunks upload failed");
        }
            
        
    }
    
    public void uploadStopped(){
        
        uploadStopped = true;
    }
    
    public void setFileHasBeenClosed(){
        
        fileCloded = true;
    }
    
    public static JSONObject uploadFileChunk(String urlString, String exsistingFileName, File localFile, int startFrom, int chunkSize){
        
        HttpURLConnection conn = null;
        BufferedReader br = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
 
        InputStream is = null;
        OutputStream os = null;
        boolean ret = false;
        String StrMessage = "";
        //String exsistingFileName = "C:\\temp\\screenCapture_20110413_052404.GIF";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";


        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1*1024*1024;


        String responseFromServer = "";

        //String urlString = "http://localhost:8080/M-Admin_1.0_Web_Module/ReceiveScreenShotServlet";


        try
        {
         //------------------ CLIENT REQUEST

            FileInputStream fileInputStream = new FileInputStream( localFile );

         // open a URL connection to the Servlet 

            URL url = new URL(urlString);


         // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();

         // Allow Inputs
         conn.setDoInput(true);

         // Allow Outputs
         conn.setDoOutput(true);

         // Don't use a cached copy.
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");

         conn.setRequestProperty("Connection", "Keep-Alive");

         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

         dos = new DataOutputStream( conn.getOutputStream() );

         dos.writeBytes(twoHyphens + boundary + lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"" + exsistingFileName +"\";"
            + " filename=\"" + exsistingFileName +"\"" + lineEnd);
         dos.writeBytes(lineEnd);



         // create a buffer of maximum size

         bytesAvailable = fileInputStream.available();
         //bufferSize = Math.min(bytesAvailable, maxBufferSize);
         //buffer = new byte[bufferSize];

         // read file and write it into form...
         int byteLengthToUpload = Math.min(chunkSize, bytesAvailable-startFrom);
         byte[] bytesToUpload = new byte[byteLengthToUpload+20];
         
         long bytesSkeeped = 0;
         while((bytesSkeeped+=fileInputStream.skip(startFrom-bytesSkeeped)) < startFrom){};
         
         fileInputStream.read(bytesToUpload, 0, byteLengthToUpload);
         
         dos.write(bytesToUpload, 0, byteLengthToUpload);
         
         /*bytesRead = fileInputStream.read(buffer, startFrom, bufferSize);

         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }*/

         // send multipart form data necesssary after file data...

         dos.writeBytes(lineEnd);
         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

         // close streams

         fileInputStream.close();
         dos.flush();
         dos.close();


        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
         System.out.println("From ServletCom CLIENT REQUEST:"+ex);
        }

        catch (IOException ioe)
        {
            ioe.printStackTrace();
         System.out.println("From ServletCom CLIENT REQUEST:"+ioe);
        }


        //------------------ read the SERVER RESPONSE


        try
        {
         inStream = new DataInputStream ( conn.getInputStream() );
         String str;
         StringBuilder responseData = new StringBuilder();
         while (( str = inStream.readLine()) != null)
         {
          System.out.println("Server response is: "+str);
          System.out.println("");
          responseData.append(str);
         }
         inStream.close();
         
         
        JSONParser jo = new JSONParser();
        JSONObject res = (JSONObject) (jo.parse(responseData.toString()));
        return res;
         

        }
        catch (IOException ioex)
        {
         System.out.println("From (ServerResponse): "+ioex);

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static JSONObject uploadFile(String urlString, String exsistingFileName, File localFile){
        
        HttpURLConnection conn = null;
        BufferedReader br = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
 
        InputStream is = null;
        OutputStream os = null;
        boolean ret = false;
        String StrMessage = "";
        //String exsistingFileName = "C:\\temp\\screenCapture_20110413_052404.GIF";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";


        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1*1024*1024;


        String responseFromServer = "";

        //String urlString = "http://localhost:8080/M-Admin_1.0_Web_Module/ReceiveScreenShotServlet";


        try
        {
         //------------------ CLIENT REQUEST

            FileInputStream fileInputStream = new FileInputStream( localFile );

         // open a URL connection to the Servlet 

            URL url = new URL(urlString);


         // Open a HTTP connection to the URL

         conn = (HttpURLConnection) url.openConnection();

         // Allow Inputs
         conn.setDoInput(true);

         // Allow Outputs
         conn.setDoOutput(true);

         // Don't use a cached copy.
         conn.setUseCaches(false);

         // Use a post method.
         conn.setRequestMethod("POST");

         conn.setRequestProperty("Connection", "Keep-Alive");

         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

         dos = new DataOutputStream( conn.getOutputStream() );

         dos.writeBytes(twoHyphens + boundary + lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"" + exsistingFileName +"\";"
            + " filename=\"" + exsistingFileName +"\"" + lineEnd);
         dos.writeBytes(lineEnd);



         // create a buffer of maximum size

         bytesAvailable = fileInputStream.available();
         bufferSize = Math.min(bytesAvailable, maxBufferSize);
         buffer = new byte[bufferSize];

         // read file and write it into form...

         bytesRead = fileInputStream.read(buffer, 0, bufferSize);

         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }

         // send multipart form data necesssary after file data...

         dos.writeBytes(lineEnd);
         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

         // close streams

         fileInputStream.close();
         dos.flush();
         dos.close();


        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
         System.out.println("From ServletCom CLIENT REQUEST:"+ex);
        }

        catch (IOException ioe)
        {
            ioe.printStackTrace();
         System.out.println("From ServletCom CLIENT REQUEST:"+ioe);
        }


        //------------------ read the SERVER RESPONSE


        try
        {
         inStream = new DataInputStream ( conn.getInputStream() );
         String str;
         StringBuilder responseData = new StringBuilder();
         while (( str = inStream.readLine()) != null)
         {
          System.out.println("Server response is: "+str);
          System.out.println("");
          responseData.append(str);
         }
         inStream.close();
         
         
        JSONParser jo = new JSONParser();
        JSONObject res = (JSONObject) (jo.parse(responseData.toString()));
        return res;
         

        }
        catch (IOException ioex)
        {
         System.out.println("From (ServerResponse): "+ioex);

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
}
