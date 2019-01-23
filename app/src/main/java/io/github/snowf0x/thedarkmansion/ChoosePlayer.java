package io.github.snowf0x.thedarkmansion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChoosePlayer extends AppCompatActivity {

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);
        sp = getSharedPreferences("Player",MODE_PRIVATE);
    }

    public void choose(View view) {
        if(view == findViewById(R.id.p_one))
            sp.edit().putBoolean("player_one",true).apply();
        else
            sp.edit().putBoolean("player_one",false).apply();
        Intent i = new Intent(this, FindPlayers.class);
        i.putExtra("is_one",view == findViewById(R.id.p_one));
        startActivity(i);
    }
}
