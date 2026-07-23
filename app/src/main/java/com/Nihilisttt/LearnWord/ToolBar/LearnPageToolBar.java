package com.Nihilisttt.LearnWord.ToolBar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import com.Nihilisttt.LearnWord.R;

public class LearnPageToolBar extends Toolbar {

    private TextView back_button;
    private TextView count_textView;
    private ImageButton like_button;
    private ImageButton delete_button;

    public LearnPageToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        inflate(context, R.layout.learn_word_tool_bar, this);//加载布局

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> ((Activity) context).finish());
        count_textView = findViewById(R.id.count_text);
        like_button = findViewById(R.id.like_button);
        delete_button = findViewById(R.id.delete_button);
    }

}
