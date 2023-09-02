package ott.hunter.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import ott.hunter.models.EpiModel
import androidx.recyclerview.widget.RecyclerView
import ott.hunter.offlinedownload.DownloadTracker
import ott.hunter.models.single_details.Season
import ott.hunter.utils.MyAppClass
import ott.hunter.offlinedownload.TrackKey
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import ott.hunter.offlinedownload.ExoDownloadState
import ott.hunter.utils.PreferenceUtils
import com.squareup.picasso.Picasso
import ott.hunter.DetailsActivity
import com.balysv.materialripple.MaterialRippleLayout
import ott.hunter.network.RetrofitClient
import ott.hunter.network.apis.UserDataApi
import ott.hunter.network.apis.LoginApi
import ott.hunter.LoginActivity
import ott.hunter.utils.ToastMsg
import com.google.android.exoplayer2.trackselection.ExoTrackSelection
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.offline.*
import ott.hunter.models.CommonModels
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.Util
import com.google.firebase.auth.FirebaseAuth
import ott.hunter.AppConfig
import ott.hunter.R
import ott.hunter.database.DatabaseHelper
import ott.hunter.network.model.User
import ott.hunter.offlinedownload.common.*
import ott.hunter.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList
import kotlin.math.roundToInt

class EpisodeAdapterKo(castcrew: String, context: Activity, items: List<EpiModel>) :
    RecyclerView.Adapter<EpisodeAdapterKo.OriginalViewHolder>(), DownloadTracker.Listener,
    ViewModelStoreOwner, LifecycleOwner {
    private var items: List<EpiModel> = ArrayList()
    private val itemsseason: List<Season> = ArrayList()
    private val ctx: Activity
    val viewHolderArray = arrayOf<OriginalViewHolder?>(null)
    private var mOnTVSeriesEpisodeItemClickListener: OnTVSeriesEpisodeItemClickListener? = null
    var viewHolder: OriginalViewHolder? = null
    var i = 0
    private val seasonNo = 0
    var deviceNoDynamic: String? = ""
    var deviceNo = ""
    var castcrew = ""
    var offlineDatabaseHelper: DatabaseHelper
    private var downloadManager: DownloadManager? = null
    private var downloadTracker: DownloadTracker? = null
    private var myDownloadHelper: DownloadHelper? = null
    private var runnableCode: Runnable? = null
    private var handler: Handler? = null
    var myApplication: MyAppClass
    var mholder: OriginalViewHolder? = null
    private val dataSourceFactory: DataSource.Factory
    var trackKeys: MutableList<TrackKey> = ArrayList()
    var obj: EpiModel? = null
    private var trackSelector: DefaultTrackSelector? = null
    var qualityParams: DefaultTrackSelector.Parameters? = null
    var pDialog: ProgressDialog? = null
    var optionsToDownload: MutableList<String> = ArrayList()
    override fun onDownloadsChanged(download: Download) {
        when(download.state) {
            Download.STATE_DOWNLOADING -> {
                if(viewHolder?.img_download_state?.drawable !is PieProgressDrawable) viewHolder?.img_download_state?.setImageDrawable(pieProgressDrawable)
                playerViewModel.startFlow(ctx, download.request.uri)
            }
            Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                viewHolder?.img_download_state?.setImageDrawable(AppCompatResources.getDrawable(ctx, R.drawable.ic_pause))
            }
            Download.STATE_COMPLETED -> {
                viewHolder?.img_download_state?.setImageDrawable(AppCompatResources.getDrawable(ctx, R.drawable.ic_download_done))
            }
            Download.STATE_REMOVING -> {
                viewHolder?.img_download_state?.setImageDrawable(AppCompatResources.getDrawable(ctx, R.drawable.ic_download))
            }
            Download.STATE_FAILED, Download.STATE_RESTARTING -> { }
        }
    }

    fun setCommonDownloadButton(exoDownloadState: ExoDownloadState, holder: OriginalViewHolder?) {
        when (exoDownloadState) {
            ExoDownloadState.DOWNLOAD_START -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_download))
                holder.downloadProgress.visibility = View.GONE
                holder.img_download_state.visibility = View.VISIBLE
            }
            ExoDownloadState.DOWNLOAD_PAUSE -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_pause))
                holder.downloadProgress.visibility = View.VISIBLE
                holder.img_download_state.visibility = View.GONE
            }
            ExoDownloadState.DOWNLOAD_RESUME -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_play))
                holder.downloadProgress.visibility = View.GONE
                holder.img_download_state.visibility = View.VISIBLE
            }
            ExoDownloadState.DOWNLOAD_RETRY -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_retry))
                holder.downloadProgress.visibility = View.GONE
                holder.img_download_state.visibility = View.VISIBLE
            }
            ExoDownloadState.DOWNLOAD_COMPLETED -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_plus))
                holder.downloadProgress.visibility = View.GONE
                holder.img_download_state.visibility = View.VISIBLE
            }
            ExoDownloadState.DOWNLOAD_QUEUE -> {
                holder!!.ll_download_video.tag = exoDownloadState
                holder.tv_download_state.text = exoDownloadState.value
                holder.img_download_state.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_queue))
                holder.downloadProgress.visibility = View.GONE
                holder.img_download_state.visibility = View.VISIBLE
            }
        }
    }

    interface OnTVSeriesEpisodeItemClickListener {
        fun onEpisodeItemClickTvSeries(
            type: String?,
            view: View?,
            obj: EpiModel?,
            position: Int,
            holder: OriginalViewHolder?
        )
    }

    private fun observerVideoStatus(singleItem: EpiModel, holder: OriginalViewHolder) {
//        itemMovie=new ItemMovie();
        if (downloadManager!!.currentDownloads.size > 0) {
            for (i in downloadManager!!.currentDownloads.indices) {
                val currentDownload = downloadManager!!.currentDownloads[i]
                if (!singleItem.streamURL.isEmpty() && currentDownload.request.uri == Uri.parse(
                        singleItem.streamURL
                    )
                ) {
                    ctx.runOnUiThread {
                        if (downloadTracker!!.downloads.size > 0) {
                            if (currentDownload.request.uri == Uri.parse(singleItem.streamURL)) {
                                val downloadFromTracker =
                                    downloadTracker!!.downloads[Uri.parse(singleItem.streamURL)]
                                if (downloadFromTracker != null) {
                                    when (downloadFromTracker.state) {
                                        Download.STATE_QUEUED -> setCommonDownloadButton(
                                            ExoDownloadState.DOWNLOAD_QUEUE,
                                            holder
                                        )
                                        Download.STATE_STOPPED -> setCommonDownloadButton(
                                            ExoDownloadState.DOWNLOAD_RESUME,
                                            holder
                                        )
                                        Download.STATE_DOWNLOADING -> {
                                            setCommonDownloadButton(
                                                ExoDownloadState.DOWNLOAD_PAUSE,
                                                holder
                                            )
                                            if (downloadFromTracker.percentDownloaded != -1f) {
                                                holder.downloadProgress.visibility = View.VISIBLE
                                                holder.img_download_state.visibility = View.GONE
                                                holder.downloadProgress.progress =
                                                    downloadFromTracker.percentDownloaded.toInt()
                                            }
                                            Log.d(
                                                "EXO STATE_DOWNLOADING ",
                                                downloadFromTracker.bytesDownloaded.toString() + " " + downloadFromTracker.contentLength
                                            )
                                            Log.d(
                                                "EXO  STATE_DOWNLOADING ",
                                                "" + downloadFromTracker.percentDownloaded
                                            )
                                        }
                                        Download.STATE_COMPLETED -> {
                                            setCommonDownloadButton(
                                                ExoDownloadState.DOWNLOAD_COMPLETED,
                                                holder
                                            )
                                            Log.d(
                                                "EXO STATE_COMPLETED ",
                                                downloadFromTracker.bytesDownloaded.toString() + " " + downloadFromTracker.contentLength
                                            )
                                            Log.d(
                                                "EXO  STATE_COMPLETED ",
                                                "" + downloadFromTracker.percentDownloaded
                                            )
                                        }
                                        Download.STATE_FAILED -> setCommonDownloadButton(
                                            ExoDownloadState.DOWNLOAD_RETRY,
                                            holder
                                        )
                                        Download.STATE_REMOVING -> {}
                                        Download.STATE_RESTARTING -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setOnEmbedItemClickListener(mItemClickListener: OnTVSeriesEpisodeItemClickListener?) {
        mOnTVSeriesEpisodeItemClickListener = mItemClickListener
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OriginalViewHolder {
        val vh: OriginalViewHolder
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_episode_item_vertical, parent, false)
        vh = OriginalViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: OriginalViewHolder, @SuppressLint("RecyclerView") position: Int) {
        mholder = holder
        val obj1 = items[position]
        obj = obj1
        holder.name.text = obj!!.epi

        //holder.seasonName.setText("Season: " + obj.getSeson());
        holder.seasonName.text = castcrew


        //holder.seasonName.setText("Season: " + obj.getSeson().);
        getProfile(PreferenceUtils.getUserId(ctx))
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        val sharedPreferences = ctx.getSharedPreferences("push", Context.MODE_PRIVATE)
        val isDark = sharedPreferences.getBoolean("dark", false)
        if (!isDark) {
            holder.name.setTextColor(ctx.resources.getColor(R.color.black))
            holder.seasonName.setTextColor(ctx.resources.getColor(R.color.black))
            holder.publishDate.setTextColor(ctx.resources.getColor(R.color.black))
        }
        Picasso.get()
            .load(obj!!.imageUrl)
            .placeholder(R.drawable.poster_placeholder)
            .into(holder.episodIv)


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
        }*/holder.cardView.setOnClickListener { v: View? ->
            if (deviceNoDynamic != null) {
                if (deviceNoDynamic != "") {
                    if (deviceNo != deviceNoDynamic) {
                        Toast.makeText(ctx, "Logged in other device", Toast.LENGTH_SHORT).show()
                        logoutUser(PreferenceUtils.getUserId(ctx))
                    } else {
                        //change
//                            DetailsActivity.castImageUrl=obj.getImageUrl();
                        (ctx as DetailsActivity).hideDescriptionLayout()
                        ctx.showSeriesLayout()
                        ctx.setMediaUrlForTvSeries(obj!!.streamURL, obj!!.seson, obj!!.epi)
                        val castSession = ctx.castSession
                        //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                        if (!castSession) {
                            if (obj!!.serverType.equals("embed", ignoreCase = true)) {
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                        "embed",
                                        v,
                                        obj,
                                        position,
                                        viewHolder
                                    )
                                }
                            } else {
                                //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                        "normal",
                                        v,
                                        obj,
                                        position,
                                        viewHolder
                                    )
                                }
                            }
                        } else {
                            ctx.showQueuePopup(ctx, holder.cardView, ctx.mediaInfo)
                        }
                        chanColor(viewHolderArray[0], position)
                        holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary))
                        holder.playStatusTv.text = "Playing"
                        holder.playStatusTv.visibility = View.VISIBLE
                        viewHolderArray[0] = holder
                    }
                } else {
                    (ctx as DetailsActivity).hideDescriptionLayout()
                    ctx.showSeriesLayout()
                    ctx.setMediaUrlForTvSeries(obj!!.streamURL, obj!!.seson, obj!!.epi)
                    val castSession = ctx.castSession
                    //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                    if (!castSession) {
                        if (obj!!.serverType.equals("embed", ignoreCase = true)) {
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                    "embed",
                                    v,
                                    obj,
                                    position,
                                    viewHolder
                                )
                            }
                        } else {
                            //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                    "normal",
                                    v,
                                    obj,
                                    position,
                                    viewHolder
                                )
                            }
                        }
                    } else {
                        ctx.showQueuePopup(ctx, holder.cardView, ctx.mediaInfo)
                    }
                    chanColor(viewHolderArray[0], position)
                    holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary))
                    holder.playStatusTv.text = "Playing"
                    holder.playStatusTv.visibility = View.VISIBLE
                    viewHolderArray[0] = holder
                }
            } else {
                (ctx as DetailsActivity).hideDescriptionLayout()
                ctx.showSeriesLayout()
                ctx.setMediaUrlForTvSeries(obj!!.streamURL, obj!!.seson, obj!!.epi)
                val castSession = ctx.castSession
                //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                if (!castSession) {
                    if (obj!!.serverType.equals("embed", ignoreCase = true)) {
                        if (mOnTVSeriesEpisodeItemClickListener != null) {
                            mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                "embed",
                                v,
                                obj,
                                position,
                                viewHolder
                            )
                        }
                    } else {
                        //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                        if (mOnTVSeriesEpisodeItemClickListener != null) {
                            mOnTVSeriesEpisodeItemClickListener!!.onEpisodeItemClickTvSeries(
                                "normal",
                                v,
                                obj,
                                position,
                                viewHolder
                            )
                        }
                    }
                } else {
                    ctx.showQueuePopup(ctx, holder.cardView, ctx.mediaInfo)
                }
                chanColor(viewHolderArray[0], position)
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary))
                holder.playStatusTv.text = "Playing"
                holder.playStatusTv.visibility = View.VISIBLE
                viewHolderArray[0] = holder
            }
        }
        try {
            DownloadService.start(ctx, MyDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(ctx, MyDownloadService::class.java)
        }

        DownloadUtil.getDownloadTracker(ctx).addListener(this)

//        /*Download video*/downloadTracker = myApplication.downloadTracker
//        downloadManager = myApplication.downloadManager
//        downloadTracker.addListener(this)
//        try {
//            DownloadService.start(ctx, DemoDownloadService::class.java)
//        } catch (e: IllegalStateException) {
//            DownloadService.startForeground(ctx, DemoDownloadService::class.java)
//        }

//        if (offlineDatabaseHelper.checkIfMyMovieExists(items.get(position).getEpisodeId())) {
//            holder.ll_download_video.setVisibility(View.INVISIBLE);
//        }else{
//            holder.ll_download_video.setVisibility(View.VISIBLE);
//        }


        val downloadRequest: DownloadRequest? =
            DownloadUtil.getDownloadTracker(ctx).getDownloadRequest(Uri.parse(items.get(position).streamURL))
//        val mediaSource = if(downloadRequest == null) {
//            // Online content
//            HlsMediaSource.Factory(DownloadUtil.getHttpDataSourceFactory(ctx)).createMediaSource(mediaItem)
//        } else {
//            // Offline content
//            DownloadHelper.createMediaSource(downloadRequest, DownloadUtil.getReadOnlyDataSourceFactory(ctx))
//        }

        playerViewModel.downloadPercent.observe(this) {
            it?.let {
                pieProgressDrawable.level = it.roundToInt()
                holder.img_download_state.invalidate()
            }
        }

        holder.ll_download_video.setOnClickListener { view: View? ->
//                if (NetworkUtils.isConnected(mContext)) {
//                    if (myApplication.getIsLogin()) {
            if (!offlineDatabaseHelper.checkIfMyMovieExists(items[position].episodeId)) {
//                println("llDownloadVideo.getTag() ==> " + holder.ll_download_video.tag)
//                if (holder.ll_download_video.tag == null) {
//                    val exoDownloadState = ExoDownloadState.DOWNLOAD_START
//                    exoVideoDownloadDecision(exoDownloadState, items[position])
//                } else {
//                    val exoDownloadState = holder.ll_download_video.tag as ExoDownloadState
//                    exoVideoDownloadDecision(exoDownloadState, items[position])
//                }

                if(DownloadUtil.getDownloadTracker(ctx).isDownloaded(mediaItem)) {
//                    Snackbar.make(this.rootView, "You've already downloaded the video", Snackbar.LENGTH_SHORT)
//                        .setAction("Delete") {
//                            DownloadUtil.getDownloadTracker(this@PlayerActivity).removeDownload(mediaItem.playbackProperties?.uri)
//                        }.show()
                } else {
                    val item = mediaItem.buildUpon()
                        .setTag((mediaItem.playbackProperties?.tag as MediaItemTag).copy(duration = 0))
                        .build()
                    if(!DownloadUtil.getDownloadTracker(ctx).hasDownload(item.playbackProperties?.uri)) {
                        DownloadUtil.getDownloadTracker(ctx).toggleDownloadDialogHelper(ctx, item)
                    } else {
                        DownloadUtil.getDownloadTracker(ctx).toggleDownloadPopupMenu(ctx, this, item.playbackProperties?.uri)
                    }
                }
            } else {
                Toast.makeText(ctx, "already", Toast.LENGTH_SHORT).show()
                //                            PrettyDialog pDialog = new PrettyDialog(mContext);
//                            pDialog
//                                    .setTitle(mContext.getString(R.string.app_name))
//                                    .setMessage("Please, Play Movie Once to download it!!")
//                                    .setIcon(R.drawable.ic_more_download)
//                                    .addButton(
//                                            "Okay",
//                                            R.color.pdlg_color_white,
//                                            R.color.pdlg_color_green,
//                                            pDialog::dismiss)
//                                    .show();
            }
        }

//        runnableCode = object : Runnable {
//            override fun run() {
//                observerVideoStatus(items[position], holder)
//                handler!!.postDelayed(this, 1000)
//            }
//        }
//        handler = Handler()
//        handler!!.post(runnableCode as Runnable)
    }

    private fun buildDataSourceFactory(): DataSource.Factory {
        return myApplication.buildDataSourceFactory()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView
        var playStatusTv: TextView
        var seasonName: TextView
        var publishDate: TextView
        var tv_download_state: TextView
        var cardView: MaterialRippleLayout
        var episodIv: ImageView
        var img_download_state: ImageView
        var ll_download_video: LinearLayout
        var downloadProgress: ProgressBar

        init {
            name = v.findViewById(R.id.name)
            playStatusTv = v.findViewById(R.id.play_status_tv)
            cardView = v.findViewById(R.id.lyt_parent)
            episodIv = v.findViewById(R.id.image)
            seasonName = v.findViewById(R.id.season_name)
            publishDate = v.findViewById(R.id.publish_date)
            ll_download_video = v.findViewById(R.id.ll_download_video)
            img_download_state = v.findViewById(R.id.img_download_state)
            tv_download_state = v.findViewById(R.id.tv_download_state)
            downloadProgress = v.findViewById(R.id.downloadProgress)
        }
    }

    private fun chanColor(holder: OriginalViewHolder?, pos: Int) {
        if (holder != null) {
            holder.name.setTextColor(ctx.resources.getColor(R.color.grey_20))
            holder.playStatusTv.visibility = View.GONE
        }
    }

    private fun getProfile(uid: String) {
        val retrofit = RetrofitClient.getRetrofitInstance()
        val api = retrofit.create(UserDataApi::class.java)
        val call = api.getUserData(AppConfig.API_KEY, uid)
        call.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        val user = response.body()
                        deviceNoDynamic = user!!.device_no
                        deviceNo = Settings.Secure.getString(
                            ctx.applicationContext.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {}
        })
    }

    /*private void logoutUser() {
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
        ((Activity) ctx).finish();
    }*/
    private fun logoutUser(id: String) {
        val retrofit = RetrofitClient.getRetrofitInstance()
        val api = retrofit.create(LoginApi::class.java)
        val call = api.postLogoutStatus(AppConfig.API_KEY, id)
        call.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    if (response.body()!!.status.equals("success", ignoreCase = true)) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut()
                        }
                        val editor = ctx.getSharedPreferences(
                            Constants.USER_LOGIN_STATUS,
                            Context.MODE_PRIVATE
                        ).edit()
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false)
                        editor.apply()
                        editor.commit()
                        val databaseHelper = DatabaseHelper(ctx)
                        databaseHelper.deleteUserData()
                        PreferenceUtils.clearSubscriptionSavedData(ctx)
                        val intent = Intent(ctx, LoginActivity::class.java)
                        ctx.startActivity(intent)
                        ctx.finish()
                    } else {
                        ToastMsg(ctx).toastIconError(response.body()!!.data)
                    }
                } else {
                    ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast))
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast))
            }
        })
    }

    private fun exoVideoDownloadDecision(exoDownloadState: ExoDownloadState, model: EpiModel) {
//        if (exoDownloadState == null || Remember.getString("movie_url", "0").isEmpty()) {
//            Toast.makeText(this, "Please, Tap Again", Toast.LENGTH_SHORT).show();
//            return;
//        }
        when (exoDownloadState) {
            ExoDownloadState.DOWNLOAD_START -> fetchDownloadOptions(model)
            ExoDownloadState.DOWNLOAD_PAUSE -> downloadManager!!.addDownload(
                downloadTracker!!.getDownloadRequest(
                    Uri.parse(model.streamURL)
                ), Download.STATE_STOPPED
            )
            ExoDownloadState.DOWNLOAD_RESUME -> downloadManager!!.addDownload(
                downloadTracker!!.getDownloadRequest(
                    Uri.parse(model.streamURL)
                ), Download.STOP_REASON_NONE
            )
            ExoDownloadState.DOWNLOAD_RETRY -> {}
            ExoDownloadState.DOWNLOAD_COMPLETED -> Toast.makeText(
                ctx,
                "Already Downloaded, Delete from Downloaded video ",
                Toast.LENGTH_SHORT
            ).show()
            else -> {}
        }
    }

    private fun fetchDownloadOptions(model: EpiModel) {
        trackKeys.clear()
        val trackSelectionFactory: ExoTrackSelection.Factory = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(ctx, trackSelectionFactory)
        if (pDialog == null || !pDialog!!.isShowing) {
            pDialog = ProgressDialog(ctx)
            pDialog!!.setTitle(null)
            pDialog!!.setCancelable(false)
            pDialog!!.setMessage("Preparing Download Options...")
            pDialog!!.show()
        }
        val downloadHelper = DownloadHelper.forHls(
            ctx,
            Uri.parse(model.streamURL),
            dataSourceFactory,
            DefaultRenderersFactory(ctx)
        )
        downloadHelper.prepare(object : DownloadHelper.Callback {
            override fun onPrepared(helper: DownloadHelper) {
                myDownloadHelper = helper
                for (i in 0 until helper.periodCount) {
                    val trackGroups = helper.getTrackGroups(i)
                    for (j in 0 until trackGroups.length) {
                        val trackGroup = trackGroups[j]
                        for (k in 0 until trackGroup.length) {
                            val track = trackGroup.getFormat(k)
                            if (shouldDownload(track)) {
                                trackKeys.add(TrackKey(trackGroups, trackGroup, track))
                            }
                        }
                    }
                }
                if (pDialog != null && pDialog!!.isShowing) {
                    pDialog!!.dismiss()
                }
                optionsToDownload.clear()
                showDownloadOptionsDialog(myDownloadHelper, trackKeys, model)
            }

            override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun showDownloadOptionsDialog(
        helper: DownloadHelper?,
        trackKeyss: List<TrackKey>,
        model: EpiModel
    ) {
        if (helper == null) {
            return
        }
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle("Select Download Format")
        val checkedItem = 1
        for (i in trackKeyss.indices) {
            val trackKey = trackKeyss[i]
            val videoResoultionDashSize = " " + trackKey.trackFormat.height + "      (" + "MB" + ")"
            optionsToDownload.add(i, videoResoultionDashSize)
        }

        // Initialize a new array adapter instance
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter(
            ctx,  // Context
            android.R.layout.simple_list_item_single_choice,  // Layout
            optionsToDownload // List
        )
        val trackKey = trackKeyss[0]
        qualityParams = trackSelector!!.parameters.buildUpon()
            .setMaxVideoSize(trackKey.trackFormat.width, trackKey.trackFormat.height)
            .setMaxVideoBitrate(trackKey.trackFormat.bitrate)
            .build()
        builder.setSingleChoiceItems(arrayAdapter, 0) { dialogInterface, i ->
            val trackKey = trackKeyss[i]
            qualityParams = trackSelector!!.parameters.buildUpon()
                .setMaxVideoSize(trackKey.trackFormat.width, trackKey.trackFormat.height)
                .setMaxVideoBitrate(trackKey.trackFormat.bitrate)
                .build()
        }
        // Set the a;ert dialog positive button
        builder.setPositiveButton(
            "Download",
            DialogInterface.OnClickListener { dialogInterface, which ->
                val itemMovie = CommonModels()
                itemMovie.setId(model.episodeId)
                itemMovie.movieName = model.epi
                itemMovie.movieDuration = "0"
                itemMovie.setImageUrl(model.imageUrl)
                itemMovie.setStremURL(model.streamURL)
                offlineDatabaseHelper.addEpisodeData(itemMovie)
                for (periodIndex in 0 until helper.periodCount) {
                    val mappedTrackInfo = helper.getMappedTrackInfo( /* periodIndex= */periodIndex)
                    helper.clearTrackSelections(periodIndex)
                    for (i in 0 until mappedTrackInfo.rendererCount) {
//                        TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                        helper.addTrackSelection(
                            periodIndex,
                            qualityParams!!
                        )
                    }
                }
                val downloadRequest = helper.getDownloadRequest(Util.getUtf8Bytes(model.streamURL))
                if (downloadRequest.streamKeys.isEmpty()) {
                    // All tracks were deselected in the dialog. Don't start the download.
                    return@OnClickListener
                }
                startDownload(downloadRequest)
                dialogInterface.dismiss()
            })
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun startDownload(downloadRequestt: DownloadRequest) {
        if (downloadRequestt.uri.toString().isEmpty()) {
            Toast.makeText(ctx, "Try Again!!", Toast.LENGTH_SHORT).show()
            return
        } else {
            downloadManager!!.addDownload(downloadRequestt)
        }
    }

    private fun shouldDownload(track: Format): Boolean {
        return track.height != 240 && track.sampleMimeType.equals("video/avc", ignoreCase = true)
    }

    init {
        this.castcrew = castcrew
        ctx = context
        this.items = items
        myApplication = MyAppClass.getInstance()
        myApplication = ctx.application as MyAppClass
        offlineDatabaseHelper = DatabaseHelper(ctx)
        dataSourceFactory = buildDataSourceFactory()
    }

    private val playerViewModel by lazy {
        ViewModelProvider(this).get(PlayerViewModel::class.java)
    }
    private val pieProgressDrawable: PieProgressDrawable by lazy {
        PieProgressDrawable().apply {
            setColor(ContextCompat.getColor(ctx, R.color.colorAccent))
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        TODO("Not yet implemented")
    }

    private val mediaItem: MediaItem by lazy {
        MediaItem.Builder()
            .setUri("")
            .setMimeType("m3u8")
            .setMediaMetadata(MediaMetadata.Builder().setTitle("video").build())
            .setTag(MediaItemTag(-1, "video"!!))
            .build()
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }


}