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

public class TotalsAdapter extends RecyclerView.Adapter<TotalsAdapter.ParcelHolder> {

    private Context context;
    private List<Total> mData;
    TokenManager manager;
    SharedPreferences sharedPref;
    AsyncHttpClient client;



    public TotalsAdapter(Context context, List<Total> mData){

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
                .inflate(R.layout.row_amount, parent, false);

        return new ParcelHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParcelHolder holder, int position) {

        final Total total = mData.get(position);

        //holder.idText.setText(parcel.id);
        holder.totalTypeText.setText(total.type);
        holder.amountText.setText(total.total);



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
        TextView totalTypeText;
        TextView amountText;



        public ParcelHolder(View view){

            super(view);

            //idText=(TextView)view.findViewById(R.id.parcels_row_id);
            totalTypeText=(TextView)view.findViewById(R.id.totals_type);
            amountText=(TextView)view.findViewById(R.id.totals_amount);



        }
    }
}
