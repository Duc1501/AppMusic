package com.lcd.mymusic.searchMusicOnline;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.ConnectionService;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.databinding.FragmentSearchOnlineBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchOnlineFragment extends Fragment implements
        SearchMusicAdapter.IMusicSearch, TextWatcher {
    private FragmentSearchOnlineBinding binding;
    private ServiceConnection conn;
    private ServiceSearchMusicOnline service;
    private ExecutorService ex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchOnlineBinding.inflate(
                inflater, container, false);
        binding.rcSearch.setLayoutManager(
                new LinearLayoutManager(getActivity()));
        binding.rcSearch.setAdapter(new SearchMusicAdapter(this));
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        connectService();
        //searchSong("Thi+thoi");
        eventInput();
        return binding.getRoot();
    }

    private void connectService() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ServiceSearchMusicOnline.MyBind myBind = (ServiceSearchMusicOnline.MyBind) iBinder;
                service = myBind.getService();
                if (service.checkEmpty()){
                    searchSong("Thi+thoi");
                }else{
                    binding.rcSearch.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent();
        intent.setClassName(getContext(),
                ServiceSearchMusicOnline.class.getName());
        getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroyView() {
        getContext().unbindService(conn);
        super.onDestroyView();
    }

    private void eventInput(){
        binding.edtSearch.addTextChangedListener(this);
    }

    public void searchSong(final String search){
        if (ex !=null && !ex.isShutdown()){
            ex.shutdown();
        }
        ex = Executors.newFixedThreadPool(1);
        new AsyncTask<String, Void, List<ItemMusicSearch>>() {
            @Override
            protected List<ItemMusicSearch> doInBackground(String... values) {
                List<ItemMusicSearch> onlines = new ArrayList<>();
                try {
                    Document c = Jsoup.connect("https://chiasenhac.vn/tim-kiem?q="
                            +values[0]+"&page_music=1&filter=all").get();
                    Elements els = c.select("div.tab-content").first().select("ul.list-unstyled");
                    for (int i = 0; i<=els.size()-1; i++){
                        Element e = els.get(i);
                        Elements childEls = e.select("li.media");
                        for (Element child : childEls){
                            String linkSong =
                                    child.select("a").first().attr("href");
                            String linkImg =
                                    child.select("a").first().select("img").attr("src");
                            String title = child.select("a").first().attr("title");
                            String singer = child.select("div.author").text();
                            onlines.add(new ItemMusicSearch(linkImg, title, singer, linkSong));
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                return onlines;
            }

            @Override
            protected void onPostExecute(List<ItemMusicSearch> itemMusicSearches) {
                service.setSearchOnline(itemMusicSearches);
                binding.rcSearch.getAdapter().notifyDataSetChanged();
            }
        }.executeOnExecutor(ex, search);
    }

    @Override
    public int getCount() {
        if (service == null){
            return 0;
        }
        return service.sizeItemMusicSearch();
    }

    @Override
    public ItemMusicSearch getData(int position) {
        return service.getData(position);
    }

    @Override
    public void onClickItemSearch(int position) {
        if (service.getCurrenPosition() == position){
            ((MainActivity)getActivity()).openPlaySearch();
            return;
        }
        service.onClickItem(position);
        ((MainActivity)getActivity()).openPlaySearch();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        searchSong(editable.toString().replace("  "," ")
        .replace(" ","+"));

    }
}
