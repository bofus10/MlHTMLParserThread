/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bots;

import Processor.Configuration;
import Processor.Data;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class TeleBot {

public static void sendMessage(String Msg,String BotToken,String ChatID) throws UnsupportedEncodingException{
    
String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

//String apiToken = "605701056:AAEPFnqA5pz9hzs1M_FKyMd8KI8ZDQDideA";
String apiToken = BotToken;
//String chatId = "-298133567";
String chatId = ChatID;
String text = Msg;
text = URLEncoder.encode(text,"UTF-8");

String urlS = String.format(urlString, apiToken, chatId, text);
//String urlEncode = URLEncoder.encode(urlS, "UTF-8");


try {    
URL url = new URL(urlS);
URLConnection conn = url.openConnection();

StringBuilder sb = new StringBuilder();
InputStream is = new BufferedInputStream(conn.getInputStream());
BufferedReader br = new BufferedReader(new InputStreamReader(is));
//BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//BufferedReader br = new BufferedReader(new InputStreamReader(((HttpURLConnection) (new URL(urlS)).openConnection()).getInputStream(), Charset.forName("UTF-8")));

String inputLine = "";

while ((inputLine = br.readLine()) != null) {
    sb.append(inputLine);
}

String response = sb.toString();
// Do what you want with response

} catch (IOException e) {
    System.out.println(e);
    }

    }

public static void PushMSG(String x, int perc) throws UnsupportedEncodingException, IOException{
    
    //Sacamos el Especial de Muza
    if(perc >= 80 ){
        TeleBot.sendMessage(x,Configuration.getConfig().getProperty("muza_token").replace("\"",""),Configuration.getConfig().getProperty("muza_chat_id").replace("\"",""));
    }else{
        TeleBot.sendMessage(x,Configuration.getConfig().getProperty("cualca_token").replace("\"",""),Configuration.getConfig().getProperty("cualca_chat_id").replace("\"","")); 
    }
}


}
