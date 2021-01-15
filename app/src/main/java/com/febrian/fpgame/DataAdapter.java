package com.febrian.fpgame;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    Context c;
    ArrayList<Data> data;
    DataAdapter(Context c, ArrayList<Data> data){
        this.c = c;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(c).inflate(R.layout.item_score, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_no.setText(Integer.toString(position + 1));
        holder.tv_name.setText(data.get(position).getUsername());
        holder.tv_score.setText(Integer.toString(data.get(position).getScore()));

        holder.tv_name.setOnClickListener(v -> {
            Intent intent = new Intent(c, DetailActivity.class);
            intent.putExtra("name", data.get(position).getUsername());
            c.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_score, tv_no;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no = itemView.findViewById(R.id.tv_no);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_score = itemView.findViewById(R.id.tv_score);
        }
    }
}
