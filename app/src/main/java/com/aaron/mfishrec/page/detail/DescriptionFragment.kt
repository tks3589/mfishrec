package com.aaron.mfishrec.page.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.aaron.mfishrec.R
import kotlinx.android.synthetic.main.fragment_description.*

class DescriptionFragment : Fragment(){

    companion object{
        val instance : DescriptionFragment by lazy {
            DescriptionFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imgurl = arguments?.getString("imgurl")
        var description = arguments?.getString("description")

        Glide.with(view)
            .load(imgurl)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(description_imageview)

        description_text.text = description

    }
}