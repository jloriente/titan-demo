package com.innerfunction.titandemo;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class Main extends TabActivity {
	TabHost tabHost;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // CREATE TABS
        Resources res = getResources();	 // Resource object to get Drawables
        tabHost = getTabHost(); 		 // The activity TabHost
        TabHost.TabSpec spec;  			 // Reusable TabSpec for each tab
        Intent intent;  				 // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        // Initialize a TabSpec for each tab and add it to the TabHost
        intent = new Intent().setClass(this, NewsActivity.class);
        spec = tabHost.newTabSpec("news").setIndicator("News",
                          res.getDrawable(R.drawable.news))
                      .setContent(intent);

        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, LogonActivity.class);
        spec = tabHost.newTabSpec("logon").setIndicator("Logon",
                          res.getDrawable(R.drawable.guide))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AboutActivity.class);
        spec = tabHost.newTabSpec("about").setIndicator("About",
                          res.getDrawable(R.drawable.about))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);


    }
}
