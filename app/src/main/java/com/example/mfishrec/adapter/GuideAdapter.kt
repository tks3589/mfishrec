package com.example.mfishrec.adapter

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mfishrec.R
import com.example.mfishrec.lib.Converter
import com.example.mfishrec.lib.ToastUtil
import com.example.mfishrec.page.container.ShowDetailActivity
import com.example.mfishrec.model.GuideModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_guide.view.*

class GuideAdapter(options:FirestoreRecyclerOptions<GuideModel>) : FirestoreRecyclerAdapter<GuideModel,GuideAdapter.GuideHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideHolder {
        return GuideHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_guide,parent,false))
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
        private var dialog: ProgressDialog? = null

        fun bindTo(model: GuideModel){
            nameText.text = model.name
            //descriptionText.text = model.description
            background.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(itemView.context)
                .load(model.imgurl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(background)

            Glide.with(itemView.context)
                .load(model.imgurl)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)

            showmoreButton.setOnClickListener {
                val items = arrayOf("詳細解說","公開資料價格","推薦料理法")
                val dialog_list = AlertDialog.Builder(itemView.context)
                var bundle = Bundle()
                bundle.putString("name",model.name)
                dialog_list.setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> {
                            bundle.putString("type","description")
                            bundle.putString("imgurl",model.imgurl)
                            bundle.putString("description",model.description)
                            toPage(bundle)
                        }
                        1 -> {
                            dialog = ProgressDialog.show(itemView.context,"抓取資料中","請稍候...")
                            bundle.putString("type","price")
                            FirebaseFirestore.getInstance()
                                .collection("price")
                                .document(model.id.toString())
                                .get().addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        dialog?.dismiss()
                                        val data = task.result?.data?.let { it1 -> Converter.convertPrice(it1) }
                                        bundle.putParcelableArrayList("data",data)
                                        toPage(bundle)
                                    }else{
                                        dialog?.dismiss()
                                        ToastUtil.loadDataErrorToast(itemView.context)
                                    }
                                }
                        }
                        else -> {
                            /*var fish_name = model.name.substring(0,model.name.indexOf("(")).trim()
                            var menuUrl = "https://cookpad.com/tw/搜尋/$fish_name"  //https://icook.tw/search/$fish_name
                            itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(menuUrl)))*/
                            dialog = ProgressDialog.show(itemView.context,"抓取資料中","請稍候...")
                            bundle.putString("type","cook")
                            FirebaseFirestore.getInstance()
                                .collection("menu")
                                .document("23")
                                .get().addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        dialog?.dismiss()
                                        val data = task.result?.data?.let { it1 -> Converter.convertMenu(it1) }
                                        bundle.putParcelableArrayList("data",data)
                                        toPage(bundle)
                                    }else{
                                        dialog?.dismiss()
                                        ToastUtil.loadDataErrorToast(itemView.context)
                                    }
                                }
                        }
                    }
                    dialogInterface.dismiss()
                }
                dialog_list.show()
            }
        }
        fun toPage(bundle:Bundle){
            var intent = Intent(itemView.context,ShowDetailActivity::class.java)
            intent.putExtra("bundle",bundle)
            itemView.context.startActivity(intent)
        }
    }
}

