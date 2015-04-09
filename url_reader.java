
 
import java.net.*;
import java.io.*;
 
public class url_reader {
 
 
  public static String address(String x){ // Example of use: url_reader.address("http://www.hackerstribe.com");
       String nextLine;
       URL url;
       URLConnection urlConn;
       InputStreamReader  inStream;
       BufferedReader buff;
       try{
          // Create the URL obect that points
          // at the default file index.html
          url  = new URL(x);
          urlConn = url.openConnection();
         inStream = new InputStreamReader( 
                           urlConn.getInputStream());
           buff= new BufferedReader(inStream);
 
       // Read and print just the first line, to read further lines, we need to put a while(true) construct.
 
            nextLine =buff.readLine();  
            if (nextLine !=null){
                return nextLine; 
            }
 
            return null;
 
 
     } catch(MalformedURLException e){
       System.out.println("Please check the URL:" + 
                                           e.toString() );
     } catch(IOException  e1){
      System.out.println("Can't read from the Internet: "+ 
                                          e1.toString() ); 
  }
 
       return null;
  }
 
 
}