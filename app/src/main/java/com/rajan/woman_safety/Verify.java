package com.rajan.woman_safety;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Verify extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        /*
         Show the Up button in the action bar.
        setupActionBar();
        */


    }


    public void verify_no(View v) {
        EditText source_no = (EditText) this.findViewById(R.id.editText1);
        String str_source_no = source_no.getText().toString();
        SQLiteDatabase db;
        int count;
        try {
            db = openOrCreateDatabase("NumDB", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS source(number VARCHAR PRIMARY KEY );");
            db.execSQL("INSERT INTO source VALUES('" + str_source_no + "');");
            Toast.makeText(getApplicationContext(), str_source_no + " Successfully Saved", Toast.LENGTH_SHORT).show();
            db.close();
            back(v);
        }
        catch(Exception e)
        {
            Toast.makeText(this,"Invalid Number or It Already Exists",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.verify, menu);
        return true;
    }



    public void back(View v) {
        Intent i_back=new Intent(Verify.this,MainActivity.class);
        startActivity(i_back);
        finish();

    }

}
