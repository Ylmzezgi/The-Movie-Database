package com.ezgiyilmaz.tmdi.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ezgiyilmaz.tmdi.R

class Onboarding1 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_onboarding1, container, false)

        val onboardingImage:ImageView=view.findViewById(R.id.img_illustration)
        onboardingImage.setImageResource(R.drawable.image1)

        val onboardingDescription:TextView=view.findViewById(R.id.descriptionButon)
        onboardingDescription.text="YENİLİKLERE GÖZ ATIN"


        return view

    }

}