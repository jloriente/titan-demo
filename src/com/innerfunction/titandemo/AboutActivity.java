package com.innerfunction.titandemo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 WebView webview = new WebView(this);
		 setContentView(webview);
	}

}
