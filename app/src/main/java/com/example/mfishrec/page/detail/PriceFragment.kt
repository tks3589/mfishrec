package com.example.mfishrec.page.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mfishrec.R
import com.example.mfishrec.model.PriceModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_price.*

class PriceFragment : Fragment() {

    companion object{
        val instance: PriceFragment by lazy {
            PriceFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var data = arguments?.getParcelableArrayList<PriceModel>("data")

        data?.let { showSpinner(it) }
        data?.let { showCard1(it) }
        data?.let { showCard2(it) }

    }

    fun showSpinner(data:ArrayList<PriceModel>){
        var marketList = arrayListOf<String>()
        var dateList = arrayListOf<String>()
        var tmpSet = hashSetOf<String>()
        for (i in 0 until data.size){
            if (tmpSet.add(data[i].market)) {
                marketList.add(data[i].market)
            }
            if (tmpSet.add(data[i].date)) {
                dateList.add(data[i].date)
            }
        }
        val market_adapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,marketList)
        val date_adapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,dateList)
        market_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        date_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        price_market_spinner.adapter = market_adapter
        price_date_spinner.adapter = date_adapter
        price_market_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
        price_date_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }

    fun showCard1(data:ArrayList<PriceModel>){
        var lastest = data[0]  //顯示最新一筆
        price_up.text = lastest.up.toString()
        price_mid.text = lastest.mid.toString()
        price_down.text = lastest.down.toString()
        price_avg.text = lastest.avg.toString()
        price_date.text = lastest.date
        price_count.text = lastest.count.toString()
    }

    fun showCard2(data:ArrayList<PriceModel>){
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
        avg_dataset.color = ContextCompat.getColor(context!!,android.R.color.holo_orange_light)
        avg_dataset.valueTextColor = ContextCompat.getColor(context!!,android.R.color.holo_orange_dark)
        up_dataset.color = ContextCompat.getColor(context!!,android.R.color.holo_red_light)
        up_dataset.valueTextColor = ContextCompat.getColor(context!!,android.R.color.holo_red_dark)
        mid_dataset.color = ContextCompat.getColor(context!!,android.R.color.holo_green_light)
        mid_dataset.valueTextColor = ContextCompat.getColor(context!!,android.R.color.holo_green_dark)
        down_dataset.color = ContextCompat.getColor(context!!,R.color.colorPrimary)
        down_dataset.valueTextColor = ContextCompat.getColor(context!!,R.color.colorPrimaryDark)
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