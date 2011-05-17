package com.innerfunction.js.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.Context;

public class ScriptResolverImpl implements ScriptResolver{

	public Reader getReader(String path, Context context) throws IOException {
		InputStream inputStream = context.getAssets().open(path);
		Reader reader = null;
		if(inputStream != null){
        	reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
		}
		return reader;
	}
}
