package com.aaron.mfishrec.adapter

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.aaron.mfishrec.R
import com.aaron.mfishrec.lib.ChartUtil
import com.aaron.mfishrec.lib.Converter
import com.aaron.mfishrec.lib.GpsMarketUtil
import com.aaron.mfishrec.lib.ToastUtil
import com.aaron.mfishrec.model.GuideModel
import com.aaron.mfishrec.model.PriceModel
import com.aaron.mfishrec.page.container.ShowDetailActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_fishimg.view.*
import kotlinx.android.synthetic.main.item_price_1.view.*
import kotlinx.android.synthetic.main.item_price_2.view.*

class RecResultAdapter(val context:Context,val fishItem:GuideModel,val fishPrice:ArrayList<PriceModel>,val currentLocation:Location?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_FISH_IMG = 10
    private val TYPE_PRICE_1 = 11
    private val TYPE_PRICE_2 = 12
    private var dialog: ProgressDialog? = null
    var index  = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_FISH_IMG -> RecFishImgHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_fishimg,parent,false))
            TYPE_PRICE_1 -> RecPrice1Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_price_1,parent,false))
            else -> RecPrice2Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_price_2,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RecFishImgHolder -> {
                holder.name.text = fishItem.name
                var marketResult = GpsMarketUtil.getClosestMarket(context,fishPrice,currentLocation) //GPS判斷最近市場
                holder.market.text = marketResult[1].toString()
                index = marketResult[0].toString().toInt()
                Log.d("marketList",fishPrice.toString())
                Glide.with(context)
                    .load(fishItem.imgurl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageview)
                holder.menu.setOnClickListener {
                    /*var fish_name = fishItem.name.substring(0,fishItem.name.indexOf("(")).trim()
                    var menuUrl = "https://cookpad.com/tw/搜尋/$fish_name"  //https://icook.tw/search/$fish_name
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(menuUrl)))*/
                    dialog = ProgressDialog.show(context,"抓取資料中","請稍候...")
                    var bundle = Bundle()
                    bundle.putString("type","cook")
                    bundle.putString("name",fishItem.name)
                    FirebaseFirestore.getInstance()
                        .collection("menu")
                        .document(fishItem.id.toString())
                        .get().addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                dialog?.dismiss()
                                val data = task.result?.data?.let { it1 -> Converter.convertMenu(it1) }
                                bundle.putParcelableArrayList("data",data)
                                toPage(bundle)
                            }else{
                                dialog?.dismiss()
                                ToastUtil.loadDataErrorToast(context)
                            }
                        }
                }
                holder.description.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setMessage(fishItem.description)
                        .create().show()
                }
            }
            is RecPrice1Holder -> {
                holder.up.text = fishPrice[index].up.toString()
                holder.mid.text = fishPrice[index].mid.toString()
                holder.down.text = fishPrice[index].down.toString()
                holder.avg.text = fishPrice[index].avg.toString()
                holder.date.text = fishPrice[index].date
                holder.count.text = fishPrice[index].count.toString()
            }
            is RecPrice2Holder -> {
                ChartUtil.showCard2(context,holder.barLine,fishPrice)
            }
        }
    }

    fun toPage(bundle:Bundle){
        var intent = Intent(context, ShowDetailActivity::class.java)
        intent.putExtra("bundle",bundle)
        context.startActivity(intent)
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TYPE_FISH_IMG
            1 -> TYPE_PRICE_1
            else -> TYPE_PRICE_2
        }
    }

    class RecFishImgHolder(view:View) : RecyclerView.ViewHolder(view){
        var name = view.result_name
        var market = view.result_market
        var imageview = view.result_imageview
        var menu = view.result_menu
        var description = view.result_description
    }
    class RecPrice1Holder(view:View) : RecyclerView.ViewHolder(view){
        var up = view.price_up
        var mid = view.price_mid
        var down = view.price_down
        var avg = view.price_avg
        var date = view.price_date
        var count = view.price_count
    }
    class RecPrice2Holder(view:View) : RecyclerView.ViewHolder(view){
        var barLine = view.bar_line
    }

}