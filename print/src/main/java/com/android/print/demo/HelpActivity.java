package com.android.print.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity implements OnClickListener
{
    private Button btnConnect;
    private Button btnPrint;
    private Button btnListenDis;
    private Button btnListenRec;
    private Button btnBondsAddr;
    private Button btnBack;
    private String text;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_list);
		initView();
	}

	protected void initView()
	{
       btnConnect=(Button)findViewById(R.id.connect_principle);
       btnConnect.setOnClickListener(this);
       btnPrint=(Button)findViewById(R.id.print_principle);
       btnPrint.setOnClickListener(this);

       btnListenDis=(Button)findViewById(R.id.discon_listen);
       btnListenDis.setOnClickListener(this);
       btnListenRec=(Button)findViewById(R.id.recon_listen);
       btnListenRec.setOnClickListener(this);
       btnBondsAddr=(Button)findViewById(R.id.address_bouds);
       btnBondsAddr.setOnClickListener(this);

       btnBack=(Button)findViewById(R.id.button_bace);
       btnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{

	    Intent intent=new Intent(this,HelpContentActivity.class);
		if(v==btnConnect)
        {
           text="help_connect.html";
           intent.putExtra("string",text);
           startActivity(intent);

        }else if(v==btnPrint)
        {
           text="help_print.html";
           intent.putExtra("string",text);
           startActivity(intent);

        }else if(v==btnListenDis)
        {
        	text="help_disconn.html";
        	 intent.putExtra("string",text);
             startActivity(intent);

        }else if(v==btnListenRec)
        {
        	text="help_reconn.html";
        	 intent.putExtra("string",text);
             startActivity(intent);

        }else if(v==btnBondsAddr)
        {
        	text="help_autoconn.html";
        	 intent.putExtra("string",text);
             startActivity(intent);

        }else if(v==btnBack)
        {
        	finish();

        }




	}


}
