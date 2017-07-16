package net.pocketmagic.android.androidbt;

import net.pocketmagic.android.utils.AppUtils;
import net.pocketmagic.android.utils.D2Control;
import net.pocketmagic.android.utils.D2Control.D2ControlEvents;
import net.pocketmagic.android.androidbt.DeviceListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// debug
    private static final String TAG = "AndroidBT";
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mBTService = null;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    //private StringBuffer mOutStringBuffer;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // interface
	RelativeLayout		m_panel;
	TextView 			m_tvStatus, m_tvReply;
	int 				m_nTotalW, m_nTotalH;
	D2Control 			m_d2;
	
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		m_nTotalW = dm.widthPixels;
		m_nTotalH = dm.heightPixels;
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	        // If the adapter is null, then Bluetooth is not supported
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        } 
	        
        int padding = 10;
        m_panel = new RelativeLayout(this);
        m_panel.setPadding(padding, padding, padding, padding);
        
        TextView tvBottom = new TextView(this);
        tvBottom.setText("www.pocketmagic.net - 2013"); tvBottom.setId(101);
        AppUtils.AddView(m_panel, tvBottom, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
        		new int[][]{new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM}, new int[]{RelativeLayout.CENTER_HORIZONTAL}},
        		-1,-1);
        
        TextView tvTop = new TextView(this);
        tvTop.setText("Android Bluetooth Control Application"); tvTop.setId(100);
        tvTop.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        AppUtils.AddView(m_panel, tvTop, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
        		new int[][]{new int[]{RelativeLayout.ALIGN_PARENT_TOP}, new int[]{RelativeLayout.ALIGN_PARENT_LEFT}},
        		-1,-1);
        
        setContentView(m_panel);
        
        // SHOW Content container
     	LinearLayout panelVContent = new LinearLayout(this);
     	panelVContent.setOrientation(LinearLayout.VERTICAL);
     	panelVContent.setPadding(padding,padding,padding,padding);
     	AppUtils.AddView(m_panel, panelVContent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
        		new int[][]{new int[]{RelativeLayout.BELOW, tvTop.getId()}, new int[]{RelativeLayout.ABOVE, tvBottom.getId()}},
        		-1,-1); 
     	// Scroll for all the controls
    	ScrollView vscroll = new ScrollView(this);
		vscroll.setFillViewport(false);
		vscroll.setScrollbarFadingEnabled(false);
		panelVContent.addView(vscroll);
		// Layout to scroll
	   	LinearLayout panelVScroll = new LinearLayout(this);
	   	panelVScroll.setOrientation(LinearLayout.VERTICAL);
	   	vscroll.addView(panelVScroll);
	   	// all cotnrols to be create in panelVScroll
	   	
	   	LinearLayout panel = new LinearLayout(this);
	   	panel.setOrientation(LinearLayout.HORIZONTAL);
	   	panelVScroll.addView(panel);
	   	
	   	Button b = new Button(this);
	   	b.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT));
	   	b.setText("Connect");
	   	b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); 
			}
		});
	   	panel.addView(b);
	   	
	   	m_tvStatus = new TextView(this);
	   	m_tvStatus.setText("Not connected.");
	   	panel.addView(m_tvStatus);
	   	
	   	
	   	
	   	TextView tv = new TextView(this);
	   	tv.setText("\nDevice interface");
	   	tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
	   	panelVScroll.addView(tv);
	   	
	   	// create
	   	for (int i=0;i<3;i++) {
	   		for (int j=0;j<3;j++);
	   	}
	   	
	   	m_d2 = new D2Control(this);
	   	m_d2.setLayoutParams(new LayoutParams( m_nTotalW /2 , m_nTotalW/2));
	   	m_d2.EnableUserInput();
	   	panelVScroll.addView(m_d2);
	  
	   	m_d2.SetOnReceiveEvents(new D2ControlEvents() {
			public void onRight() {
				Log.d(TAG, "RIGHT");
				sendMessage("6");
			}
			public void onLeft() {
				Log.d(TAG, "LEFT");
				sendMessage("4");
			}
			public void onForward() {
				Log.d(TAG, "FORWARD");
				sendMessage("8");
			}
			public void onBackward() {
				Log.d(TAG, "BACKWARD");
				sendMessage("2");
			}
		} );
	   	
	   	// update D2 for radar
        //m_d2.updateFrontObstacle(75, 100);
	  
	   	
	 	panel = new LinearLayout(this);
	   	panel.setOrientation(LinearLayout.HORIZONTAL);
	   	panelVScroll.addView(panel);
	   	
	   	b = new Button(this);
	   	b.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT));
	   	b.setText("Lights On");
	   	b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "LIGHTS ON");
				sendMessage("7");
			}
		});
	   	panel.addView(b);
	   	
	   	b = new Button(this);
	   	b.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT));
	   	b.setText("Lights Off");
	   	b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "LIGHTS OFF");
				sendMessage("9");
			}
		});
	   	panel.addView(b);
	   	
	   	m_tvReply = new TextView(this);
	   	m_tvReply.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	   	m_tvReply.setBackgroundColor(Color.DKGRAY);
	   	m_tvReply.setText("Recv:");
	   	panelVScroll.addView(m_tvReply);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
           if (mBTService == null) setupBT();
        }
    }
    
    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
            	mBTService.start();
            }
        }
    }  
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBTService != null) mBTService.stop();
        Log.d(TAG, "--- ON DESTROY ---");
    }
 
    
   
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode + " " + requestCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                //SetStatusMessage("Connecting to:"+device.getName());
                mBTService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupBT();
            	//SetStatusMessage("Bluetooth enabled. Press connect.");
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    } 
    
    void SetStatusMessage(final String msg) {
    	m_tvStatus.post(new Runnable() {
			public void run() {
				m_tvStatus.setText("   "+ msg);
			}
		});
    }
    
    private void setupBT() {
        Log.d(TAG, "setupBT()");
        // Initialize the BluetoothService to perform bluetooth connections
        mBTService = new BluetoothService(this, mHandler);
        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    } 
    
    String allMessage = "";
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	SetStatusMessage("Connected to:"+mConnectedDeviceName);
                    break;
                case BluetoothService.STATE_CONNECTING:
                	SetStatusMessage("Connecting...");
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	SetStatusMessage("Not connected.");
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                Log.d(TAG, "MESSAGE_WRITE:"+writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                final String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, "MESSAGE_READ:"+readMessage);
               
                
                allMessage += readMessage;
                if (allMessage.contains("]")) {
                	
                	if (allMessage.contains("<") && allMessage.contains(">")) {
	                	 int val = 0;
	                     	String between = allMessage.split("<|>")[1];
	                     	
	 	             	try {
	 	             		val = Integer.parseInt(between);
	 					}  catch (NumberFormatException e) {};
	 	                if (val>0) {
	 		             	final int val2 = val;
	 		                // update D2 for radar
	 		                m_d2.post(new Runnable() {
	 							public void run() {
	 								m_d2.updateFrontObstacle(val2, 200);
	 							}
	 						});
	 	                }
                	}
                     
                	final String tmp = allMessage;
                	allMessage = "";
	                m_tvReply.post(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							m_tvReply.setText("Recv:"+tmp);
						}
					});
	                //
	             	
                }

                
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBTService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
 
}
