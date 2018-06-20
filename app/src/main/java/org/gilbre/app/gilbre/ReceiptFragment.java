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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nyaguthii on 11/26/17.
 */

public class ReceiptFragment extends Fragment{
    private ChaniaDBHelper dbHelper;
    private ListView listView;
    ListAdapter adapter;
    private TokenManager manager;

    private String place;
    private Receipt[] receipts;
    Context context;
    SharedPreferences sharedPref;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new ChaniaDBHelper(getActivity());
        context=container.getContext();
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        place = sharedPref.getString("place", "TUSKER");
        return inflater.inflate(R.layout.fragment_receipt,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        listView = (ListView) view.findViewById(R.id.receipt_list_view);

        getApiReceipts();
        populateList();


    }

    public void populateList(){

        Cursor cursor = getReceipts();
        List<String> receipt_list = new ArrayList<String>();
        while(cursor.moveToNext()){
            receipt_list.add(cursor.getString(0));
        }

        adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,receipt_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int receipt_no = Integer.parseInt(adapter.getItem(i).toString());
                Cursor cursor = getReceipt(receipt_no);

                while(cursor.moveToNext()){
                    int id=cursor.getInt(0);
                    String customer =cursor.getString(1);
                    int amount=cursor.getInt(2);
                    String paymentType = cursor.getString(3);
                    String registration = cursor.getString(4);
                    String servedBy = cursor.getString(5);
                    String memberId = cursor.getString(6);

                    Bundle bundle = new Bundle();
                    bundle.putInt("id",id);
                    bundle.putString("customer",customer);
                    bundle.putInt("amount",amount);
                    bundle.putString("type",paymentType);
                    bundle.putString("registration",registration);
                    bundle.putString("servedBy",servedBy);
                    bundle.putString("memberId",memberId);

                    Fragment fragment = new ReceiptDetail();
                    fragment.setArguments(bundle);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.content_area,fragment);
                    transaction.commit();
                }

            }
        });

    }

    public void getApiReceipts(){

        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }

        client.get("http://197.248.145.182/api/payments/"+place,
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                       Gson gson = new GsonBuilder().create();
                        receipts = gson.fromJson(response,Receipt[].class);

                        for(Receipt receipt: receipts){
                            try{
                                addReceiptToDatabase(receipt);
                                //Toast.makeText(MainActivity.this,type.name, Toast.LENGTH_LONG).show();
                            }catch(Exception ex){
                                Toast.makeText(getActivity(),ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }


                    }
                });
    }
    public Cursor getReceipts(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM "+ChaniaContract.ReceiptEntry.TABLE_NAME+" ORDER BY id desc";
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getReceipt(int receipt){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT id,customer,amount,type,registration,served_by,member_id FROM "+ChaniaContract.ReceiptEntry.TABLE_NAME+" WHERE id="+receipt;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;

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
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_REGISTRATION,receipt.registration);
        values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_MEMBER_ID,receipt.memberId);

        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        db.insert(ChaniaContract.ReceiptEntry.TABLE_NAME,null,values);
        //Toast.makeText(getActivity(), "Payment Created", Toast.LENGTH_SHORT).show();


    }


}
