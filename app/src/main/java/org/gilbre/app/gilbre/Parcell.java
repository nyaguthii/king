package org.gilbre.app.gilbre;

/**
 * Created by root on 3/2/18.
 */

public class Parcell {
    public int id;
    public int amount;
    public String sender;
    public String receiver;
    public String to;
    public String from;
    public String receiver_name;
    public String sender_name;
    public String date;
    public String description;


    public Parcell(int id, int amount,String sender, String receiver, String to, String from
                   ) {
        this.id = id;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.to = to;
        this.from = from;
    }
    public Parcell(int id, int amount,String sender, String receiver, String to, String from
    ,String receiver_name,String sender_name,String date,String description) {
        this.id = id;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.to = to;
        this.from = from;
        this.sender_name=sender_name;
        this.receiver_name=receiver_name;
        this.date=date;
        this.description=description;
    }
    public Parcell(int id, int amount,String sender, String receiver, String to, String from
            ,String receiver_name,String sender_name) {
        this.id = id;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.to = to;
        this.from = from;
        this.receiver_name=receiver_name;
        this.sender_name=sender_name;


    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }




    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
