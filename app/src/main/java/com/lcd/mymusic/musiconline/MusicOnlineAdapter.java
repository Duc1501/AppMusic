package com.lcd.mymusic.musiconline;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.ItemMusicOnlineBinding;

public class MusicOnlineAdapter extends RecyclerView.Adapter<MusicOnlineAdapter.MusicOnlineViewHolder> {
    private IMusicOnline inter;

    public MusicOnlineAdapter(IMusicOnline inter) {
        this.inter = inter;
    }

    @NonNull
    @Override
    public MusicOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new MusicOnlineViewHolder(ItemMusicOnlineBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                viewGroup,
                false
        ));

    }

    @Override
    public void onBindViewHolder(@NonNull final MusicOnlineViewHolder holder, final int i) {
        ItemMusicOnline data = inter.getData(i);
        holder.binding.tvNameSongOnline.setText(data.getSongName());
        holder.binding.tvNameSingerOnline.setText(data.getArtistName());
        if (data.getLinkImage()== null ||data.getLinkImage().equals("")){
            Glide.with(holder.binding.imImageAvatarOnline)
                    .load(R.drawable.logo_music_error)
                    .into(holder.binding.imImageAvatarOnline);
        }else {
            Glide.with(holder.binding.imImageAvatarOnline)
                    .load(data.getLinkImage())
                    .error(R.drawable.logo_music_error)
                    .placeholder(R.drawable.logo_music_error)
                    .into(holder.binding.imImageAvatarOnline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inter.onClickItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return inter.getCount();
    }

    public interface IMusicOnline{
        int getCount();
        ItemMusicOnline getData(int position);
        void onClickItem(int position);
    }

    static class MusicOnlineViewHolder extends RecyclerView.ViewHolder{
        private ItemMusicOnlineBinding binding;
        public MusicOnlineViewHolder(ItemMusicOnlineBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }
}
