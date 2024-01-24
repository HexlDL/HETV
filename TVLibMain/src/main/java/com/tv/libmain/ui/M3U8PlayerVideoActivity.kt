package com.tv.libmain.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.tv.libcommon.M3U8Data
import com.tv.libmain.R
import com.tv.libmain.ui.MainFragment.Companion.INTENT_KEY_M3U8
import com.tv.libmain.databinding.ActivityM3u8PlayerVideoBinding

class M3U8PlayerVideoActivity : FragmentActivity() {

    companion object {
        private const val TAG = "PlayerActivity"
    }

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityM3u8PlayerVideoBinding.inflate(layoutInflater)
    }
    private var player: SimpleExoPlayer? = null

    // 存储播放/暂停状态
    private var playReady = true

    // 存储当前窗口索引
    private var currentWindow = 0

    // 存储当前播放位置
    private var playbackPosition = 0L

    private lateinit var m3U8Data: M3U8Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        m3U8Data = intent.getParcelableExtra(INTENT_KEY_M3U8)!!
//        VideoSupportFragment
    }


    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * 创建 ExoPlayer
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initializePlayer() {
        // 它将负责选择媒体项中的轨道
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = SimpleExoPlayer
            .Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
//                viewBinding.videoView.player = exoPlayer
                // 创建媒体项
                val mediaItem = provideBuilderMediaItem(m3U8Data)
//                val mediaItem = provideBuilderMediaItem()
                val defaultDataSourceFactory = DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, getString(R.string.app_name))
                )
                val mediaSource =
                    HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(mediaItem)
//                exoPlayer.setMediaSource(mediaSource)
//                exoPlayer.setMediaItem(mediaItem)
                // 告知播放器是否在获取所有播放资源后立即开始播放。由于 playWhenReady 最初为 true，因此在第一次运行应用时，播放会自动开始
                exoPlayer.playWhenReady = playReady
                // 告知播放器在特定窗口中寻找某个位置。currentWindow 和 playbackPosition 都初始化为零，以便在应用第一次运行时从头开始播放
                exoPlayer.seekTo(currentWindow, playbackPosition)
                // 告知播放器获取播放所需的所有资源
                exoPlayer.prepare(mediaSource)
                exoPlayer.addAnalyticsListener(object : AnalyticsListener {
                    override fun onPlaybackStateChanged(
                        eventTime: AnalyticsListener.EventTime,
                        state: Int
                    ) {
                        super.onPlaybackStateChanged(eventTime, state)
                        when (state) {
                            Player.STATE_BUFFERING -> {
                                Log.i(TAG, "=========== STATE_BUFFERING ==========")
                            }

                            Player.STATE_IDLE -> {
                                Log.i(TAG, "=========== STATE_IDLE ==========")
                            }

                            Player.STATE_READY -> {
                                Log.i(TAG, "=========== STATE_READY ==========")
                            }

                            Player.STATE_ENDED -> {
                                Log.i(TAG, "=========== STATE_ENDED ==========")
                            }

                            else -> {
                                Log.i(TAG, "=========== STATE_ERROR ==========")
                            }
                        }
                    }
                })
            }
    }


    private fun provideBuilderMediaItem(m3U8Data: M3U8Data): MediaItem {
        return MediaItem
            .Builder()
            .setUri(m3U8Data.tvgUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playReady = this.playWhenReady
            release()
        }
        player = null
    }

    private fun hideSystemUi() {
//        viewBinding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}