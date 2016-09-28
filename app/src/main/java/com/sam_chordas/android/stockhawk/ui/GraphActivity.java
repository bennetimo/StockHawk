package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteHistory;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockHistoryTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, StockHistoryTask.Callback {

    static final String STOCK_ID = "stockID";
    private static final int CURSOR_LOADER_ID = 1;
    private int mStockID;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        // Restore value of members from saved state
        mStockID = getIntent().getIntExtra(STOCK_ID, -1);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE, QuoteColumns.BIDPRICE, QuoteColumns.ISUP,},
                QuoteColumns._ID + " = " + mStockID,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_YEAR, -30);
            Date lastWeek = calendar.getTime();

            String startDate = dateFormat.format(lastWeek);
            String endDate = dateFormat.format(now);

            new StockHistoryTask(symbol, startDate, endDate, this).execute();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onQuoteHistoryLoaded(QuoteHistory quoteHistory) {
        LineChart chart = (LineChart) findViewById(R.id.linechart);
        chart.setBackgroundColor(getResources().getColor(R.color.chart_background));
        chart.setDrawGridBackground(false);
        chart.setDescription("");

        List<Quote> quotes = quoteHistory.query.result.quotes;
        chart.setKeepPositionOnRotation(true);

        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < quotes.size(); i++) {
            Quote quote = quotes.get(i);
            Entry e = new Entry(i, Float.valueOf(quote.close));
            entries.add(e);
        }
        LineDataSet dataSet = new LineDataSet(entries, "Close Price");
        dataSet.setColor(getResources().getColor(R.color.chart_line_background));
        dataSet.setValueTextColor(getResources().getColor(R.color.chart_value_text));
        dataSet.setValueTextSize(7f);
        dataSet.setLineWidth(5.0f);
        dataSet.setCircleColor(getResources().getColor(R.color.chart_value_colour));

        YAxis axisRight = chart.getAxisRight();
        axisRight.setEnabled(false);

        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setTextColor(getResources().getColor(R.color.chart_axis_labels));
        axisLeft.setTextSize(12f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(quotes));
        xAxis.setLabelCount(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(getResources().getColor(R.color.chart_axis_labels));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(90.0f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.animateY(800);

    }

    public class MyXAxisValueFormatter implements AxisValueFormatter {

        private List<Quote> mValues;

        public MyXAxisValueFormatter(List<Quote> quotes) {
            this.mValues = quotes;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues.get((int) value).date;
        }

        @Override
        public int getDecimalDigits() { return 0; }
    }
}
