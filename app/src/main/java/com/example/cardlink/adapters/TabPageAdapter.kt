package com.example.cardlink.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cardlink.fragments.NetworkFragment
import com.example.cardlink.fragments.ProfileFragment
import com.example.cardlink.fragments.ScanningFragment
import com.example.cardlink.fragments.UserQrFragment

class TabPageAdapter(fragActivity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(fragActivity) {
    override fun getItemCount(): Int {
        return tabCount
    }

    //based off selected position in tab layout we display corresponding fragment
    override fun createFragment(position: Int): Fragment {
        return when (position)
        {
            0 -> ScanningFragment()
            1 -> UserQrFragment()
            2 -> NetworkFragment()
            else -> ProfileFragment()
        }
    }


}