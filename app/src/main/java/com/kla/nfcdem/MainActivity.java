package com.kla.nfcdem;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.app.Activity;

import static com.kla.nfcdem.R.id.beam_button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button beamButton = (Button) findViewById(R.id.button);
       beamButton.setOnClickListener(new View.OnClickListener()
       //beamButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"Button tapped!",Toast.LENGTH_LONG).show();

               Intent intent = new Intent(MainActivity.this, BeamActivity.class);
               startActivity(intent);
            }
        });

        Button tagButton = (Button)findViewById(R.id.tags_button);
        tagButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public  void onClick(View v){
                //startActivity(new Intent(MainActivity.this, TagsActivity.this));
                Intent intent = new Intent(MainActivity.this, TagsActivity.class);
                startActivity(intent);

            }

        });
    }





    /*Button tags_Button = (Button) findViewById(R.id.tags_button);
    tags_Button.setOnClickListener(new View.OnClickListener(){

        @Override
         public void onClick(View v){
            startActivity(new Intent(MainActivity.this, TagsActivity.this));
        }
    });*/

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
}
