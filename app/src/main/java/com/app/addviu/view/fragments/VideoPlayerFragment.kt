package com.app.addviu.view.fragments

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.app.addviu.R
import com.app.addviu.model.homeModel.HomeData
import com.app.addviu.model.relatedModel.RelatedVideo
import com.app.addviu.presenter.VideoPlayerPresenter
import com.app.addviu.view.activity.HomeScreen
import com.app.naxtre.mvvmfinal.data.helper.Util
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.hoanganhtuan95ptit.draggable.DraggablePanel
import kotlinx.android.synthetic.main.video_player_layout.*
import vimeoextractor.OnVimeoExtractionListener
import vimeoextractor.VimeoExtractor
import vimeoextractor.VimeoVideo

class VideoPlayerFragment(val draggablePanel: DraggablePanel) : BaseFragment(), OnClickListener {

    val adUrl = ("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/"
            + "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"
            + "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"
            + "%3Dskippablelinear&correlator=")
    var videoUrl = ""
    private var isLandscape = false
    var player: SimpleExoPlayer? = null
    var adsLoader: ImaAdsLoader? = null
    private var mediaSourceFactory: ProgressiveMediaSource.Factory? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var videoPlayerPresenter: VideoPlayerPresenter? = null
    private var homeData: HomeData? = null
    private var requestedOrientation = 0

    var relatedVideo: RelatedVideo? = null
    var isVideoEnded = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeData = arguments?.getParcelable("data")

        return inflater.inflate(R.layout.video_player_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoPlayerPresenter = VideoPlayerPresenter(requireContext())
        initializePlayer()
        setOnClickListeners()
    }

    fun addFramgent() {
//        val fragmentManager = activity?.supportFragmentManager
//        val fragmentTransaction = fragmentManager?.beginTransaction()
//        val bundle = Bundle()
//        bundle.putString("uid", videoUid)
//        val videoDetailsFragment = VideoDetailsFragment()
//        videoDetailsFragment.arguments = bundle
//        fragmentTransaction?.replace(R.id.detailContainer, videoDetailsFragment, "detailsFragment")
//        fragmentTransaction?.commit()
    }

    fun setVideoPlayerVisible() {
        videoContainer.visibility = VISIBLE
    }

    fun setRelatedVideoSelected(homeData: HomeData) {
//        videoUid = homeData.uid
        videoContainer.visibility = INVISIBLE
        releasePlayer()
        adsLoader?.release()
        initializePlayer()
        addFramgent()
    }

    fun vimeoExtraction(videoFilename: String) {
        VimeoExtractor.getInstance()
            .fetchVideoWithURL(videoFilename, null, object : OnVimeoExtractionListener {
                override fun onSuccess(video: VimeoVideo?) {
                    videoUrl = video?.streams?.get(video.streams.keys.toTypedArray()[0])!!
                    val mediaSource: MediaSource =
                        mediaSourceFactory?.createMediaSource(Uri.parse(videoUrl))!!
                    val adsMediaSource =
                        AdsMediaSource(
                            mediaSource,
                            dataSourceFactory,
                            adsLoader,
                            videoContainer
                        )
                    activity?.runOnUiThread {
                        Log.e("starting", "fhfud")
                        player?.prepare(mediaSource)
                    }
                }

                override fun onFailure(throwable: Throwable?) {
                    Util.showToast(
                        "Video is corrupted or unable to load.",
                        requireContext()
                    )
                }
            })
    }


    private fun initializePlayer() {
        // Create a SimpleExoPlayer and set is as the player for content and ads.
        player = SimpleExoPlayer.Builder(requireContext()).build()

        // Create an AdsLoader with the ad tag url.
        adsLoader = ImaAdsLoader(requireContext(), Uri.parse(adUrl))

        videoContainer.player = player
        videoContainer.setShutterBackgroundColor(Color.TRANSPARENT)
        videoContainer.setKeepContentOnPlayerReset(true)
        adsLoader?.setPlayer(player)
        dataSourceFactory =
            DefaultDataSourceFactory(
                requireContext(),
                com.google.android.exoplayer2.util.Util.getUserAgent(
                    requireContext(),
                    getString(R.string.app_name)
                )
            )
        mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        player?.playWhenReady = true


        homeData?.videoFilename?.let { vimeoExtraction(it) }
        // Create the MediaSource for the content you wish to play.
    }

    private fun setOnClickListeners() {
        fullScreenImg.setOnClickListener(this)
        backImage.setOnClickListener(this)
        onVideoTouchListener()

        player?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if(playbackState == ExoPlayer.STATE_ENDED){
//                    isVideoEnded = true
//                    (context as HomeScreen).onPauseVideo()
//                }

                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    (context as HomeScreen).onPlayVideo()

                } else if (playWhenReady) {
                    (context as HomeScreen).onPauseVideo()

                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                } else {
                    (context as HomeScreen).onPauseVideo()

                    // player paused in any state
                }
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = resources.configuration.orientation
        isLandscape =
            orientation == Configuration.ORIENTATION_LANDSCAPE
        activity?.window?.let {
            videoPlayerPresenter?.makeFullScreen(
                videoExampleLayout,
                videoContainer,
                it,
                isLandscape
            )
        }
        changeFullscreenImage()
    }

//    override fun setFullScreen() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = Color.BLACK
//        }
//    }


    override fun onClick(v: View?) {
        when (v?.id!!) {
            R.id.backImage -> {
                if (isLandscape) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    draggablePanel.minimize()
                }
            }
            R.id.fullScreenImg -> {
                activity?.requestedOrientation = if (isLandscape) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
    }

    private fun changeFullscreenImage() {
        if (isLandscape) {
            fullScreenImg.setImageResource(R.drawable.ic_fullscreen_exit_white_36dp)
        } else {
            fullScreenImg.setImageResource(R.drawable.ic_fullscreen_white_36dp)
        }
    }


    override fun onResume() {
        super.onResume()
//        player?.playWhenReady = true
        player?.playbackState
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
        player?.playbackState

    }

    override fun onDestroy() {
        releasePlayer()
        adsLoader?.release()
        super.onDestroy()

    }


    fun releasePlayer() {
        adsLoader?.setPlayer(null)
        videoContainer?.player = null
        player?.release()
        player = null
    }

    fun hideControls() {
        videoContainer.useController = false
        videoContainer.hideController()
    }

    fun showControls() {
        videoContainer.useController = true
//        videoContainer.showController()
    }

    fun pauseVideo() {
        player?.playWhenReady = false
        player?.playbackState
    }

    fun playVideo() {
        if (isVideoEnded) {
            isVideoEnded = false
            player?.seekTo(0)
        }
        player?.playWhenReady = true
        player?.playbackState
    }

    private fun onVideoTouchListener() {
        videoContainer.setControllerVisibilityListener { visible ->
            if (visible == 0) {
                backImage.visibility = VISIBLE
                fullScreenImg.visibility = VISIBLE
                videoExampleLayout.keepScreenOn = false
            } else {
                backImage.visibility = GONE
                fullScreenImg.visibility = GONE
                videoExampleLayout.keepScreenOn = true
            }
        }

    }

}
