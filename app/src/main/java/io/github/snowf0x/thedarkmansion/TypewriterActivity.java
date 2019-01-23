package io.github.snowf0x.thedarkmansion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TypewriterActivity extends AppCompatActivity {

    TypeWriter typeWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typewriter);
        typeWriter = findViewById(R.id.typwtr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        typeWriter.animateText(getIntent().getStringExtra("str"),this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        typeWriter.run = false;
    }
}
