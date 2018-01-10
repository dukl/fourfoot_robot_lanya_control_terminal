package com.example.speechrec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Config extends Activity{
	
	static Config config;
	private Button connect;
	private Button video;
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final String PAR_KEY = null;
//	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
	
	public OutputStream getOutstream(){
		return this.outStream;
	}
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
//	        setContentView(R.layout.config);
	        
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBluetoothAdapter == null){
				Toast.makeText(Config.this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();    
			//    finish();    
			    return; 
			}
			if(!mBluetoothAdapter.isEnabled())     
			{    
			    Toast.makeText(Config.this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();    
			//    finish();    
			    return;    
			}
	        
	        connect = (Button) findViewById(R.id.connect);
//	        video = (Button) findViewById(R.id.video);
	        
	        connect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Set <BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
					String mac = null;
					if(devices.size()>0){
						for(Iterator iterator = devices.iterator();iterator.hasNext();){
							BluetoothDevice device = (BluetoothDevice) iterator.next();
					//		Toast.makeText(MainActivity.this,device.getName()+":"+device.getAddress(),Toast.LENGTH_LONG ).show();
							if(device.getName().equals("HC-06")){
								mac = device.getAddress();
								Toast.makeText(Config.this, "ok,HC-06",Toast.LENGTH_SHORT ).show();
							}
						}
					}else{
						Toast.makeText(Config.this, "no bonded bluetooth!",Toast.LENGTH_SHORT ).show();
					}
					
					BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac);
					mBluetoothAdapter.cancelDiscovery();
					try{
						btSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
						btSocket.connect();
						outStream = btSocket.getOutputStream();
						inStream = btSocket.getInputStream();
//						 new Thread() {
//							 @Override
//							 public void run() {
//								 
//							         }
//							    }.start();
						Toast.makeText(Config.this, "ok",Toast.LENGTH_SHORT ).show();
					}catch(IOException e){
						Toast.makeText(Config.this, "error",Toast.LENGTH_SHORT ).show();
					}
				}
			});
	        
	        video.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Config.this, MainActivity.class);
					Bundle mBundle = new Bundle();  
			        mBundle.putParcelable(PAR_KEY,  (Parcelable) outStream);
					intent.putExtras(mBundle);
	                startActivity(intent);
				}
			});
	 }
}
