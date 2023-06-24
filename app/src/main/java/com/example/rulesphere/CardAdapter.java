package com.example.rulesphere;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

        ConstraintLayout constraintLayout = (ConstraintLayout) holder.materialCardView.getParent();
        int padding;
        if (position == getItemCount() - 1) {
            padding = (int) Math.floor(120 * mainActivity.getResources().getDisplayMetrics().density);
        } else {
            padding = (int) Math.floor(20 * mainActivity.getResources().getDisplayMetrics().density);
        }
        constraintLayout.setPadding(0, 0, 0, padding);

        if(quote.isFavorite) {
            holder.favoriteButton.setChecked(true);
            holder.favoriteButton.setBackground(null);
            holder.materialCardView.setCheckedIcon(null);
            holder.materialCardView.setChecked(true);
            holder.materialCardView.setCardForegroundColor(null);

//            if (recyclerViewId == R.id.recycler_view_search) {
//                holder.materialCardView.setStrokeWidth(3);
//            }
            holder.materialCardView.setStrokeWidth(3);
        } else {
            holder.favoriteButton.setChecked(false);
            holder.materialCardView.setChecked(false);

//            if (recyclerViewId == R.id.recycler_view_search) {
//                holder.materialCardView.setStrokeWidth(0);
//            }
            holder.materialCardView.setStrokeWidth(0);
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

        holder.materialCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ViewGroup parentView = (ViewGroup) mainActivity.findViewById(R.id.appBarLayout).getParent();
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View searchContext = inflater.inflate(R.layout.search_context, parentView, false);

                TextView author = view.findViewById(R.id.author);
                TextView quote = view.findViewById(R.id.quote);
                searchContext.setElevation(20f);
                TextView authorContext = searchContext.findViewById(R.id.author);
                TextView quoteContext = searchContext.findViewById(R.id.quote);
                authorContext.setText(author.getText());
                quoteContext.setText(quote.getText());

                parentView.addView(searchContext);
                mainActivity.getWindow().setStatusBarColor(Color.parseColor("#07090B"));
                //setStatusBarColor(Color.parseColor("#07090B"));

                searchContext.findViewById(R.id.imageView).setOnClickListener(v -> {
                    parentView.removeView(searchContext);

                    if (mainActivity.isNightMode) {
                        mainActivity.getWindow().setStatusBarColor(view.getContext().getColor(R.color.md_theme_dark_searchViewInputBackground));
                        //setStatusBarColor(view.getContext().getColor(R.color.md_theme_dark_searchViewInputBackground));
                    } else {
                        mainActivity.getWindow().setStatusBarColor(view.getContext().getColor(R.color.md_theme_light_searchViewInputBackground));
                        //setStatusBarColor(view.getContext().getColor(R.color.md_theme_light_searchViewInputBackground));
                    }
                });

                searchContext.findViewById(R.id.useQuoteContext).setOnClickListener(v -> {
                    //SharedPreferencesManager.clearSharedPreferences();
                    SharedPreferencesManager.putQuote((String) quote.getText());
                    SharedPreferencesManager.putAuthor((String) author.getText());

                    searchContext.findViewById(R.id.imageView).performClick();

                    new Handler().postDelayed(() -> {
                        //mainActivity.binding.bottomNavigation.setSelectedItemId(R.id.design);
                        mainActivity.findViewById(R.id.design).performClick();
                    }, 100);

                    setStatusBarColor(view.getContext().getColor(R.color.md_theme_dark_background));
                });

                //showPopupMenu(searchContext.findViewById(R.id.ruleCard), parentView, searchContext);
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
        } else {
            viewToAnimate.clearAnimation();
        }
    }

//    private void showPopupMenu(View view, ViewGroup topParentView, View searchContext) {
//        PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.START, 0, R.style.PopupMenuStyle);
//        popupMenu.inflate(R.menu.search_popup_menu);
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                if (menuItem.getItemId() == R.id.useInDesignPopup) {
//                    SharedPreferencesManager.init(view.getContext());
//                    Set<String> stringSet = new HashSet<>();
//                    TextView author = view.findViewById(R.id.author);
//                    TextView quote = view.findViewById(R.id.quote);
//
//                    stringSet.add((String) author.getText());
//                    stringSet.add((String) quote.getText());
//
//                    SharedPreferencesManager.putStringSet("quoteForDesign", stringSet);
//
////                    new Handler().postDelayed(() -> {
////                        mainActivity.binding.bottomNavigation.setSelectedItemId(R.id.design);
////                    }, 100);
//                    return true;
//                } else if (menuItem.getItemId() == R.id.shareRulePopup) {
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu popupMenu) {
//                topParentView.removeView(searchContext);
//
//                if (mainActivity.isNightMode) {
//                    setStatusBarColor(view.getContext().getColor(R.color.md_theme_dark_searchViewInputBackground));
//                } else {
//                    setStatusBarColor(view.getContext().getColor(R.color.md_theme_light_searchViewInputBackground));
//                }
//            }
//        });
//
//        popupMenu.show();
//    }

    private ValueAnimator colorAnimation;
    public void setStatusBarColor(int color) {
        Window window = mainActivity.getWindow();
        colorAnimation = ValueAnimator.ofArgb(window.getStatusBarColor(), color);
        colorAnimation.setDuration(250);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (int) animator.getAnimatedValue();
                window.setStatusBarColor(color);
            }
        });

        colorAnimation.start();
    }
    public void cancelStatusBarColorAnimation() {
        if (colorAnimation != null && colorAnimation.isRunning()) {
            colorAnimation.cancel();
        }
    }
}