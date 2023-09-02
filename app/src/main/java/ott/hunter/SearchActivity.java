package ott.hunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clevertap.android.sdk.CleverTapAPI;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import ott.hunter.adapters.CountryAdapter;
import ott.hunter.models.CommonModels;
import ott.hunter.models.home_content.HomeContent;
import ott.hunter.models.single_details.Country;
import ott.hunter.models.single_details.Genre;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.HomeContentApi;
import ott.hunter.roomdatabase.DatabaseClient;
import ott.hunter.roomdatabase.Task;
import ott.hunter.roomdatabase.TasksAdapter;
import ott.hunter.utils.Constants;
import ott.hunter.utils.NetworkInst;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.ToastMsg;
import ott.hunter.widget.RangeSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    /*  TasksAdapter adapter;*/
    TextView tv_clearall;
    private boolean isDark;
    private RangeSeekBar range_seek_bar;
    private TextView year_min, year_max, range_tv;
    private ImageView img_search;
    RecyclerView recyclerViewhistory;

    AppCompatButton search_btn, clear_btn;
    private Button btn_flex_1, btn_flex_2, btn_flex_3;
    public static EditText search_edit_text;
    private LinearLayout rangeLayout;
    private RelativeLayout tvCategoryLayout, genreLayout, countryLayout;
    private EditText countrySpinner, tvCategorySpinner;
    public static EditText genreSpinner;
    private boolean[] selectedType = new boolean[3];
    //    private int selectedGenreId = 0;
    public static int selectedGenreId = 0;
    private int selectedTvCategoryId = 0;
    private int selectedCountryId = 0;
    RecyclerView recyclerView;

    public static String searchkeyword = "";
    private List<Genre> items = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private List<Task> taskData = new ArrayList<>();
    private List<CommonModels> countryArrayList = new ArrayList<>();
    public TextView view_more;
    SearchGridAdapter mAdapter;
    LinearLayout lnr_search;
    private CountryAdapter countryAdapter;
    RecyclerView country_rv;


    CleverTapAPI clevertapSearchdInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);

        clevertapSearchdInstance= CleverTapAPI.getDefaultInstance(getApplicationContext());


        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
//        isDark = sharedPreferences.getBoolean("dark", false);
//
//        if (isDark) {
//            setTheme(R.style.AppThemeDark);
//        } else {
//            setTheme(R.style.AppThemeLight);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        view_more = findViewById(R.id.view_more);
        lnr_search = findViewById(R.id.lnr_search);

/*

        adapter = new TasksAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SearchActivity.this, searchkeyword, Toast.LENGTH_SHORT).show();
            }
        });
*/


        initComponent();

        getLanguage();

