package com.example.mfishrec.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mfishrec.R
import com.example.mfishrec.adapter.MainPageAdapter
import com.example.mfishrec.data.Memory
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
        menuInflater.inflate(R.menu.menu_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                val mode = arrayOf("手動裁切","無裁切")
                val key = "settings"
                val settings = Memory.getData(this,key)
                var alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle("辨識模式")
                alertBuilder
                    .setSingleChoiceItems(mode,settings) { dialogInterface, i ->
                        Log.d("mode",i.toString())
                        if(i != settings) {
                            Memory.setData(this, key, i)
                            Toast.makeText(this, "切換 ${mode[i]} 模式", Toast.LENGTH_SHORT).show()
                        }
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
