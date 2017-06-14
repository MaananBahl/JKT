package com.example.ishan.jkt.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ishan.jkt.R;
import com.example.ishan.jkt.activities.ViewTicketReceiver;
import com.example.ishan.jkt.classes.Ticket;

import java.util.ArrayList;

public class TicketAdapterReceiver extends RecyclerView.Adapter<TicketAdapterReceiver.myViewHolder>{


    ArrayList<Ticket> ticketList;
    Context context;

    public TicketAdapterReceiver(ArrayList<Ticket> ticketList, Context context){
        this.ticketList = ticketList;
        this.context = context;
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView heading;
        public TextView priority;
        public TextView date_time;
        public TextView status;
        public TextView sender;
        public ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
        public Context context;

        public myViewHolder(View itemView, ArrayList<Ticket> ticketList, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.ticketList = ticketList;
            this.context = context;
            heading = (TextView)itemView.findViewById(R.id.heading);
            priority = (TextView)itemView.findViewById(R.id.priority);
            date_time = (TextView)itemView.findViewById(R.id.date_time);
            status = (TextView)itemView.findViewById(R.id.status);
            sender = (TextView)itemView.findViewById(R.id.textView12);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Ticket ticket = this.ticketList.get(ticketList.size()-position-1);
            Intent i = new Intent(this.context,ViewTicketReceiver.class);
            i.putExtra("heading",ticket.getHeading());
            i.putExtra("priority",ticket.getPriority());
            i.putExtra("status",ticket.getStatus());
            i.putExtra("message",ticket.getMessage());
            i.putExtra("create_date_time",ticket.getCreate_date_time());
            i.putExtra("update_date_time",ticket.getUpdate_date_time());
            i.putExtra("key",ticket.getKey());
            this.context.startActivity(i);
        }
    }

    public TicketAdapterReceiver(ArrayList<Ticket> ticketList){
        this.ticketList = ticketList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_type_receiver,parent,false);
        return new myViewHolder(itemView,ticketList,context);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Ticket ticket = ticketList.get(ticketList.size()-position-1);
        holder.sender.setText(ticket.getSender());
        holder.heading.setText(ticket.getHeading());
        holder.status.setText(ticket.getStatus());
        holder.priority.setText(ticket.getPriority());
        holder.date_time.setText("Create Date : " + ticket.getCreate_date_time());
    }

    @Override
    public int getItemCount() { return ticketList.size(); }
}
