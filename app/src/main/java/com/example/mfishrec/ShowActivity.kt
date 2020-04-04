package com.example.mfishrec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_show.*

class ShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        var bundle = intent.getBundleExtra("bundle")
        var type = bundle.getString("type")
        var name = bundle.getString("name")
        var score = bundle.getFloat("score")
        var rank = bundle.getInt("rank")

        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            when(type){
                "guide" -> {
                    actionBar.title = "guide"
                }
                "record" -> {
                    actionBar.title = "record"
                }
                else -> {
                    actionBar.title = "$rank , $name , $score"
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
