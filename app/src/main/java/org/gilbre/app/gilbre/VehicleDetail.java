package org.gilbre.app.gilbre;

import android.content.ContentValues;
import android.content.Context;
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

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.entity.StringEntity;
//import cz.msebera.android.httpclient.message.BasicHeader;
//import cz.msebera.android.httpclient.protocol.HTTP;




/**
 * Created by nyaguthii on 11/27/17.
 */

public class VehicleDetail extends Fragment {

    private ChaniaDBHelper dbHelper;
    String registration;
    int id;
    int amount;
    Spinner spinner;
    String paymentType;
    EditText vehiclePaymentText;
    TextView registrationText;
    Receipt receipt;
    StringEntity entity;
    JSONObject jsonObject;
    Context context;
    String receiptType;
    String receiptRegistration;
    int receiptId;
    int receiptAmount;
    String receiptCustomer;
    private int dailyTotal;
    String servedBy;
    String memberId;
    TokenManager manager;
    SharedPreferences sharedPref;
    String place;
    TextView totalAmountText;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper= new ChaniaDBHelper(getActivity());
        Bundle bundle= getArguments();
        context=container.getContext();
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));

        if(bundle !=null){
            registration=bundle.getString("registration");
            id=bundle.getInt("id");

        }

        return inflater.inflate(R.layout.fragment_vehicle_detail,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner) view.findViewById(R.id.vehicle_payment_type_spinner);
        populateSpinner();

        registrationText =(TextView) view.findViewById(R.id.registration_text);
        totalAmountText=(TextView)view.findViewById(R.id.total_text);
        registrationText.setText(registration);
        getTotal();


        vehiclePaymentText = (EditText) view.findViewById(R.id.vehicle_amount_text);

        final Button button = (Button) view.findViewById(R.id.vehicle_payment_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(vehiclePaymentText.getText().length()==0){
                    Toast.makeText(getActivity(), "Enter the Amount", Toast.LENGTH_LONG).show();
                } else {
                    //receipt = new Receipt();
                    //receipt.registration=registration;
                    button.setText("Sending....");

                    amount=Integer.parseInt(vehiclePaymentText.getText().toString());
                    paymentType=spinner.getSelectedItem().toString();
                    sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    place = sharedPref.getString("place", "");

                    //Toast.makeText(getActivity(),place, Toast.LENGTH_SHORT).show();

                    AsyncHttpClient client = new AsyncHttpClient();
                    if(manager.getToken().getAccess_token() !=null){
                        client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
                    }

                    String url = "http://197.248.145.182/api/vehicles/payments";
                    //params = new RequestParams();
                    jsonObject = new JSONObject();
                    try{
                        jsonObject.put("id",id);
                        jsonObject.put("registration",registration);
                        jsonObject.put("amount",amount);
                        jsonObject.put("paymentType",paymentType);
                        jsonObject.put("place",place);
                        entity = new StringEntity(jsonObject.toString());

                    }catch(Exception e){}

                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    client.post(context,url,entity,"application/json",
                            new TextHttpResponseHandler(){

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                    Toast.makeText(getActivity(),response+throwable.getMessage(), Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String response) {
                                    Toast.makeText(getActivity(),"Payment created", Toast.LENGTH_LONG).show();



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
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_REGISTRATION,receipt.registration);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);
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
    public void getTotal(){

        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }
        client.get("http://197.248.145.182/api/payments/"+place+"/dailyAmount",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);
                        Toast.makeText(getActivity(),response, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                       /* try{
                            JSONObject json = new JSONObject(response);
                            dailyTotal=json.getInt("total");

                        }catch(Exception e){

                        }
                      */
                        dailyTotal=Integer.parseInt(response);
                        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                        totalAmountText.setText("Kshs "+formatter.format(dailyTotal));


                    }
                });

    }

}
