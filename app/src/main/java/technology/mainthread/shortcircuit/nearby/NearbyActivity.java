package technology.mainthread.shortcircuit.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import technology.mainthread.shortcircuit.R;
import timber.log.Timber;

public class NearbyActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private NearbyAdapter adapter;
    private String deviceId;
    private NearbyWrapper nearbyWrapper;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NearbyAdapter();
        mRecyclerView.setAdapter(adapter);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Timber.d("deviceId: %s", deviceId);

        gson = new Gson();

        nearbyWrapper = new NearbyWrapper(this, new MessageListener() {
            @Override
            public void onFound(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String messageString = new String(message.getContent());
                        NearbyItem nearbyItem = gson.fromJson(messageString, NearbyItem.class);

                        if (nearbyItem.getTimestamp() < System.currentTimeMillis() + 1000) {
                            adapter.addItem(nearbyItem);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
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

    @OnClick(R.id.btn_send)
    void onSendClick() {
        nearbyWrapper.send(deviceId);
    }

}
