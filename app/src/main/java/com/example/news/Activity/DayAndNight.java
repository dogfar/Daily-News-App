package com.example.news.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.news.R;

public class DayAndNight extends Fragment {
    private Switch s;
    private LinearLayout mode;
    private TextView textmode;
    private Button b;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dayandnight, container, false);
        s = view.findViewById(R.id.change);
        mode = view.findViewById(R.id.mode);
        textmode = view.findViewById(R.id.textmode);
        b = view.findViewById(R.id.return_button);
        mode.setBackgroundColor(Color.parseColor("#ffffff"));
        if(!MainActivity.Day){
            s.setChecked(true);
            mode.setBackgroundColor(Color.parseColor("#5e5e5e"));
            textmode.setTextColor(Color.WHITE);
        }
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b_) {
                if(s.isChecked()){
                    MainActivity.Day=false;
                    mode.setBackgroundColor(Color.parseColor("#5e5e5e"));
                    textmode.setTextColor(Color.WHITE);
                    b.setTextColor(Color.WHITE);
                    ((MainActivity)(getActivity())).changeUI();
                }
                else{
                    MainActivity.Day=true;
                    mode.setBackgroundColor(Color.parseColor("#ffffff"));
                    textmode.setTextColor(Color.BLACK);
                    b.setTextColor(Color.BLACK);
                    ((MainActivity)(getActivity())).changeUI();
                }
            }
        });
        b.setVisibility(View.VISIBLE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)(getActivity())).openMainFragment();
            }
        });
        return view;
    }
}
