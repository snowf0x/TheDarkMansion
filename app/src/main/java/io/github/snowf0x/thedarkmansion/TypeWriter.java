package io.github.snowf0x.thedarkmansion;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;

public class TypeWriter extends android.support.v7.widget.AppCompatTextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 250; //Default 500ms delay
    private MediaPlayer click_sounds;
    boolean run = true;
    private Context context;


    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            if(mIndex < mText.length() && run) {
                long extra_delay = 0;
                setText(mText.subSequence(0, ++mIndex));
                if(mIndex + 1 < mText.length()) {
                    if (mText.charAt(mIndex + 1) == '.')
                        extra_delay = 500;
                    else if (mText.charAt(mIndex + 1) == ',')
                        extra_delay = 200;
                    else if (mText.charAt(mIndex + 1) != ' ')
                        extra_delay = -50;
                }
                mHandler.postDelayed(characterAdder, mDelay + extra_delay);
            }
            else {
                click_sounds.stop();
                ((Activity)context).setResult(Activity.RESULT_OK);
                ((Activity)context).finish();
            }

        }
    };

    public void animateText(CharSequence text, Context context) {
        mText = text;
        mIndex = 0;
        this.context = context;
        click_sounds = MediaPlayer.create(context,R.raw.type);
        setText("");
        click_sounds.setLooping(true);
        click_sounds.start();
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}