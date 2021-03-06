package com.example.cashbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.cashbook.fragments.HistoryFragment
import com.example.cashbook.fragments.ProfileFragment
import com.example.cashbook.fragments.TransferFragment
import com.example.cashbook.fragments.WithdrawFragment
import com.example.cashbook.fragments.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager : ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(FirebaseAuth.getInstance().currentUser==null)
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_home)
        viewPager=findViewById(R.id.viewPager)
        tabs=findViewById(R.id.tabs)
        setUpTabs()
        viewPager.offscreenPageLimit=4
        if(intent.getBooleanExtra("history?",false))
            viewPager.currentItem=3
    }


    private fun setUpTabs(){

        val adapter=ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ProfileFragment(),title = "Profile")
        adapter.addFragment(TransferFragment(),title = "Transfer")
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