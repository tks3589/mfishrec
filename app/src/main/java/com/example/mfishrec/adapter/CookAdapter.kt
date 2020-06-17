package com.example.mfishrec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mfishrec.R
import com.example.mfishrec.model.MenuModel
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_cook.view.*


class CookAdapter(val context: Context,val data:ArrayList<MenuModel>) : RecyclerView.Adapter<CookAdapter.CookHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookHolder {
        return CookHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cook,parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CookHolder, position: Int) {
        holder.cook_name.text = data[position].name

        Glide.with(context)
            .load(data[position].imgurl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.cook_background_imageview)

        Glide.with(context)
            .load(data[position].imgurl)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.cook_imageview)

        holder.cook_description.text = data[position].materials.toString()+"\n\n"+data[position].steps.toString()
    }

    class CookHolder(view:View) : RecyclerView.ViewHolder(view){
        val cook_name = view.cook_name
        val cook_imageview = view.cook_imageview
        val cook_background_imageview = view.cook_background_imageview
        val cook_description = view.cook_description
    }
}