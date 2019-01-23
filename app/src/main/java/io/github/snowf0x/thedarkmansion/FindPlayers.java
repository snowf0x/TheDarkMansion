package io.github.snowf0x.thedarkmansion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindPlayers extends AppCompatActivity {

    DatabaseReference databaseReference, my_refrence;
    private ArrayList<String> listItem;
    FirebaseAuth auth;
    boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_players);
        b = getIntent().getBooleanExtra("is_one",false);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Player2").child(auth.getUid()).onDisconnect().removeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = findViewById(R.id.title);

        if(b) {
            tv.setText(R.string.choose_a_partner);
            databaseReference.child("Player2").child(auth.getUid()).removeValue();
            Toast.makeText(this, "This list includes those who choose player 2", Toast.LENGTH_SHORT).show();
            final ListView listView = findViewById(R.id.lv);
            listItem = new ArrayList<>();
            final ListAdapter adapter= new ListAdapter(this, R.layout.listview_row,listItem);
            listView.setAdapter(adapter);
            databaseReference.child("Player2").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s6_edge) {
                    String s = dataSnapshot.getValue(String.class);
                    int i = listItem.indexOf(dataSnapshot.getKey()+"#"+s);
                    if(i < 0 && s.charAt(0) != '1')
                        listItem.add(dataSnapshot.getKey()+"#"+s);
                    else if(s.charAt(0) != '1')
                        listItem.set(i,dataSnapshot.getKey()+"#"+s);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s1) {
                    String s = dataSnapshot.getValue(String.class);
                    int i = listItem.indexOf(dataSnapshot.getKey()+"#"+s);
                    if(i < 0 && s.charAt(0) != '1')
                        listItem.add(dataSnapshot.getKey()+"#"+s);
                    else if(s.charAt(0) != '1')
                        listItem.set(i,dataSnapshot.getKey()+"#"+s);
                    adapter.notifyDataSetChanged();
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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = listItem.get(i);
                    Toast.makeText(FindPlayers.this, "Wait for their response..", Toast.LENGTH_SHORT).show();
                    databaseReference.child(s.substring(0,s.indexOf("#"))).setValue(auth.getUid()+"#"+auth.getCurrentUser().getDisplayName());
                    Intent ix = new Intent(FindPlayers.this, Chat.class);
                    ix.putExtra("room",s.substring(0,s.indexOf("#"))+auth.getUid());
                    ix.putExtra("b",b);
                    databaseReference.child(s.substring(0,s.indexOf("#"))+auth.getUid()).child("Accepted").setValue("0");
                    startActivity(ix);
                    finish();
                }
            });
        }
        else {
            tv.setText("Wait for Player 1");

            databaseReference.child("Player2").child(auth.getUid()).setValue("0#"+auth.getCurrentUser().getDisplayName());
            my_refrence = FirebaseDatabase.getInstance().getReference().child(auth.getUid());
            my_refrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String info = dataSnapshot.getValue(String.class);
                    if (info != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPlayers.this);
                        builder.setMessage(info.substring(info.indexOf("#") + 1)).setTitle("Accept Request?")
                                .setCancelable(false)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        databaseReference.child("Player2").child(auth.getUid()).setValue("1#" + auth.getCurrentUser().getDisplayName());
                                        my_refrence.removeValue();
                                        Intent i = new Intent(FindPlayers.this, Chat.class);
                                        i.putExtra("b",b);
                                        i.putExtra("room",auth.getUid()+info.substring(0,info.indexOf("#")));
                                        startActivity(i);
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        my_refrence.removeValue();
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Request canceled!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        builder.create().show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public class ListAdapter extends ArrayAdapter<String> {

        private int resourceLayout;
        private Context mContext;

        ListAdapter(Context context, int resource, ArrayList<String> items) {
            super(context, resource, items);
            this.resourceLayout = resource;
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                v = vi.inflate(resourceLayout, null);
            }

            String p = getItem(position);

            if (p != null) {
                TextView b = v.findViewById(R.id.button_lv);
                if (b != null) {
                    b.setText(p.substring(p.lastIndexOf("#")+1));
                    if(position%2 == 0)
                        b.setBackgroundColor(Color.parseColor("#2c3c71"));
                    else
                        b.setBackgroundColor(Color.parseColor("#9f1a46"));
                }
            }

            return v;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!b)
            databaseReference.child("Player2").child(auth.getUid()).setValue("1#"+auth.getCurrentUser().getDisplayName());
    }
}
