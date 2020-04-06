package com.example.mfishrec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mfishrec.R
import com.example.mfishrec.model.GuideModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.guide_item.view.*

class GuideAdapter(options:FirestoreRecyclerOptions<GuideModel>) : FirestoreRecyclerAdapter<GuideModel,GuideAdapter.GuideHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideHolder {
        return GuideHolder(LayoutInflater.from(parent.context).inflate(R.layout.guide_item,parent,false))
    }

    override fun onBindViewHolder(holder: GuideHolder, position: Int, model: GuideModel) {
        holder.bindTo(model)
    }

    class GuideHolder(view:View) : RecyclerView.ViewHolder(view){
        var nameText = view.guide_name
        var imageView = view.guide_imageview
        var background = view.guide_background_imageview
        var showmoreButton = view.guide_item_showmore
        var descriptionText = view.guide_description

        fun bindTo(model: GuideModel){
            nameText.text = model.name
            //descriptionText.text = model.description
            background.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(itemView.context)
                .load(model.imgurl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
                .into(background)

            Glide.with(itemView.context)
                .load(model.imgurl)
                .into(imageView)

            showmoreButton.setOnClickListener {
                val items = arrayOf("詳細解說","公開資料價格","推薦料理法")
                val dialog_list = AlertDialog.Builder(itemView.context)
                dialog_list.setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> {

                        }
                        1 -> {

                        }
                        else -> {

                        }
                    }
                    dialogInterface.dismiss()
                }
                dialog_list.show()
            }
        }

    }
}

