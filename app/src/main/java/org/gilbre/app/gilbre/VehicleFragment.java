package org.gilbre.app.gilbre;



import android.content.ContentValues;
import android.content.Context;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import cz.msebera.android.httpclient.Header;


/**
 * Created by nyaguthii on 11/20/17.
 */

public class VehicleFragment extends Fragment {

    Vehicle[] vehicles;
    ArrayAdapter<String> adapter;
    Context context;
    List<String> vehicle_list;
    ListView vehiclesListView;
    TokenManager manager;
    private ChaniaDBHelper dbHelper;
    String vehiclesUrl = "http://197.248.145.182/api/vehicles";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));
        return inflater.inflate(R.layout.fragment_vehicle,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //vehicle_list = new ArrayList<String>();
        dbHelper= new ChaniaDBHelper(getActivity());

        vehiclesListView =(ListView) view.findViewById(R.id.vehicle_list_view);

        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }
        client.get("http://197.248.145.182/api/vehicles",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                        Gson gson = new GsonBuilder().create();
                         vehicles = gson.fromJson(response,Vehicle[].class);

                        for(Vehicle vehicle: vehicles){
                            //vehicle_list.add(vehicle.registration);
                            saveVehicle(vehicle);
                        }


                    }
                });

        //adapter = new ArrayAdapter<String>(
        // getActivity(),android.R.layout.simple_list_item_1,vehicle_list);
        //vehiclesListView.setAdapter(adapter);
        
        populateList();
        vehiclesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Bundle bundle = new Bundle();
                //bundle.putString("registration",vehicles[i].registration);
                //bundle.putInt("id",vehicles[i].id);
                String vehicleRegistration =vehiclesListView.getItemAtPosition(i).toString();
                Bundle bundle = new Bundle();
                int id = getVehicleRegistration(vehicleRegistration);
                bundle.putString("registration",vehicleRegistration);
                bundle.putInt("id",id);

                Fragment fragment = new VehicleDetail();
                fragment.setArguments(bundle);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_area,fragment);
                transaction.commit();

            }
        });

        EditText vehicleSearch = (EditText) view.findViewById(R.id.vehicle_search);
        vehicleSearch.addTextChangedListener(new TextWatcher() {
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

    public void saveVehicle(Vehicle vehicle){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_RECEIPT_NO,receipt.receiptNo);
        values.put(ChaniaContract.VehicleEntry.COLUMN_NAME_REGISTRATION,vehicle.registration);
        values.put(ChaniaContract.VehicleEntry.COLUMN_NAME_ID,vehicle.id);
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);

        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        db.insert(ChaniaContract.VehicleEntry.TABLE_NAME,null,values);

    }

    public Cursor getVehicles(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM "+ChaniaContract.VehicleEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public void populateList(){
        Cursor cursor = getVehicles();
        List<String> vehicle_list = new ArrayList<String>();
        while(cursor.moveToNext()){
            vehicle_list.add(cursor.getString(1));
        }

        adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,vehicle_list);

        vehiclesListView.setAdapter(adapter);

    }

    public int getVehicleRegistration(String registration){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM "+ChaniaContract.VehicleEntry.TABLE_NAME+" WHERE registration="+"'"+registration+"'";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }


}
