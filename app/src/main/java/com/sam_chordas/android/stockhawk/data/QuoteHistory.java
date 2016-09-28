package com.sam_chordas.android.stockhawk.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class QuoteHistory {

   public Query query;

    public class Query {
        public int count;
        @SerializedName("results")
        public Results result;
    }

    public class Results {
        @SerializedName("quote")
        public List<Quote> quotes = new ArrayList<Quote>();
    }

}
