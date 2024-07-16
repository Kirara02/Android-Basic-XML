package com.kirara.javanfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private boolean writeMode = false;
    private Tag myTag;
    private TextView tagIdTextView;
    private TextView nfcContentTextView;
    private LinearLayout editTextContainer;
    private List<LinearLayout> messageLayouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagIdTextView = findViewById(R.id.tagIdTextView);
        nfcContentTextView = findViewById(R.id.nfcContentTextView);
        editTextContainer = findViewById(R.id.editTextContainer);
        Button addMessageButton = findViewById(R.id.addMessageButton);
        Button writeButton = findViewById(R.id.writeButton);

        addMessageButton.setOnClickListener(v -> addMessage());
        writeButton.setOnClickListener(v -> writeMessages());

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            List<NdefMessage> msgs = new ArrayList<>();
            if (rawMsgs != null) {
                for (Parcelable rawMsg : rawMsgs) {
                    msgs.add((NdefMessage) rawMsg);
                }
                buildTagViews(msgs.toArray(new NdefMessage[0]));
            }
            String uid = bytesToHex(myTag.getId());
            tagIdTextView.setText("TAG ID: " + uid);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        List<String> texts = new ArrayList<>();
        for (NdefMessage msg : msgs) {
            for (NdefRecord record : msg.getRecords()) {
                byte[] payload = record.getPayload();
                Charset textEncoding = ((payload[0] & 128) == 0) ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
                int languageCodeLength = payload[0] & 51;
                String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                texts.add(text);
            }
        }
        nfcContentTextView.setText("Message read from NFC Tag:\n" + String.join("\n", texts));
    }

    private void writeMessages() {
        if (myTag == null) {
            Toast.makeText(this, "No NFC tag detected!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            List<String> messages = new ArrayList<>();
            for (LinearLayout layout : messageLayouts) {
                EditText editText = (EditText) layout.getChildAt(0);
                messages.add(editText.getText().toString());
            }
            write(messages, myTag);
            Toast.makeText(this, "Text written to the NFC tag successfully!", Toast.LENGTH_LONG).show();
        } catch (IOException | FormatException e) {
            Toast.makeText(this, "Error during writing, is the NFC tag close enough to your device?", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void addMessage() {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);

        EditText newEditText = new EditText(this);
        newEditText.setHint("Message " + (messageLayouts.size() + 1));
        newEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setOnClickListener(v -> removeMessage(messageLayout));

        messageLayout.addView(newEditText);
        messageLayout.addView(removeButton);
        editTextContainer.addView(messageLayout);
        messageLayouts.add(messageLayout);
    }

    private void removeMessage(LinearLayout messageLayout) {
        editTextContainer.removeView(messageLayout);
        messageLayouts.remove(messageLayout);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WriteModeOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    public static void write(List<String> texts, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = new NdefRecord[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            byte[] language = "en".getBytes(Charset.forName("US-ASCII"));
            byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
            byte[] payload = new byte[1 + language.length + textBytes.length];
            payload[0] = (byte) language.length;
            System.arraycopy(language, 0, payload, 1, language.length);
            System.arraycopy(textBytes, 0, payload, 1 + language.length, textBytes.length);
            records[i] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        }
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
