package com.example.speechrec;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private List<String> list = new ArrayList<String>();//创建一个String类型的数组列表。 
	private Spinner mySpinner;  
    private ArrayAdapter<String> adapter;//创建一个数组适配器  
	String state = "   高姿态";
    
	private Button Yuyin;
    private Button Lanya;
    private Button front,back,left,right,stand;
    private Button LTurn,RTurn;
	
//    private TextView mTvResult;
//    private TextView mTvLog;
    private BuildLocalGrammar buildLocalGrammar;
    private KqwSpeechRecognizer kqwSpeechRecognizer;

    private Button connectBT;
    
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	@SuppressWarnings("unused")
	private InputStream inStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
	private static String default_address = "B4:EF:FA:2B:46:69";//default mac
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        StringBuffer param = new StringBuffer();
        param.append("appid=5a371987");
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MainActivity.this, param.toString()); 
    
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//锟斤拷锟斤拷锟斤拷锟斤拷
		ActionBar actionBar=getActionBar();
		if (actionBar!=null){
			actionBar.hide();
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//聽
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			Toast.makeText(MainActivity.this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();    
		//    finish();    
		    return; 
		}
		if(!mBluetoothAdapter.isEnabled())     
		{    
		    Toast.makeText(MainActivity.this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();    
		//    finish();    
		    return;    
		}
	
	
		 list.add("   高姿态");  
	     list.add("   中姿态");  
	     list.add("   低姿态");  
	     mySpinner = (Spinner) findViewById(R.id.state);
	     adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);//样式为原安卓里面有的android.R.layout.simple_spinner_item，让这个数组适配器装list内容。  
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	     mySpinner.setAdapter(adapter); 
	     mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				state = adapter.getItem(position);
				Toast.makeText(MainActivity.this,adapter.getItem(position) ,Toast.LENGTH_SHORT ).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(MainActivity.this,"nothing" ,Toast.LENGTH_SHORT ).show();
			}
	    	 
	     });
	     
	     Yuyin = (Button) findViewById(R.id.yuyin);
		 Yuyin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				start();
		//		Toast.makeText(MainActivity.this, "???",Toast.LENGTH_SHORT ).show();
			}
		});
		
		Lanya = (Button) findViewById(R.id.lanya);
		Lanya.setOnClickListener(new OnClickListener() {
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
							Toast.makeText(MainActivity.this, "ok,HC-06",Toast.LENGTH_SHORT ).show();
						}
					}
				}else{
					Toast.makeText(MainActivity.this, "no bonded bluetooth!",Toast.LENGTH_SHORT ).show();
				}
				
				BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac);
				mBluetoothAdapter.cancelDiscovery();
				try{
					btSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
					btSocket.connect();
					outStream = btSocket.getOutputStream();
//					inStream = btSocket.getInputStream();
//					 new Thread() {
//						 @Override
//						 public void run() {
//							 byte[] buffer = new byte[64];
//							 int cnt;
//							try {
//								cnt = inStream.read(buffer);
//								inStream.close();  
//					            String s = new String(buffer, 0, cnt);
//					            Toast.makeText(MainActivity.this, s,Toast.LENGTH_SHORT ).show();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}  
//						         }
//						    }.start();
					Toast.makeText(MainActivity.this, "ok",Toast.LENGTH_SHORT ).show();
//					Lanya.setVisibility(View.GONE);
				}catch(IOException e){
					Toast.makeText(MainActivity.this, "error",Toast.LENGTH_SHORT ).show();
//					try {
//						outStream.close();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
				}
			}
		});
       

		front = (Button) findViewById(R.id.front);
		front.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toFront();
			}
		});
		back = (Button) findViewById(R.id.back );
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toBack();
			}
		});
        left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					toLeft();
			}
		});
        right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					toRight();
			}
		});
       stand = (Button) findViewById(R.id.stand);
       stand.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			stand();
		}
	    });
       LTurn = (Button) findViewById(R.id.leftTurn);
       LTurn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			toLTurn();
		}
	    });
       RTurn = (Button) findViewById(R.id.rightTurn);
       RTurn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			toRTurn();
		}
	    });
