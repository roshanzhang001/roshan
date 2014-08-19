package com.android.mumassistant.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FormFile {
	private byte[] mData;
	private InputStream mInStream;
	private File mFile;
	private String mFilname;
	private String mParameterName;/* 请求参数名称*/
	private String mContentType = "application/octet-stream";/* 内容类型 */
	
    public FormFile(String filname, byte[] data, String parameterName, String contentType) {
        this.mData = data;
        this.mFilname = filname;
        this.mParameterName = parameterName;
        if(contentType!=null) this.mContentType = contentType;
    }
    
    public FormFile(String filname, File file, String parameterName, String contentType) {
        this.mFilname = filname;
        this.mParameterName = parameterName;
        this.mFile = file;
        try {
            this.mInStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(contentType!=null) this.mContentType = contentType;
    }
    
    public File getFile() {
        return mFile;
    }
    
    public InputStream getInStream() {
        return mInStream;
    }
    
    public byte[] getData() {
        return mData;
    }
    
    public String getFilname() {
        return mFilname;
    }
    
    public void setFilname(String filname) {
        this.mFilname = filname;
    }
    
    public String getParameterName() {
        return mParameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.mParameterName = parameterName;
    }
    
    public String getContentType() {
        return mContentType;
    }
    
    public void setContentType(String contentType) {
        this.mContentType = contentType;
    }
}
