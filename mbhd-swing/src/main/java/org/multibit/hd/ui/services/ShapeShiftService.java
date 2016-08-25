package org.multibit.hd.ui.services;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.fasterxml.jackson.core.json.*;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

    /**
     * Created by Keepkey on 8/23/16.
     */
    public class ShapeShiftService {
        Client shapeShiftClient = Client.create();
        public HashMap <String,String> CurrencyTable = this.getCurrencyType();
        static String pair ="btc_";
        final static String apiKey = "6ad5831b778484bb849da45180ac35047848e5cac0fa666454f4ff78b8c7399fea6a8ce2c7ee6287bcd78db6610ca3f538d6b3e90ca80c8e6368b6021445950b";
        private String current_currency_Pair = "";

            public HashMap<String,String> getCurrencyType(){

                HashMap <String,String> altCoinNames = new HashMap<String, String>();
                WebResource webResource = shapeShiftClient.resource("https://shapeshift.io/getcoins");
                ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
                if(response.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }
                try {
                    JSONObject Output = new JSONObject(response.getEntity(String.class));

                    Iterator<String> iter = Output.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        JSONObject currency = (JSONObject) Output.get(key);
                        String name = (String)currency.get("name");
                        String symbol = (String)currency.get("symbol");
                        altCoinNames.put(name,symbol);
                    }


                } catch (JSONException e) {
                        e.printStackTrace();
                }
                return altCoinNames;

            }

        public HashMap <String,Object> getMarketInfo(String altCoinName){
            HashMap <String,Object> marketInfo = new HashMap<String,Object>();
            final String currency_pair = pair + this.CurrencyTable.get(altCoinName).toLowerCase();
            current_currency_Pair = currency_pair;
            WebResource webResource = shapeShiftClient.resource("https://shapeshift.io/marketinfo/"+ currency_pair);
            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
            if(response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
            try {
                JSONObject Output = new JSONObject(response.getEntity(String.class));

                Iterator<String> iter = Output.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Object value =  Output.get(key);
                    marketInfo.put(key,value);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return marketInfo;
        }
        public boolean isValidAltCoinAddress (String Address,String coinName){
            final String altCoinSymbol = this.CurrencyTable.get(coinName);
            boolean isValid = false;
            WebResource webResource = shapeShiftClient.resource("https://shapeshift.io/validateAddress/" + Address + "/" + altCoinSymbol);
            System.out.println(webResource);
            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
            if(response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
            try {
                JSONObject Output = new JSONObject(response.getEntity(String.class));
                isValid = (Boolean)Output.get("isvalid");


            } catch (JSONException e) {
                e.printStackTrace();
            }
         return isValid;
        }
        public HashMap <String,Object> createTransaction(String withdrawalAddress,Long Amount,String returnAddress){
            HashMap <String,Object> transactionInfo = new HashMap<String,Object>();
            WebResource webResource = shapeShiftClient.resource("https://shapeshift.io/sendamount");
            JSONObject input = new JSONObject();
            try {
                input.put("depositAmount",Amount.toString());
                input.put("withdrawal",withdrawalAddress);
                input.put("returnAddress",returnAddress);
                input.put("apiKey",apiKey);
                input.put("pair",current_currency_Pair);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class,input.toString());
            if(response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
            String temp = response.getEntity(String.class);
            if(true) {
                try {


                    JSONObject Output = new JSONObject(temp);
                    JSONObject successOutput = (JSONObject) Output.get("success");

                    Iterator<String> iter = successOutput.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        Object value = successOutput.get(key);
                        transactionInfo.put(key, value);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return transactionInfo;
        }

        public static void main(String [] args){
            ShapeShiftService service = new ShapeShiftService();
            HashMap <String,Object> marketInfo = service.getMarketInfo("Litecoin");
            System.out.println(service.isValidAltCoinAddress("LWdfXUxLBV9nCJ6yk5Ed2pBNhiV7kTaTQJ","Litecoin"));
            HashMap <String,Object> transactionInfo = service.createTransaction("LWdfXUxLBV9nCJ6yk5Ed2pBNhiV7kTaTQJ",Long.valueOf(1),"1AhAAJ5nhMpT7fsnW9Hv3UjgP8ZFEf5e5i");

        }

    }
