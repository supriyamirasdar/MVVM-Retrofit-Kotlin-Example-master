package com.lifestyle.retail_dashboard.network

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.widget.ProgressBar
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.base.BaseApplication.getBaseAppInstance
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.http.client.HttpResponseException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

class ApiNetwork(private val activity: Activity?) {
    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        progressDialog = ProgressDialog(activity, R.style.MyTheme)
        progressDialog?.setCancelable(false)
        progressDialog?.setProgressStyle(android.R.style.Widget_ProgressBar_Small)
        val drawable = ProgressBar(activity).indeterminateDrawable.mutate()
        drawable.setColorFilter(Color.parseColor("#ffcf00"), PorterDuff.Mode.SRC_IN)
        progressDialog?.setIndeterminateDrawable(drawable)
        if (activity != null)
            if (progressDialog != null) {
                progressDialog?.show()
            }
    }

    fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.hide()
        }
    }


    fun getApiInterface(): ApiService {
        return getBaseAppInstance().apiClient.apiInterface
    }

    private fun showError(throwable: Throwable) {
        Log.d("Abrar","Throw $throwable msg ${throwable.localizedMessage}")
        var msg = ""
        if (throwable is SocketTimeoutException) {
            msg = activity!!.resources.getString(R.string.socketException)
        } else if (throwable is ConnectException) {
            msg = activity!!.resources.getString(R.string.httpexcep)
        } else if (throwable is HttpResponseException) {
            msg = activity!!.resources.getString(R.string.httpresponseexpection);
        } else if (throwable is SocketException) {
            msg = activity!!.resources.getString(R.string.socketException)
        } else if (throwable is Exception) {
            msg = activity!!.resources.getString(R.string.generalexcep_barcode)
        }
        showAlert(msg)
    }

    private fun showAlert(messageContent: String) {
        CommonUtility.showSnackBar(activity, messageContent)
    }

    @SuppressLint("CheckResult")
    fun <T> network(observable: Observable<T>, requestCode: Int, responseInterface: SuccessResponseInterface) {
        showProgressDialog()
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ T: T ->
                    hideProgressDialog()
                    Utility.hideKeyboard(activity)
                    responseInterface.onSuccess(T, requestCode)
                }) { error: Throwable ->
                    if (activity != null) {
                        hideProgressDialog()
                        Utility.hideKeyboard(activity)
                    }
                    Log.d("Abrar", "Response: $error")
                    showError(error)
                }
    }

    interface SuccessResponseInterface {
        fun onSuccess(response: Any?, requestCode: Int)
    }
}