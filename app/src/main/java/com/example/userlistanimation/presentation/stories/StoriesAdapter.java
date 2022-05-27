package com.example.userlistanimation.presentation.stories;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.useranimation.util.CircularImageView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesViewHolder> {
    private final ArrayList<Data> stories = new ArrayList<>();
    private onTimeUpdateListener onTimeUpdateListener;
    private Context context;
    ExoPlayer exoPlayer;

    public StoriesAdapter(Context context) {
        this.context = context;
    }

    public void setOnTimeUpdateListener(onTimeUpdateListener onTimeUpdateListener) {
        this.onTimeUpdateListener = onTimeUpdateListener;
    }

    public void setPostsList(List<Data> items) {
        stories.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoriesAdapter.StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.stories_item, parent, false);
        return new StoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesAdapter.StoriesViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (!payloads.isEmpty()) {
            boolean isVisible = (boolean) payloads.get(0);
            if (isVisible) {
                Data story = stories.get(holder.getAbsoluteAdapterPosition());
                if (story.isVideo()) {
                    if (exoPlayer == null) {
                        exoPlayer = new ExoPlayer.Builder(context).build();
                        holder.imageView.setVisibility(View.GONE);
                        try {
                            // Build the media item.
                            MediaItem mediaItem = MediaItem.fromUri(story.getVideoUrl());
                            // Set the media item to be played.
                            exoPlayer.setMediaItem(mediaItem);
                            // Prepare the player.
                            exoPlayer.prepare();
                            // Start the playback.
                            exoPlayer.play();
                            // Bind the player to the view.
                            holder.playerControlView.setPlayer(exoPlayer);
                            exoPlayer.addListener(new Player.Listener() {
                                @Override
                                public void onPlaybackStateChanged(int playbackState) {
                                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                                    if (playbackState == ExoPlayer.STATE_READY) {
                                        holder.playerControlView.setVisibility(View.VISIBLE);
                                        long duration = exoPlayer.getDuration();
                                        story.setTime(duration);
                                        if (onTimeUpdateListener != null) {
                                            onTimeUpdateListener.onViewPlaying(duration);
                                        }

                                    }
                                    if (playbackState == ExoPlayer.STATE_ENDED) {
                                        if (exoPlayer != null) {
                                            exoPlayer.release();
                                            exoPlayer = null;
                                        }
                                    }
                                }
                            });
                        } catch (Exception ignored) {
                        }
                    }
                } else {
                    onTimeUpdateListener.onViewPlaying(story.getTime());
                    holder.playerControlView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    Glide.with(holder.imageView.getContext())
                            .load(story.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageView);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class StoriesViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerControlView;
        ImageView imageView;

        public StoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.main_image);
            playerControlView = itemView.findViewById(R.id.playerView);
        }
    }

    public interface onTimeUpdateListener {
        void onViewPlaying(long duration);
    }
}
