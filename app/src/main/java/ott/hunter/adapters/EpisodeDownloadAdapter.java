package ott.hunter.adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;

import ott.hunter.R;
import ott.hunter.database.downlaod.DownloadViewModel;
import ott.hunter.models.DownloadInfo;
import ott.hunter.models.DownloadModel;
import ott.hunter.models.VideoFile;
import ott.hunter.models.single_details.DownloadLink;
//import ott.hunter.service.DownloadHelper;
import ott.hunter.utils.ItemAnimation;

public class EpisodeDownloadAdapter extends RecyclerView.Adapter<EpisodeDownloadAdapter.SeasonDownloadViewModel> {
    private static final String TAG = "SeasonDownloadAdapter";
    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    String title = "";

    private Activity context;
    private List<DownloadLink> downloadLinks;
    private DownloadViewModel viewModel;
    private DownloadInfo download;
    int id;
    ArrayList<DownloadModel> downloadModelArrayList;
    private List<VideoFile> videoFiles = new ArrayList<>();
    private List<DownloadInfo> downloadingFileList;
    int dl_progress = 0;

    public EpisodeDownloadAdapter(Activity context, List<DownloadLink> downloadLinks, DownloadViewModel viewModel,
                                  List<DownloadInfo> downloadingFileList, List<VideoFile> videoFiles) {
        this.context = context;
        this.downloadLinks = downloadLinks;
        this.viewModel = viewModel;
        this.downloadingFileList = downloadingFileList;
        this.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public SeasonDownloadViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.season_download_item, parent, false);
        return new SeasonDownloadViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonDownloadViewModel holder, int position) {
        if (downloadLinks != null) {
            DownloadLink downloadLink = downloadLinks.get(position);
            holder.episodeName.setText(downloadLink.getLabel());

//            getDownloadedData();

            if (downloadingFileList.size() > 0) {
                for (int i = 0; i < downloadingFileList.size(); i++) {
//                    if (downloadLink.getDownloadLinkId().equals(downloadingFileList.get(i).getVideoId())) {
//                        int progress = downloadingFileList.get(i).getPercentage();
//
//                        if (progress == 100) {
//                            holder.line_progress.setVisibility(View.GONE);
//                            holder.downloadImageView.setVisibility(View.GONE);
////                                context.stopService(new Intent(context, DownloadService.class));
//                        } else {
//                            if (progress == 0) {
//                                holder.line_progress.setVisibility(View.GONE);
//                                holder.downloadImageView.setVisibility(View.VISIBLE);
//                            } else {
//                                boolean _isDownloading = isDownloading(context, downloadingFileList.get(i).getDownloadId());
//                                if (_isDownloading) {
//                                    title = downloadLink.getLabel();
//                                    //update percentage
//                                    int finalI = i;
//                                    context.runOnUiThread(() -> {
//
//                                        // Stuff that updates the UI
//
//                                        int prog = updateProgress(context, downloadingFileList.get(finalI).getDownloadId(), holder, downloadLink.getVideosId());
//                                        Log.e(TAG, "run: Download progress: " + prog);
//                                        if (viewModel != null) {
//                                            download = new DownloadInfo(id, title, prog/*, downloadLink.getDownloadLinkId()*/);
//                                            viewModel.update(download);
//
//                                            boolean _isDownloading1 = isDownloading(context, downloadingFileList.get(finalI).getDownloadId());
//                                            if (_isDownloading1) {
//                                                holder.line_progress.setVisibility(View.VISIBLE);
//                                                holder.line_progress.setProgress(prog);
//                                                holder.downloadImageView.setVisibility(View.GONE);
//                                            } else {
//                                                holder.line_progress.setVisibility(View.GONE);
//                                                holder.downloadImageView.setVisibility(View.GONE);
//                                            }
//                                        }
//
//                                    });
//
//                                } else {
//                                    holder.line_progress.setVisibility(View.GONE);
//                                    int status = getStatus(context, downloadingFileList.get(i).getDownloadId());
//                                    if (status == 8) {
//                                        holder.downloadImageView.setVisibility(View.GONE);
//                                    } else {
//                                        holder.downloadImageView.setVisibility(View.VISIBLE);
//                                    }
//                                }
////                                if (progress == 100 /*|| progress == 97 || progress == 98 || progress == 99*/) {
////                                    Toast.makeText(context, "Downloaded successfully", Toast.LENGTH_SHORT).show();
////                                    holder.line_progress.setVisibility(View.GONE);
//////                                context.stopService(new Intent(context, DownloadService.class));
////                                }
//                            }
//                        }
//                    } else {
//                    }
                }
            }

            holder.lnr_download.setOnClickListener(v -> {

                if (downloadLink.isInAppDownload()) {
                    //in app download enabled
                    if (viewModel != null) {
//                        if (downloadingFileList.size() > 0) {
//                            for (int i = 0; i < downloadingFileList.size(); i++) {
////                                if (downloadLink.getDownloadLinkId().equals(downloadingFileList.get(i).getVideoId())) {
////                                    Toast.makeText(context, "Already downloaded", Toast.LENGTH_SHORT).show();
////                                    break;
////                                } else {
//                                    DownloadHelper helper = new DownloadHelper(
//                                            downloadLink.getLabel(),
//                                            downloadLink.getDownloadLinkId(),
//                                            downloadLink.getDownloadUrl(),
//                                            context,
//                                            viewModel);
//                                    helper.downloadFile();
//
////                                }
//                            }
//                        } else {
//                            DownloadHelper helper = new DownloadHelper(
//                                    downloadLink.getLabel(),
//                                    downloadLink.getDownloadLinkId(),
//                                    downloadLink.getDownloadUrl(),
//                                    context,
//                                    viewModel);
//                            helper.downloadFile();
//                        }
                    }

                } else {
                    String url = downloadLink.getDownloadUrl();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });
        }
    }

//    public int getStatus(Context context, long downloadId) {
//        DownloadManager downloadManager =
//                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Query query = new DownloadManager.Query();
//        query.setFilterById(downloadId);// filter your download bu download Id
//        Cursor c = downloadManager.query(query);
//        if (c.moveToFirst()) {
//            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            c.close();
//            Log.i("DOWNLOAD_STATUS", String.valueOf(status));
////            Toast.makeText(context, String.valueOf(status), Toast.LENGTH_SHORT).show();
//            return status;
//        }
//        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT");
//        return -1;
//    }
//
//    public int updateProgress(Context context, long downloadId, SeasonDownloadViewModel holder, String videoId) {
//        DownloadManager downloadManager =
//                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Query query = new DownloadManager.Query();
//        query.setFilterById(downloadId);// filter your download bu download Id
//        Cursor c = downloadManager.query(query);
//        if (c.moveToFirst()) {
//            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//
//            if (status == DownloadManager.STATUS_RUNNING) {
//                try {
//                    int bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                    int bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                    dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
//
//
//                } catch (Exception e) {
//                    Log.e(TAG, "run: Download cancel of id: " + id);
//                    //delete percentage
////                            if (viewModel != null) {
////                                viewModel.delete(download);
////                            }
//
//                } finally {
//                    c.close();
//                }
//
//            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                dl_progress = 100;
//            }
//            Log.i("DOWNLOAD_STATUS", String.valueOf(status));
//        }
//        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT");
//
//        return dl_progress;
//    }
//
//    public boolean isDownloading(Context context, long downloadId) {
//        return getStatus(context, downloadId) == DownloadManager.STATUS_RUNNING;
//    }

    public void updateDownloadData() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return downloadLinks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class SeasonDownloadViewModel extends RecyclerView.ViewHolder {
        private TextView episodeName;
        private ImageView downloadImageView;
        private CardView seasonDownloadLayout;
        CircleProgressBar line_progress;
        LinearLayoutCompat lnr_download;

        public SeasonDownloadViewModel(@NonNull View itemView) {
            super(itemView);
            episodeName = itemView.findViewById(R.id.episodeNameOfSeasonDownload);
            downloadImageView = itemView.findViewById(R.id.downloadImageViewOfSeasonDownload);
            seasonDownloadLayout = itemView.findViewById(R.id.seasonDownloadLayout);
            line_progress = itemView.findViewById(R.id.line_progress);
            lnr_download = itemView.findViewById(R.id.lnr_download);
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

}
