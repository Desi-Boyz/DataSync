package com.myapps.dhruv.datasync;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class settings extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText user = (EditText)findViewById(R.id.user);
        EditText ip= (EditText)findViewById(R.id.host);
        EditText pass = (EditText)findViewById(R.id.pass);
        EditText pcname = (EditText)findViewById(R.id.pcname);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        user.setText(sharedPref.getString("ftpname",null));
        ip.setText(sharedPref.getString("host",null));
        pass.setText(sharedPref.getString("ftppass",null));
        pcname.setText(sharedPref.getString("userpc",null));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save(View v){

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        EditText user = (EditText)findViewById(R.id.user);
        EditText ip= (EditText)findViewById(R.id.host);
        EditText pass = (EditText)findViewById(R.id.pass);
        EditText pcname = (EditText)findViewById(R.id.pcname);

       String ftpname = user.getText().toString();
        String host = ip.getText().toString();
        String ftppass = pass.getText().toString();
        String userpc = pcname.getText().toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();

        Log.v("ipraw",host);

        editor.putString("ftpname",ftpname);
        editor.putString("host",host);
        editor.putString("ftppass",ftppass);
        editor.putString("userpc",userpc);
        editor.commit();

        Toast.makeText(getApplicationContext(), "Details Saved Successfully", Toast.LENGTH_SHORT).show();




    }

}
