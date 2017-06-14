package com.example.ishan.jkt.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.ishan.jkt.R;
import com.example.ishan.jkt.classes.Comment;
import java.util.ArrayList;

public class ChatTicketAdapter extends RecyclerView.Adapter<ChatTicketAdapter.myViewHolder>{

    ArrayList<Comment> commentArrayList;
    LinearLayout ll;
    CardView cv;

    public ChatTicketAdapter(ArrayList<Comment> commentArrayList1){
        this.commentArrayList = commentArrayList1;

    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        public TextView sender;
        public TextView comment;
        public TextView date;
        public ArrayList<Comment> commentArrayList;


        public myViewHolder(View itemview,ArrayList<Comment> commentArrayList){
            super(itemview);
            this.commentArrayList = commentArrayList;
            sender = (TextView)itemview.findViewById(R.id.sender);
            comment = (TextView)itemview.findViewById(R.id.comment);
            date = (TextView)itemview.findViewById(R.id.date);
            ll = (LinearLayout)itemview.findViewById(R.id.ll);
            cv = (CardView)itemview.findViewById(R.id.card);
        }
    }


    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_ticket_type,parent,false);
        return new myViewHolder(itemView,commentArrayList);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Comment comment = commentArrayList.get(position);
        holder.sender.setText(comment.getSender());
        holder.comment.setText(comment.getComment());
        holder.date.setText(comment.getDate());

        String sender = comment.getSender().trim();

        if(sender.equals("Me")){
            cv.setCardBackgroundColor(Color.parseColor("#F0F4C3"));
            ll.setGravity(Gravity.RIGHT);
        }
        else{
            cv.setCardBackgroundColor(Color.parseColor("#B2EBF2"));
            ll.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }
}
