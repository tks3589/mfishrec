package com.example.mfishrec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mfishrec.adapter.RecordDetailAdapter
import com.example.mfishrec.data.Record
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : AppCompatActivity() {

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
}
