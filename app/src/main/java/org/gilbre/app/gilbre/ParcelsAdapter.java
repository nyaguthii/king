package org.gilbre.app.gilbre;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 3/22/18.
 */

public class ParcelsAdapter extends ArrayAdapter<Parcell> {

    private FragmentActivity activity;

    public ParcelsAdapter(FragmentActivity activity, ArrayList<Parcell> parcels){
        super(activity.getApplicationContext().getApplicationContext(),0,parcels);
        this.activity=activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Parcell parcel =getItem(position);
        if(convertView ==null){
           convertView= LayoutInflater.from(getContext()).inflate(R.layout.parcel_list,parent,false) ;
        }

        TextView idTextView =(TextView) convertView.findViewById(R.id.parcels_parcel_id);
        TextView senderTextView =(TextView) convertView.findViewById(R.id.parcels_parcel_sender);
        TextView receiverTextView =(TextView) convertView.findViewById(R.id.parcels_parcel_receiver);

        idTextView.setText(String.valueOf(parcel.id));
        senderTextView.setText(parcel.sender);
        receiverTextView.setText(parcel.receiver);

        Button button =(Button)convertView.findViewById(R.id.show_parcel);

        button.setTag(parcel);


        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Parcell parcell = (Parcell)view.getTag();

                int id=parcell.id;
                String from =parcell.from;
                String to=parcell.to;
                int amount = parcell.amount;
                String sender = parcell.sender;
                String receiver = parcell.receiver;
                String receiver_name = parcell.receiver_name;
                String sender_name = parcell.sender_name;


                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                bundle.putString("from",from);
                bundle.putString("to",to);
                bundle.putInt("amount",amount);
                bundle.putString("sender",sender);
                bundle.putString("receiver",receiver);
                bundle.putString("sender_name",sender_name);
                bundle.putString("receiver_name",receiver_name);
                //bundle.putString("servedBy",servedBy);


                Fragment fragment = new ParcelDetailFragment();
                fragment.setArguments(bundle);
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_area,fragment);
                transaction.commit();
            }
        });

        return convertView;
    }
}
