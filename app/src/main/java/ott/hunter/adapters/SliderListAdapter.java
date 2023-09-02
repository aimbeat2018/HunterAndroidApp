package ott.hunter.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import ott.hunter.DetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.WebViewActivity;
import ott.hunter.models.home_content.Slide;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ott.hunter.utils.MyAppClass;

public class SliderListAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    private final Activity context;
    private final ArrayList<Slide> mList;


    public SliderListAdapter(Activity context, ArrayList<Slide> itemChannels) {
        this.context = context;
        this.mList = itemChannels;
        inflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.slider_item, container, false);
        assert imageLayout != null;
        TextView textView = imageLayout.findViewById(R.id.textView);

        textView.setText(mList.get(position).getTitle());
        ImageView imageView = imageLayout.findViewById(R.id.imageview);

        Picasso.get().load(mList.get(position).getImageLink()).into(imageView);


        View lyt_parent = imageLayout.findViewById(R.id.lyt_parent);
        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mList.get(position).getActionType().equalsIgnoreCase("tvseries") || mList.get(position).getActionType().equalsIgnoreCase("movie")) {
//                    if (PreferenceUtils.isMandatoryLogin(MyAppClass.getContext())){
//                        if (PreferenceUtils.isLoggedIn(MyAppClass.getContext())){
//                            Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
//                            intent.putExtra("vType",mList.get(position).getActionType());
//                            intent.putExtra("id",mList.get(position).getActionId());
//
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            MyAppClass.getContext().startActivity(intent);
//                        }else {
//                            MyAppClass.getContext().startActivity(new Intent(MyAppClass.getContext(), LoginActivity.class));
//                        }
//                    }else {
//                        Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
//                        intent.putExtra("vType",mList.get(position).getActionType());
//                        intent.putExtra("id",mList.get(position).getActionId());
//
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        MyAppClass.getContext().startActivity(intent);
//                    }
                    Intent intent = new Intent(MyAppClass.getContext(), DetailsActivity.class);
                    intent.putExtra("vType", mList.get(position).getActionType());
                    intent.putExtra("id", mList.get(position).getActionId());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyAppClass.getContext().startActivity(intent);

                } else if (mList.get(position).getActionType().equalsIgnoreCase("webview")) {
                    Intent intent = new Intent(MyAppClass.getContext(), WebViewActivity.class);
                    intent.putExtra("url", mList.get(position).getActionUrl());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyAppClass.getContext().startActivity(intent);

                } else if (mList.get(position).getActionType().equalsIgnoreCase("external_browser")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(position).getActionUrl()));

                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyAppClass.getContext().startActivity(browserIntent);

                } else if (mList.get(position).getActionType().equalsIgnoreCase("tv")) {
                    if (PreferenceUtils.isMandatoryLogin(MyAppClass.getContext())) {
                        if (PreferenceUtils.isLoggedIn(MyAppClass.getContext())) {
                            Intent intent = new Intent(MyAppClass.getContext(), DetailsActivity.class);
                            intent.putExtra("vType", mList.get(position).getActionType());
                            intent.putExtra("id", mList.get(position).getActionId());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            MyAppClass.getContext().startActivity(intent);
                        } else {
                            MyAppClass.getContext().startActivity(new Intent(MyAppClass.getContext(), LoginActivity.class));
                        }
                    } else {
                        Intent intent = new Intent(MyAppClass.getContext(), DetailsActivity.class);
                        intent.putExtra("vType", mList.get(position).getActionType());
                        intent.putExtra("id", mList.get(position).getActionId());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MyAppClass.getContext().startActivity(intent);
                    }
                }
            }
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
