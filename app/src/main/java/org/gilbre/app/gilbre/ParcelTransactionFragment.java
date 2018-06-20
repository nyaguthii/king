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
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.entity.StringEntity;
//import cz.msebera.android.httpclient.message.BasicHeader;
//import cz.msebera.android.httpclient.protocol.HTTP;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;


public class ParcelTransactionFragment extends Fragment {
    TokenManager manager;
    private ChaniaDBHelper dbHelper;
    private TextView amountText;
    private String place;
    private int dailyTotal;
    private Place[] places;
    private int amount;
    private String sender;
    private String receiver;
    private String to;
    private String from;
    StringEntity entity;
    Context context;

    Spinner toSpinner;
    //Spinner fromSpinner;
    Button saveParcelButton;
    EditText senderText;
    EditText receiverText;
    EditText senderNameText;
    EditText receiverNameText;
    EditText descriptionText;
    TextView totalAmountText;

    int parcelId;
    int parcelAmount;
    String parcelSender;
    String parcelReceiver;
    String sender_name;
    String receiver_name;
    String parcelType;
    //String parcelFrom;
    String description;
    String parcelTo;
    String servedBy;
    Parcell parcell;
    SharedPreferences sharedPref;
    String amountUrl;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));
        context=container.getContext();
        return inflater.inflate(R.layout.fragment_parcel_transaction,null);
    }

        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


            dbHelper= new ChaniaDBHelper(getActivity());
            amountText = (TextView) view.findViewById(R.id.parcel_amount_text);
            toSpinner = (Spinner) view.findViewById(R.id.to_spinner);
            //fromSpinner = (Spinner) view.findViewById(R.id.from_spinner);
            saveParcelButton=(Button) view.findViewById(R.id.save_parcel_button);
            senderText = (EditText) view.findViewById(R.id.sender_text);
            receiverText = (EditText) view.findViewById(R.id.reciever_text);
            senderNameText = (EditText) view.findViewById(R.id.parcel_sender_name_text);
            receiverNameText = (EditText) view.findViewById(R.id.parcel_receiver_name_text);
            totalAmountText=(TextView)view.findViewById(R.id.total_amount);
            descriptionText=(EditText)view.findViewById(R.id.parcel_description_text);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            place = sharedPref.getString("place", "");


              populateSpinner();
              getTotal();



                  saveParcelButton.setOnClickListener(new View.OnClickListener() {
                      public void onClick(View v) {
                          if(amountText.getText().length()==0){
                              Toast.makeText(getActivity(), "Enter the Amount", Toast.LENGTH_LONG).show();
                          }else if(senderText.getText().length()==0){
                              Toast.makeText(getActivity(), "Enter the Sender Number", Toast.LENGTH_LONG).show();
                          }else if(senderText.getText().length()!=10){
                              Toast.makeText(getActivity(), "Enter Correct number", Toast.LENGTH_LONG).show();
                          }else if(receiverText.getText().length()!=10){
                              Toast.makeText(getActivity(), "Enter Correct number", Toast.LENGTH_LONG).show();

                          }else if(receiverText.getText().length()==0){
                              Toast.makeText(getActivity(), "Enter the Receiver Number", Toast.LENGTH_LONG).show();
                          }else if(senderNameText.getText().length()==0){
                              Toast.makeText(getActivity(), "Enter the Sender Name", Toast.LENGTH_LONG).show();

                          }else if(receiverNameText.getText().length()==0) {
                              Toast.makeText(getActivity(), "Enter the Receiver Name", Toast.LENGTH_LONG).show();
                          }else if(descriptionText.getText().length()==0) {
                              Toast.makeText(getActivity(), "Enter the Description Name", Toast.LENGTH_LONG).show();
                          }else{
                              saveParcelButton.setText("Sending...");
                             amount=Integer.parseInt(amountText.getText().toString());
                             to=toSpinner.getSelectedItem().toString();
                             //from=fromSpinner.getSelectedItem().toString();
                             sender=senderText.getText().toString();
                             receiver=receiverText.getText().toString();
                             sender_name=senderNameText.getText().toString();
                             receiver_name=receiverNameText.getText().toString();
                             description=descriptionText.getText().toString();

                              AsyncHttpClient client = new AsyncHttpClient();
                              if(manager.getToken().getAccess_token() !=null){
                                  client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
                              }

                              String url = "http://197.248.145.182/api/parcels";
                              //params = new RequestParams();
                              JSONObject jsonObject = new JSONObject();
                              try{
                                  jsonObject.put("amount", amount);
                                  jsonObject.put("to", to);
                                  jsonObject.put("from", place);
                                  jsonObject.put("place", place);
                                  jsonObject.put("sender", sender);
                                  jsonObject.put("receiver", receiver);
                                  jsonObject.put("sender_name", sender_name);
                                  jsonObject.put("receiver_name", receiver_name);
                                  jsonObject.put("description",description);
                                  entity = new StringEntity(jsonObject.toString());

                              }catch(Exception e){}

                              entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                              client.post(context,url,entity,"application/json",
                                      new TextHttpResponseHandler(){

                                          @Override
                                          public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                              Toast.makeText(getActivity(),response, Toast.LENGTH_LONG).show();

                                          }

                                          @Override
                                          public void onSuccess(int statusCode, Header[] headers, String response) {

                                              //Gson gson = new GsonBuilder().create();
                                              //receipt = gson.fromJson(response,Receipt.class);
                                              //Toast.makeText(getActivity(),response, Toast.LENGTH_LONG).show();

                                            /**
                                              try{
                                                  JSONObject json = new JSONObject(response);
                                                  parcelId=json.getInt("id");
                                                  parcelAmount=json.getInt("amount");
                                                  parcelType=json.getString("type");
                                                  servedBy=json.getString("servedBy");
                                                  parcelTo=json.getString("to");
                                                  parcelFrom=json.getString("from");
                                                  parcelReceiver=json.getString("receiver");
                                                  parcelSender=json.getString("sender");

                                              }catch(Exception e){

                                              }

                                              //customerNameText.setText(receiptCustomer);
                                              parcell = new Parcell(parcelId,parcelAmount,
                                                     parcelTo,parcelFrom,parcelReceiver,parcelSender);
                                              addParcelToDatabase(parcell);

                                             **/
                                              Toast.makeText(getActivity(),"Parcel saved", Toast.LENGTH_LONG).show();

                                              Fragment fragment = new MyParcels();
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

    public void populateSpinner(){
        Cursor cursor = getPlaces();

        List<String> types = new ArrayList<String>();
        while(cursor.moveToNext()){
            types.add(cursor.getString(0));
        }

        ArrayAdapter placesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, types);

        placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(placesAdapter);
        //fromSpinner.setAdapter(placesAdapter);
    }
    public Cursor getPlaces(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT name FROM "+ChaniaContract.PlaceEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;

    }
    public void addParcelToDatabase(Parcell parcell){
        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_ID, parcell.id);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_AMOUNT, parcell.amount);

        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_SENDER, parcell.sender);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_RECEIVER, parcell.receiver);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_TO, parcell.to);
        values.put(ChaniaContract.ParcelEntry.COLUMN_NAME_FROM, parcell.from);


        //Toast.makeText(getActivity(),receipt.customer+""+receipt.amount+""+receipt.paymentType, Toast.LENGTH_LONG).show();
        db.insert(ChaniaContract.ParcelEntry.TABLE_NAME,null,values);
        Toast.makeText(getActivity(), "Parcell Created", Toast.LENGTH_SHORT).show();


    }

    public void getTotal(){

        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }
        client.get("http://197.248.145.182/api/parcels/"+place+"/dailyAmount",
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