//        if (!isDark) {
//            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            search_btn.setBackgroundResource(R.drawable.btn_rect_primary);
//            search_btn.setTextColor(getResources().getColor(R.color.white));
//            genreSpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_light));
//            tvCategorySpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_light));
//            countrySpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_light));
//            //flex btn
//            btn_flex_1.setBackground(getResources().getDrawable(R.drawable.btn_rounded_primary_outline_flex));
//            btn_flex_2.setBackground(getResources().getDrawable(R.drawable.btn_rounded_primary_outline_flex));
//            btn_flex_3.setBackground(getResources().getDrawable(R.drawable.btn_rounded_primary_outline_flex));
//        } else {
//            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
////            search_btn.setBackgroundResource(R.drawable.btn_rect_primary);
////            search_btn.setTextColor(getResources().getColor(R.color.white));
////            clear_btn.setTextColor(getResources().getColor(R.color.white));
//            range_seek_bar.setBarHighlightColor(getResources().getColor(R.color.grey_60));
//            range_seek_bar.setRightThumbColor(getResources().getColor(R.color.grey_60));
//            range_seek_bar.setRightThumbHighlightColor(getResources().getColor(R.color.grey_90));
//            range_seek_bar.setLeftThumbColor(getResources().getColor(R.color.grey_60));
//            range_seek_bar.setLeftThumbHighlightColor(getResources().getColor(R.color.grey_90));
//            //spinner
//            genreSpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_dark));
//            tvCategorySpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_dark));
//            countrySpinner.setBackground(getResources().getDrawable(R.drawable.edit_text_round_bg_overlay_dark));
//            //flex btn
//            btn_flex_1.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline_flex));
//            btn_flex_2.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline_flex));
//            btn_flex_3.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline_flex));
//        }

        toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "profile_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }


    private void initComponent() {
        search_btn = findViewById(R.id.search_btn);
        recyclerViewhistory = findViewById(R.id.recyclerViewhistory);
        tv_clearall = findViewById(R.id.tv_clearall);
        clear_btn = findViewById(R.id.clear_btn);
        genreLayout = findViewById(R.id.genre_layout);
        genreSpinner = findViewById(R.id.genre_spinner);
        tvCategoryLayout = findViewById(R.id.tv_category_layout);
        country_rv = findViewById(R.id.country_rv);


        tvCategorySpinner = findViewById(R.id.tv_category_spinner);
        countryLayout = findViewById(R.id.country_layout);
        countrySpinner = findViewById(R.id.country_spinner);
        btn_flex_1 = findViewById(R.id.btn_flex_1);
        btn_flex_2 = findViewById(R.id.btn_flex_2);
        btn_flex_3 = findViewById(R.id.btn_flex_3);
        btn_flex_1.setSelected(true);
        btn_flex_2.setSelected(true);
        btn_flex_3.setSelected(true);
        selectedType[0] = true;
        selectedType[1] = true;
        selectedType[2] = true;


        //get searched history from local db

        getTasks();


        tv_clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteTask();


            }
        });


        //populate genre list
        List<String> genreList = new ArrayList<>();
        if (Constants.genreList != null) {
            genreList.add(0, "All Genres");
            for (int i = 0; i < Constants.genreList.size(); i++) {
                genreList.add((i + 1), Constants.genreList.get(i).getName());
            }
        }
        final String[] genreArray = new String[genreList.size()];
        for (int i = 0; i < genreList.size(); i++) {
            genreArray[i] = genreList.get(i);
        }


        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //   recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 12), true));

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new SearchGridAdapter(this, Constants.genreList);
        recyclerView.setAdapter(mAdapter);

        genreSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("Select Genre");
                builder.setSingleChoiceItems(genreArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) v).setText(genreArray[i]);
                        if (i != 0) {
                            selectedGenreId = Integer.parseInt(Constants.genreList.get(i - 1).getGenreId());
                            SearchGridAdapter.selectedIndex = -1;
                            mAdapter.notifyDataSetChanged();
                        } else {
                            selectedGenreId = 0;

                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });


        //setup tv category spinner
        List<String> tvCategoryList = new ArrayList<>();
        if (Constants.tvCategoryList != null) {
            tvCategoryList.add(0, "All Categories");
            for (int i = 0; i < Constants.tvCategoryList.size(); i++) {
                tvCategoryList.add((i + 1), Constants.tvCategoryList.get(i).getLiveTvCategory());
            }
        }
        final String[] tvCategoryArray = new String[tvCategoryList.size()];
        for (int i = 0; i < tvCategoryList.size(); i++) {
            tvCategoryArray[i] = tvCategoryList.get(i);
        }

        tvCategorySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("Select Tv Category");
                builder.setSingleChoiceItems(tvCategoryArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) v).setText(tvCategoryArray[i]);
                        if (i != 0)
                            selectedTvCategoryId = Integer.parseInt(Constants.tvCategoryList.get(i - 1).getLiveTvCategoryId());
                        else
                            selectedTvCategoryId = 0;
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        //setup country spinner
        final List<String> countryList = new ArrayList<>();
        if (Constants.countryList != null) {
//            countryList.add(0, "All Countries");
            countryList.add(0, "All Language");
            for (int i = 0; i < Constants.countryList.size(); i++) {
                countryList.add((i + 1), Constants.countryList.get(i).getName());
            }
        }

        final String[] countryArray = new String[(countryList.size())];
        for (int i = 0; i < countryList.size(); i++) {
            countryArray[i] = countryList.get(i);
        }
        countrySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
