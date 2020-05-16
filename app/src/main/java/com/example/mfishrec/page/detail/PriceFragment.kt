package com.example.mfishrec.page.detail

import android.os.Bundle
import android.util.Log
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

        //data?.let { showData(it) }
        data?.let { showSpinner(it) }
        //data?.let { showCard1(it) }
        data?.let { showCard2(it) }

    }
    fun showData(data:ArrayList<PriceModel>){
        Log.d("showdata",data.toString())
    }

    fun showSpinner(data:ArrayList<PriceModel>){
        var marketList = arrayListOf<String>()
        var dateList = arrayListOf<String>()
        var nameList = arrayListOf<String>()
        var date_tmpSet = hashSetOf<String>()
        var name_tmpSet = hashSetOf<String>()
        var market_tmpSet = hashSetOf<String>()
        for (i in 0 until data.size){
            if (date_tmpSet.add(data[i].date)) {
                dateList.add(data[i].date)
            }
        }
        val market_adapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,marketList)
        val date_adapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,dateList)
        val name_adapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,nameList)
        market_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        date_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        price_date_spinner.adapter = date_adapter
        price_name_spinner.adapter = name_adapter
        price_market_spinner.adapter = market_adapter
        price_date_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                marketList.clear()
                market_tmpSet.clear()
                for (i in 0 until data.size){
                    if(data[i].date == dateList[p2] && market_tmpSet.add(data[i].market)){
                        marketList.add(data[i].market)
                    }
                }
                marketList.sort()
                market_adapter.notifyDataSetChanged()
                price_market_spinner.setSelection(0)
                if(price_market_spinner.selectedItem!=null && price_name_spinner.selectedItem!=null)
                    queryData(price_date_spinner.selectedItem.toString(),price_market_spinner.selectedItem.toString(),price_name_spinner.selectedItem.toString(),data)?.let {
                        showCard1(it)
                    }?:run{
                        Log.d("querydata","date_spinner_NULL_DATA")
                    }
            }
        }
        price_market_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                nameList.clear()
                name_tmpSet.clear()
                for (i in 0 until data.size){
                    if(data[i].date ==  price_date_spinner.selectedItem.toString() && data[i].market == marketList[p2] && name_tmpSet.add(data[i].name)){ //first
                        nameList.add(data[i].name)
                    }
                }
                nameList.sort()
                name_adapter.notifyDataSetChanged()
                price_name_spinner.setSelection(0)
                //Log.d("name_adapter",price_name_spinner.selectedItemPosition.toString())
                if(price_name_spinner.selectedItem!=null)
                    queryData(price_date_spinner.selectedItem.toString(),price_market_spinner.selectedItem.toString(),price_name_spinner.selectedItem.toString(),data)?.let {
                        showCard1(it)
                    }?:run{
                        Log.d("querydata","market_spinner_NULL_DATA")
                    }
            }
        }
        price_name_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                queryData(price_date_spinner.selectedItem.toString(),price_market_spinner.selectedItem.toString(),price_name_spinner.selectedItem.toString(),data)?.let {
                    showCard1(it)
                }?:run{
                    Log.d("querydata","name_spinner_NULL_DATA")
                }
            }
        }

    }

    fun queryData(date:String,market:String,name:String,data:ArrayList<PriceModel>): PriceModel? {
        Log.d("querydata",data.size.toString())
        for (i in 0 until data.size){
            if(data[i].date == date && data[i].market == market && data[i].name == name){
                return data[i]
            }
        }
        return null
    }

    fun showCard1(data:PriceModel){
        var lastest = data
        price_up.text = lastest.up.toString()
        price_mid.text = lastest.mid.toString()
        price_down.text = lastest.down.toString()
        price_avg.text = lastest.avg.toString()
        price_date.text = lastest.date
        price_count.text = lastest.count.toString()
    }

    //要算平均才對
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