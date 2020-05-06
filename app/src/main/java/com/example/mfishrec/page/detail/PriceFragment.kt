package com.example.mfishrec.page.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mfishrec.R
import com.example.mfishrec.model.PriceModel
import kotlinx.android.synthetic.main.fragment_price.*

class PriceFragment : Fragment() {

    companion object{
        val instance: PriceFragment by lazy {
            PriceFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var data = arguments?.getParcelableArrayList<PriceModel>("data")

        var lastest = data!![0]
        price_up.text = lastest.up.toString()
        price_mid.text = lastest.mid.toString()
        price_down.text = lastest.down.toString()
        price_avg.text = lastest.avg.toString()
        price_date.text = lastest.date
        price_count.text = lastest.count.toString()


    }
}