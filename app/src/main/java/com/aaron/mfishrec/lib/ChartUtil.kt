package com.aaron.mfishrec.lib

import android.content.Context
import androidx.core.content.ContextCompat
import com.aaron.mfishrec.R
import com.aaron.mfishrec.model.PriceModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartUtil {
    companion object{
        //要算平均才對
        fun showCard2(context:Context, bar_line: LineChart, data:ArrayList<PriceModel>){
            val linechart = bar_line
            val avg_entries = ArrayList<Entry>()
            val up_entries = ArrayList<Entry>()
            val mid_entries = ArrayList<Entry>()
            val down_entries = ArrayList<Entry>()
            val date = arrayListOf<String>()
            var dateSet = hashSetOf<String>()
            var tmp = 0f
            for (i in 0 until data.size){
                if (dateSet.add(data[i].date)) {
                    date.add(data[i].date.substring(3))
                    avg_entries.add(Entry(tmp, data[i].avg.toFloat()))
                    up_entries.add(Entry(tmp, data[i].up.toFloat()))
                    mid_entries.add(Entry(tmp, data[i].mid.toFloat()))
                    down_entries.add(Entry(tmp, data[i].down.toFloat()))
                    tmp+=1
                }
            }
            val avg_dataset = LineDataSet(avg_entries,"平均價")
            val up_dataset = LineDataSet(up_entries,"上價")
            val mid_dataset = LineDataSet(mid_entries,"中價")
            val down_dataset = LineDataSet(down_entries,"下價")
            avg_dataset.color = ContextCompat.getColor(context,android.R.color.holo_orange_light)
            avg_dataset.valueTextColor = ContextCompat.getColor(context,android.R.color.holo_orange_dark)
            up_dataset.color = ContextCompat.getColor(context,android.R.color.holo_red_light)
            up_dataset.valueTextColor = ContextCompat.getColor(context,android.R.color.holo_red_dark)
            mid_dataset.color = ContextCompat.getColor(context,android.R.color.holo_green_light)
            mid_dataset.valueTextColor = ContextCompat.getColor(context,android.R.color.holo_green_dark)
            down_dataset.color = ContextCompat.getColor(context, R.color.colorPrimary)
            down_dataset.valueTextColor = ContextCompat.getColor(context,R.color.colorPrimaryDark)
            val xAxis = linechart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            val formatter = IAxisValueFormatter{
                    value, axis ->   date[value.toInt()]
            }
            xAxis.granularity = 1f
            xAxis.valueFormatter = formatter
            val yAxisRight = linechart.axisRight
            yAxisRight.isEnabled = false
            val yAxisLeft = linechart.axisLeft
            yAxisLeft.granularity = 1f
            // Setting Data
            var allData = ArrayList<LineDataSet>()
            allData.add(up_dataset)
            allData.add(mid_dataset)
            allData.add(down_dataset)
            allData.add(avg_dataset)
            val data2 = LineData(allData as List<ILineDataSet>?)
            linechart.data = data2
            //linechart.animateX(2500)
            //refresh
            linechart.invalidate()
        }
    }

}