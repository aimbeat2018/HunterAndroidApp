package ott.hunter.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ott.hunter.AppConfig;
import ott.hunter.GoldDetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.R;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.EpiModel;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.User;
import ott.hunter.utils.Constants;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GoldEpisodeAdapter extends RecyclerView.Adapter<GoldEpisodeAdapter.OriginalViewHolder> {

    private List<EpiModel> items = new ArrayList<>();
    private Context ctx;
    final GoldEpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private OnTVSeriesEpisodeItemClickListener mOnTVSeriesEpisodeItemClickListener;
    GoldEpisodeAdapter.OriginalViewHolder viewHolder;
    int i = 0;
    private int seasonNo;
    String deviceNoDynamic = "";
    String deviceNo = "";

    public interface OnTVSeriesEpisodeItemClickListener {
        void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnEmbedItemClickListener(OnTVSeriesEpisodeItemClickListener mItemClickListener) {
        this.mOnTVSeriesEpisodeItemClickListener = mItemClickListener;
    }

    public GoldEpisodeAdapter(Context context, List<EpiModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public GoldEpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GoldEpisodeAdapter.OriginalViewHolder vh;
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item_vertical, parent, false);
        vh = new GoldEpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GoldEpisodeAdapter.OriginalViewHolder holder, final int position) {

        final EpiModel obj = items.get(position);
        holder.name.setText(obj.getEpi());
        holder.seasonName.setText("Season: " + obj.getSeson());
        getProfile(PreferenceUtils.getUserId(ctx));
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);
        if (!isDark) {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.seasonName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.publishDate.setTextColor(ctx.getResources().getColor(R.color.black));
        }

        Picasso.get()
                .load(obj.getImageUrl())
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);



        /*if (seasonNo == 0) {
            if (position==i){
                chanColor(viewHolderArray[0],position);
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);
                viewHolderArray[0] =holder;
                i = items.size()+items.size() + items.size();

            }
        }*/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deviceNoDynamic != null) {
                    if (!deviceNoDynamic.equals("")) {
                        if (!deviceNo.equals(deviceNoDynamic)) {
                            Toast.makeText(ctx, "Logged in other device", Toast.LENGTH_SHORT).show();
                            logoutUser(PreferenceUtils.getUserId(ctx));
                        } else {
                            ((GoldDetailsActivity) ctx).hideDescriptionLayout();
                            ((GoldDetailsActivity) ctx).showSeriesLayout();
                            ((GoldDetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                            boolean castSession = ((GoldDetailsActivity) ctx).getCastSession();
                            //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                            if (!castSession) {
                                if (obj.getServerType().equalsIgnoreCase("embed")) {
                                    if (mOnTVSeriesEpisodeItemClickListener != null) {
                                        mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                                    }
                                } else {
                                    //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                                    if (mOnTVSeriesEpisodeItemClickListener != null) {
                                        mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                                    }
                                }
                            } else {
                                ((GoldDetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((GoldDetailsActivity) ctx).getMediaInfo());

                            }

                            chanColor(viewHolderArray[0], position);
                            holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                            holder.playStatusTv.setText("Playing");
                            holder.playStatusTv.setVisibility(View.VISIBLE);


                            viewHolderArray[0] = holder;
                        }
                    } else {
                        ((GoldDetailsActivity) ctx).hideDescriptionLayout();
                        ((GoldDetailsActivity) ctx).showSeriesLayout();
                        ((GoldDetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                        boolean castSession = ((GoldDetailsActivity) ctx).getCastSession();
                        //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                        if (!castSession) {
                            if (obj.getServerType().equalsIgnoreCase("embed")) {
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                                }
                            } else {
                                //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                                }
                            }
                        } else {
                            ((GoldDetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((GoldDetailsActivity) ctx).getMediaInfo());

                        }

                        chanColor(viewHolderArray[0], position);
                        holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.playStatusTv.setText("Playing");
                        holder.playStatusTv.setVisibility(View.VISIBLE);


                        viewHolderArray[0] = holder;
                    }
                } else {
                    ((GoldDetailsActivity) ctx).hideDescriptionLayout();
                    ((GoldDetailsActivity) ctx).showSeriesLayout();
                    ((GoldDetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                    boolean castSession = ((GoldDetailsActivity) ctx).getCastSession();
                    //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                    if (!castSession) {
                        if (obj.getServerType().equalsIgnoreCase("embed")) {
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                            }
                        } else {
                            //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                            }
                        }
                    } else {
                        ((GoldDetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((GoldDetailsActivity) ctx).getMediaInfo());

                    }

                    chanColor(viewHolderArray[0], position);
                    holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                    holder.playStatusTv.setText("Playing");
                    holder.playStatusTv.setVisibility(View.VISIBLE);


                    viewHolderArray[0] = holder;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv, seasonName, publishDate;
        public MaterialRippleLayout cardView;
        public ImageView episodIv;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView = v.findViewById(R.id.lyt_parent);
            episodIv = v.findViewById(R.id.image);
            seasonName = v.findViewById(R.id.season_name);
            publishDate = v.findViewById(R.id.publish_date);
        }
    }

    private void chanColor(GoldEpisodeAdapter.OriginalViewHolder holder, int pos) {

        if (holder != null) {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }


    private void getProfile(String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        User user = response.body();
                        deviceNoDynamic = user.getDevice_no();
                        deviceNo = Settings.Secure.getString(ctx.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }

//    private void logoutUser() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            FirebaseAuth.getInstance().signOut();
//        }
//
//        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
//        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
//        editor.apply();
//        editor.commit();
//
//        DatabaseHelper databaseHelper = new DatabaseHelper(ctx);
//        databaseHelper.deleteUserData();
//
//        PreferenceUtils.clearSubscriptionSavedData(ctx);
//
//        Intent intent = new Intent(ctx, LoginActivity.class);
//        ctx.startActivity(intent);
//        ((Activity) ctx).finish();
//    }

    private void logoutUser(String id) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<User> call = api.postLogoutStatus(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();
                        }

                        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(ctx);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(ctx);

                        Intent intent = new Intent(ctx, LoginActivity.class);
                        ctx.startActivity(intent);
                        ((Activity)ctx).finish();
                    }  else {
                        new ToastMsg(ctx).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast));
            }
        });
    }

}