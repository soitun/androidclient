package org.nuntius.android.service;

import java.util.List;

import org.nuntius.android.client.AbstractMessage;
import org.nuntius.android.client.EndpointServer;
import org.nuntius.android.client.PollingClient;

import android.content.Context;
import android.util.Log;

/**
 * The polling thread.
 * @author Daniele Ricci
 * @version 1.0
 */
public class PollingThread extends Thread {

    private final Context mContext;
    private final EndpointServer mServer;
    private String mAuthToken;
    private boolean mRunning;

    private PollingClient mClient;
    private MessageListener mListener;

    public PollingThread(Context context, EndpointServer server) {
        this.mContext = context;
        this.mServer = server;
    }

    public void setAuthToken(String token) {
        this.mAuthToken = token;
    }

    public void setMessageListener(MessageListener listener) {
        this.mListener = listener;
    }

    public void run() {
        mRunning = true;
        mClient = new PollingClient(mServer, mAuthToken);

        while(mRunning) {
            try {
                List<AbstractMessage> list = mClient.poll();
                if (list != null) {
                    Log.i("PollingThread", list.toString());

                    if (mListener != null)
                        mListener.incoming(list);
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "polling error", e);
            }
        }
    }

    /**
     * Shuts down this polling thread gracefully.
     */
    public synchronized void shutdown() {
        mRunning = false;
        try {
            join();
        }
        catch (InterruptedException e) {
            // ignored
        }
    }
}
