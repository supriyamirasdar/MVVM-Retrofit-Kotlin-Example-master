package com.lifestyle.retail_dashboard.view.homescreen.fragment

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase

class HomeViewModel : ViewModel() {
    private var usecaseList: MutableLiveData<List<Usecase>>? = null
    private var navigationList: MutableLiveData<List<Usecase>>? = null
    private var planogramTypeList: MutableLiveData<List<Usecase>>? = null

    internal fun getUseCaseList(context: FragmentActivity): MutableLiveData<List<Usecase>> {
        if (usecaseList == null) {
            usecaseList = MutableLiveData()
            getUseCase(context)
        }
        return usecaseList as MutableLiveData<List<Usecase>>
    }

    internal fun getPlanogramTypeList(planogramSelected: String): MutableLiveData<List<Usecase>> {
        if (planogramTypeList == null) {
            planogramTypeList = MutableLiveData()
            getPlanogramType(planogramSelected)
        }
        return planogramTypeList as MutableLiveData<List<Usecase>>
    }

    internal fun getNavigationList(): MutableLiveData<List<Usecase>> {
        if (navigationList == null) {
            navigationList = MutableLiveData()
            navigationList()
        }
        return navigationList as MutableLiveData<List<Usecase>>
    }

    private fun navigationList() {
        val navigation = ArrayList<Usecase>()
        navigation.add(Usecase("Home", R.drawable.ic_home))
        navigation.add(Usecase("Help Center", R.drawable.ic_help_center))
        navigation.add(Usecase("Manual", R.drawable.ic_user_manual))
        navigation.add(Usecase("Logout", R.drawable.ic_logout))
        navigationList?.postValue(navigation)
    }

    private fun getPlanogramType(planogramSelected: String) {
        val planogramType = ArrayList<Usecase>()
        if (planogramSelected.equals("Visual Merchandising",true)) {
            planogramType.add(Usecase("Planogram ", R.drawable.ic_polidrom))
        }else if (planogramSelected.equals("Learning & Development",true)){
            planogramType.add(Usecase("Catalog & Content", R.drawable.ic_catalogue))
            planogramType.add(Usecase("Training", R.drawable.ic_training))
        }
        planogramTypeList?.postValue(planogramType)
    }

    private fun getUseCase(context: FragmentActivity) {
        val usecasse = ArrayList<Usecase>()
        usecasse.add(Usecase("Brand Performance", R.drawable.ic_store_dashboard))
        usecasse.add(Usecase("Visual Merchandising", R.drawable.ic_polidrom))
        usecasse.add(Usecase("Employee Attendance", R.drawable.ic_attendence))
        //usecasse.add(Usecasse("Report", R.drawable.ic_reports))
        usecasse.add(Usecase("Learning & Development", R.drawable.ic_training))
        //usecasse.add(Usecase("OJE Score", R.drawable.ic_oje_score))
        usecasse.add(Usecase(context.resources.getString(R.string.retail_audit_translation), R.drawable.ic_inspection))
        usecasse.add(Usecase(context.resources.getString(R.string.esculation), R.drawable.ic_esculation))
        usecasse.add(Usecase(context.resources.getString(R.string.brand_rank), R.drawable.ic_brand_rank))
        usecasse.add(Usecase(context.resources.getString(R.string.invetory), R.drawable.ic_inventory))
        usecasse.add(Usecase(context.resources.getString(R.string.store_contact_num), R.drawable.ic_csr))
        usecaseList?.postValue(usecasse)
    }
}