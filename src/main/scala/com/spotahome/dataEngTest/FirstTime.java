package com.spotahome.dataEngTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class FirstTime {

    public static void main(String args[]) throws Exception{
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("vn3sBDdZoNaaNg8YpBOBCh9yo", "VLimdECKrB7Z0RILOSwxqYnQSeyNWQwScGpnY0ZMT0vwwxhxgx");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try{
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                }else{
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                    System.exit(-1);
                }else{
                    te.printStackTrace();
                    System.exit(-1);
                }
            }
        }
        //persist to the accessToken for future reference.
        System.out.println("Successfully ");
        System.exit(0);
    }

}
