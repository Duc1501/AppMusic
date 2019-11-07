package com.lcd.mymusic.searchMusicOnline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.ItemMusicSearchBinding;

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.SearchMusicViewHolder> {
    private IMusicSearch inter;

    public SearchMusicAdapter(IMusicSearch inter) {
        this.inter = inter;
    }

    @NonNull
    @Override
    public SearchMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchMusicViewHolder(ItemMusicSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchMusicViewHolder holder, final int i) {
        ItemMusicSearch data = inter.getData(i);
        holder.binding.nameSongSearch.setText(data.getSongName());
        holder.binding.nameSingerSearch.setText(data.getArtistName());
        if (data.getLinkImage() == null || data.getLinkImage().equals("")){
            Glide.with(holder.binding.avataSearch)
                    .load(R.drawable.logo_music_error)
                    .into(holder.binding.avataSearch);
        }else {
            Glide.with(holder.binding.avataSearch)
                    .load(data.getLinkImage())
                    .error(R.drawable.logo_music_error)
                    .placeholder(R.drawable.logo_music_error)
                    .into(holder.binding.avataSearch);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inter.onClickItemSearch(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return inter.getCount();
    }

    public interface IMusicSearch{
        int getCount();
        ItemMusicSearch getData(int position);
        void onClickItemSearch(int position);
    }

    static class SearchMusicViewHolder extends RecyclerView.ViewHolder{
        private ItemMusicSearchBinding binding;
        public SearchMusicViewHolder(ItemMusicSearchBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
