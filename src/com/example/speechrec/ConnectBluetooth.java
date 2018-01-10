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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConnectBluetooth extends Activity{
	
	private Button connectBT;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.bluetooth);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			Toast.makeText(ConnectBluetooth.this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();    
		//    finish();    
		    return; 
		}
		if(!mBluetoothAdapter.isEnabled())     
		{    
		    Toast.makeText(ConnectBluetooth.this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();    
		//    finish();    
		    return;    
		} 
		
		connectBT = (Button) findViewById(R.id.connect);
		
		connectBT.setOnClickListener(new OnClickListener() {
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
							Toast.makeText(ConnectBluetooth.this, "ok,HC-06",Toast.LENGTH_SHORT ).show();
						}
					}
				}else{
					Toast.makeText(ConnectBluetooth.this, "no bonded bluetooth!",Toast.LENGTH_SHORT ).show();
				}
				
				BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac);
				mBluetoothAdapter.cancelDiscovery();
				try{
					btSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
					btSocket.connect();
					outStream = btSocket.getOutputStream();
					inStream = btSocket.getInputStream();
					 new Thread() {
						 @Override
						 public void run() {
							 
						         }
						    }.start();
					Toast.makeText(ConnectBluetooth.this, "ok",Toast.LENGTH_SHORT ).show();
				}catch(IOException e){
					Toast.makeText(ConnectBluetooth.this, "error",Toast.LENGTH_SHORT ).show();
				}
			}
		});
		
	}
}
