package com.sam_chordas.android.stockhawk.service;

import com.sam_chordas.android.stockhawk.data.QuoteHistory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YahooFinanceService {

    @GET("/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<QuoteHistory> getHistoricalStockData(@Query("q") String query);

}
