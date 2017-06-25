/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windowweatherinfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author Alexey
 */
public class WindowEngine  implements ActionListener{

    Window window;
    private final String USER_AGENT = "Mozilla/5.0";

    public WindowEngine(Window window) {
        this.window = window;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String response= null;
        try {
            response=sendGet();
            ParseJSON(response);
            DetectUsbDeviceAndSaveDataToUsbInAnotherThread(response);
            
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(null, ex.getMessage());
            Logger.getLogger(WindowEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    	// HTTP GET request
    private String sendGet() throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+this.window.fieldCity.getText()+"&appid="+this.window.fieldKey.getText();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        StringBuffer response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        con.getInputStream()
                ))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }

    private void ParseJSON(String response) throws JSONException {
        org.json.JSONObject parsedObject = new org.json.JSONObject(response);
        
        JSONObject target  = parsedObject.getJSONObject("main");

        this.window.fieldTemperature.setText(String.format("%.2f °C", target.getDouble("temp")-273));

        Integer value =  target.getInt("humidity");
        this.window.fieldHumidity.setText(value.toString() + "%");

        value = target.getInt("pressure");
        this.window.fieldPressure.setText(value.toString());

        target = parsedObject.getJSONObject("wind");

        try {
            value = target.getInt("speed");
            this.window.fieldSpeed.setText(value.toString() + " m/s");
        } catch (JSONException jSONException) {
            this.window.fieldSpeed.setText("0 m/s");
        }
        try {
            value = target.getInt("deg");
            this.window.fieldDegree.setText(value.toString());
        } catch (JSONException jSONException) {
            this.window.fieldDegree.setText("0");
        }
        
    }



    private String DetectUsbDevice() {
        for (FileStore store: FileSystems.getDefault().getFileStores()) {
            if (store.type().toLowerCase().contains("fat")) {
                if (store.toString().startsWith("/media/"+System.getProperty("user.name"))) {
                    return store.toString().substring(0,store.toString().indexOf("(")).trim()+"/";
                } 
                else {
                    String path = store.toString().substring(store.toString().indexOf("(")+1,store.toString().indexOf(")"));
                    String type =  FileSystemView.getFileSystemView().getSystemTypeDescription(new File(path));
                    if (type.toLowerCase().contains("removable") || type.toLowerCase().contains("rimovibile") || type.toLowerCase().contains("съемный диск")) {
                        return path;
                    }
                }
            }
        }
        return null;
    }

    private void SaveDataToUsb(String path, String response) {
        File file = new File(path);
 
        try {
                if(!file.exists()){
                    file.createNewFile();
                }
                try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                    out.print(response);
                }
        } catch(IOException e) {    
            JOptionPane.showConfirmDialog(null, e.getMessage(),"Information",JOptionPane.DEFAULT_OPTION);
            //throw new RuntimeException(e);    
        }
    }

    private void DetectUsbDeviceAndSaveDataToUsbInAnotherThread(String response) {
        new Thread(() -> {
            String path = DetectUsbDevice();
            if (path != null) {
                path += ".properties";
                SaveDataToUsb(path, response);
                JOptionPane.showConfirmDialog(null, "Информация на флешку записана","Information",JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showConfirmDialog(null, "Информация на флешку не записана\n т.к. она не найдена","Information",JOptionPane.DEFAULT_OPTION);
        }).start();
    }
}
