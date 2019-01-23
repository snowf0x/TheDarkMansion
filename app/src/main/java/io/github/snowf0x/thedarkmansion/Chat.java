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
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    EmojiPopup emojiPopup;
    EmojiEditText editText;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    int player = 1,accepted= 0;
    private MediaPlayer background;
    ArrayList<String> messages;
    Chatcounter chatcounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.main_activity_chat_bottom_message_edittext);
        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root)).build(editText);
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        background = MediaPlayer.create(this,R.raw.spookybg);
        background.setLooping(true);
        messages = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("room")+"");

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
        chatcounter = new Chatcounter();
        recyclerView.setAdapter(adapter);

    }

    private void p2() {
        startTyping("It's been 4 days since Jenny was missing. Such a joyful girl cant run away like that, even I can sense a fear on Joe's face. Something's fishy.. I think... I think.. It was the time that we should ask her best friend Joe.\n........");

    }

    private void p1() {
        accepted++;
        startTyping("Annie's has been upset and curious about Jenny's dissaperiance. Maybe she's.. upto something.. Should I ask her? After all she is by bff for a reason!\n.........");
    }
    private void startTyping(String s){
        Intent i = new Intent(this,TypewriterActivity.class);
        i.putExtra("str",s);
        startActivityForResult(i,1);

    }

    public void send(View view) {
        databaseReference.push().setValue(player+editText.getText().toString());
        editText.setText("");
        editText.setHint("");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                background.start();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private class Chatcounter{
        private int P1, P2;

        void AddP1(){
            P1++;
            if (player == 2) {
                switch (P1) {
                    case 1:
                        editText.setText("Something surely creepy about this.. I am on a mission you in?");
                        break;
                    case 2:
                        editText.setHint("Should we ask her?");
                        break;
                    case 4:
                        new AlertDialog.Builder(Chat.this).setTitle("Something's in the bag").setMessage("Looks like a picture! Hurry Grab it!").setPositiveButton("Grab it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editText.setText("\'1");
                                send(null);
                            }
                        }).create().show();
                }
            }
            Toast.makeText(Chat.this, "P1-"+P1, Toast.LENGTH_SHORT).show();

        }
        void AddP2(){
            P2++;

            if (player == 1) {
                switch (P2) {
                    case 1:
                        editText.setHint("HEll yeah!");
                        break;
                    case 2:
                        editText.setHint("I don't think that'll work");
                        break;
                    case 3:
                        editText.setHint("How about I check her bag? Hope that'll help");
                        break;
                }
                Toast.makeText(Chat.this, "P2-"+P2, Toast.LENGTH_SHORT).show();

            }
        }
        private Chatcounter() {
            P1 = 0;
            P2 = 0;
            if(player == 1)
                editText.setText("Hey Annie! Still thinking about Joe?");
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
