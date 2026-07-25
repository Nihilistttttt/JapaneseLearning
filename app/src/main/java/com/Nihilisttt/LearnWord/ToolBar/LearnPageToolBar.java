package com.Nihilisttt.LearnWord.ToolBar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;

public class LearnPageToolBar extends androidx.appcompat.widget.Toolbar {

    private TextView back_button;
    private TextView count_textView;
    private ImageButton font_size_button;
    private ImageButton delete_button;
    private LearnPageStateViewModel stateViewModel;

    public LearnPageToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        inflate(context, R.layout.learn_word_tool_bar, this);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> ((Activity) context).finish());
        count_textView = findViewById(R.id.count_text);
        font_size_button = findViewById(R.id.font_size_button);
        delete_button = findViewById(R.id.delete_button);
    }

    public void setStateViewModel(LearnPageStateViewModel stateViewModel) {
        this.stateViewModel = stateViewModel;
        if (font_size_button != null) {
            font_size_button.setOnClickListener(v -> {
                if (this.stateViewModel != null) {
                    this.stateViewModel.cycleFontSize();
                }
            });
        }
    }

    public void setCountText(String text) {
        if (count_textView != null) {
            count_textView.setText(text);
        }
    }
}
