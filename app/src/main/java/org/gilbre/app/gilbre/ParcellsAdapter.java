package org.gilbre.app.gilbre;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/24/18.
 */

public class ParcellsAdapter extends RecyclerView.Adapter<ParcellsAdapter.ParcelHolder> {

    private Context context;
    private List<Parcell> mData;
    TokenManager manager;
    SharedPreferences sharedPref;
    AsyncHttpClient client;



    public ParcellsAdapter(Context context, List<Parcell> mData){

        this.context=context;
        this.mData=mData;

        manager=TokenManager.getINSTANCE(context.getSharedPreferences("prefs", Context.MODE_PRIVATE));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }


    }
    @Override
    public ParcelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_parcels, parent, false);

        return new ParcelHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParcelHolder holder, int position) {

        final Parcell parcel = mData.get(position);

        //holder.idText.setText(parcel.id);
        holder.fromText.setText(parcel.from);
        holder.senderText.setText(parcel.sender);
        holder.receiverText.setText(parcel.receiver);
        holder.receiverNameText.setText(parcel.receiver_name);
        holder.senderNameText.setText(parcel.sender_name);
        holder.descriptionText.setText(parcel.description);

        holder.sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.post("http://197.248.145.182/api/parcels/"+parcel.id+"/update",
                        new TextHttpResponseHandler(){

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                //Log.e("AsyncHttpClient","response "+response);
                                Toast.makeText(context,"Failed", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String response) {

                                Toast.makeText(context,response, Toast.LENGTH_LONG).show();

                            }
                        });
            }
        });

        //holder.dateText.setText(parcel.date);

    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ParcelHolder extends RecyclerView.ViewHolder{

        //TextView idText;
        TextView receiverText;
        TextView senderText;
        TextView fromText;
        TextView receiverNameText;
        TextView senderNameText;
        Button sendSms;
        //TextView dateText;
        TextView descriptionText;


        public ParcelHolder(View view){

            super(view);

            //idText=(TextView)view.findViewById(R.id.parcels_row_id);
            receiverText=(TextView)view.findViewById(R.id.parcels_row_receiver);
            senderText=(TextView)view.findViewById(R.id.parcels_row_sender);
            fromText=(TextView)view.findViewById(R.id.parcels_row_from);
            receiverNameText=(TextView)view.findViewById(R.id.parcels_row_receivername);
            senderNameText=(TextView)view.findViewById(R.id.parcels_row_sendername);
            //dateText=(TextView)view.findViewById(R.id.parcels_row_date);
            descriptionText=(TextView)view.findViewById(R.id.parcels_row_description);
            sendSms=(Button)view.findViewById(R.id.send_sms_button);


        }
    }
}
