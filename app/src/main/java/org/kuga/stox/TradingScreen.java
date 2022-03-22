package org.kuga.stox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

public class TradingScreen extends AppCompatActivity {

    private TradingCardAdapter tradingCardAdapter;
    private RecyclerView tradingCardContainer;
    private RecyclerView.LayoutManager tradingCardContainerLayoutManager;
    private final ArrayList<Trade> dataSet = new ArrayList<>();
    private final HashMap<Stock, Double> heldStocks = new HashMap<>();
    private double workingCapital;

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

        ArrayList<String> stockInput = new ArrayList<>();
        stockInput.add("AMD");
        generateStocks(stockInput);
        updateData();
    }

    protected void updateData() {
        Trade testTrade0 = new Trade("testTrade0", "SELL", 23431.123423, 0.772342);
        Trade testTrade1 = new Trade("testTrade1", "BUY", 4251.2312, 0.1324);
        dataSet.add(testTrade0);
        dataSet.add(testTrade1);
    }

    protected void generateStocks(ArrayList<String> stockInput) {
        for (String name : stockInput) {
            heldStocks.put(new Stock(name), 0.0);
        }
    }
}