package com.tv.libmain.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tv.libcommon.HandlerData
import com.tv.libcommon.M3U8Data
import com.tv.libmain.R


class MainFragment : BrowseSupportFragment() {
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var defaultBackground: Drawable
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var groupByM3U8Data: Map<String, List<M3U8Data>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareBackgroundManager()

        setupUIElements()

        val data = readFile()

        loadRows(data)

        setOnItemViewSelectedListener { _, item, _, row ->
            Log.i(TAG, "setOnItemViewSelectedListener item = $item row  = ${row.headerItem.name}")
            if (item != null) {
                groupByM3U8Data[row.headerItem.name]?.forEach {
                    if (it.tvgName == item) {
                        Log.i(TAG, "current selected m3u8 $it")
                    }
                }
            }
        }

        setOnItemViewClickedListener { _, item, _, row ->
            Log.i(TAG, "setOnItemViewClickedListener item = $item row  = ${row.headerItem.name}")
            if (item != null) {
                groupByM3U8Data[row.headerItem.name]?.forEach {
                    if (it.tvgName == item) {
                        Log.i(TAG, "setOnItemViewClickedListener current click m3u8 $it")
                        val intent = Intent(requireContext(), PlaybackActivity::class.java)
                        intent.putExtra(INTENT_KEY_M3U8, it)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun readFile(): String {
        // 从 assets 目录读取iptv数据
        val content = HandlerData.fromAssetsReadFile(requireContext(), "iptv_m3u8")
        // 解析iptv数据
        val data = HandlerData.parser(content)
        // 写入磁盘缓存
//        HandlerData.writeDisk(data, "iptv_m3u8.json")

        return data
    }

    private fun loadRows(data: String) {
        val gson = Gson()
        val m3U8Data =
            gson.fromJson<MutableList<M3U8Data>>(
                data,
                object : TypeToken<MutableList<M3U8Data>>() {}.type
            )
        // 去除重复数据并按 groupTitle 分组
        groupByM3U8Data = HandlerData.removeRepeatData(m3U8Data).groupBy {
            it.groupTitle!!
        }
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        groupByM3U8Data.forEach {
            val gridHeader = HeaderItem(it.key)

            val gridPresenter = GridItemPresenter()
            val gridRowAdapter = ArrayObjectAdapter(gridPresenter)

            it.value.forEach { m3U8Data ->
                gridRowAdapter.add(m3U8Data.tvgName)
            }
            rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))
        }
        adapter = rowsAdapter
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.browse_title)

        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(requireActivity().window)
        defaultBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.default_background)!!
        displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    private inner class GridItemPresenter : Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.textSize = 28F
            view.gravity = Gravity.CENTER
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }

    companion object {
        const val INTENT_KEY_M3U8 = "intent_key_m3u8"
        private const val TAG = "MainFragment"

        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val GRID_ITEM_WIDTH = 600
        private const val GRID_ITEM_HEIGHT = 200
    }
}