package io.github.snowf0x.thedarkmansion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class Chat extends AppCompatActivity {

    EmojiPopup emojiPopup;
    EmojiEditText editText;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    int player = 1,accepted= 0;
    private MediaPlayer background;
    ArrayList<String> messages;
    ChatCounter chatcounter;
    GifImageView wallpaper;
    private TextWatcher textwatcher;
    private String pretypedMessage, cover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.main_activity_chat_bottom_message_edittext);
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root)).build(editText);
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        background = MediaPlayer.create(this,R.raw.school_ambiance);
        background.setLooping(true);
        wallpaper = findViewById(R.id.backg);
        messages = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("room")+"");
        textwatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= pretypedMessage.length()) {
                    if (!pretypedMessage.startsWith(charSequence.toString())) {
                        editText.setText(pretypedMessage.substring(0, charSequence.length()));
                        editText.setSelection(editText.getText().length());
                    }
                }
                else{
                    editText.setText(pretypedMessage);
                    Toast.makeText(Chat.this, "Press send!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        if(!getIntent().getBooleanExtra("b",true)) {
            adapter = new ChatAdapter(messages, 2);
            databaseReference.child("Accepted").setValue("1");
            player = 2;
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(!dataSnapshot.getKey().equals("Accepted")) {
                        messages.add(dataSnapshot.getValue(String.class));
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        if(messages.get(adapter.getItemCount()-1).startsWith("1"))
                            chatcounter.AddP1();
                        else
                            chatcounter.AddP2();

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            p2();
        }
        else {
            adapter = new ChatAdapter(messages, 1);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getKey().equals("Accepted") && accepted == 0) {
                        if (dataSnapshot.getValue(String.class).equals("1"))
                            p1();
                    } else {
                        messages.add(dataSnapshot.getValue(String.class));
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        if(messages.get(adapter.getItemCount()-1).startsWith("1"))
                             chatcounter.AddP1();
                        else
                            chatcounter.AddP2();

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getKey().equals("Accepted")) {
                        if (dataSnapshot.getValue(String.class).equals("1"))
                            p1();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        recyclerView.setAdapter(adapter);

    }

    private void p2() {
        cover = "\"It's been 4 days since Jenny was killed herself. Such a joyful girl cant do something like that, Ahh.. enough thing I am going to be late to school today..";
        startTyping(cover);
        chatcounter = new ChatCounter();

    }

    private void p1() {
        accepted++;
        cover = "Annie's has been upset and curious about Jenny's dissaperiance. Maybe she's.. upto something.. even I can sense a fear on Joe's face. Whats's happening these days??";
        startTyping(cover);
        chatcounter = new ChatCounter();
    }
    private void startTyping(String s){
        Intent i = new Intent(this,TypewriterActivity.class);
        i.putExtra("str",s);
        startActivityForResult(i,1);

    }

    public void send(View view) {
        String s = editText.getText().toString();
        if(s != null) {
            databaseReference.push().setValue(player+s);
            editText.setText("");
            editText.setHint("");
            editText.removeTextChangedListener(textwatcher);
        }
        else
            Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                background.start();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                background.start();
                Alerter.create(Chat.this)
                        .setTitle("Event Log")
                        .setText(cover+"\n(swipe to dismiss)")
                        .enableInfiniteDuration(true)
                        .enableSwipeToDismiss()
                        .show();
            }
        }
    }
    private class ChatCounter {
        private int P1, P2,P1_ghost= 0,P2_ghost=1;

        void AddP1(){
            if(P2_ghost > 0) {
                P1++;
                Log.d("@@@@@@@-ADD-P1", "P1 = " + P1 + "----P2_Ghost = " + P2_ghost + "(Now 0), P1_Ghost=" + P1_ghost);
                P2_ghost = 0;

                if (player == 2) {
                    switch (P1) {
                        case 3:
                            setText("Hey! btw I wanted to say something..");
                            break;
                        case 4:
                            setText("About Jenny...😥");
                            break;
                        case 5:
                            setText("I fell strange that Jenny killed herself \uD83D\uDE41\uD83D\uDE16");
                            break;
                        case 6:
                            setText("I mean such a positive girl.. why?");
                            break;
                        case 7:
                            setText("hmm? 🧐🧐");
                            break;
                        case 8:
                            setText("I think she's attending extra classes..\nI don't think she will come soon");
                            background.release();
                            background = MediaPlayer.create(Chat.this, R.raw.bell);
                            background.setLooping(false);
                            background.start();
                            break;
                        case 9:
                            setText("What should we do?");
                            break;
                        case 10:
                            setText("Let me check her bag.. maybe we can find some evidence?\nStand in front of me and GIVE COVER!");
                            break;
                        case 11:
                            new AlertDialog.Builder(Chat.this).setTitle("Bag").setMessage("press search to check Joe's bag")
                                    .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            editText.setText("\'1");
                                            send(null);
                                            editText.setText("Hmm an apology letter? Sorry for my decisions? Ouija Bord?");
                                            send(null);
                                        }
                                    }).setCancelable(false).create().show();
                            break;
                        case 12:
                            setText("Oh My God!.. we should go home now, it seems like it's gonna rain");
                            break;
                        case 13:
                            setText("You too.. \uD83D\uDE00");
                            wallpaper.setImageResource(R.drawable.rain);
                            background.stop();
                            background.release();
                            background = MediaPlayer.create(Chat.this, R.raw.rain);
                            background.setLooping(true);
                            background.start();
                            break;
                    }
                } else {
                    switch (P1) {
                        case 8:
                            background.stop();
                            background.release();
                            background = MediaPlayer.create(Chat.this, R.raw.bell);
                            background.setLooping(false);
                            background.start();
                            break;
                        case 13:
                            wallpaper.setImageResource(R.drawable.rain);
                            background.stop();
                            background.release();
                            background = MediaPlayer.create(Chat.this, R.raw.rain);
                            background.setLooping(true);
                            background.start();
                            break;
                    }
                }
            }
            else
                Log.d("@@@@@@@-ADD-P1","P1 = "+P1+"----P2_Ghost = "+P2_ghost+"(not 0, OUTSIDE IF), P1_Ghost="+(1+P1_ghost));
            P1_ghost++;
        }
        void AddP2(){
            if(P1_ghost > 0) {
                P2++;
                Log.d("@@@@@@@-ADD-P2","P2 = "+P2+"----P1_Ghost = "+P1_ghost+"(Now 0), P1_Ghost="+P1_ghost);
                P1_ghost = 0;
                if (player == 1) {
                    switch (P2) {
                        case 6:
                            setText("her bff Joe's is surely hiding something, She's hiding fear in her sadness");
                            break;
                        case 7:
                            setText("I doubt she did something.. btw  where is she? \uD83D\uDE44");
                            break;
                        case 8:
                            setText("Oh Crap! School's now over too 😑");
                            break;
                        case 11:
                            setText("\uD83D\uDE28 Did she kill her!? WTF...\nbe online at home we got a serious discussion");
                            break;
                        case 12:
                            setText("Take care Annie! Bye");
                            break;
                        case 13:
                            break;
                    }
                }
                else{
                    switch (P2){
                        case 13:
                            //Toast.makeText(Chat.this, "Rain should be triggered 2", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
                Log.d("@@@@@@@-ADD-P2","P2 = "+P2+"----P1_Ghost = "+P1_ghost+"(not 0, OUTSIDE IF), P1_Ghost="+(1+P1_ghost));

            P2_ghost++;


        }
        private ChatCounter() {
            P1 = 0;
            P2 = 0;
            if(player == 1){
                pretypedMessage = "Good Morning Annie! \uD83D\uDE0A";
                editText.addTextChangedListener(textwatcher);
            }
        }

        public int getP1() {
            return P1;
        }

        public void setP1(int p1) {
            P1 = p1;
        }

        public int getP2() {
            return P2;
        }

        public void setP2(int p2) {
            P2 = p2;
        }
    }

    private void setText(String s) {
        pretypedMessage = s;
        editText.addTextChangedListener(textwatcher);
    }

    public void toggle(View view) {
        emojiPopup.toggle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        background.stop();
        background = null;
        databaseReference.removeValue();
    }
}
