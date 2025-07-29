package com.lifestyle.retail_dashboard.view.homescreen.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleNavigationItemBinding
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.utils.Utility.changeIconColor
import com.lifestyle.retail_dashboard.utils.Utility.goToNextScreen
import com.lifestyle.retail_dashboard.view.helpcenter.HelpCenterActivity
import com.lifestyle.retail_dashboard.view.homescreen.activity.HomeScreenActivty
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase
import com.lifestyle.retail_dashboard.view.login.LoginActivity
import com.lifestyle.retail_dashboard.view.manual.ManualActivity
import com.lifestyle.retail_dashboard.view.manual.ManualActivityNew
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.SingleRACDetail

class NavigationAdapter(val activity: Activity, val navigationList: List<Usecase>) : RecyclerView.Adapter<NavigViewHolder>(), Usecase.ItemClickListner {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigViewHolder {
        val binding: SingleNavigationItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_navigation_item, parent, false)
        return NavigViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return navigationList.size

    }

    override fun onBindViewHolder(holder: NavigViewHolder, position: Int) {
        val item = navigationList[position]
        holder.bindItems(item)

        if (selectedPosition == position) {
            holder.binding.navigText.setTextColor(activity.resources.getColor(R.color.white))
            changeIconColor(activity, holder.binding.navigIcon, item.icon, R.color.white)
        } else {
            holder.binding.navigText.setTextColor(activity.resources.getColor(R.color.text))
            changeIconColor(activity, holder.binding.navigIcon, item.icon, R.color.text)
        }

        holder.binding.root.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }

        holder.binding.onItemClick = this
    }

    override fun onItemClick(view: View, item: String) {
        /*if (item.equals("Logout",true)){
            PreferenceUtils.Logout()
            goToNextScreen(context,LoginActivity.java::class)
        }*/
        if (item.equals("Home", true)) {
            if (activity is HomeScreenActivty) {
                val activity = activity as HomeScreenActivty
                activity.closeDrawer()
            }
        } else if (item.equals("Help Center", true)) {
            if (activity is HomeScreenActivty) {
                val activity = activity as HomeScreenActivty
                activity.closeDrawer()
            }
            goToNextScreen(activity, HelpCenterActivity::class.java)
        } else if (item.equals("Manual", true)) {
            if (activity is HomeScreenActivty) {
                val activity = activity as HomeScreenActivty
                activity.closeDrawer()
            }
            goToNextScreen(activity, ManualActivityNew::class.java)
        } else if (item.equals("Logout", true)) {
            if (activity is HomeScreenActivty) {
                val activity = activity as HomeScreenActivty
                activity.closeDrawer()
            }
            PreferenceUtils.Logout()
            goToNextScreen(activity, LoginActivity::class.java)
            activity.finish()
        }
    }
}

class NavigViewHolder(val binding: SingleNavigationItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(usecase: Usecase) {
        binding.navig = usecase
    }
}