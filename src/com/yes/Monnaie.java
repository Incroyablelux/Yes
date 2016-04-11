package com.yes;

import java.io.IOException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class Monnaie extends Activity implements OnKeyListener
{
	EditText valEuro=null;
	EditText valDollar=null;
	EditText valYen=null;
	EditText valLivre=null;
	EditText valYuan=null;
	EditText valRuble=null;
	TextView time=null;
	double txS=1.34, txY=127.44, txL=0.7, txZ=8.0707, txP=43.0380;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monnaie);
		valEuro = (EditText)findViewById(R.id.valeurEuro);
		valEuro.setOnKeyListener(this);
		valDollar = (EditText)findViewById(R.id.valeurDollar);
		valDollar.setOnKeyListener(this);
		valYen = (EditText)findViewById(R.id.valeurYen);
		valYen.setOnKeyListener(this);
		valLivre = (EditText)findViewById(R.id.valeurLivre);
		valLivre.setOnKeyListener(this);
		valYuan = (EditText)findViewById(R.id.valeurYuan);
		valYuan.setOnKeyListener(this);
		valRuble = (EditText)findViewById(R.id.valeurRuble);
		valRuble.setOnKeyListener(this);
		time = (TextView)findViewById(R.id.Time);
		
		Thread t=new Thread();
		t.execute();
	}
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.monnaie, menu);
		return true;
	}

	public boolean onKey(View v, int c, KeyEvent k)
	{
		if ((v==valEuro) && (k.getAction()==KeyEvent.ACTION_UP) && !valEuro.getText().toString().isEmpty())
			devise(valEuro, valDollar, valYen, valLivre, valYuan, valRuble, txS, txY, txL, txZ, txP);	

		if ((v==valDollar) && (k.getAction()==KeyEvent.ACTION_UP) && !valDollar.getText().toString().isEmpty())
			devise(valDollar, valEuro, valYen, valLivre, valYuan, valRuble, 1/txS, (1/txS)*txY, (1/txS)*txL, (1/txS)*txZ, (1/txS)*txP);

		if ((v==valYen) && (k.getAction()==KeyEvent.ACTION_UP) && !valYen.getText().toString().isEmpty())
			devise(valYen, valEuro, valDollar, valLivre, valYuan, valRuble, 1/txY, (1/txY)*txS, (1/txY)*txL, (1/txY)*txZ, (1/txY)*txP);

		if ((v==valLivre) && (k.getAction()==KeyEvent.ACTION_UP) && !valLivre.getText().toString().isEmpty())
			devise(valLivre, valEuro, valDollar, valYen, valYuan, valRuble, 1/txL, (1/txL)*txS, (1/txL)*txY, (1/txL)*txZ, (1/txL)*txP);
		
		if ((v==valYuan) && (k.getAction()==KeyEvent.ACTION_UP) && !valYuan.getText().toString().isEmpty())
			devise(valYuan, valEuro, valDollar, valYen, valLivre, valRuble, 1/txZ, (1/txZ)*txS, (1/txZ)*txY, (1/txZ)*txL, (1/txZ)*txP);
		
		if ((v==valRuble) && (k.getAction()==KeyEvent.ACTION_UP) && !valRuble.getText().toString().isEmpty())
			devise(valRuble, valEuro, valDollar, valYen, valLivre, valYuan, 1/txP, (1/txP)*txS, (1/txP)*txY, (1/txP)*txL, (1/txP)*txZ);
			
		if (valEuro.getText().toString().isEmpty() || valDollar.getText().toString().isEmpty() || valYen.getText().toString().isEmpty() || valLivre.getText().toString().isEmpty() || valYuan.getText().toString().isEmpty() || valRuble.getText().toString().isEmpty())
		{
			valEuro.setText("");
			valDollar.setText("");
			valYen.setText("");
			valLivre.setText("");
			valYuan.setText("");
			valRuble.setText("");
		}
		return false;
	}
    
	public void devise(EditText n1, EditText n2, EditText n3, EditText n4, EditText n5, EditText n6, double d1, double d2, double d3, double d4, double d5)
	{	
		n2.setText(String.valueOf(Double.parseDouble(n1.getText().toString())*d1));
		n3.setText(String.valueOf(Double.parseDouble(n1.getText().toString())*d2));
		n4.setText(String.valueOf(Double.parseDouble(n1.getText().toString())*d3));
		n5.setText(String.valueOf(Double.parseDouble(n1.getText().toString())*d4));
		n6.setText(String.valueOf(Double.parseDouble(n1.getText().toString())*d5));
	}
	
	public class Thread extends AsyncTask<Void, Void, Void>
	{
		String date;
		
		protected Void doInBackground(Void... params)
		{
			xml(1);		// on essaye de lancer en online, et sinon en offline via un catch{}
			return null;
		}
				
		public void xml(int mode)
		{
			try
			{
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		        factory.setNamespaceAware(true);
		        XmlPullParser xpp = factory.newPullParser();
		        if (mode==1)
		        {
		        	URL url = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
		        	xpp.setInput(url.openConnection().getInputStream(), null);
		        }
		        else
		        	xpp.setInput(getResources().openRawResource(R.raw.eurofxref), null);
		        int eventType = xpp.getEventType();
		         
		        while (eventType != XmlPullParser.END_DOCUMENT) 
			    {
		        	if(eventType==XmlPullParser.START_TAG && xpp.getName().equals("Cube"))
			        {
			        	if (xpp.getAttributeCount() == 1)
			        	{
			        		date = xpp.getAttributeValue(0);
			        	}
			        	else if (xpp.getAttributeCount()==2 && xpp.getAttributeValue(0).equals("USD"))
			        	{
			        		txS=Double.parseDouble( xpp.getAttributeValue(1) );
			        	}
			        	else if (xpp.getAttributeCount()==2 && xpp.getAttributeValue(0).equals("JPY"))
			        	{
			        		txY=Double.parseDouble( xpp.getAttributeValue(1) );
			        	}
			        	else if (xpp.getAttributeCount()==2 && xpp.getAttributeValue(0).equals("LVL"))
			        	{
			        		txL=Double.parseDouble( xpp.getAttributeValue(1) );
			        	}
			        	else if (xpp.getAttributeCount()==2 && xpp.getAttributeValue(0).equals("CNY"))
			        	{
			        		txZ=Double.parseDouble( xpp.getAttributeValue(1) );
			        	}
			        	else if (xpp.getAttributeCount()==2 && xpp.getAttributeValue(0).equals("RUB"))
			        	{
			        		txP=Double.parseDouble( xpp.getAttributeValue(1) );
			        	}
			        }
		        	eventType = xpp.next();
		        }
			}
			catch (XmlPullParserException e) {xml(0);}
			catch (IOException e){xml(0);}  
		}
		
		protected void onPostExecute(Void result)
		{	
			time.setText(date);	
			time.setTextColor(Color.GREEN);	
		}
	}	
}
