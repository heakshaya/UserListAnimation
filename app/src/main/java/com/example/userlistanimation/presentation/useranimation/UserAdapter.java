package com.example.userlistanimation.presentation.useranimation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.userlistanimation.R;

import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.useranimation.util.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final ArrayList<Data> posts = new ArrayList<>();


    public void setPostsList(List<com.example.userlistanimation.data.model.post.Data> items) {
        posts.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

       Data post = posts.get(position);
        holder.userName.setText(post.getOwner().getFirstName() + " " + post.getOwner().getLastName());
        Glide.with(holder.circularImageView.getContext())
                .load(post.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.circularImageView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Data post;
        if (!payloads.isEmpty()) {
            boolean isVisible = (boolean) payloads.get(0);
            post = posts.get(position);
            post.setVisibleLayers(isVisible);
            if (holder.getAbsoluteAdapterPosition() == 0) {
                if (post.isVisibleLayers()) {
                    holder.layer1.animate().alpha(1f).setDuration(0);
                    holder.layer2.animate().alpha(1f).setDuration(0);
                    post.setVisibleLayers(false);
                } else {
                    holder.layer1.animate().alpha(0.0f).setDuration(500);
                    holder.layer2.animate().alpha(0.0f).setDuration(300);
                }
            } else {
                if (post.isVisibleLayers()) {
                    holder.layer1.animate().alpha(1f).setDuration(1000);
                    holder.layer2.animate().alpha(1f).setDuration(1000);
                    post.setVisibleLayers(false);
                } else {
                    holder.layer1.animate().alpha(0.0f).setDuration(300);
                    holder.layer2.animate().alpha(0.0f).setDuration(100);
                }
            }
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircularImageView circularImageView;
        View layer1, layer2;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            circularImageView = itemView.findViewById(R.id.circleImage);
            layer1 = itemView.findViewById(R.id.user_layer_1);
            layer2 = itemView.findViewById(R.id.user_layer_2);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }



}