//                builder.setTitle("Select Country");
                builder.setTitle("Select Language");
                builder.setSingleChoiceItems(countryArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) v).setText(countryArray[i]);
                        if (i != 0)
                            selectedCountryId = Integer.parseInt(Constants.countryList.get(i - 1).getCountryId());
                        else
                            selectedCountryId = 0;
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });


        range_tv = findViewById(R.id.rangeTV);
        rangeLayout = findViewById(R.id.range_picker_layout);
        range_seek_bar = findViewById(R.id.range_seek_bar);
        img_search = findViewById(R.id.img_search);
        year_min = findViewById(R.id.year_min);
        year_max = findViewById(R.id.year_max);
        search_edit_text = findViewById(R.id.search_text);


        //set min year and max year
        range_seek_bar.setMaxValue(Float.parseFloat(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        range_seek_bar.setMinValue(Float.parseFloat(getString(R.string.year_range_start)));
        // set listener
        range_seek_bar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                year_min.setText(String.valueOf(minValue));
                year_max.setText(String.valueOf(maxValue));
            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();

                save_text_history();

                HashMap<String, Object> SearchKeywordAction= new HashMap<String, Object>();
                SearchKeywordAction.put("Keyword",search_edit_text.getText().toString() );
                clevertapSearchdInstance.pushEvent("Searched", SearchKeywordAction);

            }
        });


        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
                save_text_history();

                HashMap<String, Object> SearchKeywordAction= new HashMap<String, Object>();
                SearchKeywordAction.put("Keyword",search_edit_text.getText().toString() );
                clevertapSearchdInstance.pushEvent("Searched", SearchKeywordAction);


            }
        });


        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getLanguage() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        HomeContentApi api = retrofit.create(HomeContentApi.class);
        Call<ResponseBody> call = api.getCountry(AppConfig.API_KEY, PreferenceUtils.getUserId(SearchActivity.this));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray countryArray = jsonObject.getJSONArray("all_country");

                        for (int i = 0; i < countryArray.length(); i++) {
                            JSONObject jsonObject1 = countryArray.getJSONObject(i);
                            CommonModels country = new CommonModels();
                            country.setId(jsonObject1.getString("country_id"));
                            country.setTitle(jsonObject1.getString("name"));
                            country.setImageUrl(jsonObject1.getString("image_url"));
                            countryArrayList.add(country);
                        }

                        if (countryArrayList.size() > 0) {
                            country_rv.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.HORIZONTAL, false));
                            country_rv.setHasFixedSize(true);
                            country_rv.setNestedScrollingEnabled(false);
                            countryAdapter = new CountryAdapter(SearchActivity.this, countryArrayList, "home");
                            country_rv.setAdapter(countryAdapter);
                            countryLayout.setVisibility(View.VISIBLE);
                        } else {
                            countryLayout.setVisibility(View.GONE);
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        countryLayout.setVisibility(View.GONE);
                    }
                } else {
                    countryLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                countryLayout.setVisibility(View.GONE);
                //              swipeRefreshLayout.setRefreshing(false);
//                shimmerFrameLayout.stopShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                coordinatorLayout.setVisibility(View.VISIBLE);
//                scrollView.setVisibility(View.GONE);

            }
        });
    }

    private void deleteTask() {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(SearchActivity.this).getAppDatabase()
                        .taskDao()
                        .delete(taskData);
                getTasks();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }

    private void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<Task>> {

            @Override
            protected List<Task> doInBackground(Void... voids) {
                taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                taskData = new ArrayList<>();
                taskData = tasks;
                if (tasks.size() > 0) {
                    TasksAdapter adapter = new TasksAdapter(SearchActivity.this, tasks, search_edit_text);
                    //  recyclerView.setAdapter(adapter);

                    recyclerViewhistory.setLayoutManager(new LinearLayoutManager(SearchActivity.this,
                            RecyclerView.VERTICAL, false));
                    recyclerViewhistory.setAdapter(adapter);
                    lnr_search.setVisibility(View.VISIBLE);
                } else {
                    lnr_search.setVisibility(View.GONE);
                }
            }
        }


        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void save_text_history() {


        final String sTask = search_edit_text.getText().toString().trim();

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                Task task = new Task();
                task.setTask(sTask);
                task.setDesc("test data");
                task.setFinishBy("test data");
                task.setFinished(false);

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }


