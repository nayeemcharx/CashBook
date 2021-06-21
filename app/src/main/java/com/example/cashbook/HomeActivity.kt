package com.example.cashbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.example.cashbook.fragments.HistoryFragment
import com.example.cashbook.fragments.ProfileFragment
import com.example.cashbook.fragments.TransferFragment
import com.example.cashbook.fragments.WithdrawFragment
import com.example.cashbook.fragments.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var transferButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewPager=findViewById(R.id.viewPager)
        tabs=findViewById(R.id.tabs)
        setUpTabs()
    }


    private fun setUpTabs(){
        val adapter=ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ProfileFragment(this),title = "Profile")
        adapter.addFragment(TransferFragment(this),title = "Transfer")
        adapter.addFragment(WithdrawFragment(),title = "withdraw")
        adapter.addFragment(HistoryFragment(),title = "History")




        viewPager.adapter=adapter
        tabs.setupWithViewPager(viewPager)


        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_account_box_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_transfer_within_a_station_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_withdraw)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_baseline_history_24)

    }
}