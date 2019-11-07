package com.lcd.mymusic.musiconline;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.databinding.FragmentMusicOnlineBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicOnlineFragment extends Fragment implements MusicOnlineAdapter.IMusicOnline {

    private FragmentMusicOnlineBinding binding;
    private ServiceConnection conn;
    private ServiceMusicOnline service;
    private ExecutorService ex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMusicOnlineBinding.inflate(inflater, container, false);
        pareHtml();
        binding.rcMusicOnline.setLayoutManager(
                new LinearLayoutManager(getActivity()));
        binding.rcMusicOnline.setAdapter(new MusicOnlineAdapter(this));
        //  eventInput();
        connectService();
        return binding.getRoot();
    }

    private void connectService() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ServiceMusicOnline.MyBind bind =
                        (ServiceMusicOnline.MyBind) iBinder;
                service = bind.getService();
                if (service.checkEmpty()) {
                    pareHtml();
                } else {
                    binding.rcMusicOnline.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent();
        intent.setClassName(getContext(),
                ServiceMusicOnline.class.getName());
        //Kết nối đến service
        getContext().bindService(intent, conn,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroyView() {
        getContext().unbindService(conn);
        super.onDestroyView();
    }

    public void searchSong(String search) {
        if (ex != null && !ex.isShutdown()) {
            ex.isShutdown();
        }
        ex = Executors.newFixedThreadPool(1);
        new AsyncTask<String, Void, List<ItemMusicOnline>>() {

            @Override
            protected List<ItemMusicOnline> doInBackground(String... values) {
                List<ItemMusicOnline> onlines = new ArrayList<>();
                try {
                    Document c = Jsoup.connect("https://chiasenhac.vn/tim-kiem?q="
                            + values[0] + "&page_music=1&filter=all").get();
                    Elements els = c.select("div.tab-content").first().select("ul.list-unstyled");
                    for (int i = 0; i <= els.size() - 1; i++) {
                        Element e = els.get(i);
                        Elements childEls = e.select("li.media");
                        for (Element child : childEls) {
                            String linkSong =
                                    child.select("a").first().attr("href");
                            String linkImg =
                                    child.select("a").first().select("img").attr("src");
                            String title = child.select("a").first().attr("title");
                            String singer = child.select("div.author").text();
                            onlines.add(new ItemMusicOnline(linkImg, title, singer, linkSong));
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                return onlines;
            }

            @Override
            protected void onPostExecute(List<ItemMusicOnline> itemMusicOnlines) {
                service.setMusicOnline(itemMusicOnlines);
                binding.rcMusicOnline.getAdapter().notifyDataSetChanged();
                ex = null;
            }
        }.executeOnExecutor(ex, search);
    }

    @SuppressLint("StaticFieldLeak")
    public void pareHtml() {
        new AsyncTask<String, Void, List<ItemMusicOnline>>() {

            @Override
            protected List<ItemMusicOnline> doInBackground(String... values) {
                try {
                    return loadMusiconline("https://chiasenhac.vn/bang-xep-hang/tuan.html");
                } catch (IOException | IndexOutOfBoundsException  e) {
                    e.printStackTrace();
                    SystemClock.sleep(1000);
                    try {
                        return loadMusiconline("https://chiasenhac.vn/bang-xep-hang/tuan.html");
                    } catch (IOException | NullPointerException exc) {
                        exc.printStackTrace();
                        return null;
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ItemMusicOnline> itemMusicOnlines) {
                service.setMusicOnline(itemMusicOnlines);
                binding.rcMusicOnline.getAdapter().notifyDataSetChanged();
            }
        }.execute();
    }

    private List<ItemMusicOnline> loadMusiconline(String link) throws IOException, NullPointerException {
        List<ItemMusicOnline> onlines = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements els = doc.select("div.tab-content").select("div.tab_music_bxh");
        Element e = els.get(1);
        Elements childEls = e.select("li.media");
        for (Element child : childEls) {
            String linkSong =
                    child.select("a").first().attr("href");
            String linkImage = child.select("a").first().select("img").attr("src");
            String nameSong = child.select("a").first().attr("title");
            String nameSinger = child.select("div.author").text();
            onlines.add(new ItemMusicOnline(linkImage, nameSong, nameSinger, linkSong));
        }
        return onlines;
    }

    @Override
    public int getCount() {
        if (service == null) {
            return 0;
        }
        return service.sizeItemMusicOnline();
    }

    @Override
    public ItemMusicOnline getData(int position) {
        return service.getData(position);
    }

    @Override
    public void onClickItem(int position) {
        if (service.getCurrentPosition() == position) {
            ((MainActivity) getActivity())
                    .openPlayer();
            return;
        }
        service.onClickItem(position);
        ((MainActivity) getActivity())
                .openPlayer();
    }
}
