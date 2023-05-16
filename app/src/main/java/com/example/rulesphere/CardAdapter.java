package com.example.rulesphere;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private int lastPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView quote;

        public ViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.author);
            quote = (TextView) itemView.findViewById(R.id.quote);
        }
    }

    private List<Quote> quotes;

    public CardAdapter(List<Quote> q) {
        quotes = q;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.quote_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int position) {
        Quote quote = quotes.get(position);

        holder.author.setText(quote.author);
        holder.quote.setText(quote.quote);

        if(quote.quote.contains("a")) {

        } else {
            // mora bit jer recycler view ne vraca na normalno ako nije istina
        }

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.recycler_item_anim);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}