package com.example.mfishrec.page.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mfishrec.R
import com.example.mfishrec.adapter.CookAdapter
import com.example.mfishrec.model.MenuModel
import kotlinx.android.synthetic.main.fragment_cook.*

class CookFragment : Fragment() {
    companion object{
        val instance: CookFragment by lazy {
            CookFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cook, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var data = arguments?.getParcelableArrayList<MenuModel>("data")
        cook_recyclerview.layoutManager = LinearLayoutManager(context)
        cook_recyclerview.setHasFixedSize(true)
        cook_recyclerview.adapter = data?.let { CookAdapter(context!!, it) }
    }
}