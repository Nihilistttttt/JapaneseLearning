package com.Nihilisttt.LearnWord.Fragment.FirstPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nihilisttt.LearnWord.R;

import java.time.LocalDate;

public class BeforeSignInFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before_sign_in, container, false);
        TextView sign_in_data_text = view.findViewById(R.id.before_sign_in_date_text);
        LocalDate localDate = LocalDate.now();
        String dayOfWeek = "";
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        switch (localDate.getDayOfWeek().getValue()) {
            case 1:
                dayOfWeek = weekDays[0];
                break;
            case 2:
                dayOfWeek = weekDays[1];
                break;
            case 3:
                dayOfWeek = weekDays[2];
                break;
            case 4:
                dayOfWeek = weekDays[3];
                break;
            case 5:
                dayOfWeek = weekDays[4];
                break;
            case 6:
                dayOfWeek = weekDays[5];
                break;
            case 7:
                dayOfWeek = weekDays[6];
                break;

        }
        String date = String.valueOf(localDate.getMonth().getValue()) + "/" + String.valueOf(localDate.getDayOfMonth()) + " " + dayOfWeek;

        sign_in_data_text.setText(date);
        return view;

    }
}
