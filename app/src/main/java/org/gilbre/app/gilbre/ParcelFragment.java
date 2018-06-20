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


public class ParcelFragment extends Fragment {

    private ChaniaDBHelper dbHelper;
    private ListView listView;
    ParcelsAdapter adapter;
    //ParcelAdapter adapter;
    //ListAdapter adapter;

    ArrayList<Parcell> parcelsList;
    private Parcell[] parcels;
    Context context;
    SharedPreferences sharedPref;
    private TokenManager manager;
    private String place;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_parcel,null);
        dbHelper = new ChaniaDBHelper(getActivity());
        context=container.getContext();
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        place = sharedPref.getString("place", "TUSKER");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        //parcelsList = new ArrayList<Parcell>();

        listView = (ListView) view.findViewById(R.id.parcel_list_view);
        getApiParcels();
        //adapter = new ParcelListAdapter(getActivity(),parcelsList);
         populateList();


    }

    public void getApiParcels(){

        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }

        client.get("http://197.248.145.182/api/parcels/"+place,
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {


                        //Toast.makeText(getActivity(),response, Toast.LENGTH_LONG).show();
                        Gson gson = new GsonBuilder().create();
                        parcels = gson.fromJson(response,Parcell[].class);

                        for(Parcell parcell: parcels){
                            try{
                                //parcelsList.add(parcell.sender);
                                addParcelToDatabase(parcell);
                                //Toast.makeText(getActivity(),parcell.sender_name, Toast.LENGTH_LONG).show();
                            }catch(Exception ex){
                                Toast.makeText(getActivity(),ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }


                    }
                });



    }

    public void populateList(){
        parcelsList=new ArrayList<Parcell>();
        Cursor cursor = getParcels();
        while(cursor.moveToNext()){

            int id=cursor.getInt(0);
            int amount = cursor.getInt(1);
            String to=cursor.getString(2);
            String from=cursor.getString(3);
            String receiver = cursor.getString(4);
            String sender = cursor.getString(5);
            String receiver_name = cursor.getString(6);
            String sender_name = cursor.getString(7);

            //String servedBy = cursor.getString(7);


            Parcell parcell = new Parcell(id,amount,sender, receiver, to,from,receiver_name,sender_name);

            parcelsList.add(parcell);


        }

         adapter = new ParcelsAdapter(getActivity(),parcelsList);

        listView.setAdapter(adapter);
      /**
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int parcel_no = Integer.parseInt(view.getTag().toString());
                Toast.makeText(getActivity(),view.getTag().toString(), Toast.LENGTH_LONG).show();
               Cursor cursor = getParcel(parcel_no);

                while(cursor.moveToNext()){
                    int id=cursor.getInt(0);
                    String from =cursor.getString(1);
                    String to=cursor.getString(2);
                    int amount = cursor.getInt(3);
                    String sender = cursor.getString(4);
                    String receiver = cursor.getString(5);
                    //String servedBy = cursor.getString(5);
                    //String memberId = cursor.getString(6);

                    Bundle bundle = new Bundle();
                    bundle.putInt("id",id);
                    bundle.putString("from",from);
                    bundle.putString("to",to);
                    bundle.putInt("amount",amount);
                    bundle.putString("sender",sender);
                    bundle.putString("receiver",receiver);
                    //bundle.putString("servedBy",servedBy);


                    Fragment fragment = new ParcelDetailFragment();
                    fragment.setArguments(bundle);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.content_area,fragment);
                    transaction.commit();
                }


            }

        });
       **/

    }


    public Cursor getParcels(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT id,amount,to_place,from_place,receiver,sender,receiver_name,sender_name FROM "+ChaniaContract.ParcelEntry.TABLE_NAME+" ORDER BY id desc";
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getParcel(int parcel){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT id,from_place,to_place,amount,sender,receiver,sender_name,receiver_name FROM "+ChaniaContract.ParcelEntry.TABLE_NAME+" WHERE id="+parcel;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;

    }

    public void addParcelToDatabase(Parcell parcel){
        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_ID,parcel.id);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_AMOUNT,parcel.amount);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_FROM,parcel.from);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_TO,parcel.to);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_SENDER,parcel.sender);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_RECEIVER,parcel.receiver);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_SENDER_NAME,parcel.sender_name);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_RECEIVER_NAME,parcel.receiver_name);
        //values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_MEMBER_ID,receipt.memberId);

        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        db.insert(ChaniaContract.ParcelEntry.TABLE_NAME,null,values);
        //Toast.makeText(getActivity(), parcel.receiver_name, Toast.LENGTH_SHORT).show();


    }


}
