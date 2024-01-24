package com.tv.libcommon

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.util.regex.Pattern

object HandlerData {

    fun fromAssetsReadFile(ctx: Context, filename: String): String {
        val inputStream = ctx.resources.assets.open(filename)
        return inputStream.readBytes().toString(Charset.defaultCharset())
    }

    fun parser(content: String): String {
        val removeEXTM3uContent = content.replaceFirst("#EXTM3U", "")
        val removeEXTINFContent = removeEXTM3uContent.replace("#EXTINF:-1,", "")
        var replaceContent = removeEXTINFContent
            .replace("tvg-id=", "{\"tvg-id\":")
            .replace("tvg-name=", "\"tvg-name\":")
            .replace("tvg-logo=", "\"tvg-logo\":")
            .replace("group-title=", "\"group-title\":")
            .replace("http://", "\"tvg-url\":\"http://")
            .replace(".m3u8", ".m3u8\"},")
            .replace(" ", ",")
            .replace("\"tvg-url\"", "\",\"tvg-url\"")
            .replace("\"group-title\":\"其他\",", "\"group-title\":\"其他\",\"group-desc\":\"")
            .replace("\"group-title\":\"央视\",", "\"group-title\":\"央视\",\"group-desc\":\"")
            .replace("\"group-title\":\"卫视\",", "\"group-title\":\"卫视\",\"group-desc\":\"")
            .replace("\"group-title\":\"江苏\",", "\"group-title\":\"江苏\",\"group-desc\":\"")
            .replace("\"group-title\":\"香港\",", "\"group-title\":\"香港\",\"group-desc\":\"")
            .replace("\"group-title\":\"上海\",", "\"group-title\":\"上海\",\"group-desc\":\"")
            .replace("\"group-title\":\"北京\",", "\"group-title\":\"北京\",\"group-desc\":\"")
            .replace("\"group-title\":\"广东\",", "\"group-title\":\"广东\",\"group-desc\":\"")
            .replace("\"group-title\":\"黑龙江\",", "\"group-title\":\"黑龙江\",\"group-desc\":\"")
            .replace("\"group-title\":\"安徽\",", "\"group-title\":\"安徽\",\"group-desc\":\"")
            .replace("\"group-title\":\"NEWTV\",", "\"group-title\":\"NEWTV\",\"group-desc\":\"")

        val p: Pattern = Pattern.compile("\\s*|\t|\r|\n")
        replaceContent = p.matcher(replaceContent).replaceAll("")
        return StringBuilder(replaceContent).insert(0, "[")
            .insert(replaceContent.length + 1, "]")
            .delete(replaceContent.length, replaceContent.length + 1)
            .toString()

    }

    fun writeDisk(data: String, childFilename: String) {
        val downloadFile =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadFile, childFilename)
        if (!file.exists()) {
            file.createNewFile()
        } else {
            //清空文本内容
            val fileWriter = FileWriter(file)
            fileWriter.write("")
            fileWriter.flush()
            fileWriter.close()
        }

        RandomAccessFile(file, "rwd").apply {
            seek(file.length())
            write(data.toByteArray())
            close()
        }

    }

    fun removeRepeatData(m3U8Data: MutableList<M3U8Data>): MutableList<M3U8Data> {
        val disData = mutableListOf<M3U8Data>()
        m3U8Data.forEach {
            if (disData.contains(it).not()) {
                disData.add(it)
            }
        }
        return disData
    }
}