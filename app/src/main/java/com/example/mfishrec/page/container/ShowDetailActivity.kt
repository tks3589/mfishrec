package com.example.mfishrec.page.container

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.mfishrec.R
import kotlinx.android.synthetic.main.activity_show_detail.*

class ShowDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)

        var bundle = intent.getBundleExtra("bundle")
        var type = bundle.getString("type")

        setSupportActionBar(toolbar)
        var actionBar = supportActionBar

        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            when(type){
                "description" -> {
                    actionBar.title = "Description"
                }
                "price" -> {
                    actionBar.title = "Price"
                }
                "cook" -> {
                    actionBar.title = "Cook"
                }
                else -> {

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
