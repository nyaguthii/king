package org.gilbre.app.gilbre;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/28/18.
 */

public class MyParcels extends Fragment {

    TokenManager manager;
    SharedPreferences sharedPref;
    private String place;
    Context context;
    Parcell[] parcels;
    List<Parcell> parcelList;
    RecyclerView recyclerView;
    ParcellsAdapter adapter;
    LinearLayoutManager llm;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_myparcels,null);
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
        context=container.getContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        place = sharedPref.getString("place", "TUSKER");



        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parcelList = new ArrayList<Parcell>();
        recyclerView =(RecyclerView)view.findViewById(R.id.my_parcels_recycler);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }

        client.get("http://197.248.145.182/api/parcels/"+place+"/my/parcels",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        //Log.e("AsyncHttpClient","response "+response);
                        Toast.makeText(getActivity(),"Failed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                        Gson gson = new GsonBuilder().create();
                        parcels = gson.fromJson(response,Parcell[].class);
                        //Toast.makeText(getActivity(),response.toString(), Toast.LENGTH_LONG).show();

                       for(Parcell parcel: parcels){

                            parcelList.add(parcel);

                            //Toast.makeText(getActivity(),parcel.receiver+parcel.from+parcel.receiver_name, Toast.LENGTH_LONG).show();
                        }
                        adapter = new ParcellsAdapter(context,parcelList);
                        recyclerView.setAdapter(adapter);

                    }
                });


    }

}
