package com.dramabite.thebes.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dramabite.thebes.app.activity.premium.MemberFragment
import com.dramabite.thebes.app.activity.premium.RewardFragment

class PremiumPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RewardFragment()
            else -> MemberFragment()
        }
    }
}