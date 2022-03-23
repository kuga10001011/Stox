package org.kuga.stox;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TradingCardAdapter extends RecyclerView.Adapter<TradingCardAdapter.ViewHolder> {
    private ArrayList<Trade> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout tradingCardBase;

        public ViewHolder(View view) {
            super(view);
            tradingCardBase = view.findViewById(R.id.tradingCardBase);
        }

        public TextView getStockNameView() {
            return tradingCardBase.findViewById(R.id.stockName);
        }

        public TextView getTradeActionView() {
            return tradingCardBase.findViewById(R.id.tradeAction);
        }

        public TextView getTradeQuantityView() {
            return tradingCardBase.findViewById(R.id.tradeQuantity);
        }

        public TextView getPriceView() {
            return tradingCardBase.findViewById(R.id.price);
        }

        public ConstraintLayout getTradingCardBase() {
            return tradingCardBase;
        }
    }

    public TradingCardAdapter(ArrayList<Trade> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trading_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getStockNameView().setText(dataSet.get(position).getName());
        holder.getTradeActionView().setText(dataSet.get(position).getAction());
        if (dataSet.get(position).getAction().equals("SELL")) {
            holder.getTradingCardBase().setBackgroundColor(Color.parseColor("#FF4D000B"));
        }
        else {
            holder.getTradingCardBase().setBackgroundColor(Color.parseColor("#FF003700"));
        }
        DecimalFormat decimalFormatter = new DecimalFormat("#0.00");
        holder.getTradeQuantityView().setText(decimalFormatter.format(dataSet.get(position).getQuantity()));
        holder.getPriceView().setText(decimalFormatter.format(dataSet.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
