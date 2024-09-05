package com.ezgiyilmaz.tmdi.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ezgiyilmaz.tmdi.intro.Onboarding1
import com.ezgiyilmaz.tmdi.intro.Onboarding2

class OnboardingAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Onboarding1()
            1 -> Onboarding2()
            else -> Onboarding1()
        }
    }
}