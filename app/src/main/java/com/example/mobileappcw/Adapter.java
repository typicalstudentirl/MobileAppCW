package com.example.mobileappcw;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Adapter";

    private Context context;
    ArrayList<stackModel> StackItemsLists;

    public Adapter(Context context, ArrayList<stackModel> StackItemsList){
        this.context = context;
        this.StackItemsLists = StackItemsList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.recycler_view,parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final stackModel StackItems = StackItemsLists.get(position);
        ((Item)holder).title.setText(StackItems.title);
        try{
            Picasso.with(context).load(StackItems.getImage()).into(((Item) holder).image);
        }catch (Exception e) {
            e.printStackTrace();
        }

        ((Item) holder).link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: starts");
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(StackItems.getLink()));
                context.startActivity(intent);
                Log.d(TAG, "onClick: ends");
            }
        });
        Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        return StackItemsLists.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView link;

        public Item(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.image_text);
            image = itemView.findViewById(R.id.image_graph);
            link = itemView.findViewById(R.id.textViewLink);
        }
    }
}