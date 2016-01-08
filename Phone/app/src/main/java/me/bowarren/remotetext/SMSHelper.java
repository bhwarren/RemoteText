package me.bowarren.remotetext;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bhwarren on 11/20/15.
 */
public class SMSHelper {


    //what to do when recieved a text --> needs to implement the android lib
    public static void onRecieved(){

    }

    //send the actual sms that was gotten from computer
    public static void sendText(String phoneNumber, String message){

    }

    //get latest texts for the computer
    public static JSONObject getLatestTexts(){
        //for the latest X threads, get latest
        return null;
    }

    public static JSONObject sendLatestTexts(){
        return null;
    }


    public class ContactThread {
        private final int NUMMESSAGES = 5;

        private String contactName;
        private String phoneNumber;
        private ArrayList<String> messages;

        public ContactThread(String number){
            phoneNumber = number;
            contactName = getNameFromNumber(number);
            messages = getLatestMessages(NUMMESSAGES);
        }

        private ArrayList<String> getLatestMessages(int numMsgs){
            return null;
        }
        private void updateMessages(){
            messages = getLatestMessages(NUMMESSAGES);
        }

        public String getContactName(){
            return contactName;
        }
        public String getPhoneNumber(){
            return phoneNumber;
        }
        public ArrayList<String> getMessages(){
            return messages;
        }

        public String getNameFromNumber(String number){
            return null;
        }

    }

}
