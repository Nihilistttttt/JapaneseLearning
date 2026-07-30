package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

@SuppressLint("ViewConstructor")
public class ExtendedInfoView extends LinearLayout {

    public ExtendedInfoView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType,
                           List<Etymology> etymologies, List<UsageDistinction> usageDistinctions,
                           List<GrammarPoint> grammarPoints, List<Idiom> idioms) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        boolean needDivider = false;

        if (!etymologies.isEmpty()) {
            if (needDivider) addDivider(context);
            addSectionTitle("語源");
            addView(new EtymologyView(context, lifecycleOwner, layoutType, etymologies));
            needDivider = true;
        }

        if (!usageDistinctions.isEmpty()) {
            if (needDivider) addDivider(context);
            addSectionTitle("用法区分");
            addView(new UsageDistinctionView(context, lifecycleOwner, layoutType, usageDistinctions));
            needDivider = true;
        }

        if (!grammarPoints.isEmpty()) {
            if (needDivider) addDivider(context);
            addSectionTitle("文法");
            addView(new GrammarPointView(context, lifecycleOwner, layoutType, grammarPoints));
            needDivider = true;
        }

        List<Idiom> validIdioms = new java.util.ArrayList<>();
        for (Idiom idiom : idioms) {
            if (idiom != null) validIdioms.add(idiom);
        }
        if (!validIdioms.isEmpty()) {
            if (needDivider) addDivider(context);
            addSectionTitle("熟語");
            addView(new IdiomView(context, lifecycleOwner, layoutType, validIdioms));
        }
    }

    private void addDivider(Context context) {
        View divider = new View(context);
        int dividerHeight = (int) (8 * context.getResources().getDisplayMetrics().density);
        divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight));
        addView(divider);
    }

    private void addSectionTitle(String title) {
        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(getContext().getResources().getColor(R.color.md_detail_label));
        int marginBottom = (int) (4 * getContext().getResources().getDisplayMetrics().density);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, marginBottom);
        tv.setLayoutParams(params);
        addView(tv);
    }
}