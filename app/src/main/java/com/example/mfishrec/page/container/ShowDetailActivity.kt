package com.example.mfishrec.page.container

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.mfishrec.R
import com.example.mfishrec.model.MenuModel
import com.example.mfishrec.model.PriceModel
import com.example.mfishrec.page.detail.CookFragment
import com.example.mfishrec.page.detail.DescriptionFragment
import com.example.mfishrec.page.detail.PriceFragment
import kotlinx.android.synthetic.main.activity_show_detail.*

class ShowDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)

        var bundle = intent.getBundleExtra("bundle")
        var type = bundle.getString("type")
        var name = bundle.getString("name")

        val transaction = supportFragmentManager.beginTransaction()

        setSupportActionBar(toolbar)
        var actionBar = supportActionBar

        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            when(type){
                "description" -> {
                    actionBar.title = "Description : $name"
                    var imgurl = bundle.getString("imgurl")
                    var description = bundle.getString("description")
                    var detailBundel = Bundle()
                    detailBundel.putString("imgurl",imgurl)
                    detailBundel.putString("description",description)
                    var fragment = DescriptionFragment.instance
                    fragment.arguments = detailBundel
                    transaction.add(R.id.fragment_container,fragment)
                    transaction.commit()
                }
                "price" -> {
                    actionBar.title = "Price : $name"
                    var data = bundle.getParcelableArrayList<PriceModel>("data")
                    var priceBundel = Bundle()
                    priceBundel.putParcelableArrayList("data",data)
                    var fragment = PriceFragment.instance
                    fragment.arguments = priceBundel
                    transaction.add(R.id.fragment_container,fragment)
                    transaction.commit()
                }
                "cook" -> {
                    actionBar.title = "Cook : $name"
                    var data = bundle.getParcelableArrayList<MenuModel>("data")
                    var menuBundel = Bundle()
                    menuBundel.putParcelableArrayList("data",data)
                    var fragment = CookFragment.instance
                    fragment.arguments = menuBundel
                    transaction.add(R.id.fragment_container,fragment)
                    transaction.commit()
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
