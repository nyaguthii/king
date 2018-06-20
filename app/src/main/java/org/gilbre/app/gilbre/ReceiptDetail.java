package org.gilbre.app.gilbre;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import  android.bluetooth.BluetoothDevice;
import  android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;
import android.widget.Toast;

import java.util.concurrent.RunnableFuture;
import java.util.logging.LogRecord;



/**
 * Created by nyaguthii on 11/26/17.
 */

public class ReceiptDetail extends Fragment {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    String customer;
    int id;
    int amount;
    String paymentType;
    String servedBy;
    String registration;
    String memberId;

    TextView statusText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle= getArguments();

        if(bundle !=null){
            customer=bundle.getString("customer");
            id=bundle.getInt("id");
            paymentType=bundle.getString("type");
            amount=bundle.getInt("amount");
            registration=bundle.getString("registration");
            servedBy=bundle.getString("servedBy");
            memberId=bundle.getString("memberId");

        }
        return inflater.inflate(R.layout.fragment_receipt_detail,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView customerText =(TextView) view.findViewById(R.id.customer_name_receipt_text);
        customerText.setText(customer);
        TextView paymentTypeText =(TextView) view.findViewById(R.id.receipt_type_text);
        paymentTypeText.setText(paymentType);
        TextView amountText =(TextView) view.findViewById(R.id.receipt_amount_text);
        amountText.setText("Kshs "+amount);

        Button btnConnect = (Button) view.findViewById(R.id.connect_button);
        Button btnDisconnect = (Button) view.findViewById(R.id.disconnect_button);
        Button btnPrint = (Button) view.findViewById(R.id.print_button);

        statusText = (TextView) view.findViewById(R.id.status_text_view);
        FindBluetoothDevice();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    //disconnectBT();
                    openBluetoothPrinter();

                }catch (Exception ex){
                    statusText.setText(ex.getMessage());
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    disconnectBT();
                }catch (Exception ex){
                    statusText.setText(ex.getMessage());

                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    statusText.setText(ex.getMessage());
                }
            }
        });
    }

    void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                statusText.setText("No Bluetooth Adapter found");
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                    // My Bluetoth printer name is BTP_F09F1
                    if(pairedDev.getName().equals("MTP-II")){
                        bluetoothDevice=pairedDev;
                        statusText.setText("Bluetooth Printer Attached: "+pairedDev.getName());
                        break;
                    }
                }
            }else{
                statusText.setText("Did not pair: ");
            }

            //statusText.setText("Bluetooth Printer Attached");
        }catch(Exception ex){
            statusText.setText(ex.getMessage());
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException{
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();
            statusText.setText("Bluetooth opened");

            beginListenData();

        }catch (Exception ex){
            statusText.setText(ex.getMessage());
        }
    }
    void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                statusText.setText(data);
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                            statusText.setText(ex.getMessage());
                        }
                    }

                }
            });

            thread.start();
            statusText.setText("Ready to print");
        }catch (Exception ex){
            statusText.setText(ex.getMessage());
        }
    }

    // Printing Text to Bluetooth Printer //
    void printData() throws  IOException{

        try{
            String msg = "CHANIA TRAVELLERS SACCO";
            msg+="\n";
            msg+="***************************";
            msg+="\n";
            msg+="ORIGINAL RECEIPT";
            msg+="\n";
            msg+="***************************";
            msg+="\n";
            msg+="Date :"+new Date();
            msg+="\n";
            msg+="***************************";
            msg+="\n";
            msg+="Member No: "+memberId;
            msg+="\n";
            msg+="Member: "+customer;
            msg+="\n";
            msg+="Receipt No: "+id;
            msg+="\n";
            msg+="Type: "+paymentType;
            msg+="\n";
            msg+="Amount Kshs:"+String.format("%,d", amount);
            msg+="\n";
            msg+="Vehicle: "+registration;
            msg+="\n";
            msg+="Served By: "+servedBy;
            msg+="\n";
            msg+="\n";
            msg+="\n";
            msg+="\n";

            outputStream.write(msg.getBytes());
            statusText.setText("Printing Text...");
            disconnectBT();
        }catch (Exception ex){
            statusText.setText(ex.getMessage());
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            //bluetoothSocket.close();
            //thread.stop();
            statusText.setText("Printer Disconnected.");
        }catch (Exception ex){
            statusText.setText(ex.getMessage());
        }
    }
}
