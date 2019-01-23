package io.github.snowf0x.thedarkmansion;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vanniktech.emoji.EmojiTextView;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<String> text;
    private int player;

    ChatAdapter(ArrayList<String> text, int player) {
        this.text = text;
        this.player = player;
       
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_row,viewGroup,false));
    }
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        String message = text.get(i);
        if(Character.getNumericValue(message.charAt(0)) == player) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            viewHolder.emojiTextView.setLayoutParams(params);
        }
        if(message.charAt(1) != '\'') {
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.emojiTextView.setVisibility(View.VISIBLE);
            viewHolder.emojiTextView.setText(message.substring(1));
        }
        else{
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.emojiTextView.setVisibility(View.GONE);
            //TODO: Images here!
            switch(message.charAt(2)){
                case '1':
                    viewHolder.imageView.setImageResource(R.drawable.ouija);
                    break;
                case '2':
                    viewHolder.imageView.setImageResource(R.drawable.snow);
                    break;
                case '3':
                    viewHolder.imageView.setImageResource(R.drawable.snow);
                    break;
                case '4':
                    viewHolder.imageView.setImageResource(R.drawable.snow);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return text.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        GifImageView imageView;
        EmojiTextView emojiTextView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            emojiTextView = itemView.findViewById(R.id.chatTextView);
        }
    }
}
