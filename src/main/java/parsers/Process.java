/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import Processor.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;



public class Process implements Runnable{
    String vendor;
    
    public Process(String x){
        vendor = x;
        
    }
    
    public void run(){
        try {
               //System.out.println("run");
               int pagenum = 0;
                try {
                    Document doc;
            //String vendor = "82564432";
            boolean first = true;
            int j = 0;
            OkHttpClient client = new OkHttpClient();
            Response response;
                do {
                    

                    Request request = new Request.Builder()
                      .url(String.format("https://api.mercadolibre.com/sites/MLA/search?search_type=scan&seller_id=%s&offset=%d",vendor,j))
                      .get()
                      .addHeader("cache-control", "no-cache")
                      .build();

                    response = client.newCall(request).execute();
                    //System.out.println(response.body().string());

                    //System.out.println(doc.getElementsByTag("body").text().replaceAll("^\\[", "").replaceAll("\\]$", ""));
                    //JSONObject obj = new JSONObject(doc.getElementsByTag("body").text());
                    JSONObject Jobject = new JSONObject(response.body().string());
                    //JSONArray arr = new JSONArray(doc.getEle).string());mentsByTag("body").text());
                        if(first){
                            pagenum = Jobject.getJSONObject("paging").getInt("total");
                            //System.out.println(pagenum);
                            first = false;
                        }
                    JSONArray arr = Jobject.getJSONArray("results");
                    
                    //System.out.println(arr.toString());
                    
                    if(arr.length() == 0){
                           //System.out.println(Data.getVendorDisplay().get(Data.getVendorID().get(vendor))+": Vacio");
                        }else{
                            for (int i = 0; i < arr.length(); i++)
                            {
                                String id = arr.getJSONObject(i).getString("id");
                                String articulo = arr.getJSONObject(i).getString("title").replaceAll("\"", "");
                                String price = String.valueOf(arr.getJSONObject(i).getInt("price")).replace(".", "").replace(",", ".");
                                String link = arr.getJSONObject(i).getString("permalink").replaceAll("\"", "");
                               
                               //System.out.println("id: "+id + " articulo "+ articulo + " price" + price + "link" + link);
                               //if(!price.isEmpty()){
                               String item = String.format("('%s',\"%s\",\"%s\",\"%s\",'%s')",id,Data.getVendorID().get(vendor),articulo.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll("^ +| +$|( )+", "$1"),link,price);
                               //System.out.println(item);
                               Data.getInstance().setColaParser(item);
                               }
                            }  

                
                 j = j + 50;
                 response.close();
                }while(j < pagenum);    
                    //System.out.println(Data.getVendorDisplay().get(Data.getVendorID().get(vendor))+" Finished " + Data.getTiendas().size());
                    Data.getTiendas().remove(vendor);
                    
               } 
               catch (HttpStatusException e) {
                   //System.out.println("Process:77"+e+" "+vendor);
                   Data.getTiendas().remove(vendor);
                   Data.getInstance().setColaLog("Thread :"+vendor+" HTTPStatus"+e+"\n");
                } catch (IOException e) {
                   //System.out.println("Process:82"+e+" "+vendor);
                   Data.getTiendas().remove(vendor);
                   Data.getInstance().setColaLog("Thread :"+vendor+" IOException"+e+"\n");
               }catch(NullPointerException e){
                    //System.out.println("Process:87"+e+" "+vendor);
                    Data.getTiendas().remove(vendor);
                    //System.out.println("Tienda Size: "+Data.getTiendas().size());
                    Data.getInstance().setColaLog("Thread :"+vendor+" NullPointer"+e+"\n");
                }catch(JSONException e){
                    System.out.println(Data.getVendorDisplay().get(Data.getVendorID().get(vendor))+e+" : "+pagenum);
                    Data.getTiendas().remove(vendor);
                    //System.out.println("Tienda Size: "+Data.getTiendas().size());
                    Data.getInstance().setColaLog("Thread :"+vendor+" NullPointer"+e+"\n");
                }
            
           
            //System.out.println("Thread :"+vendor+" is done");
            Data.getInstance().setColaLog("Thread :"+vendor+" is done"+"\n");
        } catch (NumberFormatException e) {
            Data.getTiendas().remove(vendor);
            System.out.println("Process:98 NumberFormat"+e+" "+vendor);
        }
        
    }
    
    
}
