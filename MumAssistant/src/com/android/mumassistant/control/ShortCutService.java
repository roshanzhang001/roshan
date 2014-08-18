package com.android.mumassistant.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class ShortCutService extends Service{
	private InetAddress mServerAddress;
	private DatagramPacket mPacket;
	private int mPort;
	private Socket mSocket = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private boolean mIs_connect = false;
	private byte[] mShotdata = null;
	private byte[] mData = null;
	private byte[] mDataLen = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		int msg = intent.getIntExtra("MSG",0);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
		
	}

	public class shortcut extends Thread {
		private CONNECT connect;
		private int number;
		
		public shortcut(CONNECT c, int number){
			this.connect = c;
			this.number = number;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(true){
				try {
					mShotdata = null;
					mShotdata = startShot();
					if(mShotdata == null){
						mShotdata = startShot();
						connect.put(mShotdata);
					}else{
						connect.put(mShotdata);
					}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
		}		
	}
	
	public class CONNECT {
		private byte[] contents;
		private boolean available = false;
		
		public synchronized byte[] get(){
			
			while (available == false) {
				try { 
                    wait(); 
                    } catch (InterruptedException e) { 
                    	
                    }
			}
			available = false;
			notifyAll();
			return contents;
			
		}
		
		public synchronized void put(byte[] value) { 
            while (available == true) { 
                try { 
                    wait(); 
                    } catch (InterruptedException e) { } 
            } 
            contents = value; 
            available = true; 
               
            notifyAll(); 
        } 
	}
	
	public class SEND extends Thread { 
        private CONNECT connect; 
        private int number; 
       
        public SEND(CONNECT c, int number) { 
            connect = c; 
            this.number = number; 
        } 
       
        public void run() { 
                    mData = null; 
                    mDataLen = null; 
                while(true){ 
                        try { 
                        	mData = connect.get(); 
                                System.out.println("Send--->" + Thread.currentThread().getId()); 
                                mDataLen = new byte[4];         
                                        //一帧一帧发 
                                mDataLen[0] = (byte) (mData.length & 0xff); 
                                mDataLen[1] = (byte) (mData.length >> 8 & 0xff); 
                                mDataLen[2] = (byte) (mData.length >> 16 & 0xff); 
                                mDataLen[3] = (byte) (mData.length >> 24 & 0xff);         
                                    mPacket = new DatagramPacket(mData,mData.length,mServerAddress,mPort); 
                                    try { 
                                            //socket.send(packet); 
                                            mOutputStream.write(mDataLen); 
                                            mOutputStream.write(mData,0,mData.length);                         
                                            mOutputStream.flush();                                                 
                                    } catch (IOException e) { 
                                            // TODO Auto-generated catch block 
                                            e.printStackTrace();                                                 
                                            try { 
                                            	mOutputStream.close(); 
                                                mSocket.close(); 
                                            } catch (IOException e1) { 
                                                    // TODO Auto-generated catch block 
                                                    e1.printStackTrace(); 
                                            }                                 
                                    }         
                                } catch (Exception e) { 
                                        // TODO: handle exception 
                                        e.printStackTrace(); 
                                } 
   
                                   
            } 
        } 
    } 
	
	private void connect(String Ip_address,String Ip_port) { 
        // TODO Auto-generated method stub 
        Ip_address.trim(); 
        Ip_port.trim(); 
        if(Ip_address.length()==0||Ip_port.length()==0) 
                System.out.println("ip_server edit is empty or port_server is empty!"); 
        else
        { 
                mPort= Integer.parseInt(Ip_port);         
                try { 
                    //创建一个Socket对象，指定服务器端的IP地址和端口号 
                   mSocket = new Socket(); 
                   mSocket.connect(new InetSocketAddress(Ip_address, mPort), 1000); 
                       
                    if(mSocket.isConnected()==true){ 
                            mOutputStream = mSocket.getOutputStream(); 
                                mInputStream = mSocket.getInputStream(); 
                            mIs_connect = true;                             
                    } 
                       
                } catch (Exception e) { 
                        if(e.getClass()==SocketTimeoutException.class){  
                        } 
                    // TODO Auto-generated catch block 
                e.printStackTrace(); 
                   
                } 
        } 
	}
	
	private void close() { 
        // TODO Auto-generated method stub 
        try{ 
                mIs_connect = false; 
                mSocket.shutdownInput(); 
                mSocket.shutdownOutput(); 
                mSocket.close();
                //socket.disconnect();                         
        } catch (Exception e){ 
                e.printStackTrace(); 
        } 
	}
	
	private byte[] startShot(){
		WindowManager mWindowManager = (WindowManager)getSystemService(getApplicationContext().WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		Matrix mDisplayMatrix = new Matrix();	
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		Bitmap mScreenBitmap;
		mDisplay.getRealMetrics(mDisplayMetrics);
		float[] dims = { mDisplayMetrics.widthPixels,
				mDisplayMetrics.heightPixels };
		float degrees = getDegreesForRotation(mDisplay.getRotation());
		boolean requiresRotation = (degrees > 0);
		if (requiresRotation) {
			mDisplayMatrix.reset();
			mDisplayMatrix.preRotate(-degrees);
			mDisplayMatrix.mapPoints(dims);
			dims[0] = Math.abs(dims[0]);
			dims[1] = Math.abs(dims[1]);
		}
		mScreenBitmap = Surface.screenshot((int) dims[0], (int) dims[1]);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		
		//Bitmap Bmp = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
		return baos.toByteArray();
	}
	
    private float getDegreesForRotation(int value) {
        switch (value) {
        case Surface.ROTATION_90:
            return 360f - 90f;
        case Surface.ROTATION_180:
            return 360f - 180f;
        case Surface.ROTATION_270:
            return 360f - 270f;
        }
        return 0f;
    }
}
