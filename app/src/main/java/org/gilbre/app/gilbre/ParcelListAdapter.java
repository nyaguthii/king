package org.gilbre.app.gilbre;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by root on 3/19/18.
 */

public class ParcelListAdapter extends BaseAdapter {

    private Context context;
    private List<Parcell> parcelsList;


    public ParcelListAdapter(Context context, List<Parcell> parcelsList) {
        this.context = context;
        this.parcelsList = parcelsList;
    }

    @Override
    public int getCount() {
        return parcelsList.size();
    }

    @Override
    public Object getItem(int position) {
        return parcelsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view.inflate(context,R.layout.parcel_list,null);

        TextView idTextView =(TextView) view.findViewById(R.id.parcels_parcel_id);
        TextView senderTextView =(TextView) view.findViewById(R.id.parcels_parcel_sender);
        TextView receiverTextView =(TextView) view.findViewById(R.id.parcels_parcel_receiver);

        idTextView.setText(String.valueOf(parcelsList.get(position).id));
        senderTextView.setText(parcelsList.get(position).sender);
        receiverTextView.setText(parcelsList.get(position).receiver);

        view.setTag(parcelsList.get(position).id);
        return view;
    }
}
