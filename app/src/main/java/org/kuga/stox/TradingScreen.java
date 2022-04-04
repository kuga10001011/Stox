package org.kuga.stox;

import androidx.annotation.NonNull;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import io.polygon.kotlin.sdk.rest.AggregateDTO;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import io.polygon.kotlin.sdk.websocket.DefaultPolygonWebSocketListener;
import io.polygon.kotlin.sdk.websocket.PolygonWebSocketChannel;
import io.polygon.kotlin.sdk.websocket.PolygonWebSocketClient;
import io.polygon.kotlin.sdk.websocket.PolygonWebSocketCluster;
import io.polygon.kotlin.sdk.websocket.PolygonWebSocketMessage;
import io.polygon.kotlin.sdk.websocket.PolygonWebSocketSubscription;

public class TradingScreen extends AppCompatActivity {

    private TradingCardAdapter tradingCardAdapter;
    private RecyclerView tradingCardContainer;
    private RecyclerView.LayoutManager tradingCardContainerLayoutManager;
    private final ArrayList<Trade> dataSet = new ArrayList<>();
    private final HashMap<String, Stock> stocksByName = new HashMap<>();
    private double workingCapital;
    private String incomingAPIKey;
    private String outgoingAPIKey;
    private PolygonWebSocketClient webSocketClient;
    private AlpacaAPI outgoingClient;

    // Testing Code
    private PolygonRestClient incomingClient;
    private boolean live = false;

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

        outgoingAPIKey = getIntent().getStringExtra("OUTPUT_API_KEY");
        if (live) {
            outgoingClient = new AlpacaAPI("", outgoingAPIKey, EndpointAPIType.LIVE, DataAPIType.SIP);
        }

        generateStocks(new ArrayList<>(Collections.singleton("AMD")));

        initializeWebsocket();
    }

    protected void buyStock(Stock target, long time) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        double qty = Double.parseDouble(decimalFormat.format(workingCapital / target.getOpenPrice(time)));
        if (qty > 0) {
            double price = target.getOpenPrice(time);
            if (workingCapital > price * qty) {
                Trade trade = new Trade(target, "BUY", price, qty);
                workingCapital -= price * qty;
                dataSet.add(trade);
                new OutgoingAPI(trade).start();
            }
        }
    }

    protected void sellStock(Stock target, long time) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        Double qty = target.getHeldQuantity();
        if (qty > 0) {
            double price = target.getOpenPrice(time);
            Trade trade = new Trade(target, "SELL", price, qty != null ? qty : 0.0);
            workingCapital += Double.parseDouble(decimalFormat.format(price * (qty != null ? qty : 0.0)));
            dataSet.add(trade);
            new OutgoingAPI(trade).start();
        }
    }

    protected void generateStocks(ArrayList<String> stockInput) {
        for (String name : stockInput) {
            Stock stock = new Stock(name);
            stocksByName.put(name, stock);
        }
    }

    protected void initializeWebsocket() {
        webSocketClient = new PolygonWebSocketClient(incomingAPIKey, PolygonWebSocketCluster.Stocks, new DefaultPolygonWebSocketListener() {
            @Override
            public void onReceive(@NonNull PolygonWebSocketClient polygonWebSocketClient, @NonNull PolygonWebSocketMessage webSocketMessage) {
                if (webSocketMessage instanceof PolygonWebSocketMessage.StocksMessage.Aggregate) {
                    PolygonWebSocketMessage.StocksMessage.Aggregate webSocketMessageAggregate = (PolygonWebSocketMessage.StocksMessage.Aggregate) webSocketMessage;
                    Stock stock = stocksByName.get(webSocketMessageAggregate.getTicker());
                    Long currentTime = webSocketMessageAggregate.getEndTimestampMillis();
                    if (stock != null && currentTime != null) {
                        stock.updatePrice(webSocketMessageAggregate);
                        if (Engine.toSell(stock, currentTime)) {
                            sellStock(stock, currentTime);
                        }
                        else if (Engine.toBuy(stock, currentTime)) {
                            buyStock(stock, currentTime);
                        }
                    }
                }
            }
        });

        webSocketClient.connectBlocking();
        List<PolygonWebSocketSubscription> subscriptions = new ArrayList<>();
        for (String name : stocksByName.keySet()) {
            subscriptions.add(new PolygonWebSocketSubscription(PolygonWebSocketChannel.Stocks.AggPerSecond.INSTANCE, name));
        }
        webSocketClient.subscribeBlocking(subscriptions);
    }

    public class UIManager implements Runnable {

        @Override
        public void run() {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            ((TextView) findViewById(R.id.workingCapital)).setText(numberFormat.format(workingCapital));
            tradingCardAdapter.notifyItemChanged(dataSet.size() - 1);
        }
    }

    public class OutgoingAPI extends Thread {

        private final Trade trade;
        private Double BUY_PERCENTAGE_OFFSET = 1.01;
        private Double SELL_PERCENTAGE_OFFSET = 0.99;

        public OutgoingAPI(Trade trade) {
            this.trade = trade;
        }

        @Override
        public void run() {
            try {
                Order order;
                if (live) {
                    if (trade.getAction().equals("BUY")) {
                        order = outgoingClient.orders().requestLimitOrder(trade.getName(), (int) trade.getQuantity(), OrderSide.BUY, OrderTimeInForce.DAY, trade.getPrice() * BUY_PERCENTAGE_OFFSET, false);
                    }
                    else {
                        order = outgoingClient.orders().requestLimitOrder(trade.getName(), (int) trade.getQuantity(), OrderSide.SELL, OrderTimeInForce.DAY, trade.getPrice() * SELL_PERCENTAGE_OFFSET, false);
                    }
                    sleep(5000);
                    order = outgoingClient.orders().get(order.getId(), false);
                    trade.updatePriceAndQuantity(Double.parseDouble(order.getAverageFillPrice()), Double.parseDouble(order.getFilledQuantity()));
                }
                runOnUiThread(new UIManager());
            }
            catch (AlpacaClientException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

}