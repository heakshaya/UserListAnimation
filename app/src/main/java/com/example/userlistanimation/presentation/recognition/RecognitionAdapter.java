package com.example.userlistanimation.presentation.recognition;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.Constants;
import com.example.userlistanimation.presentation.useranimation.util.CircularImageView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.ArrayList;
import java.util.List;

public class RecognitionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Data> stories = new ArrayList<>();
    private onTimeUpdateListener onTimeUpdateListener;
    private Context context;
    ExoPlayer exoPlayer;

    public RecognitionAdapter(Context context) {
        this.context = context;
    }

    public void setStoriesList(List<Data> items) {
        stories.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnTimeUpdateListener(onTimeUpdateListener onTimeUpdateListener) {
        this.onTimeUpdateListener = onTimeUpdateListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        try {
            switch (viewType) {
                case Constants.ITEM_TYPE_MAIN_IMAGE:
                    View v1 = inflater.inflate(R.layout.item_recognition_main_image, parent, false);
                    return new MainImageViewHolder(v1);

                case Constants.ITEM_TYPE_USER:
                    View v2 = inflater.inflate(R.layout.item_recognition_user, parent, false);
                    return new UserViewHolder(v2);

                case Constants.ITEM_TYPE_MEDIA_IMAGE:
                    View v3 = inflater.inflate(R.layout.item_recognition_media_image, parent, false);
                    return new MediaImageViewHolder(v3);

                case Constants.ITEM_TYPE_MEDIA_VIDEO:
                    View v4 = inflater.inflate(R.layout.item_recognition_media_video, parent, false);
                    return new MediaVideoViewHolder(v4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && (boolean) payloads.get(0)) {
            try {
                Log.d("Recognition", "holder.getItemViewType():" + holder.getItemViewType());

                if (holder != null) {
                    switch (holder.getItemViewType()) {
                        case Constants.ITEM_TYPE_MAIN_IMAGE: {
                            MainImageViewHolder mainImageViewHolder = (MainImageViewHolder) holder;
                            onTimeUpdateListener.onViewPlaying(stories.get(position).getTime(),stories.get(position));
                            mainImageViewHolder.mainImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.main_image_recognition));
                           /* Glide.with(mainImageViewHolder.mainImageView.getContext())
                                    .load(stories.get(position).getImage())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mainImageViewHolder.mainImageView);*/
                            break;
                        }
                        case Constants.ITEM_TYPE_USER: {
                            UserViewHolder userViewHolder = (UserViewHolder) holder;
                            onTimeUpdateListener.onViewPlaying(stories.get(position).getTime(),stories.get(position));
                            userViewHolder.userName.setText(stories.get(position).getOwner().getFirstName() + " " + stories.get(position).getOwner().getLastName());

                            Glide.with(userViewHolder.userImageView.getContext())
                                    .load(stories.get(position).getImage())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(userViewHolder.userImageView);
                            break;
                        }
                        case Constants.ITEM_TYPE_MEDIA_IMAGE: {
                            MediaImageViewHolder mediaImageViewHolder = (MediaImageViewHolder) holder;
                            onTimeUpdateListener.onViewPlaying(stories.get(position).getTime(),stories.get(position));
                            Glide.with(mediaImageViewHolder.mediaImage.getContext())
                                    .load(stories.get(position).getImage())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mediaImageViewHolder.mediaImage);
                            break;
                        }
                        case Constants.ITEM_TYPE_MEDIA_VIDEO: {
                            MediaVideoViewHolder mediaVideoViewHolder = (MediaVideoViewHolder) holder;

                            if (exoPlayer == null) {
                                exoPlayer = new ExoPlayer.Builder(context).build();
                                try {
                                    // Build the media item.
                                    MediaItem mediaItem = MediaItem.fromUri(stories.get(position).getVideoUrl());
                                    // Set the media item to be played.
                                    exoPlayer.setMediaItem(mediaItem);
                                    // Prepare the player.
                                    exoPlayer.prepare();
                                    // Start the playback.
                                    exoPlayer.play();
                                    // Bind the player to the view.
                                    mediaVideoViewHolder.playerControlView.setPlayer(exoPlayer);
                                    exoPlayer.addListener(new Player.Listener() {
                                        @Override
                                        public void onPlaybackStateChanged(int playbackState) {

                                            Player.Listener.super.onPlaybackStateChanged(playbackState);
                                            if (playbackState == ExoPlayer.STATE_READY) {
                                                long duration = exoPlayer.getDuration();
                                                stories.get(position).setTime(duration);
                                                if (onTimeUpdateListener != null) {
                                                    onTimeUpdateListener.onViewPlaying(duration, stories.get(position));
                                                }
                                            }
                                            if (playbackState == ExoPlayer.STATE_ENDED) {
                                                if (exoPlayer != null) {
                                                    exoPlayer.stop();
                                                    exoPlayer.release();
                                                    exoPlayer = null;
                                                }
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.d("Recognition", "Exoplayer error:" + e.getMessage());
                                }
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (stories.get(position).getItemType() == Constants.ITEM_TYPE_MAIN_IMAGE) {
            return Constants.ITEM_TYPE_MAIN_IMAGE;
        } else if (stories.get(position).getItemType() == Constants.ITEM_TYPE_USER) {
            return Constants.ITEM_TYPE_USER;
        } else if (stories.get(position).getItemType() == Constants.ITEM_TYPE_MEDIA_IMAGE) {
            return Constants.ITEM_TYPE_MEDIA_IMAGE;
        } else if (stories.get(position).getItemType() == Constants.ITEM_TYPE_MEDIA_VIDEO) {
            return Constants.ITEM_TYPE_MEDIA_VIDEO;
        }
        return 0;
    }

    private class MainImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView mainImageView;

        MainImageViewHolder(View v) {
            super(v);
            mainImageView = v.findViewById(R.id.main_image);

        }
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView userImageView;
        private TextView userName;

        UserViewHolder(View v) {
            super(v);
            userImageView = v.findViewById(R.id.circleImage);
            userName = v.findViewById(R.id.user_name);

        }
    }


    private class MediaImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView mediaImage;

        MediaImageViewHolder(View v) {
            super(v);
            mediaImage = v.findViewById(R.id.media_image);
        }

    }

    private class MediaVideoViewHolder extends RecyclerView.ViewHolder {
        private StyledPlayerView playerControlView;

        MediaVideoViewHolder(View v) {
            super(v);
            playerControlView = v.findViewById(R.id.playerView);

        }

    }

    public interface onTimeUpdateListener {
        void onViewPlaying(long duration, Data data);
    }
}
