package org.gilbre.app.gilbre;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;



//import cz.msebera.android.httpclient.Header;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    PaymentType[] types;
    Place[] places;

    private TokenManager manager;
    Context context;

    //String typesUrl = "http://wandatt.com/chania2/public/api/types";
    //String placesUrl = "http://wandatt.com/chania2/public/api/places";
    //StringEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        manager=TokenManager.getINSTANCE(getSharedPreferences("prefs", MODE_PRIVATE));


        AsyncHttpClient client = new AsyncHttpClient();
        if(manager.getToken().getAccess_token() !=null){
            client.addHeader("Authorization","Bearer "+manager.getToken().getAccess_token());
        }

        client.get("http://197.248.145.182/api/types",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                        Gson gson = new GsonBuilder().create();
                        types = gson.fromJson(response,PaymentType[].class);

                        for(PaymentType type: types){
                            try{
                                addPaymentTypeToDb(type);
                                //Toast.makeText(MainActivity.this,type.name, Toast.LENGTH_LONG).show();
                            }catch(Exception ex){
                                Toast.makeText(MainActivity.this,ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }


                    }
                });



        client.get("http://197.248.145.182/api/places",
                new TextHttpResponseHandler(){

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient","response "+response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {

                        Gson gson = new GsonBuilder().create();
                        places = gson.fromJson(response,Place[].class);

                        for(Place place: places){
                            try{
                                addPlaceToDb(place);
                            }catch(Exception ex){
                                Toast.makeText(MainActivity.this,ex.getMessage(), Toast.LENGTH_LONG).show();
                            }


                        }


                    }
                });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        PreferenceFragment preferenceFragment=null;
        int id = item.getItemId();

        if (id == R.id.nav_customers) {
            fragment = new CustomerFragment();
        } else if (id == R.id.nav_vehicles) {
            fragment = new VehicleFragment();
        }  else if (id == R.id.nav_receipts) {
            fragment = new ReceiptFragment();
        }else if (id == R.id.nav_parcel) {
          fragment = new ParcelTransactionFragment();
        }else if (id == R.id.nav_parcels) {
            fragment = new ParcelFragment();
        }
        else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        }
        else if (id == R.id.nav_parcel_myparcels) {
            fragment = new MyParcels();
        }
        else if (id == R.id.nav_totals) {
            fragment = new TotalsFragment();
        }
        else if (id == R.id.nav_logout) {
            fragment = new LogoutFragment();

        }


        if(fragment !=null){


            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.content_area,fragment);
            transaction.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addPaymentTypeToDb(PaymentType type) throws Exception{

        ChaniaDBHelper dbHelper= new ChaniaDBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_RECEIPT_NO,receipt.receiptNo);
        values.put(ChaniaContract.PaymentTypeEntry.COLUMN_NAME_ID,type.id);
        values.put(ChaniaContract.PaymentTypeEntry.COLUMN_NAME_NAME,type.name);

        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);

        Toast.makeText(this,type.id+""+type.name+"", Toast.LENGTH_LONG).show();
        db.insertOrThrow(ChaniaContract.PaymentTypeEntry.TABLE_NAME,null,values);

    }
    public void addPlaceToDb(Place place) throws Exception{

        ChaniaDBHelper dbHelper= new ChaniaDBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_RECEIPT_NO,receipt.receiptNo);
        values.put(ChaniaContract.PlaceEntry.COLUMN_NAME_ID,place.id);
        values.put(ChaniaContract.PlaceEntry.COLUMN_NAME_NAME,place.name);

        //values.put(ChaniaContract.ReceiptEntry.COLUMN_NAME_SERVED_BY,receipt.servedBy);

        //Toast.makeText(this,place.id+""+place.name+"", Toast.LENGTH_LONG).show();
        db.insertOrThrow(ChaniaContract.PlaceEntry.TABLE_NAME,null,values);

    }



}
