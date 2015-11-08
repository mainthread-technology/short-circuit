package technology.mainthread.shortcircuit.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import technology.mainthread.shortcircuit.R;
import technology.mainthread.shortcircuit.nearby.NearbyItem;
import technology.mainthread.shortcircuit.nearby.NearbyWrapper;
import technology.mainthread.shortcircuit.views.BatteryView;

import static android.nfc.NdefRecord.createMime;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private int currentCharge = 100;
    private int currentChargeChange = 0;

    @Bind(R.id.current_charge)
    TextView txtCurrentCharge;
    @Bind(R.id.battery)
    BatteryView batteryView;
    @Bind(R.id.gameOver)
    TextView txtGameOver;
    @Bind(R.id.add)
    Button add;
    @Bind(R.id.minus)
    Button minus;

    private NearbyWrapper nearbyWrapper;
    private Gson gson;
    private String deviceId;

    private final MessageListener listener = new MessageListener() {
        @Override
        public void onFound(final Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String messageString = new String(message.getContent());
                    NearbyItem nearbyItem = gson.fromJson(messageString, NearbyItem.class);

                    if (nearbyItem.getTimestamp() < System.currentTimeMillis() + 1000) {
                        updateBattery(-20);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBattery(-10);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBattery(+10);
            }
        });

        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        setUpNewGame();
        setupNearby();
    }

    public void setUpNewGame() {
        txtCurrentCharge.setText("100%");
        updateBattery(100);
        txtGameOver.setVisibility(View.GONE);
    }

    private void setupNearby() {
        gson = new Gson();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        nearbyWrapper = new NearbyWrapper(this, listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nearbyWrapper.onStart();
    }

    @Override
    protected void onStop() {
        nearbyWrapper.onStop();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        nearbyWrapper.onActivityResult(requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                setUpNewGame();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage[] msgs;

        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                runCardAction(new String(msgs[i].getRecords()[0].getPayload()));
            }
        }
    }

    private void runCardAction(String command) {
        int updateCharge = 0;

        String[] parts = command.split(" ");

        if (parts.length == 2) {
            switch (parts[0]) {
                case "charge":
                    updateBattery((int) (Float.parseFloat(parts[1])));
                    break;
                case "drain":
                    updateBattery((int) (Float.parseFloat(parts[1])));
                    break;
            }

            return;
        }

        switch (command) {
            case "1":
            case "2":
                Toast.makeText(getApplicationContext(), "Charge 10%", Toast.LENGTH_SHORT).show();
                updateCharge = +10;
                break;
            case "3":
                Toast.makeText(getApplicationContext(), "Charge 20%!", Toast.LENGTH_SHORT).show();
                updateCharge = +20;
                break;
            case "4":
                Toast.makeText(getApplicationContext(), "Charge 30%!", Toast.LENGTH_SHORT).show();
                updateCharge = +30;
                break;
            case "5":
            case "6":
                Toast.makeText(getApplicationContext(), "Drain 10%!", Toast.LENGTH_SHORT).show();
                updateCharge = -10;
                break;
            case "7":
                Toast.makeText(getApplicationContext(), "Drain 20%!", Toast.LENGTH_SHORT).show();
                updateCharge = -20;
                break;
            case "8":
                Toast.makeText(getApplicationContext(), "Drain 30%!", Toast.LENGTH_SHORT).show();
                updateCharge = -30;
                break;
            case "9":
                Toast.makeText(getApplicationContext(), "Drain 40%!", Toast.LENGTH_SHORT).show();
                updateCharge = -40;
                break;
            case "10":
                Toast.makeText(getApplicationContext(), "Drain 50%!", Toast.LENGTH_SHORT).show();
                updateCharge = -50;
                break;
            case "11":
                Toast.makeText(getApplicationContext(), "Drain half your charge!", Toast.LENGTH_SHORT).show();
                updateCharge = -(currentCharge / 2);
                break;
            case "12":
                Toast.makeText(getApplicationContext(), "Steal 10% from someone!", Toast.LENGTH_SHORT).show();
                updateCharge = 10;
                break;
            case "13":
                Toast.makeText(getApplicationContext(), "Steal 20% from someone!", Toast.LENGTH_SHORT).show();
                updateCharge = 20;
                break;
            case "14":
                Toast.makeText(getApplicationContext(), "Steal 10% from everyone!", Toast.LENGTH_SHORT).show();
                updateCharge = 30;
                nearbyWrapper.send(deviceId);
                break;
            case "15":
            case "16":
                Toast.makeText(getApplicationContext(), "Drain 20% from someone!", Toast.LENGTH_SHORT).show();
                updateCharge = -20;
                break;
            case "17":
                Toast.makeText(getApplicationContext(), "Give 10% to someone!", Toast.LENGTH_SHORT).show();
                updateCharge = 10;
                break;
            case "18":
            case "19":
                Toast.makeText(getApplicationContext(), "Drain 30% from someone!", Toast.LENGTH_SHORT).show();
                updateCharge = -30;
                break;
            case "20":
                updateCharge = -20;
                Toast.makeText(getApplicationContext(), "Drain from Everyone!", Toast.LENGTH_SHORT).show();
                nearbyWrapper.send(deviceId);
                break;
        }
        updateBattery(updateCharge);
    }

    private void updateBattery(int updateCharge) {
        currentChargeChange = updateCharge;
        currentCharge += updateCharge;
        if (currentCharge <= 0) {
            currentCharge = 0;

            txtGameOver.setVisibility(View.VISIBLE);
        } else if (currentCharge > 100) {
            currentCharge = 100;
        }
        txtCurrentCharge.setText(String.format("%1$d%%", currentCharge));
        batteryView.setCurrentBattery(((float) currentCharge) / 100f);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = "drain " + Float.toString(currentChargeChange * -1);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{createMime(
                        "text/plain", text.getBytes())
                        , NdefRecord.createApplicationRecord("technology.mainthread.droidcon.hackathon")
                });

        return msg;
    }
}