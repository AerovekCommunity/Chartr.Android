/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.aerovek.chartr.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.aerovek.chartr.R
import org.aerovek.chartr.ui.AppConstants

private const val NUM_PAGES = 4

class OnboardingActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_slide_activity)

        viewPager = findViewById(R.id.pager)

        val adapter = OnboardingPageAdapter(this)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            println("TabLayoutMediator called with - tab: ${tab.id}, position: $position")
        }.attach()

        val button: MaterialButton = findViewById(R.id.continueToAppBtn)
        button.setOnClickListener {
            val finishIntent = Intent()
            finishIntent.putExtra("status", "ok")
            setResult(AppConstants.ONBOARDING_COMPLETE_REQUEST_CODE, finishIntent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // Uncomment the code below to allow the system to handle the back button press
            // This will call finish() on this activity and pops the back stack.
            //super.onBackPressed()
        } else {
            // Otherwise, select the previous page.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class OnboardingPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            var title = ""
            var body = ""
            var imageRes = 0
            var imageContentDesc = ""

            when (position) {
                1 -> {
                    title = resources.getString(R.string.built_on_blockchain_title)
                    body = resources.getString(R.string.built_on_blockchain_desc)
                    imageContentDesc = resources.getString(R.string.chain_img_content_desc)
                    imageRes = R.drawable.ic_outline_insert_link_128
                }
                2 -> {
                    title = resources.getString(R.string.pay_with_aero_title)
                    body = resources.getString(R.string.pay_with_aero_desc)
                    imageContentDesc = resources.getString(R.string.coin_img_content_sec)
                    imageRes = R.drawable.ic_outline_monetization_on_128
                }
                3 -> {
                    title = resources.getString(R.string.decentralized_title)
                    body = resources.getString(R.string.decentralized_desc)
                    imageContentDesc = resources.getString(R.string.decentralized_img_content_sec)
                    imageRes = R.drawable.ic_outline_flight_takeoff_128
                }
                else -> {
                    title = resources.getString(R.string.welcome_title)
                    body = resources.getString(R.string.welcome_desc)
                    imageContentDesc = resources.getString(R.string.aerojet_img_content_desc)
                    imageRes = R.drawable.ic_outline_local_airport_128
                }
            }
            return OnboardingFragment(title, body, imageContentDesc, imageRes)
        }
    }
}