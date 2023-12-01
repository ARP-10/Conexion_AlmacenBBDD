package JSON;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Lectura {

    /*
     * Lee el archivo JSON a través de una URL, lo convierte a String
     * y lo devuelve una vez llamada la función de convertirJSON
     */

        private String url;
        public Lectura(String url){
            this.url = url;
        }


        public StringBuffer leerArchivo(){
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String linea = null;
                while((linea = reader.readLine())!=null){
                    stringBuffer.append(linea);
                }
                return stringBuffer;
            } catch (
                    IOException e) {
                throw new RuntimeException(e);
            }
        }

        public JSONObject convertirJSON(StringBuffer stringBuffer){
            stringBuffer = leerArchivo();
            return new JSONObject(stringBuffer.toString());
        }

}
