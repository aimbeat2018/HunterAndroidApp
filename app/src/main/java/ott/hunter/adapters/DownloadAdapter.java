package ott.hunter.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ott.hunter.database.DatabaseHelper;
import ott.hunter.database.downlaod.DownloadViewModel;
import ott.hunter.R;
import ott.hunter.models.CommonModels;
import ott.hunter.models.Work;

import java.util.ArrayList;
import java.util.List;


public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.OriginalViewHolder> {
    private static final String TAG = "DownloadAdapter";
    private static final int DOWNLOAD_JOB_KEY = 101;
    private List<CommonModels> items = new ArrayList<>();
    private Activity context;
    private boolean isDialog;
    private View v = null;
    private DownloadViewModel viewModel;
    DatabaseHelper databaseHelper;


    public DownloadAdapter(Activity context, List<CommonModels> items, boolean isDialog, DownloadViewModel viewModel) {
        this.context = context;
        this.items = items;
        this.isDialog = isDialog;
        this.viewModel = viewModel;
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public DownloadAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownloadAdapter.OriginalViewHolder vh;
        if (isDialog) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item_vertical, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item, parent, false);
        }
        vh = new DownloadAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DownloadAdapter.OriginalViewHolder holder, final int position) {

        final CommonModels obj = items.get(position);
        holder.name.setText(obj.getTitle());
        holder.resolution.setText(obj.getResulation() + ",");
        holder.size.setText(obj.getFileSize());

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obj.isInAppDownload()) {
                    //in app download enabled
                    //startDownloadWithNotification(obj.getStremURL(), obj.getTitle());
                    Work work = new Work();
                    work.setDownloadId(Integer.parseInt(obj.getId()));
                    work.setDownloadSize(obj.getFileSize());
                    work.setDownloadStatus("");
                    work.setAppCloseStatus("");
                    work.setWorkId(obj.getId());
                    work.setFileName(obj.getTitle());
                    work.setUrl(obj.getStremURL());

                    databaseHelper.insertWork(work);

//                    DownloadHelper helper = new DownloadHelper(obj.getTitle(),
//                            obj.getStremURL(),
//                            context,
//                            viewModel
//                    );
//                    helper.downloadFile();

                } else {
                    String url = obj.getStremURL();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, resolution, size;
        public LinearLayout itemLayout;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            resolution = v.findViewById(R.id.resolution_tv);
            size = v.findViewById(R.id.size_tv);
            itemLayout = v.findViewById(R.id.item_layout);
        }
    }

}