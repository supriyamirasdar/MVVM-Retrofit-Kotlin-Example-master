package com.smarttoolfactory.tutorial5_1navigationui_bottomnavigation.blankfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smarttoolfactory.tutorial5_1navigationui_bottomnavigation.R


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(),"SettingsFragment onViewCreated() this: $this", Toast.LENGTH_SHORT).show()
        println("🔥 SettingsFragment onViewCreated() this: $this")
    }

}
