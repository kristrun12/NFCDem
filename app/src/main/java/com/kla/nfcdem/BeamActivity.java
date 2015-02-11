package com.kla.nfcdem;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;
import android.nfc.NfcManager;


public class BeamActivity extends ActionBarActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{

    NfcAdapter mNfcAdapter;
    EditText mEditText;
    private  static final String MIME_TYPE = "application/com.kla.nfcdem";
    private static final String PACKAGE_NAME = "com.kla.nfcdem";
    private  static final int MESSAGE_SENT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);

        mEditText = (EditText)findViewById(R.id.beam_edit_text);

        //check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null){
            Toast.makeText(this, "Sorry, NFC is not available on this device",
                    Toast.LENGTH_SHORT).show();
        }

        //Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        //Register callback to lister for message sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this,this);
    }

    /**
     * Implenentation for the CreateNdefMessageCallback interface
     *
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event){

        String text = mEditText.getText().toString();
        NdefMessage msg = new NdefMessage(new NdefRecord[]{
                NfcUtils.createRecord(MIME_TYPE,text.getBytes()),
                NdefRecord.createApplicationRecord(PACKAGE_NAME)
        });
        return msg;
    }

    //this handler receives a message from onNdefPushComplete
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    //implementation for the OnNdefPushCompleteCallback interface
    @Override
    public void onNdefPushComplete(NfcEvent arg0)
    {
        //A handler is needed to send messages to the activity when this
        //callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    @Override
    public void onNewIntent (Intent intent){

        //onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        //check to see that the Activity started due to an Android Beam
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processIntent(getIntent());
        }
    }

    //Parses the NDEF Message from the intent and toast to the user
    void processIntent(Intent intent)
    {
        Parcelable[]rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        //in this context, only one message was sent over beam
        NdefMessage msg = (NdefMessage)rawMsgs[0];
        //record 0 contains the MIME type, record 1 is the AAR, if present
        String payload = new String(msg.getRecords()[0].getPayload());
        Toast.makeText(getApplicationContext(),"Message received over beam: "+payload,
                Toast.LENGTH_LONG).show();

    }

}
