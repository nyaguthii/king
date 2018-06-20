package org.gilbre.app.gilbre;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import cz.msebera.android.httpclient.Header;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;


/**
 * Created by nyaguthii on 11/20/17.
 */

public class CustomerFragment extends Fragment {
    Customer[] customers;
    ArrayAdapter<String> adapter;
    Context context;
    private ChaniaDBHelper dbHelper;
    ListView customersListView;
    TokenManager manager;

    String customersUrl = "http://197.248.145.182/api/customers";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));
        return inflater.inflate(R.layout.fragment_customer,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dbHelper= new ChaniaDBHelper(getActivity());
        customersListView =(ListView) view.findViewById(R.id.customer_list_view);


        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }

        client.get("http://197.248.145.182/api/customers",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        //Log.e("AsyncHttpClient","response "+response);
                        Toast.makeText(getActivity(),"Failed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                        Gson gson = new GsonBuilder().create();
                        customers = gson.fromJson(response,Customer[].class);

                        for(Customer customer: customers){
                            saveCustomer(customer);
                        }


                    }
                });

        populateList();
       customersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               String customerName =customersListView.getItemAtPosition(i).toString();
               Bundle bundle = new Bundle();
               int id = getCustomerId(customerName);
               bundle.putString("name",customerName);
               bundle.putInt("id",id);

               //Toast.makeText(getActivity(),customerName, Toast.LENGTH_LONG).show();

               Fragment fragment = new CustomerDetail();
               fragment.setArguments(bundle);
               FragmentManager manager = getActivity().getSupportFragmentManager();
               FragmentTransaction transaction = manager.beginTransaction();
               transaction.replace(R.id.content_area,fragment);
               transaction.commit();

           }
       });

        EditText customerSearch = (EditText) view.findViewById(R.id.customer_search);
        customerSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void saveCustomer(Customer customer){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_RECEIPT_NO,receipt.receiptNo);
        values.put(ChaniaContract.CustomerEntry.COLUMN_NAME_NAME,customer.name);
        values.put(ChaniaContract.CustomerEntry.COLUMN_NAME_ID,customer.id);
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);

        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        db.insert(ChaniaContract.CustomerEntry.TABLE_NAME,null,values);

    }

    public Cursor getCustomers(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM "+ChaniaContract.CustomerEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public void populateList(){
        Cursor cursor = getCustomers();
        List<String> customer_list = new ArrayList<String>();
        while(cursor.moveToNext()){
            customer_list.add(cursor.getString(1));
        }

        adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,customer_list);

        customersListView.setAdapter(adapter);

    }

    public int getCustomerId(String name){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM "+ChaniaContract.CustomerEntry.TABLE_NAME+" WHERE name="+"'"+name+"'";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }


}
