package org.kuga.stox;

import net.jacobpeterson.alpaca.model.endpoint.orders.Order;

public class Trade {

    private Stock stock;
    private String action;
    private double price;
    private double quantity;
    private Order order;

    public Trade(Stock stock, String action, double price, double quantity, Order order) {
        this.stock = stock;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
        this.stock.setAcquisitionTrade(this);
        this.order = order;
    }

    public String getName() {
        return stock.getName();
    }

    public String getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }
}
