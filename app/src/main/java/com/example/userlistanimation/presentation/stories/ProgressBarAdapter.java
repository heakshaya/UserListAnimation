package com.example.userlistanimation.presentation.stories;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.Constants;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarAdapter extends RecyclerView.Adapter<ProgressBarAdapter.ProgressViewHolder> {

    private Context mContext;
    private Handler handler = new Handler();
    private ViewPager2 viewPager2;
    private final ArrayList<Data> stories = new ArrayList<>();

    public ProgressBarAdapter(Context mContext, ViewPager2 viewPager2) {
        this.mContext = mContext;
        this.viewPager2 = viewPager2;
    }
    public void setPostsList(List<Data> items) {
        stories.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
       /* holder.progressBar=new ProgressBar(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.progressBar.setProgress(50,true);
        }else{
            holder.progressBar.setProgress(50);
        }
        Log.d("UserStories","ProgressBar:"+holder.progressBar.getProgress());*/
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Log.d("UserStories", "Inside ProgressBarAdapter payload is empty:" + !payloads.isEmpty() + ", position:" + position);

        if (!payloads.isEmpty()) {
            boolean isVisible = (boolean) payloads.get(0);
            if (isVisible) {
                Data story = stories.get(holder.getAbsoluteAdapterPosition());

                final int[] i = {0};
                new Thread(new Runnable() {
                    public void run() {
                        while (i[0] < 100) {
                            i[0] += 1;
                            // Update the progress bar and display the current value in text view
                            handler.post(new Runnable() {
                                public void run() {
                                    holder.progressBar.setProgress(i[0]);
                                   /* Log.d("UserStories","viewpager position: "+viewPager2.getCurrentItem());
                                    Log.d("UserStories", "ProgressBar progress:" + i[0] + " position:" + position);*/
                                }
                            });
                            try {
                                Thread.sleep(story.getTime()/100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }


    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
