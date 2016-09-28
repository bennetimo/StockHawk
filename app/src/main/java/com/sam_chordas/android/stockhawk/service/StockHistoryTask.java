package com.sam_chordas.android.stockhawk.service;

import android.os.AsyncTask;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteHistory;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockHistoryTask extends AsyncTask<String, Void, QuoteHistory> {

    private final String LOG_TAG = getClass().getSimpleName();

    private Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl("https://query.yahooapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private YahooFinanceService mService = mRetrofit.create(YahooFinanceService.class);
    private String mUrlQuery;
    private Callback mCallback;

    public StockHistoryTask(String symbol, String startDate, String endDate, Callback callback) {
        mCallback = callback;
        mUrlQuery = "select * from yahoo.finance.historicaldata where symbol='" + symbol + "' and startDate='" + startDate + "' and endDate='" + endDate + "' | sort(field='date') | reverse()";

    }

    @Override
    protected void onPostExecute(QuoteHistory quoteHistory) {
        if(quoteHistory != null) {
            mCallback.onQuoteHistoryLoaded(quoteHistory);
        }
    }

    protected QuoteHistory doInBackground(String... params) {
        try {
            //Connect to Yahoo Finance DB
            Call<QuoteHistory> historyCall = mService.getHistoricalStockData(mUrlQuery);
            Response<QuoteHistory> response = historyCall.execute();

            return response.body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
            return null;
        }
    }

    public interface Callback {
        void onQuoteHistoryLoaded(QuoteHistory quoteHistory);
    }

}