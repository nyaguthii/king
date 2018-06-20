package org.gilbre.app.gilbre;

/**
 * Created by nyaguthii on 11/25/17.
 */

public class Receipt {
    public int id;
    public String customer;
    public int amount;
    public String paymentType;
    public String registration;
    public String servedBy;
    public String memberId;

    public Receipt(int id,int amount,String customer,String paymentType,String registration,String servedBy,String memberId){
        this.id=id;
        this.amount=amount;
        this.customer=customer;
        this.paymentType=paymentType;
        this.registration=registration;
        this.servedBy=servedBy;
        this.memberId=memberId;
    }
    public Receipt(int id,int amount,String customer,String paymentType,String servedBy,String memberId){
        this.id=id;
        this.amount=amount;
        this.customer=customer;
        this.paymentType=paymentType;
        this.servedBy=servedBy;
        this.memberId=memberId;
    }
    public Receipt(int id,int amount,String customer,String paymentType,String servedBy){
        this.id=id;
        this.amount=amount;
        this.customer=customer;
        this.paymentType=paymentType;
        this.servedBy=servedBy;
    }

    public Receipt(){}

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }
    public void setServedBy(String servedBy) {
        this.servedBy =servedBy;
    }
}
