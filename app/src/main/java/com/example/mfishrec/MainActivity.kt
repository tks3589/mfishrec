package com.example.mfishrec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.example.mfishrec.adapter.MainPageAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager.adapter =
            MainPageAdapter(this, supportFragmentManager)
        tabs_main.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                val mode = arrayOf("裁剪辨識","直接辨識")
                var alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle("模式")
                alertBuilder
                    .setSingleChoiceItems(mode,0) { dialogInterface, i ->
                        Log.d("mode",i.toString())
                        dialogInterface.dismiss()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //FunctionFragment透過ucrop.start getActivity.startForResult出去 所以FunctionFragment onActivityResult吃不到ucrop  要透過父類送回去
        supportFragmentManager.fragments.get(0).onActivityResult(requestCode,resultCode, data)
    }
}
