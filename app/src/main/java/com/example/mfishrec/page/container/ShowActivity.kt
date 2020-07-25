package com.example.mfishrec.page.container

import android.app.ProgressDialog
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mfishrec.R
import com.example.mfishrec.adapter.GuideAdapter
import com.example.mfishrec.adapter.RecResultAdapter
import com.example.mfishrec.adapter.RecordDetailAdapter
import com.example.mfishrec.data.Record
import com.example.mfishrec.lib.Converter
import com.example.mfishrec.lib.ToastUtil
import com.example.mfishrec.model.GuideModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : AppCompatActivity() {
    private var guideAdapter: GuideAdapter? = null
    private var dialog: ProgressDialog? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        var bundle = intent.getBundleExtra("bundle")
        var type = bundle.getString("type")

        setSupportActionBar(toolbar)
        var actionBar = supportActionBar

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            recyclerview.layoutManager = LinearLayoutManager(this)
            recyclerview.setHasFixedSize(true)
            when(type){
                "guide" -> {
                    actionBar.title = "Guide"
                    val query = FirebaseFirestore.getInstance()
                        .collection("items")
                        .orderBy("id",Query.Direction.ASCENDING)
                        .limit(30)
                    val options = FirestoreRecyclerOptions.Builder<GuideModel>()
                        .setQuery(query,GuideModel::class.java)
                        .build()
                    guideAdapter = GuideAdapter(options)
                    recyclerview.adapter = guideAdapter
                }
                "record" -> {
                    actionBar.title = "Record"
                    var model = bundle.getSerializable("record")
                    recyclerview.adapter = RecordDetailAdapter(this, model as Record)
                }
                else -> {
                    dialog = ProgressDialog.show(this,"抓取資料中","請稍候...")
                    var name = bundle.getString("name")
                    var score = bundle.getFloat("score")
                    var rank = bundle.getInt("rank")
                    actionBar.title = "Result"
                    //GPS
                    var currentLocation: Location? = null
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                        if(it.isSuccessful){
                            currentLocation = it.result

                        }else{

                        }
                        //load this fish guide and price data -> bind to recycler adapter
                        var instance = FirebaseFirestore.getInstance()
                        instance.collection("items")
                            .whereEqualTo("id",rank)
                            .limit(1).get().addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    var fishItem = task.result?.documents?.get(0)?.data?.let {
                                        Converter.convertGuide(it)
                                    }
                                    instance.collection("price")
                                        .document(rank.toString()).get().addOnCompleteListener { task ->
                                            if(task.isSuccessful){
                                                var fishPrice = task.result?.data?.let {
                                                    Converter.convertPrice(it)
                                                }
                                                dialog?.dismiss()
                                                if(fishItem!=null && fishPrice!=null)
                                                    recyclerview.adapter = RecResultAdapter(this,fishItem,fishPrice,currentLocation!!)
                                            }else{
                                                ToastUtil.loadDataErrorToast(this)
                                            }
                                        }
                                }else{
                                    ToastUtil.loadDataErrorToast(this)
                                }
                            }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        guideAdapter?.let {
            it.startListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        guideAdapter?.let {
            it.stopListening()
        }
    }
}
