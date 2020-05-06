package com.example.mfishrec.adapter

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.mfishrec.page.container.ShowDetailActivity
import com.example.mfishrec.model.GuideModel
import com.example.mfishrec.model.PriceModel
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
                                .get().addOnSuccessListener {
                                    //Log.d("guideAdapter","${it.data}")
                                    dialog?.dismiss()
                                    val data = it.data?.let { it1 -> convertPrice(it1) }
                                    bundle.putParcelableArrayList("data",data)
                                    //Log.d("guideAdapter","${data?.size}")
                                    toPage(bundle)
                                }
                        }
                        else -> {
                            bundle.putString("type","cook")
                            toPage(bundle)
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
        fun convertPrice(map: Map<String,Any>):ArrayList<PriceModel>{
            var sortedMap = map.toSortedMap(compareByDescending { it })
            var data = arrayListOf<PriceModel>()
            sortedMap.forEach {(date,allValue) ->
                //Log.d("guideAdapter","$date : $allValue")
                val marketValue = allValue as Map<String,Any>
                marketValue.forEach{(market,fishAll) ->
                    //Log.d("guideAdapter","$date : $market : $fishAll")
                    val fishAllValue = fishAll as Map<String,Any>
                    fishAllValue.forEach{(fish,fishDetail) ->
                        //Log.d("guideAdapter","$date : $market : $fish : $fishDetail")
                        val fishDetailValue = fishDetail as Map<String,Any>
                        val id = fishDetailValue.get("id") as Long
                        val up = fishDetailValue.get("up") as Double
                        val mid = fishDetailValue.get("mid") as Double
                        val down = fishDetailValue.get("down") as Double
                        val avg = fishDetailValue.get("avg") as Double
                        val count = fishDetailValue.get("count") as Double
                        data.add(PriceModel(date,market,fish,id,up,mid,down,avg,count))
                    }
                }
            }
            return data
        }
    }
}

