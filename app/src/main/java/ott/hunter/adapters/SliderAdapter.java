package ott.hunter.adapters;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.islamkhsh.CardSliderAdapter;
import ott.hunter.DetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.R;
import ott.hunter.WebViewActivity;
import ott.hunter.models.home_content.Slide;
import ott.hunter.utils.PreferenceUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import ott.hunter.utils.MyAppClass;

public class SliderAdapter extends CardSliderAdapter<Slide> {

    public SliderAdapter(@NotNull ArrayList<Slide> items) {
        super(items);
    }

    @Override
    public void bindView(int i, @NotNull View view, @Nullable final Slide slide) {
        if (slide != null){
            TextView textView = view.findViewById(R.id.textView);

            textView.setText(slide.getTitle());
            ImageView imageView = view.findViewById(R.id.imageview);
//            Picasso.get().load(slide.getImageLink()).into(imageView);

            View lyt_parent = view.findViewById(R.id.lyt_parent);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (slide.getActionType().equalsIgnoreCase("tvseries") || slide.getActionType().equalsIgnoreCase("movie")){
                        if (PreferenceUtils.isMandatoryLogin(MyAppClass.getContext())){
                            if (PreferenceUtils.isLoggedIn(MyAppClass.getContext())){
                                Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
                                intent.putExtra("vType",slide.getActionType());
                                intent.putExtra("id",slide.getActionId());

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MyAppClass.getContext().startActivity(intent);
                            }else {
                                MyAppClass.getContext().startActivity(new Intent(MyAppClass.getContext(), LoginActivity.class));
                            }
                        }else {
                            Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
                            intent.putExtra("vType",slide.getActionType());
                            intent.putExtra("id",slide.getActionId());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            MyAppClass.getContext().startActivity(intent);
                        }

                    }else if (slide.getActionType().equalsIgnoreCase("webview")){
                        Intent intent = new Intent(MyAppClass.getContext(), WebViewActivity.class);
                        intent.putExtra("url", slide.getActionUrl());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MyAppClass.getContext().startActivity(intent);

                    }else if (slide.getActionType().equalsIgnoreCase("external_browser")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slide.getActionUrl()));

                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MyAppClass.getContext().startActivity(browserIntent);

                    }else if (slide.getActionType().equalsIgnoreCase("tv")){
                        if (PreferenceUtils.isMandatoryLogin(MyAppClass.getContext())){
                            if (PreferenceUtils.isLoggedIn(MyAppClass.getContext())){
                                Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
                                intent.putExtra("vType",slide.getActionType());
                                intent.putExtra("id",slide.getActionId());

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MyAppClass.getContext().startActivity(intent);
                            }else {
                                MyAppClass.getContext().startActivity(new Intent(MyAppClass.getContext(), LoginActivity.class));
                            }
                        }else {
                            Intent intent=new Intent(MyAppClass.getContext(), DetailsActivity.class);
                            intent.putExtra("vType",slide.getActionType());
                            intent.putExtra("id",slide.getActionId());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            MyAppClass.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }


    @Override
    public int getItemContentLayout(int i) {
        return R.layout.slider_item;
    }
}
