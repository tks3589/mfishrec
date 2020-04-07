package com.example.mfishrec.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mfishrec.page.main.FunctionFragment
import com.example.mfishrec.R
import com.example.mfishrec.page.main.RecordFragment

class MainPageAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (position == 0) return FunctionFragment.instance
        else return RecordFragment.instance
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getStringArray(R.array.MainPageTitle)[position]
    }
}