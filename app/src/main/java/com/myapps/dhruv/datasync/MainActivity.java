package com.myapps.dhruv.datasync;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends ActionBarActivity {

    String spinitem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner = (Spinner) findViewById(R.id.opt);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*------------------------------------------------------------------------------------*/


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    public void getClipBoard(View view) {


        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String pasteData = "";


        if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();

            TextView cliptext = (TextView) findViewById(R.id.clip);
            TextView clipcon = (TextView) findViewById(R.id.clipcont);
            cliptext.setText(pasteData);
            clipcon.setText("Your Clipboard Content : ");

            File clip;

            FileOutputStream fout;

            try {
                Toast.makeText(getApplicationContext(), "File has been successfully created",
                        Toast.LENGTH_SHORT).show();
                clip = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "clipboard.txt");
                clip.createNewFile();
                fout = new FileOutputStream(clip);
                fout.write(pasteData.getBytes());
                Toast.makeText(getApplicationContext(), "File has been successfully created",
                        Toast.LENGTH_LONG).show();
                fout.close();
                Thread thread = getThread();
                thread.start();

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


                Spinner spinner = (Spinner) findViewById(R.id.opt);
                spinitem = spinner.getSelectedItem().toString();
                Log.v("spinitem", spinitem);

                try {


                    FTPClient con = null;
                    try {

                        EditText rawip = (EditText) findViewById(R.id.host);
                        String ip = rawip.getText().toString();
                        con = new FTPClient();
                        con.connect(ip);
                        con.setBufferSize(1024000);
                        if (con.login("dhruv", "")) {

                            con.enterLocalPassiveMode(); // important!
                            con.setFileType(FTP.BINARY_FILE_TYPE);
                            String data;


                            if (spinitem.equals("Downloads")) {
                                File[] list = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();

                                Log.e("files in downloads: ", String.valueOf(list.length));

                                int i = 0;

                                for (i = 0; i < list.length; i++) {

                                    if (list[i].isDirectory()) {


                                    } else {
                                        String currentfile = list[i].toString();
                                        Date lastMod = new Date(list[i].lastModified());
                                        SimpleDateFormat last = new SimpleDateFormat("yyyyMMddHHmmss");
                                        last.setTimeZone(TimeZone.getTimeZone("GMT"));
                                        Log.v("VERBOSE", last.format(lastMod));
                                        int b = currentfile.lastIndexOf('/');
                                        String filename = currentfile.substring(b + 1);

                                        Log.v("File Name ", filename);

                                        data = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename;
                                        FileInputStream in = new FileInputStream(new File(data));
                                        boolean result = con.storeFile("/c-drive/Users/dhruv19/Documents/Datasync/" + filename, in);

                                        con.setModificationTime("/c-drive/Users/dhruv19/Documents/Datasync/" + filename, last.format(lastMod));
                                        in.close();
                                        if (result) Log.v("upload result", filename + " uploaded");
                                    }
                                }
                            } else if (spinitem.equals("Clipboard")) {

                                data = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/clipboard.txt";
                                FileInputStream in = new FileInputStream(new File(data));
                                boolean result = con.storeFile("c-drive/Users/dhruv19/Documents/Datasync/clipboard.txt", in);

                                in.close();
                                if (result) Log.v("upload result", "clipboard.txt uploaded");


                            }
                            con.logout();
                            con.disconnect();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });


    }
}