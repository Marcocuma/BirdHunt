package com.example.juegofinal.objetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.juegofinal.MainGame;
import com.example.juegofinal.R;

public class ArcadeFragment extends Fragment {
    public int score;
    public TextView scoreTV;
    public Button play;

    public ArcadeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arcade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scoreTV = view.findViewById(R.id.textViewNumScore);
        play = view.findViewById(R.id.button_play_arcade);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainGame.class);
                intent.putExtra("tiempoMaximo",120);
                intent.putExtra("puntuacionMax",0);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getContext());
        score = sharedPreferences.getInt("score",0);
        scoreTV.setText(String.valueOf(score));
    }
}