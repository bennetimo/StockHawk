package com.sam_chordas.android.stockhawk.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

// Load data if we find an internet connection
public class NetworkManager extends BroadcastReceiver {

    private boolean mFirstLoad = true;
    private Context mContext;

    public NetworkManager(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        retrieveInitialStocks(context);
    }

    // If the user started the app with no connection and now we have detected one, load the initial stocks
    public void retrieveInitialStocks(Context context){
        if (mFirstLoad && Utils.hasNetwork(context)) {
            mFirstLoad = false;
            Intent serviceIntent = createServiceIntent();
            serviceIntent.putExtra("tag", "init");
            context.startService(serviceIntent);
        }
    }

    public void networkUnavailableToast(){
        Toast.makeText(mContext, mContext.getString(R.string.error_network_unavailable), Toast.LENGTH_SHORT).show();
    }

    public Intent createServiceIntent(){
        return new Intent(mContext, StockIntentService.class);
    }

}