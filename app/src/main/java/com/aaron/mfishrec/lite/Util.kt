package com.aaron.mfishrec.lite

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.PriorityQueue
import kotlin.Comparator

fun <T> priorityQueue(capacity: Int, block: (Comparator<T>)) =
    PriorityQueue<T>(capacity, block)

fun ByteArray.toBitmap(): Bitmap {
    val byteArrayInputString = ByteArrayInputStream(this)
    return BitmapFactory.decodeStream(byteArrayInputString)
}

@Throws(IOException::class)
fun AssetManager.loadModelFile(modelPath: String): MappedByteBuffer {
    val fileDescriptor = this.openFd(modelPath)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

@Throws(IOException::class)
fun AssetManager.loadLabelList(labelPath: String): List<String> {
    val labelList = ArrayList<String>()
    val reader = BufferedReader(InputStreamReader(this.open(labelPath)))
    reader.use {
        while (true) {
            val line = reader.readLine() ?: break
            labelList.add(line)
        }
    }
    return labelList
}