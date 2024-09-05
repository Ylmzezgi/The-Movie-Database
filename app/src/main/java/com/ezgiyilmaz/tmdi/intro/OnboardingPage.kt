package com.ezgiyilmaz.tmdi.intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.ezgiyilmaz.tmdi.Adapter.OnboardingAdapter
import com.ezgiyilmaz.tmdi.main.MainActivity
import com.ezgiyilmaz.tmdi.R

class OnboardingPage : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding_page)

        viewPager=findViewById(R.id.viewPager)
        dotsIndicator=findViewById(R.id.dotsIndicator)

        val adapter= OnboardingAdapter(this)
        viewPager.adapter=adapter

        setupDotsIndicator()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btn_next).setOnClickListener{
            completeOnboarding()
            if(viewPager.currentItem < adapter.itemCount-1){
                viewPager.currentItem+=1
            }else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        findViewById<Button>(R.id.btn_skip).setOnClickListener{
            completeOnboarding()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun completeOnboarding() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("isFirstRun", false)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun setupDotsIndicator() {
        val dots = arrayOfNulls<ImageView>(3)

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dotsIndicator.addView(dots[i], params)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in dots.indices) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@OnboardingPage,
                            if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                        )
                    )
                }
            }
        })
    }
}