//        mTvResult = (TextView) findViewById(R.id.tv_result);
//        mTvLog = (TextView) findViewById(R.id.tv_log);

        /**
         * 初始化本地语法构造器
         */
        buildLocalGrammar = new BuildLocalGrammar(this) {

            @Override
            public void result(String errMsg, String grammarId) {
                // errMsg为null 构造成功
                if (TextUtils.isEmpty(errMsg)) {
                    Toast.makeText(MainActivity.this, "构造成功"+grammarId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "构造失败", Toast.LENGTH_SHORT).show();
                }
            }
        };

        /**
         * 初始化离线命令词识别器
         */
        kqwSpeechRecognizer = new KqwSpeechRecognizer(this) {

            @Override
            public void speechLog(String log) {
                // 录音Log信息的回调
//                mTvLog.setText(log);
            }

            @Override
            public void resultData(String data) {
                // 是识别结果的回调
//                mTvResult.setText(data);
                String cmd = data;
                if(cmd.equals("前进")){
                	toFront();
                	Toast.makeText(MainActivity.this, "前进", Toast.LENGTH_SHORT).show();
                }
                if(cmd.equals("后退")){
                	toBack();
                	Toast.makeText(MainActivity.this, "后退", Toast.LENGTH_SHORT).show();
                }
                if(cmd.equals("左移")){
                	toLeft();
                	Toast.makeText(MainActivity.this, "左移", Toast.LENGTH_SHORT).show();
                }
                if(cmd.equals("右移")){
                	toRight();
                	Toast.makeText(MainActivity.this, "右移", Toast.LENGTH_SHORT).show();
                }
                if(cmd.equals("站立")){
                	stand();
                //	Toast.makeText(MainActivity.this, "右移", Toast.LENGTH_SHORT).show();
                }
            }


			@Override
            public void initListener(boolean flag) {
                // 初始化的回调
                if (flag) {
                    Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                }
            }
        };

        /**
         * 构造本地语法文件，只有语法文件有变化的时候构造成功一次即可，不用每次都构造
         */
        buildLocalGrammar.buildLocalGrammar();

    }

    /**
     * 开始识别按钮
     * 
     * @param view
     */
   
    public void start() {
 //       mTvResult.setText(null);
        // 开始识别
        kqwSpeechRecognizer.startListening();
    }
    
    private void toRight()  {
    	byte [] msgBuffer = new byte[7];
    	
    	if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x08;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x1f;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		Toast.makeText(MainActivity.this, "无低姿态", Toast.LENGTH_LONG).show();
    	}
		
		try{
			if(state.equals("   低姿态")){
			}else{
			   outStream.write(msgBuffer);
			}
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}

	private void toLeft()  {
    	byte [] msgBuffer = new byte[7];
    	if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x07;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x1e;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		Toast.makeText(MainActivity.this, "无低姿态", Toast.LENGTH_LONG).show();
    	}
		
		try{
			if(state.equals("   低姿态")){
			}else{
				 outStream.write(msgBuffer);
			}
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}

	private void toBack()  {
    	byte [] msgBuffer = new byte[7];
    	if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x02;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x1b;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x16;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}
		
		try{
			outStream.write(msgBuffer);
			//Toast.makeText(MainActivity.this, "write ready!!!", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}

	private void toFront() {
    	byte [] msgBuffer = new byte[7];
    	if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x01;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x06;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x09;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}

		try{
			outStream.write(msgBuffer);
			//Toast.makeText(MainActivity.this, "write ready!!!", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}
	
	private void stand(){
		byte [] msgBuffer = new byte[7];
		if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x00;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x19;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x14;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}
		try{
			outStream.write(msgBuffer);
			//Toast.makeText(MainActivity.this, "write ready!!!", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}
	private void toLTurn(){
		byte [] msgBuffer = new byte[7];
		if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x03;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x1c;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x17;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}
		try{
			outStream.write(msgBuffer);
			//Toast.makeText(MainActivity.this, "write ready!!!", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}
	private void toRTurn(){
		byte [] msgBuffer = new byte[7];
		if(state.equals("   中姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x04;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   高姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x1d;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}else if(state.equals("   低姿态")){
    		msgBuffer[0] = 0x55;
    		msgBuffer[1] = 0x55;
    		msgBuffer[2] = 0x05;
    		msgBuffer[3] = 0x06;
    		msgBuffer[4] = 0x18;
    		msgBuffer[5] = 0x05;
    		msgBuffer[6] = 0x00;
    	}
		try{
			outStream.write(msgBuffer);
			//Toast.makeText(MainActivity.this, "write ready!!!", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			Toast.makeText(MainActivity.this, "write wrong!!!", Toast.LENGTH_LONG).show();
//			try {
//				outStream.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}


}