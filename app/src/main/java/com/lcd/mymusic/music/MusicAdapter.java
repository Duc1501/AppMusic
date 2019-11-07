package com.lcd.mymusic.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.ItemMusicBinding;

import java.io.File;
import java.text.SimpleDateFormat;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private IMusic inter;

    public MusicAdapter(IMusic inter){
        this.inter=inter;
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return  new MusicHolder(
                ItemMusicBinding.inflate(
                        LayoutInflater.from(viewGroup.getContext()),
                        viewGroup,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicHolder musicHolder, int i) {
        MusicOffline data = inter.getData(i);
        musicHolder.binding.tvNameBaihat.setText(data.getName());
        musicHolder.binding.tvNameCasi.setText(data.getNameAtist());
        musicHolder.binding.tvTime.setText(
                new SimpleDateFormat("mm:ss")
                    .format(data.getDuration())
        );
        if (data.getPathAlbum() == null || data.getPathAlbum().equals("")){
            Glide.with(musicHolder.itemView.getContext())
                    .load(R.drawable.logo_music_error)
                    .into(musicHolder.binding.ivShowImage);
        }else {
            Glide.with(musicHolder.itemView.getContext())
                    .load(new File(data.getPathAlbum()))
                    .placeholder(R.drawable.logo_music_error)
                    .error(R.drawable.logo_music_error)
                    .into(musicHolder.binding.ivShowImage);
        }

        musicHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inter.onClickItem(musicHolder.getAdapterPosition(), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inter.getCount();
    }

    static class MusicHolder extends RecyclerView.ViewHolder{
        private ItemMusicBinding binding;
        public MusicHolder(ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }

    public interface IMusic{
        int getCount();

        MusicOffline getData(int position);
        void onClickItem(int position, boolean isOpen);
    }

}
