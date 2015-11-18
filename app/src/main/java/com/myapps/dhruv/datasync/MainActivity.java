package com.myapps.dhruv.datasync;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class MainActivity extends ActionBarActivity {

    String spinitem = "";
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.send);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sendas, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            Intent intent = new Intent(getApplicationContext(),settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*------------------------------------get clipboard content------------------------------------------------*/


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    public void getClipBoard(View view) {

        Spinner spinner = (Spinner) findViewById(R.id.send);
        spinitem = spinner.getSelectedItem().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String pasteData = "";



        if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();

            TextView cliptext = (TextView) findViewById(R.id.clip);
            TextView clipcon = (TextView) findViewById(R.id.clipcont);

            clipcon.setText("Your Clipboard Content : ");
            cliptext.setText(pasteData);

            if(spinitem.equals("Link")){
                pasteData = "<script>window.googleJavaScriptRedirect=1</script><META name=\"referrer\" content=\"origin\"><script>var n={navigateTo:function(b,a,d){if(b!=a&&b.google){if(b.google.r){b.google.r=0;b.location.href=d;a.location.replace(\"about:blank\");}}else{a.location.replace(d);}}};n.navigateTo(window.parent,window,\"" +pasteData+ "\");\n" +
                        "</script><noscript><META http-equiv=\"refresh\" content=\"0;URL='"+pasteData+"'\"></noscript>";
            }

            File clip;

            FileOutputStream fout;

            try {
               /* Toast.makeText(getApplicationContext(), "File has been successfully created",
                        Toast.LENGTH_SHORT).show();*/
                clip = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "clipboard.txt");
                clip.createNewFile();
                fout = new FileOutputStream(clip);
                fout.write(pasteData.getBytes());
                /*Toast.makeText(getApplicationContext(), "File has been successfully created",
                        Toast.LENGTH_LONG).show();*/
                fout.close();
                Thread thread = getThread();
                thread.start();
                thread.join();

                if(flag){

                    Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"An Error Occurred, Upload Unsuccessful",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


/*---------------------------------------- connection thread ---------------------------------*/

    public Thread getThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("try","try");

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

                String username = sharedPref.getString("ftpname",null);
                String ip = sharedPref.getString("host","host");
                String password = sharedPref.getString("ftppass",null);
                String pcname = sharedPref.getString("userpc",null);

                Log.v("ip",ip);

                FTPClient con = null;

                    try {



                        Log.v("ip",ip);
                        Log.v("try","try");


                        con = new FTPClient();
                        con.connect(ip);
                        con.setBufferSize(10240000);
                        if (con.login(username,password)) {

                            con.enterLocalPassiveMode(); // important!
                            con.setFileType(FTP.BINARY_FILE_TYPE);

                            String data;
                            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                            Log.v("verbose",spinitem);

                                if(spinitem.equals("Text")) {
                                    data = dir.toString() + "/clipboard.txt";
                                    Log.v("verbose", data);
                                    FileInputStream in = new FileInputStream(new File(data));
                                    boolean result = con.storeFile("/c-drive/Users/" + pcname + "/Desktop/clipboard.txt", in);

                                    if (result) {
                                        flag = true;
                                    }
                                    in.close();

                                }else if(spinitem.equals("Link")){

                                    File oldfile = new File(dir,"clipboard.txt");
                                    File newfile = new File(dir,"clipboard.htm");
                                    oldfile.renameTo(newfile);

                                    data = dir.toString() + "/clipboard.htm";
                                    FileInputStream in = new FileInputStream(new File(data));
                                    boolean result = con.storeFile("/c-drive/Users/" + pcname + "/Desktop/clipboard.htm", in);

                                    if (result) {
                                        flag = true;
                                    }
                                    in.close();
                                }



                            }
                            con.logout();
                            con.disconnect();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }

        });


    }
}