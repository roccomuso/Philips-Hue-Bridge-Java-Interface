/*
 * Interaction with Philips Hue Bridge.
 * 
 * Before you start with encoding and decoding JSON using Java, you will need to install any of the JSON modules available.
 * For this application I downloaded and installed JSON.simple and add the location of json-simple-1.1.1.jar file to environment variable CLASSPATH.
 * 
 */

 
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
 
import org.json.simple.*; //lib json-simple-1.1.1.jar
import org.json.simple.parser.*;
 
 
public class hue_json {
 
    private static String upnp = "http://www.meethue.com/api/nupnp"; // UPNP service to get automatically hue_bridge_ip in LAN.
    private static String hue_bridge_ip = null;
    private static String base_url = "/api/newdeveloper/"; // User autenticated on the bridge: /newdeveloper/
 
    public hue_json(){ //constructor checks for hue bridge local ip address.
 
        try{ // get Hue bridge local IP address if not specified
            if (hue_bridge_ip == null){ //the ip address is not specified, we'll try to get it automatically
 
            // try to get ip from meethue website.
 
                String r = url_reader.address(upnp);
                //System.out.println(r);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(r);
                JSONArray array = (JSONArray) obj;
                JSONObject obj2 = (JSONObject)array.get(0);
 
                hue_bridge_ip = (obj2.get("internalipaddress")).toString();
                //System.out.println(hue_bridge_ip);
 
            }
 
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Error fetching Bridge IP from Meethue.com UPNP service. Make sure the bridge is connected or "+upnp+" reachable"); //System.out.println(e);
            System.exit(1); // Terminate the application.
        }catch (Exception e){
            System.out.println("Generic Exception: "+e);
        }
    }
 
    public hue_json(String ip){ // overloading to define manually hue bridge local ip address
        hue_bridge_ip = ip;
    }
 
    public String getHueBridgeIp(){
        System.out.println(hue_bridge_ip);
        return hue_bridge_ip;
    }
    public String getUpnp(){
        System.out.println(upnp);
        return upnp;
    }
    public String getBaseUrl(){
        System.out.println(base_url);
        return base_url;
    }
    public void setBridgeIp(String z){ // set different ip address for the bridge
        hue_bridge_ip = z;
    }
 
    private static Object getLightProperty(String property_name, Integer n_luce) throws Exception{ // retrieve the value of specified property name.
 
        try{
        String r = url_reader.address("http://"+hue_bridge_ip+base_url+"lights/"+n_luce+"/");
        //System.out.println(r);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(r);
        JSONObject state = (JSONObject) obj;
        JSONObject on = (JSONObject) state.get("state");
 
        return on.get(property_name);
        }
        catch (ClassCastException e){
            System.out.println("Bulb number "+n_luce+" not registered... Bad request!");
            System.exit(1); // Terminate the application.
            return false;
        }
 
    }
 
    public boolean getLightState(Integer n_luce) throws Exception{
        return (boolean) getLightProperty("on", n_luce);
    }
    public Long getLightHue(Integer n_luce) throws Exception{
        return (Long) getLightProperty("hue", n_luce);
    }
    public Long getLightBrightness(Integer n_luce) throws Exception{
        return (Long) getLightProperty("bri", n_luce);
    }
    public Long getLightSaturation(Integer n_luce) throws Exception{
        return (Long) getLightProperty("sat", n_luce);
    }
 
    public static int setLightProperty(Integer n_luce, String prop, Object x) throws Exception{
        URL url;
 
        if (n_luce == 0){ url = new URL("http://"+hue_bridge_ip+base_url+"groups/0/action/");}
            else{    
            url = new URL("http://"+hue_bridge_ip+base_url+"lights/"+n_luce+"/state/");
            }
 
        try{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT"); //Because Hue Bridge RESTful API need PUT request to process a cmd.
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
 
      JSONObject payload = new JSONObject();
 
      /* // data types:
      payload.put("name", "foo");
      payload.put("num", new Integer(100));
      payload.put("balance", new Double(1000.21));
      payload.put("is_vip", new Boolean(true));
      */
      payload.put(prop, x);
      //System.out.print(payload);
 
        osw.write(payload.toString());
 
        osw.flush();
        osw.close();
 
         return connection.getResponseCode(); // 200 - OK (Good response)
 
    }catch (java.net.UnknownHostException e){
     System.out.println("Impossibile connettersi al bridge HUE locale!");
     return -1;   
    }catch (Exception e){
     System.out.println("Eccezione generica: "+ e);
     return -1;
    }
 
    }    
 
    public void setLightState(Integer n_luce, boolean val) throws Exception{
        setLightProperty(n_luce, "on", val);
    }
    public void setLightHue(Integer n_luce, Integer val) throws Exception{
        setLightProperty(n_luce, "hue", val);
    }
    public void setLightSaturation(Integer n_luce, Integer val) throws Exception{
        setLightProperty(n_luce, "sat", val);
    }
    public void setLightBrightness(Integer n_luce, Integer val) throws Exception{
        setLightProperty(n_luce, "bri", val);
    }
    public void setLightEffectOn(Integer n_luce) throws Exception{
        setLightProperty(n_luce, "effect", "colorloop");
    }    
    public void setLightEffectOff(Integer n_luce) throws Exception{
        setLightProperty(n_luce, "effect", "none");
    }  
 
    public void all_off() throws Exception{ // to turn off all the light we can call the method all_off() or directly turn_light(0, false);
      setLightState(0, false); //light 0 means all the lights in the system. (better, the groups 0 contains all the lights registered on the bridge)
    }
 
    public void all_on() throws Exception{ // to turn on all the light we can call the method all_on() or directly turn_light(0, true);
      setLightState(0, true); //light 0 means all the lights in the system.
    }
 
 
    public static void main(String[] args) throws Exception {
 
        hue_json HUE = new hue_json(); // If not specified, the constructor will automatically find the hue bridge local ip address.
        // hue_json HUE = new hue_json("192.168.1.3"); // Manually insert of hue bridge ip.
        /*
        System.out.println(HUE.getLightState(3));
        System.out.println(HUE.getLightBrightness(3));
        System.out.println(HUE.getLightSaturation(3));
        System.out.println(HUE.getLightHue(3));
 
        HUE.all_off();
        HUE.all_on();
        HUE.setLightState(3, true);
        HUE.setLightHue(3, 15000);
        HUE.setLightSaturation(3, 100);
        HUE.setLightBrightness(3, 192);
 
        HUE.setLightEffectOn(3);    
        HUE.setLightEffectOff(3);
        */
        HUE.setLightEffectOff(1);
    }
 
}