package org.kuga.stox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import io.polygon.kotlin.sdk.rest.AggregateDTO;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;

public class TradingScreen extends AppCompatActivity {

    private TradingCardAdapter tradingCardAdapter;
    private RecyclerView tradingCardContainer;
    private RecyclerView.LayoutManager tradingCardContainerLayoutManager;
    private final ArrayList<Trade> dataSet = new ArrayList<>();
    private final HashMap<Stock, Double> heldStocks = new HashMap<>();
    private double workingCapital;
    private String incomingAPIKey;

    // Testing Code
    private PolygonRestClient incomingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_screen);
        tradingCardAdapter = new TradingCardAdapter(dataSet);
        tradingCardContainer = findViewById(R.id.tradingCardContainer);
        tradingCardContainer.setAdapter(tradingCardAdapter);
        tradingCardContainerLayoutManager = new LinearLayoutManager(this);
        tradingCardContainer.setLayoutManager(tradingCardContainerLayoutManager);

        workingCapital = Double.parseDouble(getIntent().getStringExtra("WORKING_CAPITAL"));
        ((TextView) findViewById(R.id.workingCapital)).setText(getIntent().getStringExtra("WORKING_CAPITAL"));
        incomingAPIKey = getIntent().getStringExtra("INPUT_API_KEY");

        // Testing Code
        /*incomingClient = new PolygonRestClient(incomingAPIKey);

        ArrayList<String> stockInput = new ArrayList<>();
        stockInput.add("AMD");
        generateStocks(stockInput);

        for (Stock stock : heldStocks.keySet()) {
            queryPolygon(stock);
            updateData(stock);
        }*/

        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            String name = "stock" + i;
            Stock stock = new Stock(name);
            heldStocks.put(stock, random.nextDouble());
            updateData(stock);
        }

    }

    // Testing Code
    protected void queryPolygon(Stock target) {
        AggregatesDTO agg = incomingClient.getAggregatesBlocking(new AggregatesParameters("AMD", 1, "minute", "2022-03-21", "2022-03-21", false, 50000));
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("New York"));
        for (int i = 0; i < agg.getResults().size(); i++) {
            AggregateDTO currentAgg = agg.getResults().get(i);
            if (currentAgg.getTimestampMillis() != null) {
                currentDate.setTimeInMillis(currentAgg.getTimestampMillis());
            }
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);
            long keyLong = currentDate.getTimeInMillis();
            if (currentAgg.getOpen() != null) {
                target.setOpenPrice(keyLong, currentAgg.getOpen());
            }
        }
    }

    protected void updateData(Stock target) {
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("New York"));
        currentDate.clear();
        currentDate.set(2022, 2, 21, 13, 30);
        long startLong = currentDate.getTimeInMillis();
        Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("New York"));
        endDate.clear();
        endDate.set(2022, 2, 21, 20, 0);
        long endLong = endDate.getTimeInMillis();
        while (startLong < endLong) {
            int decision = (int) Math.round(Math.random());
            if (decision == 1) {
                buyStock(target, startLong);
            } else {
                sellStock(target, startLong);
            }
            startLong += 1000 * 60 * 60;
        }
    }

    protected void buyStock(Stock target, long time) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        double qty = Double.parseDouble(decimalFormat.format(workingCapital / target.getOpenPrice(time)));
        double price = target.getOpenPrice(time);
        if (workingCapital > price * qty) {
            Double prevQty = heldStocks.getOrDefault(target, 0.0);
            heldStocks.put(target, prevQty != null ? prevQty : 0.0 + qty);
            Trade trade = new Trade(target, "BUY", price, qty);
            workingCapital -= price * qty;
            dataSet.add(trade);
            ((TextView) findViewById(R.id.workingCapital)).setText(String.valueOf(workingCapital));
        }
    }

    protected void sellStock(Stock target, long time) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        Double qty = heldStocks.getOrDefault(target, 0.0);
        double price = target.getOpenPrice(time);
        Trade trade = new Trade(target, "SELL", price, qty != null ? qty : 0.0);
        workingCapital += Double.parseDouble(decimalFormat.format(price * (qty != null ? qty : 0.0)));
        dataSet.add(trade);
        ((TextView) findViewById(R.id.workingCapital)).setText(String.valueOf(workingCapital));

    }

    protected void generateStocks(ArrayList<String> stockInput) {
        for (String name : stockInput) {
            heldStocks.put(new Stock(name), 0.0);
        }
    }
}