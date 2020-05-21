package com.example.mfishrec.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mfishrec.R
import com.example.mfishrec.lib.ChartUtil
import com.example.mfishrec.model.GuideModel
import com.example.mfishrec.model.PriceModel
import kotlinx.android.synthetic.main.item_fishimg.view.*
import kotlinx.android.synthetic.main.item_price_1.view.*
import kotlinx.android.synthetic.main.item_price_2.view.*

class RecResultAdapter(val context:Context,val fishItem:GuideModel,val fishPrice:ArrayList<PriceModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_FISH_IMG = 10
    private val TYPE_PRICE_1 = 11
    private val TYPE_PRICE_2 = 12

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
                holder.market.text = fishPrice[0].market
                Glide.with(context)
                    .load(fishItem.imgurl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageview)
                holder.menu.setOnClickListener {
                    var fish_name = fishItem.name.substring(0,fishItem.name.indexOf("(")).trim()
                    var menuUrl = "https://cookpad.com/tw/搜尋/$fish_name"  //https://icook.tw/search/$fish_name
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(menuUrl)))
                }
                holder.description.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setMessage(fishItem.description)
                        .create().show()
                }
            }
            is RecPrice1Holder -> {
                holder.up.text = fishPrice[0].up.toString()
                holder.mid.text = fishPrice[0].mid.toString()
                holder.down.text = fishPrice[0].down.toString()
                holder.avg.text = fishPrice[0].avg.toString()
                holder.date.text = fishPrice[0].date
                holder.count.text = fishPrice[0].count.toString()
            }
            is RecPrice2Holder -> {
                ChartUtil.showCard2(context,holder.barLine,fishPrice)
            }
        }
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