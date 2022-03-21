package org.kuga.stox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TradingCardAdapter extends RecyclerView.Adapter<TradingCardAdapter.ViewHolder> {
    private ArrayList<String> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout tradingCardBase;
        public ViewHolder(View view) {
            super(view);
            tradingCardBase = view.findViewById(R.id.tradingCardBase);
        }
    }

    public TradingCardAdapter(ArrayList<String> dataSet) {
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

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
