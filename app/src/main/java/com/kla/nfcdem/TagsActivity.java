package com.kla.nfcdem;

import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TagsActivity extends ActionBarActivity {

    //initializing UI elements
    private Button mEnableWriteButton;
    private EditText mTextField;
    private NfcAdapter mNfcAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        mTextField = (EditText)findViewById(R.id.text_field);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mEnableWriteButton = (Button)findViewById(R.id.enable_write_button);
        mEnableWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTagWriteReady(!isWriteReady);
                mProgressBar.setVisibility(isWriteReady ? View.VISIBLE : View.GONE);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null){
            Toast.makeText(this, "Sorry, NFC is not available on this device",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private  boolean isWriteReady = false;
    //enable this activity to write to a tag
    public  void setTagWriteReady(boolean isWriteReady) {
        this.isWriteReady = isWriteReady;
        if (isWriteReady) {
            IntentFilter[] writeTagFilters = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
            mNfcAdapter.enableForegroundDispatch(
                    TagsActivity.this, NfcUtils.getPendingIntent(TagsActivity.this),
                    writeTagFilters, null);
        } else {
            //Disable dispath if not writing tags
            mNfcAdapter.disableForegroundDispatch(TagsActivity.this);
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        //onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isWriteReady && NfcAdapter.ACTION_TAG_DISCOVERED.equals((
                getIntent().getAction()
                ))){
            processWriteIntent(getIntent());
        }else if (!isWriteReady && (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())
        || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))){
            processReadIntent(getIntent());

        }
    }

    public  void processReadIntent(Intent intent){

        List<NdefMessage> intentMessages = NfcUtils.getMessagesFromIntent(intent);
        List<String>payloadStrings = new ArrayList<String>(intentMessages.size());

        for(NdefMessage message : intentMessages){
            for(NdefRecord record : message.getRecords()){
                byte[] payload = record.getPayload();
                String payloadString = new String(payload);

                if (!TextUtils.isEmpty(payloadString))
                    payloadStrings.add(payloadString);
            }
        }
        if(!payloadStrings.isEmpty()){
            Toast.makeText(TagsActivity.this, "Read from tag: " + TextUtils.join("," ,payloadStrings),Toast.LENGTH_LONG).show();
        }
    }

    private  static final String MIME_TYPE = "application/com.kla.nfcdem";

    public  void processWriteIntent(Intent intent)
    {
        if(isWriteReady && NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())){

            Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String tagWriterMessage = mTextField.getText().toString();
            byte[] payload = new String(tagWriterMessage).getBytes();

            if(detectedTag != null && NfcUtils.writeTag(
                    NfcUtils.createMessage(MIME_TYPE,payload),detectedTag
            )){
                Toast.makeText(this, "wrote " + tagWriterMessage + " to a tag!",
                        Toast.LENGTH_LONG).show();
                setTagWriteReady(false);
            }else
            {
                Toast.makeText(this, "write failed. Please try again. ",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tags, menu);
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
