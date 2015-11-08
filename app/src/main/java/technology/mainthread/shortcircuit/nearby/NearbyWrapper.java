package technology.mainthread.shortcircuit.nearby;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.UUID;

import timber.log.Timber;

public class NearbyWrapper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_RESOLVE_ERROR = 20;

    private final GoogleApiClient mGoogleApiClient;
    private final Activity activity;
    private final MessageListener messageListener;
    private final Handler handler;

    private boolean mResolvingError;

    public NearbyWrapper(Activity activity, MessageListener messageListener) {
        this.activity = activity;
        this.messageListener = messageListener;
        this.handler = new Handler();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void onStart() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            // Clean up when the user leaves the activity.
            Nearby.Messages.unsubscribe(mGoogleApiClient, messageListener)
                    .setResultCallback(new ErrorCheckingCallback("unsubscribe()"));
        }
        mGoogleApiClient.disconnect();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // Permission granted or error resolved successfully then we proceed
                // with publish and subscribe..
                subscribe();
            } else {
                // This may mean that user had rejected to grant nearby permission.
                Timber.e("Failed to resolve error with code " + resultCode);
            }
        }
    }

    // Callbacks
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
                    @Override
                    public void run() {
                        subscribe();
                    }
                })
        );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.e("onConnectionFailed %s", connectionResult);
    }

    private void subscribe() {
        Nearby.Messages.subscribe(mGoogleApiClient, messageListener)
                .setResultCallback(new ErrorCheckingCallback("subscribe()"));
    }

    public void send(String deviceId) {
        UUID uuid = UUID.randomUUID();

        String stringMessage = "{\"id\":\"" + uuid + "\", \"device\":\"" + deviceId + "\", \"timestamp\":" + System.currentTimeMillis() + "}";

        final Message message = new Message(stringMessage.getBytes());
        Nearby.Messages.publish(mGoogleApiClient, message);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Nearby.Messages.unpublish(mGoogleApiClient, message);
            }
        }, 10000);
    }

    /**
     * A simple ResultCallback that displays a toast when errors occur.
     * It also displays the Nearby opt-in dialog when necessary.
     */
    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final String method;
        private final Runnable runOnSuccess;

        private ErrorCheckingCallback(String method) {
            this(method, null);
        }

        private ErrorCheckingCallback(String method, @Nullable Runnable runOnSuccess) {
            this.method = method;
            this.runOnSuccess = runOnSuccess;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Timber.i(method + " succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            status.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
                            mResolvingError = true;
                        } catch (IntentSender.SendIntentException e) {
                            Timber.i(method + " failed with exception: " + e);
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.  So having a toast while
                        // resolving dialog is in progress is confusing, so just log it.
                        Timber.i(method + " failed with status: " + status
                                + " while resolving error.");
                    }
                } else {
                    Timber.i(method + " failed with : " + status
                            + " resolving error: " + mResolvingError);
                }
            }
        }
    }
}