/*

    private void showdata() {

        String data=db.getRecords();
       //text.setText(db.getRecords());
        Toast.makeText(getApplicationContext(), db.getRecords(), Toast.LENGTH_LONG).show();

    }


    private void insertsqlite() {

        db.insertRecord(search_edit_text.getText().toString());
        Toast.makeText(getApplicationContext(), "record inserted", Toast.LENGTH_LONG).show();
    }
*/


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void search() {
        String searchType = "";
        String search_text = search_edit_text.getText().toString();
        if (selectedType[0]) searchType += "movie";
        if (selectedType[1]) searchType += "tvseries";
        if (selectedType[2]) searchType += "live";
        int range_from = Integer.parseInt(year_min.getText().toString());
        int range_to = Integer.parseInt(year_max.getText().toString());

        if (TextUtils.isEmpty(search_text) && TextUtils.isEmpty(searchType)) {
            new ToastMsg(SearchActivity.this).toastIconError(getResources()
                    .getString(R.string.searcHError_message));
            return;
        } else if (!new NetworkInst(SearchActivity.this).isNetworkAvailable()) {
            new ToastMsg(SearchActivity.this).toastIconError(getResources()
                    .getString(R.string.no_internet));
            return;
        }


        Intent searchIntent = new Intent(SearchActivity.this, SearchResultActivity.class);
        searchIntent.putExtra("q", search_text);
        searchIntent.putExtra("type", searchType);
        searchIntent.putExtra("range_to", range_to);
        searchIntent.putExtra("range_from", range_from);
        searchIntent.putExtra("tv_category_id", selectedTvCategoryId);

        //from spinner
        searchIntent.putExtra("genre_id", selectedGenreId);

        //from grid list
        //  searchIntent.putExtra("genre_id", SearchGridAdapter.selected_id);

        searchIntent.putExtra("country_id", selectedCountryId);
        startActivity(searchIntent);
    }

    public void btToggleClick(View view) {
        if (view instanceof Button) {
            Button b = (Button) view;
            if (b.isSelected()) {
                b.setTextColor(getResources().getColor(R.color.grey_40));
                if (b.getText().equals(getResources().getString(R.string.movie))) {
                    selectedType[0] = false;
                    if (!selectedType[1]) {
                        rangeLayout.setVisibility(View.GONE);
                        range_tv.setVisibility(View.GONE);
                        genreLayout.setVisibility(View.GONE);
                        genreSpinner.setVisibility(View.GONE);
                        countryLayout.setVisibility(View.GONE);
                        countrySpinner.setVisibility(View.GONE);
                    }
                } else if (b.getText().equals(getResources().getString(R.string.tv_series))) {
                    selectedType[1] = false;
                    if (!selectedType[0]) {
                        rangeLayout.setVisibility(View.GONE);
                        range_tv.setVisibility(View.GONE);
                        genreLayout.setVisibility(View.GONE);
                        genreSpinner.setVisibility(View.GONE);
                        countryLayout.setVisibility(View.GONE);
                        countrySpinner.setVisibility(View.GONE);
                    }
                } else if (b.getText().equals(getResources().getString(R.string.live_tv))) {
                    selectedType[2] = false;
                    tvCategoryLayout.setVisibility(View.GONE);
                    tvCategorySpinner.setVisibility(View.GONE);
                }

            } else {
                b.setTextColor(getResources().getColor(R.color.white));
                if (b.getText().equals(getResources().getString(R.string.movie))) {
                    selectedType[0] = true;
//                    rangeLayout.setVisibility(View.VISIBLE);
//                    range_tv.setVisibility(View.VISIBLE);
                    genreLayout.setVisibility(View.VISIBLE);
                    genreSpinner.setVisibility(View.VISIBLE);
                    countryLayout.setVisibility(View.VISIBLE);
                    countrySpinner.setVisibility(View.VISIBLE);
                } else if (b.getText().equals(getResources().getString(R.string.tv_series))) {
                    selectedType[1] = true;
//                    rangeLayout.setVisibility(View.VISIBLE);
//                    range_tv.setVisibility(View.VISIBLE);
                    genreLayout.setVisibility(View.VISIBLE);
                    genreSpinner.setVisibility(View.VISIBLE);
                    countryLayout.setVisibility(View.VISIBLE);
                    countrySpinner.setVisibility(View.VISIBLE);
                } else if (b.getText().equals(getResources().getString(R.string.live_tv))) {
                    selectedType[2] = true;
                    tvCategoryLayout.setVisibility(View.VISIBLE);
                    tvCategorySpinner.setVisibility(View.VISIBLE);
                }
            }
            b.setSelected(!b.isSelected());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
