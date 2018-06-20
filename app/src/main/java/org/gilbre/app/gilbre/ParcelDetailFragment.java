package org.gilbre.app.gilbre;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


public class ParcelDetailFragment extends Fragment {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    String from;
    String to;
    int id;
    int amount;
    String sender;
    String receiver;
    String receiverName;
    String senderName;
    //String servedBy;


    TextView statusText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle= getArguments();

        if(bundle !=null){
            from=bundle.getString("from");
            id=bundle.getInt("id");
            to=bundle.getString("to");
            amount=bundle.getInt("amount");
            sender=bundle.getString("sender");
            receiver=bundle.getString("receiver");
            receiverName=bundle.getString("receiver_name");
            senderName=bundle.getString("sender_name");
            //servedBy=bundle.getString("servedBy");


        }
        return inflater.inflate(R.layout.fragment_parcel_detail,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView fromText =(TextView) view.findViewById(R.id.print_from);
        fromText.setText(from);
        TextView toText =(TextView) view.findViewById(R.id.print_to);
        toText.setText(to);
        TextView amountText =(TextView) view.findViewById(R.id.print_amount);
        amountText.setText("Kshs "+amount);

        TextView senderText =(TextView) view.findViewById(R.id.print_sender);
        senderText.setText(sender);
        TextView receiverText =(TextView) view.findViewById(R.id.print_receiver);
        receiverText.setText(receiver);

        TextView senderNameText =(TextView) view.findViewById(R.id.print_sender_name);
        senderNameText.setText(senderName);
        TextView receiverNameText =(TextView) view.findViewById(R.id.print_receiver_name);
        receiverNameText.setText(receiverName);


        Button btnConnect = (Button) view.findViewById(R.id.print_connect);
        Button btnDisconnect = (Button) view.findViewById(R.id.print_disconnect);
        Button btnPrint = (Button) view.findViewById(R.id.print_print);

        statusText = (TextView) view.findViewById(R.id.print_status);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    FindBluetoothDevice();
                    openBluetoothPrinter();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    disconnectBT();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    ex.printStackTrace();
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
            }

            //statusText.setText("Bluetooth Printer Attached");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException {
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
                        }
                    }

                }
            });

            thread.start();
            statusText.setText("Ready to print");
        }catch (Exception ex){
            ex.printStackTrace();
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
            msg+="Receipt No: "+id;
            msg+="\n";
            msg+="Sender: "+sender;
            msg+="\n";
            msg+="Receiver: "+receiver;
            msg+="\n";
            msg+="From: "+from;
            msg+="\n";
            msg+="To: "+to;
            msg+="\n";
            msg+="Amount Kshs:"+String.format("%,d", amount);
            msg+="\n";
            msg+="\n";
            msg+="\n";
            msg+="\n";
            statusText.setText("Printing Text...");
            outputStream.write(msg.getBytes());
            disconnectBT();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            //bluetoothSocket.close();
            statusText.setText("Printer Disconnected.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
