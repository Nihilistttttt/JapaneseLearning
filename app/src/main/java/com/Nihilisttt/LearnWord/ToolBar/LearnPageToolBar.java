package com.Nihilisttt.LearnWord.ToolBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.google.android.material.card.MaterialCardView;

public class LearnPageToolBar extends androidx.appcompat.widget.Toolbar {

    private static final int POPUP_WIDTH_DP = 300;
    private static final int[] WORD_LEVELS = {4, 5, 6};
    private static final int[] SUB_LEVELS = {1, 2, 3};

    private ImageButton back_button;
    private TextView count_textView;
    private ImageButton font_size_button;
    private ImageButton delete_button;
    private LearnPageStateViewModel stateViewModel;

    private PopupWindow fontSizePopup;
    private TextView[] wordButtons;
    private TextView[] subButtons;

    public LearnPageToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        inflate(context, R.layout.learn_word_tool_bar, this);
        setContentInsetsAbsolute(0, 0);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> ((Activity) context).finish());
        count_textView = findViewById(R.id.count_text);
        font_size_button = findViewById(R.id.font_size_button);
        delete_button = findViewById(R.id.delete_button);
    }

    public void setStateViewModel(LearnPageStateViewModel stateViewModel) {
        this.stateViewModel = stateViewModel;
        if (font_size_button != null) {
            font_size_button.setOnClickListener(v -> showFontSizePopup());
        }
    }

    public void observeFontSize(LifecycleOwner owner) {
        if (stateViewModel != null) {
            stateViewModel.getWordFontLevel().observe(owner, level -> {
                if (wordButtons != null) updateButtonStates(wordButtons, WORD_LEVELS, level);
            });
            stateViewModel.getSubFontLevel().observe(owner, level -> {
                if (subButtons != null) updateButtonStates(subButtons, SUB_LEVELS, level);
            });
        }
    }

    private void showFontSizePopup() {
        if (stateViewModel == null) return;
        Context context = getContext();
        float density = context.getResources().getDisplayMetrics().density;

        if (fontSizePopup != null && fontSizePopup.isShowing()) {
            fontSizePopup.dismiss();
            return;
        }

        fontSizePopup = new PopupWindow(context);
        fontSizePopup.setOutsideTouchable(true);
        fontSizePopup.setElevation(16);
        fontSizePopup.setFocusable(true);
        fontSizePopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialCardView cardView = new MaterialCardView(context);
        cardView.setRadius(16);
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_card_background));
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * density);
        container.setPadding(padding, padding, padding, padding);

        int wordLevel = stateViewModel.getWordFontLevel().getValue() != null
                ? stateViewModel.getWordFontLevel().getValue() : Constants.FONT_SIZE_NORMAL;
        int subLevel = stateViewModel.getSubFontLevel().getValue() != null
                ? stateViewModel.getSubFontLevel().getValue() : Constants.FONT_SIZE_NORMAL;

        wordButtons = new TextView[WORD_LEVELS.length];
        subButtons = new TextView[SUB_LEVELS.length];

        container.addView(createSizeSelector(context, "词头大小", wordLevel, WORD_LEVELS, wordButtons, level -> stateViewModel.setWordFontLevel(level)));

        LinearLayout divider = new LinearLayout(context);
        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.md_detail_label));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (1 * density));
        int dividerMargin = (int) (8 * density);
        dividerParams.topMargin = dividerMargin;
        dividerParams.bottomMargin = dividerMargin;
        divider.setLayoutParams(dividerParams);
        container.addView(divider);

        container.addView(createSizeSelector(context, "其他大小", subLevel, SUB_LEVELS, subButtons, level -> stateViewModel.setSubFontLevel(level)));

        cardView.addView(container);
        fontSizePopup.setContentView(cardView);
        fontSizePopup.setWidth((int) (POPUP_WIDTH_DP * density));
        fontSizePopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        fontSizePopup.setOnDismissListener(() -> {
            wordButtons = null;
            subButtons = null;
        });

        fontSizePopup.showAsDropDown(font_size_button, 0, (int) (8 * density));
    }

    private View createSizeSelector(Context context, String label, int currentLevel,
                                    int[] levels, TextView[] buttons, OnLevelChangedListener listener) {
        float density = context.getResources().getDisplayMetrics().density;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView labelView = new TextView(context);
        labelView.setText(label);
        labelView.setTextColor(ContextCompat.getColor(context, R.color.md_detail_label));
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.bottomMargin = (int) (8 * density);
        labelView.setLayoutParams(labelParams);
        layout.addView(labelView);

        LinearLayout buttonRow = new LinearLayout(context);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setGravity(Gravity.CENTER_VERTICAL);

        int activeColor = ContextCompat.getColor(context, R.color.md_primary);
        int inactiveColor = ContextCompat.getColor(context, R.color.md_card_background);
        int textColor = ContextCompat.getColor(context, R.color.md_on_surface);
        int activeTextColor = ContextCompat.getColor(context, R.color.md_on_primary);

        int buttonSize = (int) (36 * density);
        int buttonMargin = (int) (3 * density);

        for (int i = 0; i < levels.length; i++) {
            int level = levels[i];
            TextView btn = new TextView(context);
            btn.setText(String.valueOf(i + 1));
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            applyButtonStyle(btn, level == currentLevel, activeColor, inactiveColor, activeTextColor, textColor, density);

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, buttonSize, 1f);
            btnParams.leftMargin = buttonMargin;
            btnParams.rightMargin = buttonMargin;
            btn.setLayoutParams(btnParams);

            btn.setOnClickListener(v -> listener.onLevelChanged(level));

            buttons[i] = btn;
            buttonRow.addView(btn);
        }

        layout.addView(buttonRow);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        return layout;
    }

    private void applyButtonStyle(TextView btn, boolean isActive,
                                   int activeColor, int inactiveColor, int activeTextColor, int textColor, float density) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(8 * density);
        if (isActive) {
            bg.setColor(activeColor);
            btn.setTextColor(activeTextColor);
        } else {
            bg.setColor(inactiveColor);
            bg.setStroke((int) (1 * density), activeColor);
            btn.setTextColor(textColor);
        }
        btn.setBackground(bg);
    }

    private void updateButtonStates(TextView[] buttons, int[] levels, Integer currentLevel) {
        if (currentLevel == null) return;
        Context context = getContext();
        int activeColor = ContextCompat.getColor(context, R.color.md_primary);
        int inactiveColor = ContextCompat.getColor(context, R.color.md_card_background);
        int textColor = ContextCompat.getColor(context, R.color.md_on_surface);
        int activeTextColor = ContextCompat.getColor(context, R.color.md_on_primary);
        float density = context.getResources().getDisplayMetrics().density;

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                applyButtonStyle(buttons[i], levels[i] == currentLevel, activeColor, inactiveColor, activeTextColor, textColor, density);
            }
        }
    }

    public void setCountText(String text) {
        if (count_textView != null) {
            count_textView.setText(text);
        }
    }

    private interface OnLevelChangedListener {
        void onLevelChanged(int level);
    }
}
