package com.example.mfishrec.page.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.mfishrec.R
import com.example.mfishrec.lib.ChartUtil
import com.example.mfishrec.model.PriceModel
import kotlinx.android.synthetic.main.fragment_price.*
import kotlinx.android.synthetic.main.item_price_1.*
import kotlinx.android.synthetic.main.item_price_2.*

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
        data?.let { ChartUtil.showCard2(view.context,bar_line,it) }

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
}