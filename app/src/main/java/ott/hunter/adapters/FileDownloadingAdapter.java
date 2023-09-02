package ott.hunter.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ott.hunter.R;
import ott.hunter.models.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class FileDownloadingAdapter extends RecyclerView.Adapter<FileDownloadingAdapter.ViewHolder> {
    private Context context;
    private Activity activity;
    private List<DownloadInfo> downloadInfoList = new ArrayList<>();
    int dl_progress = 0;

    public FileDownloadingAdapter(Context context, List<DownloadInfo> downloadInfoList,Activity activity) {
        this.context = context;
        this.activity = activity;
        this.downloadInfoList = downloadInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_file_download_item, parent,
                false);

        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadInfo info = downloadInfoList.get(position);
        holder.fileNameTv.setText(info.getFileName());

        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        int status = getStatus(context,info.getDownloadId());
                        if(status == DownloadManager.STATUS_RUNNING){
                            int progress = updateProgress(context,info.getDownloadId(),holder,"");
                            holder.progressBar.setProgress(progress);
                            holder.downloadAmountTv.setText(progress + "%");
                        }
                    }
                });

            }
        }).start();

    }

    public int getStatus(Context context, long downloadId) {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);// filter your download bu download Id
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            c.close();
            Log.i("DOWNLOAD_STATUS", String.valueOf(status));
//            Toast.makeText(context, String.valueOf(status), Toast.LENGTH_SHORT).show();
            return status;
        }
        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT");
        return -1;
    }

    public int updateProgress(Context context, long downloadId, ViewHolder holder, String videoId) {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);// filter your download bu download Id
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            if (status == DownloadManager.STATUS_RUNNING) {
                try {
                    int bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);


                } catch (Exception e) {
//                    Log.e(TAG, "run: Download cancel of id: " + id);
                    //delete percentage
//                            if (viewModel != null) {
//                                viewModel.delete(download);
//                            }

                } finally {
                    c.close();
                }

            }
            Log.i("DOWNLOAD_STATUS", String.valueOf(status));
        }
        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT");

        return dl_progress;
    }

    @Override
    public int getItemCount() {
        return downloadInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fileNameTv, downloadAmountTv;
        public ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameTv = itemView.findViewById(R.id.file_name_tv);
            downloadAmountTv = itemView.findViewById(R.id.download_amount_tv);
            progressBar = itemView.findViewById(R.id.progressBarOne);
        }
    }
}
