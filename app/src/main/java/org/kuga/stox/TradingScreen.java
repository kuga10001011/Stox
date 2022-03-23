package org.kuga.stox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    private final HashMap<Stock, Integer> heldStocks = new HashMap<>();
    private double workingCapital;
    private double initialCapital;
    private String incomingAPIKey;

    // Testing Code
    private PolygonRestClient incomingClient;
    private boolean live = false;

    private AlpacaAPI outgoingClient;

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
        initialCapital = Double.parseDouble(getIntent().getStringExtra("WORKING_CAPITAL"));
        ((TextView) findViewById(R.id.workingCapital)).setText(getIntent().getStringExtra("WORKING_CAPITAL"));
        incomingAPIKey = getIntent().getStringExtra("INPUT_API_KEY");

        if (live) {
            String keyID = "";
            String secretKey = "";
            outgoingClient = new AlpacaAPI(keyID, secretKey, EndpointAPIType.LIVE, DataAPIType.SIP);
        }

        // Testing Code
        incomingClient = new PolygonRestClient(incomingAPIKey);

        ArrayList<String> stockInput = new ArrayList<>();
        stockInput.add("AMD");
        generateStocks(stockInput);

        for (Stock stock : heldStocks.keySet()) {
            queryPolygon(stock);
            updateData(stock);
        }

    }

    // Testing Code
    protected void queryPolygon(Stock target) {
        AggregatesDTO agg = incomingClient.getAggregatesBlocking(new AggregatesParameters("AMD", 1, "minute", "2022-03-21", "2022-03-21", false, 50000));
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("New York"));
        for (int i = 0; i < agg.getResults().size(); i++) {
            AggregateDTO currentAgg = agg.getResults().get(i);
            currentDate.setTimeInMillis(currentAgg.getTimestampMillis());
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);
            long keyLong = currentDate.getTimeInMillis();
            target.setOpenPrice(keyLong, currentAgg.getOpen());
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
            if (decision == 1 && heldStocks.get(target) < .0001) {
                buyStock(target, startLong);
            } else if (decision == 1 && heldStocks.get(target) > .0001) {
                sellStock(target, startLong);
            }
            startLong += 1000 * 60;
        }
        sellStock(target, endLong);
    }

    protected boolean buyStock(Stock target, long time) {
        int qty = (int) (workingCapital / target.getOpenPrice(time));
        double price = target.getOpenPrice(time);
        if (workingCapital > price * qty) {
            try {
                Order buyOrder;
                if (live) {
                    buyOrder = outgoingClient.orders().requestLimitOrder(target.getName(), qty, OrderSide.BUY, OrderTimeInForce.CLS, price * 1.001, false);
                    price = Double.parseDouble(buyOrder.getAverageFillPrice());
                } else {
                    buyOrder = null;
                }
                Trade trade = new Trade(target, "BUY", price, qty, buyOrder);
                heldStocks.put(target, heldStocks.getOrDefault(target, 0) + qty);
                workingCapital -= price * qty;
                dataSet.add(trade);
                ((TextView) findViewById(R.id.workingCapital)).setText(String.valueOf(workingCapital));
                tradingCardContainer.smoothScrollToPosition(tradingCardAdapter.getItemCount());
                return true;
            } catch (AlpacaClientException e) {
                return false;
            }
        }
        return false;
    }

    protected boolean sellStock(Stock target, long time) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        int qty = heldStocks.getOrDefault(target, 0);
        if (qty > 0) {
            try {
                double price = target.getOpenPrice(time);
                Order sellOrder;
                if (live) {
                    sellOrder = outgoingClient.orders().requestLimitOrder(target.getName(), qty, OrderSide.SELL, OrderTimeInForce.CLS, price * .999, false);
                } else {
                    sellOrder = null;
                }
                heldStocks.put(target, 0);
                Trade trade = new Trade(target, "SELL", price, qty, sellOrder);
                workingCapital += Double.parseDouble(decimalFormat.format(price * qty));
                dataSet.add(trade);
                ((TextView) findViewById(R.id.workingCapital)).setText(String.valueOf(workingCapital));
                ((TextView) findViewById(R.id.capitalGain)).setText(String.valueOf(workingCapital - initialCapital));
                tradingCardContainer.smoothScrollToPosition(tradingCardAdapter.getItemCount());
                return true;
            } catch (AlpacaClientException e) {
                return false;
            }
        }
        return false;
    }

    protected void generateStocks(ArrayList<String> stockInput) {
        for (String name : stockInput) {
            heldStocks.put(new Stock(name), 0);
        }
    }
}