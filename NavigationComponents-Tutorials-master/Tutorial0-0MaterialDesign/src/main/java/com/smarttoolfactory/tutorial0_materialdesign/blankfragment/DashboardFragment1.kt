package com.smarttoolfactory.tutorial0_materialdesign.blankfragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.smarttoolfactory.tutorial0_materialdesign.R
import com.smarttoolfactory.tutorial0_materialdesign.databinding.FragmentDashboard1Binding


class DashboardFragment1 : BaseDataBindingFragment<FragmentDashboard1Binding>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_dashboard1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(activity, "CameraFragment onViewCreated() this: $this", Toast.LENGTH_SHORT)
            .show()
        println("🔥 CameraFragment onViewCreated() this: $this")
    }

}
