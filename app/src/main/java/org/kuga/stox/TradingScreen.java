package org.kuga.stox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class TradingScreen extends AppCompatActivity {

    private TradingCardAdapter tradingCardAdapter;
    private RecyclerView tradingCardContainer;
    private RecyclerView.LayoutManager tradingCardContainerLayoutManager;
    private ArrayList<Trade> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_screen);
        tradingCardAdapter = new TradingCardAdapter(dataSet);
        tradingCardContainer = (RecyclerView) findViewById(R.id.tradingCardContainer);
        tradingCardContainer.setAdapter(tradingCardAdapter);
        tradingCardContainerLayoutManager = new LinearLayoutManager(this);
        tradingCardContainer.setLayoutManager(tradingCardContainerLayoutManager);
        updateData();
    }

    protected void updateData() {
        Trade testTrade0 = new Trade("testTrade0", "SELL", 23431.123423, 0.772342);
        Trade testTrade1 = new Trade("testTrade1", "BUY", 4251.2312, 0.1324);
        dataSet.add(testTrade0);
        dataSet.add(testTrade1);
    }

}