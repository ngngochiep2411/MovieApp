package com.example.movieapp.ui.comment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movieapp.ui.auth.login.LoginFragment
import com.example.movieapp.ui.auth.signin.SignInFragment

class CommentPagerAdapter(
    fragmentActivity: FragmentActivity,
) :
    FragmentStateAdapter(fragmentActivity) {

    val fragments = mutableMapOf<Int, Fragment>()


    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return fragments.getOrPut(position) {
            when (position) {
                0 -> LoginFragment.newInstance()
                1 -> SignInFragment.newInstance()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }


}