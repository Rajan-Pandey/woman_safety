package com.rajan.woman_safety;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Display extends Activity{


    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        try {
            SQLiteDatabase db;
            db = openOrCreateDatabase("NumDB", Context.MODE_PRIVATE, null);

            c = db.rawQuery("SELECT * FROM details", null);
            if (c.getCount() == 0) {
                showMessage("Error", "No records found.");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext()) {
                buffer.append("Name: " + c.getString(0) + "\n");
                buffer.append("Number: " + c.getString(1) + "\n");
            }
            showMessage("Registered Mobile Numbers are: ", buffer.toString());
        }
        catch (Exception e)
        {
            Toast.makeText(this, "No records Found , Please Add Some ", Toast.LENGTH_SHORT).show();
            Intent i_register = new Intent(Display.this,Register.class);
            startActivity(i_register);
            finish();
        }
    }

    public void call_Location(View v)
    {
        Intent i_startActivity=new Intent(Display.this,BgService.class);
        startActivity(i_startActivity);
        finish();
    }

    public void showMessage(String title,String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void back(View v) {
        Intent i_back=new Intent(Display.this,MainActivity.class);
        startActivity(i_back);
        finish();

    }

}









