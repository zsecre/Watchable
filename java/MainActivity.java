package com.example.watchable;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    // ── API constants ─────────────────────────────────────────────────────────
    private static final String TMDB_BEARER =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNmY0N2U0NzAyZjBmZmJiMGM5Nzg4ZDA2OTk1ZWNkZSIsIm5iZiI6MTc3NjE0NDc3My4yNjgsInN1YiI6IjY5ZGRkMTg1ZTUzMmY2OTFkZWQ5NDEwOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.dy8WanI7kFpTfCorNjBgEiHfx3nJVvBrpz9EZ6veHqo";
    private static final String IMG_BACKDROP = "https://image.tmdb.org/t/p/w780";
    private static final String IMG_POSTER   = "https://image.tmdb.org/t/p/w342";

    private static final String URL_MOVIES =
            "https://api.themoviedb.org/3/trending/movie/week?language=en-US&page=1";
    private static final String URL_TV =
            "https://api.themoviedb.org/3/trending/tv/week?language=en-US&page=1";
    private static final String URL_ANIME =
            "https://api.jikan.moe/v4/seasons/now?limit=10&page=1";

    // ── Views ─────────────────────────────────────────────────────────────────
    private ViewPager2   bannerPager;
    private TextView     tvBannerBadge, tvBannerTitle, tvBannerType, tvBannerRating;
    private LinearLayout dotContainer, genreContainer, bottomNavBar;
    private RecyclerView rvAnime, rvMovies, rvTvShows;

    // ── Adapter-bound data lists ──────────────────────────────────────────────
    // Banner combines: first 5 movies + 5 TV + 5 anime = up to 15 slides
    private final List<BannerItem>  allBannerItems = new ArrayList<>();
    private final List<BannerItem>  bannerMovies   = new ArrayList<>();
    private final List<BannerItem>  bannerTv       = new ArrayList<>();
    private final List<BannerItem>  bannerAnime    = new ArrayList<>();

    // Content rows — 10 items each
    private final List<ContentItem> animeList  = new ArrayList<>();
    private final List<ContentItem> movieList  = new ArrayList<>();
    private final List<ContentItem> tvList     = new ArrayList<>();

    // ── Adapters ──────────────────────────────────────────────────────────────
    private BannerAdapter  bannerAdapter;
    private ContentAdapter animeAdapter, moviesAdapter, tvAdapter;

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<View>    dots          = new ArrayList<>();
    private int                 currentPage   = 0;
    private int                 selectedGenre = 0;
    private final AtomicInteger fetchCounter  = new AtomicInteger(0);

    private static final long AUTO_SCROLL_DELAY = 4000L;
    private Handler      autoScrollHandler;
    private Runnable     autoScrollRunnable;
    private OkHttpClient httpClient;

    private static final String[] GENRES =
            {"Action", "Adventure", "Comedy", "Drama", "Horror", "Sci-Fi"};

    // Placeholder colors cycled per item index
    private static final int[] PH_COLORS = {
            R.color.ph_1, R.color.ph_2, R.color.ph_3, R.color.ph_4,
            R.color.ph_5, R.color.ph_6, R.color.ph_7
    };

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        httpClient = new OkHttpClient();
        bindViews();
        initAdapters();
        setupGenreChips();
        setupBottomNav();

        // Fire all 3 API requests concurrently
        fetchTrendingMovies();
        fetchTrendingTv();
        fetchLatestAnime();
    }

    @Override protected void onResume()  { super.onResume();  startAutoScroll(); }
    @Override protected void onPause()   { super.onPause();   stopAutoScroll();  }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (autoScrollHandler != null) autoScrollHandler.removeCallbacksAndMessages(null);
    }

    // ── View binding ──────────────────────────────────────────────────────────
    private void bindViews() {
        bannerPager    = findViewById(R.id.bannerPager);
        tvBannerBadge  = findViewById(R.id.tvBannerBadge);
        tvBannerTitle  = findViewById(R.id.tvBannerTitle);
        tvBannerType   = findViewById(R.id.tvBannerType);
        tvBannerRating = findViewById(R.id.tvBannerRating);
        dotContainer   = findViewById(R.id.dotContainer);
        genreContainer = findViewById(R.id.genreContainer);
        bottomNavBar   = findViewById(R.id.bottomNavBar);
        rvAnime        = findViewById(R.id.rvAnime);
        rvMovies       = findViewById(R.id.rvMovies);
        rvTvShows      = findViewById(R.id.rvTvShows);
    }

    // ── Wire up empty adapters — data arrives later via API ───────────────────
    private void initAdapters() {
        // Banner ViewPager2
        bannerAdapter = new BannerAdapter(allBannerItems);
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(1);
        bannerPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                currentPage = position;
                updateBannerInfo(position);
                updateDots(position);
            }
        });

        // Content rows
        animeAdapter  = new ContentAdapter(animeList);
        moviesAdapter = new ContentAdapter(movieList);
        tvAdapter     = new ContentAdapter(tvList);

        bindRow(rvAnime,    animeAdapter);
        bindRow(rvMovies,   moviesAdapter);
        bindRow(rvTvShows,  tvAdapter);

        // Auto-scroll runnable
        autoScrollHandler  = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override public void run() {
                if (!allBannerItems.isEmpty()) {
                    int next = (currentPage + 1) % allBannerItems.size();
                    bannerPager.setCurrentItem(next, true);
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };
    }

    private void bindRow(RecyclerView rv, ContentAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);
    }

    // ── Called on UI thread when all 3 fetches have completed ─────────────────
    private void onAllFetchesDone() {
        // Build 15-slide banner: 5 movies + 5 TV + 5 anime
        allBannerItems.clear();
        int movieCount = Math.min(5, bannerMovies.size());
        int tvCount    = Math.min(5, bannerTv.size());
        int animeCount = Math.min(5, bannerAnime.size());
        for (int i = 0; i < movieCount; i++) allBannerItems.add(bannerMovies.get(i));
        for (int i = 0; i < tvCount;    i++) allBannerItems.add(bannerTv.get(i));
        for (int i = 0; i < animeCount; i++) allBannerItems.add(bannerAnime.get(i));

        // Notify all adapters
        bannerAdapter.notifyDataSetChanged();
        animeAdapter.notifyDataSetChanged();
        moviesAdapter.notifyDataSetChanged();
        tvAdapter.notifyDataSetChanged();

        // Boot up banner UI
        if (!allBannerItems.isEmpty()) {
            buildDots(allBannerItems.size(), 0);
            updateBannerInfo(0);
            startAutoScroll();
        }
    }

    /** Increments fetch counter; when all 3 are done calls onAllFetchesDone on UI thread. */
    private void signalFetchDone() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (fetchCounter.incrementAndGet() == 3) {
                    onAllFetchesDone();
                }
            }
        });
    }

    // ── API: Trending Movies (TMDB) ───────────────────────────────────────────
    private void fetchTrendingMovies() {
        Request request = new Request.Builder()
                .url(URL_MOVIES)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", TMDB_BEARER)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                signalFetchDone();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                final List<BannerItem>  tempBanner  = new ArrayList<>();
                final List<ContentItem> tempContent = new ArrayList<>();
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        JSONObject json    = new JSONObject(body.string());
                        JSONArray  results = json.getJSONArray("results");
                        for (int i = 0; i < Math.min(10, results.length()); i++) {
                            JSONObject item     = results.getJSONObject(i);
                            String     title    = item.optString("title", "Unknown");
                            double     rating   = item.optDouble("vote_average", 0.0);
                            String     backdrop = item.optString("backdrop_path", "");
                            String     poster   = item.optString("poster_path", "");
                            String     rStr     = String.format(Locale.US, "%.1f", rating);
                            int        phColor  = PH_COLORS[i % PH_COLORS.length];

                            String bannerUrl = backdrop.isEmpty() ? "" : IMG_BACKDROP + backdrop;
                            String posterUrl = poster.isEmpty()   ? "" : IMG_POSTER   + poster;

                            tempBanner.add(new BannerItem(
                                    title, "Movie", rStr, "MOVIE", bannerUrl, phColor));
                            tempContent.add(new ContentItem(title, rStr, posterUrl, phColor));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        bannerMovies.addAll(tempBanner);
                        movieList.addAll(tempContent);
                        signalFetchDone();
                    }
                });
            }
        });
    }

    // ── API: Trending TV Shows (TMDB) ─────────────────────────────────────────
    private void fetchTrendingTv() {
        Request request = new Request.Builder()
                .url(URL_TV)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", TMDB_BEARER)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                signalFetchDone();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                final List<BannerItem>  tempBanner  = new ArrayList<>();
                final List<ContentItem> tempContent = new ArrayList<>();
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        JSONObject json    = new JSONObject(body.string());
                        JSONArray  results = json.getJSONArray("results");
                        for (int i = 0; i < Math.min(10, results.length()); i++) {
                            JSONObject item     = results.getJSONObject(i);
                            // TV shows use "name" not "title"
                            String     title    = item.optString("name", "Unknown");
                            double     rating   = item.optDouble("vote_average", 0.0);
                            String     backdrop = item.optString("backdrop_path", "");
                            String     poster   = item.optString("poster_path", "");
                            String     rStr     = String.format(Locale.US, "%.1f", rating);
                            int        phColor  = PH_COLORS[i % PH_COLORS.length];

                            String bannerUrl = backdrop.isEmpty() ? "" : IMG_BACKDROP + backdrop;
                            String posterUrl = poster.isEmpty()   ? "" : IMG_POSTER   + poster;

                            tempBanner.add(new BannerItem(
                                    title, "TV Show", rStr, "TV", bannerUrl, phColor));
                            tempContent.add(new ContentItem(title, rStr, posterUrl, phColor));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        bannerTv.addAll(tempBanner);
                        tvList.addAll(tempContent);
                        signalFetchDone();
                    }
                });
            }
        });
    }

    // ── API: Latest Anime (Jikan / MyAnimeList) ───────────────────────────────
    private void fetchLatestAnime() {
        Request request = new Request.Builder()
                .url(URL_ANIME)
                .get()
                .addHeader("accept", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                signalFetchDone();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                final List<BannerItem>  tempBanner  = new ArrayList<>();
                final List<ContentItem> tempContent = new ArrayList<>();
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        JSONObject json    = new JSONObject(body.string());
                        JSONArray  data    = json.getJSONArray("data");
                        for (int i = 0; i < Math.min(10, data.length()); i++) {
                            JSONObject item   = data.getJSONObject(i);
                            String     title  = item.optString("title", "Unknown");
                            double     score  = item.optDouble("score", 0.0);
                            String     rStr   = score > 0
                                    ? String.format(Locale.US, "%.1f", score) : "N/A";
                            int        phColor = PH_COLORS[i % PH_COLORS.length];

                            // Poster image: images.jpg.large_image_url
                            String posterUrl = "";
                            JSONObject images = item.optJSONObject("images");
                            if (images != null) {
                                JSONObject jpg = images.optJSONObject("jpg");
                                if (jpg != null) posterUrl = jpg.optString("large_image_url", "");
                            }

                            // Banner image: prefer trailer thumbnail (16:9),
                            // fallback to poster
                            String bannerUrl = "";
                            JSONObject trailer = item.optJSONObject("trailer");
                            if (trailer != null) {
                                JSONObject ti = trailer.optJSONObject("images");
                                if (ti != null) {
                                    bannerUrl = ti.optString("large_image_url", "");
                                    // Jikan returns "null" string when absent
                                    if ("null".equals(bannerUrl)) bannerUrl = "";
                                }
                            }
                            if (bannerUrl.isEmpty()) bannerUrl = posterUrl;

                            tempBanner.add(new BannerItem(
                                    title, "Anime", rStr, "ANIME", bannerUrl, phColor));
                            tempContent.add(new ContentItem(title, rStr, posterUrl, phColor));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        bannerAnime.addAll(tempBanner);
                        animeList.addAll(tempContent);
                        signalFetchDone();
                    }
                });
            }
        });
    }

    // ── Banner info overlay ───────────────────────────────────────────────────
    private void updateBannerInfo(int i) {
        if (allBannerItems.isEmpty() || i >= allBannerItems.size()) return;
        BannerItem b = allBannerItems.get(i);
        tvBannerBadge.setText(b.badge);
        tvBannerTitle.setText(b.title);
        tvBannerType.setText(b.type);
        tvBannerRating.setText(b.rating);
    }

    private void startAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null)
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }
    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null)
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    // ── Dot indicators ────────────────────────────────────────────────────────
    private void buildDots(int count, int active) {
        dotContainer.removeAllViews();
        dots.clear();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            dotContainer.addView(dot, makeDotParams(i == active));
            dot.setBackground(ContextCompat.getDrawable(this,
                    i == active ? R.drawable.dot_active : R.drawable.dot_inactive));
            dots.add(dot);
        }
    }

    private void updateDots(int active) {
        for (int i = 0; i < dots.size(); i++) {
            boolean on = (i == active);
            dots.get(i).setLayoutParams(makeDotParams(on));
            dots.get(i).setBackground(ContextCompat.getDrawable(this,
                    on ? R.drawable.dot_active : R.drawable.dot_inactive));
        }
    }

    private LinearLayout.LayoutParams makeDotParams(boolean active) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                dp(active ? 20 : 6), dp(active ? 5 : 6));
        p.setMargins(0, 0, dp(4), 0);
        p.gravity = Gravity.CENTER_VERTICAL;
        return p;
    }

    // ── Genre chips ───────────────────────────────────────────────────────────
    private void setupGenreChips() {
        genreContainer.removeAllViews();
        for (int i = 0; i < GENRES.length; i++) {
            final int idx = i;
            TextView chip = new TextView(this);
            chip.setText(GENRES[i]);
            chip.setTextSize(13f);
            chip.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, dp(10), 0);
            chip.setLayoutParams(p);
            chip.setPadding(dp(18), dp(8), dp(18), dp(8));
            if (i == selectedGenre) {
                chip.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_bg));
                chip.setTextColor(ContextCompat.getColor(this, R.color.bg_dark));
            } else {
                chip.setBackground(ContextCompat.getDrawable(this, R.drawable.genre_chip_bg));
                chip.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent));
            }
            chip.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    selectedGenre = idx;
                    setupGenreChips();
                }
            });
            genreContainer.addView(chip);
        }
    }

    // ── Bottom navigation ─────────────────────────────────────────────────────
    private void setupBottomNav() {
        final String[] labels = {"All", "Movies", "Tv", "Library", "Me"};
        final int[]    icons  = {
                R.drawable.ic_nav_all, R.drawable.ic_nav_movies, R.drawable.ic_nav_tv,
                R.drawable.ic_nav_library, R.drawable.ic_nav_me};

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams ip =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            item.setLayoutParams(ip);
            item.setPadding(0, dp(8), 0, dp(6));

            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconP = new LinearLayout.LayoutParams(dp(22), dp(22));
            iconP.gravity = Gravity.CENTER_HORIZONTAL;
            icon.setLayoutParams(iconP);
            icon.setImageResource(icons[i]);
            icon.setColorFilter(i == 0
                    ? ContextCompat.getColor(this, R.color.cyan_accent)
                    : ContextCompat.getColor(this, R.color.gray_text));

            TextView lbl = new TextView(this);
            LinearLayout.LayoutParams lblP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lblP.gravity   = Gravity.CENTER_HORIZONTAL;
            lblP.topMargin = dp(3);
            lbl.setLayoutParams(lblP);
            lbl.setText(labels[i]);
            lbl.setTextSize(9.5f);
            lbl.setTextColor(i == 0
                    ? ContextCompat.getColor(this, R.color.cyan_accent)
                    : ContextCompat.getColor(this, R.color.gray_text));

            View dot = new View(this);
            LinearLayout.LayoutParams dotP = new LinearLayout.LayoutParams(dp(4), dp(4));
            dotP.gravity   = Gravity.CENTER_HORIZONTAL;
            dotP.topMargin = dp(3);
            dot.setLayoutParams(dotP);
            dot.setBackground(ContextCompat.getDrawable(this, R.drawable.nav_dot));
            dot.setVisibility(i == 0 ? View.VISIBLE : View.INVISIBLE);

            item.addView(icon);
            item.addView(lbl);
            item.addView(dot);

            item.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    for (int j = 0; j < bottomNavBar.getChildCount(); j++) {
                        LinearLayout child = (LinearLayout) bottomNavBar.getChildAt(j);
                        ImageView ci = (ImageView) child.getChildAt(0);
                        TextView  cl = (TextView)  child.getChildAt(1);
                        View      cd = child.getChildAt(2);
                        boolean   on = (j == idx);
                        int color = on
                                ? ContextCompat.getColor(MainActivity.this, R.color.cyan_accent)
                                : ContextCompat.getColor(MainActivity.this, R.color.gray_text);
                        ci.setColorFilter(color);
                        cl.setTextColor(color);
                        cd.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
                    }
                }
            });
            bottomNavBar.addView(item);
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INNER CLASS — BannerItem
    // ═════════════════════════════════════════════════════════════════════════
    static class BannerItem {
        final String title, type, rating, badge, imageUrl;
        final int    colorRes;
        BannerItem(String title, String type, String rating,
                   String badge, String imageUrl, int colorRes) {
            this.title    = title;    this.type     = type;
            this.rating   = rating;  this.badge    = badge;
            this.imageUrl = imageUrl; this.colorRes = colorRes;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INNER CLASS — ContentItem
    // ═════════════════════════════════════════════════════════════════════════
    static class ContentItem {
        final String title, rating, imageUrl;
        final int    colorRes;
        ContentItem(String title, String rating, String imageUrl, int colorRes) {
            this.title    = title;    this.rating   = rating;
            this.imageUrl = imageUrl; this.colorRes = colorRes;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INNER CLASS — BannerAdapter  (ViewPager2)
    // ═════════════════════════════════════════════════════════════════════════
    class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {
        private final List<BannerItem> items;
        BannerAdapter(List<BannerItem> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner, parent, false);
            return new VH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            BannerItem item = items.get(pos);
            ColorDrawable ph = new ColorDrawable(
                    ContextCompat.getColor(MainActivity.this, item.colorRes));
            Glide.with(MainActivity.this)
                    .load(item.imageUrl)
                    .placeholder(ph).error(ph).centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .into(h.iv);
        }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ImageView iv;
            VH(@NonNull View v) { super(v); iv = v.findViewById(R.id.ivBannerImage); }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INNER CLASS — ContentAdapter  (all 3 horizontal rows)
    // ═════════════════════════════════════════════════════════════════════════
    class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.VH> {
        private final List<ContentItem> items;
        ContentAdapter(List<ContentItem> items) { this.items = items; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_content_card, parent, false);
            return new VH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            ContentItem item = items.get(pos);
            h.tvTitle.setText(item.title);
            h.tvRating.setText(item.rating);
            ColorDrawable ph = new ColorDrawable(
                    ContextCompat.getColor(MainActivity.this, item.colorRes));
            Glide.with(MainActivity.this)
                    .load(item.imageUrl)
                    .placeholder(ph).error(ph).centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .into(h.ivPoster);
        }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ImageView ivPoster;
            final TextView  tvTitle, tvRating;
            VH(@NonNull View v) {
                super(v);
                ivPoster = v.findViewById(R.id.ivPoster);
                tvTitle  = v.findViewById(R.id.tvTitle);
                tvRating = v.findViewById(R.id.tvRating);
            }
        }
    }
}
