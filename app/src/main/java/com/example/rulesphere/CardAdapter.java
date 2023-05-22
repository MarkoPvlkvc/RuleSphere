package com.example.rulesphere;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private int lastPosition = -1;
    private MainActivity mainActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView quote;
        public TextView id;
        public MaterialButton favoriteButton;
        public MaterialCardView materialCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.author);
            quote = (TextView) itemView.findViewById(R.id.quote);
            id = (TextView) itemView.findViewById(R.id.quote_id);
            favoriteButton = (MaterialButton) itemView.findViewById(R.id.favoriteButton);
            materialCardView = (MaterialCardView) itemView.findViewById(R.id.ruleCard);
        }
    }

    private List<Quote> quotes;

    public CardAdapter(List<Quote> q, MainActivity mainActivity) {
        quotes = q;
        this.mainActivity = mainActivity;
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
        String quoteId = quote.id + "";
        holder.id.setText(quoteId);

        if(quote.isFavorite) {
            holder.favoriteButton.setChecked(true);
            holder.favoriteButton.setBackground(null);
            holder.materialCardView.setCheckedIcon(null);
            holder.materialCardView.setChecked(true);
            holder.materialCardView.setCardForegroundColor(null);
        } else {
            holder.favoriteButton.setChecked(false);
            holder.materialCardView.setChecked(false);
        }

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialButton favoriteButton = (MaterialButton) view;

                ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
                MaterialCardView materialCardView = (MaterialCardView) constraintLayout.getParent();
                TextView ruleId = (TextView) constraintLayout.findViewById(R.id.quote_id);

                mainActivity.favoriteAQuote(ruleId.getText().toString());

                materialCardView.setCheckedIcon(null);
                materialCardView.toggle();
                favoriteButton.setBackground(null);
                materialCardView.setCardForegroundColor(null);

                if (materialCardView.isChecked()) {
                    materialCardView.setStrokeWidth(5);
                } else {
                    materialCardView.setStrokeWidth(0);
                }

                try {
                    RecyclerView rv = mainActivity.findViewById(R.id.recycler_view_myRules);

                    if (rv != null)
                        mainActivity.updateMyRulesList(rv);
                } catch (NullPointerException ignored) {}

                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
                favoriteButton.startAnimation(animation);
            }
        });

        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            boolean isDoubleClick = false;
            Handler handler = new Handler();
            final int DOUBLE_CLICK_DELAY = 300;
            @Override
            public void onClick(View view) {
                MaterialCardView materialCardView = (MaterialCardView) view;
                MaterialButton favoriteButton = materialCardView.findViewById(R.id.favoriteButton);
                TextView ruleId = (TextView) materialCardView.findViewById(R.id.quote_id);

                materialCardView.setCheckedIcon(null);

                if (!isDoubleClick) {
                    isDoubleClick = true;
                    handler.postDelayed(() -> {
                        isDoubleClick = false;
                    }, DOUBLE_CLICK_DELAY);
                } else {
                    materialCardView.toggle();

                    favoriteButton.setChecked(materialCardView.isChecked());
                    favoriteButton.setBackground(null);
                    materialCardView.setCardForegroundColor(null);

                    if (materialCardView.isChecked()) {
                        materialCardView.setStrokeWidth(5);
                    } else {
                        materialCardView.setStrokeWidth(0);
                    }

                    mainActivity.favoriteAQuote(ruleId.getText().toString());

                    try {
                        RecyclerView rv = mainActivity.findViewById(R.id.recycler_view_myRules);

                        if (rv != null)
                            mainActivity.updateMyRulesList(rv);
                    } catch (NullPointerException ignored) {}

                    Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
                    favoriteButton.startAnimation(animation);
                }
            }
        });

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