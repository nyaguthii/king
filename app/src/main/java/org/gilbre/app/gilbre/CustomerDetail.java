package org.gilbre.app.gilbre;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;


import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.entity.StringEntity;
//import cz.msebera.android.httpclient.message.BasicHeader;
//import cz.msebera.android.httpclient.protocol.HTTP;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;


/**
 * Created by nyaguthii on 11/24/17.
 */

public class CustomerDetail extends Fragment {


    private ChaniaDBHelper dbHelper;
    String name;
    int id;
    int amount;
    String paymentType;
    EditText amountEditText;
    Spinner spinner;
    Receipt receipt;
    StringEntity entity;
    JSONObject jsonObject;
    Context context;
    //ByteArrayEntity bEntity;
    TextView customerNameText;
    //RequestParams params;
    String receiptType;
    int receiptId;
    int receiptAmount;
    String receiptCustomer;
    String servedBy;
    String memberId;
    TokenManager manager;
    SharedPreferences sharedPref;
    String place;
    PaymentType[] types;
    String payUrl = "http://192.248.145.182/api/customers/payments";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper= new ChaniaDBHelper(getActivity());
        Bundle bundle= getArguments();
        context=container.getContext();
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));

        if(bundle !=null){
           name=bundle.getString("name");
           id=bundle.getInt("id");

        }
        //TextView customerNameText = (TextView)
        return inflater.inflate(R.layout.fragment_customer_detail,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerNameText = (TextView) view.findViewById(R.id.customer_name_text);
        customerNameText.setText(name);

        spinner = (Spinner) view.findViewById(R.id.type_spinner);

        populateSpinner();

        amountEditText = (EditText) view.findViewById(R.id.amount_edit_text);

        final Button button = (Button) view.findViewById(R.id.customer_save_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(amountEditText.getText().length()==0){
                    Toast.makeText(getActivity(), "Enter the Amount", Toast.LENGTH_LONG).show();
                } else {
                    button.setText("Sending....");
                    paymentType = spinner.getSelectedItem().toString();
                    //receipt = new Receipt();
                    //receipt.customer=name;
                    amount=Integer.parseInt(amountEditText.getText().toString());
                    sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    place = sharedPref.getString("place", "");
                    jsonObject = new JSONObject();

                    try{
                        jsonObject.put("id", id);
                        jsonObject.put("customer", name);
                        jsonObject.put("amount", amount);
                        jsonObject.put("place", place);
                        jsonObject.put("paymentType", paymentType);
                        //entity = new StringEntity(jsonObject.toString());

                    }catch(Exception e){}

                    AsyncHttpClient client = new AsyncHttpClient();
                    if(manager.getToken().getAccess_token() !=null){
                        client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
                    }
                    String url = "http://197.248.145.182/api/customers/payments";
                    //params = new RequestParams();
                    jsonObject = new JSONObject();
                    try{
                        jsonObject.put("id", id);
                        jsonObject.put("customer", name);
                        jsonObject.put("amount", amount);
                        jsonObject.put("place", place);
                        jsonObject.put("paymentType", paymentType);
                        entity = new StringEntity(jsonObject.toString());

                    }catch(Exception e){}
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    client.post(context,url,entity,"application/json",
                            new TextHttpResponseHandler(){

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                    Toast.makeText(getActivity(),"Payment not created", Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String response) {

                                    //Gson gson = new GsonBuilder().create();
                                    //receipt = gson.fromJson(response,Receipt.class);
                                    try{
                                        JSONObject json = new JSONObject(response);
                                        receiptCustomer=json.getString("customer");
                                        receiptId=json.getInt("id");
                                        receiptAmount=json.getInt("amount");
                                        receiptType=json.getString("paymentType");
                                        servedBy=json.getString("servedBy");
                                        memberId=json.getString("memberId");

                                    }catch(Exception e){

                                    }

                                    //customerNameText.setText(receiptCustomer);
                                    receipt = new Receipt(receiptId,receiptAmount,receiptCustomer,receiptType,servedBy,memberId);
                                    addReceiptToDatabase(receipt);

                                    Fragment fragment = new ReceiptFragment();
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.content_area,fragment);
                                    transaction.commit();


                                }
                            });


                }

            }
        });


    }

   public void addReceiptToDatabase(Receipt receipt){
        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_ID,receipt.id);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_AMOUNT,receipt.amount);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_CUSTOMER,receipt.customer);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_TYPE,receipt.paymentType);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_REGISTRATION,"ALL");
       values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_MEMBER_ID,receipt.memberId);

       //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
       db.insert(ChaniaContract.ReceiptEntry.TABLE_NAME,null,values);
       Toast.makeText(getActivity(), "Payment Created", Toast.LENGTH_SHORT).show();


    }
    public void populateSpinner(){
       Cursor cursor = getTypes();

        List<String> types = new ArrayList<String>();
        while(cursor.moveToNext()){
            types.add(cursor.getString(0));
        }

        ArrayAdapter typesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, types);

        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typesAdapter);

    }
    public Cursor getTypes(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT name FROM "+ChaniaContract.PaymentTypeEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;

    }


}
