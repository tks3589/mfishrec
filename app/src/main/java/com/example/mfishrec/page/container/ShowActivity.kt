package com.example.mfishrec.page.container

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mfishrec.R
import com.example.mfishrec.adapter.GuideAdapter
import com.example.mfishrec.adapter.RecordDetailAdapter
import com.example.mfishrec.data.Record
import com.example.mfishrec.model.GuideModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : AppCompatActivity() {
    private var guideAdapter: GuideAdapter? = null

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
                    var name = bundle.getString("name")
                    var score = bundle.getFloat("score")
                    var rank = bundle.getInt("rank")
                    actionBar.title = name
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
