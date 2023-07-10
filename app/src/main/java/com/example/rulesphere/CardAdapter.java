package com.example.rulesphere;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private int lastPosition = -1;
    private MainActivity mainActivity;
    private int recyclerViewId;

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

    public CardAdapter(List<Quote> q, MainActivity mainActivity, int id) {
        quotes = q;
        this.mainActivity = mainActivity;
        recyclerViewId = id;
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

            holder.materialCardView.setStrokeWidth(5);
        } else {
            holder.favoriteButton.setChecked(false);
            holder.materialCardView.setChecked(false);

            holder.materialCardView.setStrokeWidth(0);
        }

        RecyclerView rv = mainActivity.findViewById(R.id.recycler_view_myRules);
        MaterialButton myRulesFavorites = mainActivity.findViewById(R.id.myRulesFavorites);

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialButton favoriteButton = (MaterialButton) view;

                //ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
                MaterialCardView materialCardView = holder.materialCardView;
                TextView ruleId = holder.id;

                mainActivity.favoriteAQuote(ruleId.getText().toString());
                materialCardView.toggle();
                materialCardView.setCardForegroundColor(null);

                if (materialCardView.isChecked()) {
                    materialCardView.setStrokeWidth(5);
                } else {
                    materialCardView.setStrokeWidth(0);
                }

                try {
                    if (rv != null && myRulesFavorites.isChecked()) {
                        int position = holder.getAdapterPosition();
                        if (materialCardView.isChecked()) {
                            mainActivity.call_updateFavoritesPersonal(true, position, true);
                        } else {
                            mainActivity.call_updateFavoritesPersonal(true, position, false);
                        }
                    }
                } catch (NullPointerException ignored) {}

                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.like_anim);
                favoriteButton.startAnimation(animation);
            }
        });

        holder.materialCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ViewGroup parentView = (ViewGroup) mainActivity.findViewById(R.id.appBarLayout).getParent();
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View searchContext = inflater.inflate(R.layout.search_context, parentView, false);

                TextView author = view.findViewById(R.id.author);
                TextView quote = view.findViewById(R.id.quote);
                TextView quoteId = view.findViewById(R.id.quote_id);
                searchContext.setElevation(20f);
                TextView authorContext = searchContext.findViewById(R.id.author);
                TextView quoteContext = searchContext.findViewById(R.id.quote);
                TextView quoteIdContext = searchContext.findViewById(R.id.quote_id);
                authorContext.setText(author.getText());
                quoteContext.setText(quote.getText());
                quoteIdContext.setText(quoteId.getText());

                if (recyclerViewId != R.id.recycler_view_myRules) {
                    searchContext.findViewById(R.id.deleteQuoteContext).setVisibility(View.GONE);
                } else {
                    MaterialButton myRulesFavorites = mainActivity.findViewById(R.id.myRulesFavorites);

                    if (myRulesFavorites.isChecked()) {
                        searchContext.findViewById(R.id.deleteQuoteContext).setVisibility(View.GONE);
                    }
                }

                parentView.addView(searchContext);
                mainActivity.getWindow().setStatusBarColor(Color.parseColor("#060606"));
                //setStatusBarColor(Color.parseColor("#07090B"));

                searchContext.findViewById(R.id.imageView).setOnClickListener(v -> {
                    parentView.removeView(searchContext);

                    int color = mainActivity.getColorFromResource(mainActivity, android.R.attr.colorBackground);
                    mainActivity.getWindow().setStatusBarColor(color);
                });

                searchContext.findViewById(R.id.useQuoteContext).setOnClickListener(v -> {
                    //SharedPreferencesManager.clearSharedPreferences();
                    SharedPreferencesManager.putQuote((String) quote.getText());
                    SharedPreferencesManager.putAuthor((String) author.getText());

                    searchContext.findViewById(R.id.imageView).performClick();

                    new Handler().postDelayed(() -> {
                        mainActivity.findViewById(R.id.design).performClick();
                    }, 50);
                });

                searchContext.findViewById(R.id.deleteQuoteContext).setOnClickListener(v -> {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            QuoteDao quoteDao = getDb().quoteDao();
                            Quote quote = quoteDao.getQuote((String) quoteIdContext.getText());
                            quoteDao.delete(quote);
                        }
                    });

                    int position = holder.getAdapterPosition();
                    mainActivity.call_updateFavoritesPersonal(false, position, false);
                    parentView.removeView(searchContext);
                });
                return true;
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
                        if (rv != null && myRulesFavorites.isChecked()) {
                            int position = holder.getAdapterPosition();
                            if (materialCardView.isChecked()) {
                                mainActivity.call_updateFavoritesPersonal(true, position, true);
                            } else {
                                mainActivity.call_updateFavoritesPersonal(true, position, false);
                            }
                        }
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
        } else {
            viewToAnimate.clearAnimation();
        }
    }

    public RuleSphereDatabase getDb() {
        return RuleSphereDatabase.getInstance(mainActivity);
    }

    public void setQuotes(List<Quote> q) {
        int previousSize = quotes.size();
        quotes = q;
        lastPosition = -1; // So that animations start again
    }
}