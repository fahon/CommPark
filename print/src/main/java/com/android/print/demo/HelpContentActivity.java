package com.android.print.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class HelpContentActivity extends Activity implements OnClickListener
{
     private Button btnBack;
     private WebView web;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_content);
		initView();

	}

	protected void initView()
	{

	    Intent intent = this.getIntent();
		String text=intent.getStringExtra("string");



		web=(WebView)findViewById(R.id.webView1);
		web.loadUrl("file:///android_asset/"+text);


		btnBack=(Button)findViewById(R.id.button_bace);
		btnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
       if(v==btnBack)
       {
    	   finish();
       }
	}

}
