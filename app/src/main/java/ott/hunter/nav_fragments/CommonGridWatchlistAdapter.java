package ott.hunter.nav_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.clevertap.android.sdk.CleverTapAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ott.hunter.AppConfig;
import ott.hunter.DetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.R;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.CommonModels;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.FavouriteApi;
import ott.hunter.network.model.FavoriteModel;
import ott.hunter.utils.ItemAnimation;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class CommonGridWatchlistAdapter extends RecyclerView.Adapter<CommonGridWatchlistAdapter.OriginalViewHolder> {


    private List<CommonModels> items = new ArrayList<>();

    private Context ctx;
    private DatabaseHelper db;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    String userId = "";
    String id = "";

    CleverTapAPI clevertapRemovedwatchlistInstance;

    public CommonGridWatchlistAdapter(Context context, List<CommonModels> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public CommonGridWatchlistAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CommonGridWatchlistAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_watchlist_image_albums, parent, false);
        vh = new CommonGridWatchlistAdapter.OriginalViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(CommonGridWatchlistAdapter.OriginalViewHolder holder, final int position) {
        final CommonModels obj = items.get(position);

        holder.qualityTv.setText(obj.getQuality());
        holder.releaseDateTv.setText(obj.getReleaseDate());
        holder.name.setText(obj.getTitle());

        Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder).into(holder.image);
        holder.cat_name.setText(obj.getTitle());
        holder.duration.setText(obj.getMovieDuration());
        holder.cat_type.setText(obj.getVideoType());
        holder.description.setText(obj.getDescription());
        //holder.description.setText(obj.getDescription());

        userId = PreferenceUtils.getUserId(ctx);
        id = obj.getId();


        clevertapRemovedwatchlistInstance= CleverTapAPI.getDefaultInstance(ctx.getApplicationContext());


        holder.option.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(holder.option.getContext(), v);

                popup.inflate(R.menu.product_option_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                // Intent intent1 = new Intent(SellerProductListActivity.this, AddProductImageActivity.class);
                                //sepearet 3 images upload
                              /*  Intent intent1 = new Intent(ctx, UplaodProductImageActivity.class);
                                intent1.putExtra("productId", model.getProductId());
                                //  intent1.putExtra("productId", productid);
                                startActivity(intent1);*/

                                removewatchlistitem(position);

                                return true;


                            default:
                                return false;
                        }
                    }
                });



                   /* popup.setGravity(Gravity.RIGHT);

                    try {
                        Field mFieldPopup=popup.getClass().getDeclaredField("mPopup");
                        mFieldPopup.setAccessible(true);
                        MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popup);
                        mPopup.setForceShowIcon(true);
                    } catch (Exception e) {

                    }*/

                popup.show();

            }
        });




/*

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.isMandatoryLogin(ctx)) {
                    if (PreferenceUtils.isLoggedIn(ctx)) {
                        goToDetailsActivity(obj);
                    } else {
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                } else {
                    goToDetailsActivity(obj);
                }
            }
        });
*/


        holder.play_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (PreferenceUtils.isMandatoryLogin(ctx)) {
//                    if (PreferenceUtils.isLoggedIn(ctx)) {
//                        goToDetailsActivity(obj);
//                    } else {
//                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
//                    }
//                } else {
//                    goToDetailsActivity(obj);
//                }
                goToDetailsActivity(obj);
            }
        });

        setAnimation(holder.itemView, position);

    }


    private void removewatchlistitem(int position) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.removeFromFavorite(AppConfig.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        //  isFav = false;
                        new ToastMsg(ctx).toastIconSuccess(response.body().getMessage());



                       /* HashMap<String, Object> removedFromWatchlistAction= new HashMap<String, Object>();
                        removedFromWatchlistAction.put("video id",id );
                        removedFromWatchlistAction.put("Title", title);
                        removedFromWatchlistAction.put("Content Type", videoType);
                        clevertapRemovedwatchlistInstance.pushEvent("Removed from watchlist", removedFromWatchlistAction);
*/

                        //to refresh list after deleting item
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();


                    } else {
                        //  isFav = true;
                        new ToastMsg(ctx).toastIconError(response.body().getMessage());
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        // imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    }
                }
            }


            @Override
            public void onFailure(Call<FavoriteModel> call, Throwable t) {

                new ToastMsg(ctx).toastIconError(ctx.getString(R.string.fetch_error));

            }
        });
    }


    private void goToDetailsActivity(CommonModels obj) {
        Intent intent = new Intent(ctx, DetailsActivity.class);
        intent.putExtra("vType", obj.getVideoType());
        intent.putExtra("id", obj.getId());
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // ProductItemLayoutBinding layoutBinding;

        public ImageView image;
        public TextView name, cat_name, qualityTv, releaseDateTv, duration, cat_type, option;
        public MaterialRippleLayout lyt_parent;

        public View view;
        PopupMenu popup;
        public CardView cardView;
        ImageView play_logo;
        TextView description;

        public OriginalViewHolder(View v) {
            super(v);
            view = v;
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            play_logo = v.findViewById(R.id.play_logo);
            cat_type = v.findViewById(R.id.cat_type);
            cat_name = v.findViewById(R.id.cat_name);
            duration = v.findViewById(R.id.duratipn);
            description = v.findViewById(R.id.description);
            option = v.findViewById(R.id.menu);

            lyt_parent = v.findViewById(R.id.lyt_parent);
            qualityTv = v.findViewById(R.id.quality_tv);
            releaseDateTv = v.findViewById(R.id.release_date_tv);
            cardView = v.findViewById(R.id.top_layout);
            cardView = v.findViewById(R.id.top_layout);
        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }


    public void removeItem(int position) {
        removewatchlistitem(position);
//        items.remove(position);
//        notifyItemRemoved(position);
    }

    public List<CommonModels> getData() {
        return items;
    }

}