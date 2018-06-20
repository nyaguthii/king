package org.gilbre.app.gilbre;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;

//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.entity.StringEntity;
//import cz.msebera.android.httpclient.message.BasicHeader;
//import cz.msebera.android.httpclient.protocol.HTTP;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;


public class LogoutFragment extends Fragment {
    TokenManager manager;
    Context context;
    StringEntity entity;
    String logoutUrl = "http://wandatt.com/chania2/public/api/logout";

    //StringEntity entity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context=container.getContext();
        manager=TokenManager.getINSTANCE(getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE));
        return inflater.inflate(R.layout.fragment_logout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) view.findViewById(R.id.logout_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AsyncHttpClient client = new AsyncHttpClient();
                if(manager.getToken().getAccess_token() !=null){
                    Toast.makeText(getActivity(),"ACCESS TOKEN AVAILABLE", Toast.LENGTH_LONG).show();
                    client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
                }
                String url = "http://197.248.145.182/api/logout";
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.post(url,
                //client.get(url,
                        new TextHttpResponseHandler(){

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Toast.makeText(getActivity(),"Did non logout", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String response) {

                                //Gson gson = new GsonBuilder().create();
                                //receipt = gson.fromJson(response,Receipt.class);
                                manager.deleteToken();
                                Intent intent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(intent);


                            }
                        });

            }
        });
    }

}
