package io.github.snowf0x.thedarkmansion;

import android.app.Application;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

public class ApplicationClass extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        EmojiManager.install(new EmojiOneProvider());

    }


    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
