package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.backup

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleAuditDetailItemBinding
import com.lifestyle.retail_dashboard.databinding.SingleUpdateAuditDetailNewBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.CreateNewRACActivity
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.SingleRACDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACLineItem
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.RetailAudit
import java.util.ArrayList


class SingleRacBusinessAdapter(val context: Context, var planogramList: MutableList<GetRACLineItem>): RecyclerView.Adapter<SingleBusinessViewHolder>() {

    private var filterationList = planogramList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleBusinessViewHolder {
        val binding: SingleUpdateAuditDetailNewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_update_audit_detail_new, parent, false)
        return SingleBusinessViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return planogramList.size
    }

    override fun onBindViewHolder(holder: SingleBusinessViewHolder, position: Int) {
        val retailAudit = planogramList[position]
        holder.bindItems(retailAudit)
        //holder.binding.deleteListner = deletePlanoListner

        val assignToList: ArrayList<String> = arrayListOf()
        val ratingList: ArrayList<String> = arrayListOf()
        ratingList.addAll(context.resources.getStringArray(R.array.number_1_to_10).toList())
        assignToList.add("BM")
        assignToList.add("CM")
        assignToList.add("Other")

        if (retailAudit.lineStatus.equals("Open", true)) {
            holder.binding.openIssueLayout.visibility = View.VISIBLE
            holder.binding.radioGroupBT.check(R.id.rbOpen)
        } else {
            holder.binding.openIssueLayout.visibility = View.GONE
            holder.binding.radioGroupBT.check(R.id.rbClosed)
            holder.binding.rbClosed.isEnabled = false
            holder.binding.rbOpen.isEnabled = false
            holder.binding.root.isEnabled = false
        }

        if (retailAudit.lineStatus.equals("Open", true)) {
            holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#807F7F"))
            holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))

            holder.binding.btnShowRemarks.visibility= View.GONE
            holder.binding.btnRemark.visibility= View.VISIBLE

            holder.binding.etComment.visibility= View.VISIBLE
            holder.binding.etRemark.visibility= View.VISIBLE

            holder.binding.rbOpen.visibility = View.VISIBLE
        } else {
            holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#02B509"))
            holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))

            holder.binding.btnShowRemarks.visibility= View.VISIBLE
            holder.binding.btnRemark.visibility= View.GONE

            holder.binding.etComment.visibility= View.GONE
            holder.binding.etRemark.visibility= View.GONE
            //holder.binding.rbClosed.visibility = View.INVISIBLE
            holder.binding.rbOpen.visibility = View.GONE
        }

        if (retailAudit.lineStatus.equals("Open", true)) {
            holder.binding.radioGroupBT.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
                if (checkedId == R.id.rbClosed) {
                    holder.binding.openIssueLayout.visibility = View.GONE
                    val remarks = holder.binding.etComment.text.toString().trim()

                    // racLineNo = retailAudit.racLineNo
                    if (context is SingleRACDetail) {
                        val activity = context as SingleRACDetail
                        val position = holder.adapterPosition
                        activity.updateRacLineNo(retailAudit.racLineNo);
                    }

                    if (remarks.isNotEmpty()) {
                        //updateLineItem(remarks)
                        if (context is SingleRACDetail) {
                            val activity = context as SingleRACDetail
                            val position = holder.adapterPosition
                            activity.updateLineItemRemarks(remarks);
                        }
                    } else {
                        if (context is SingleRACDetail) {
                            val activity = context as SingleRACDetail
                            CommonUtility.showSnackBar(activity, "Enter Response Remark")
                        }
                        holder.binding.radioGroupBT.check(R.id.rbOpen)
                        holder.binding.etComment.visibility = View.VISIBLE
                    }
                } else if (checkedId == R.id.rbOpen) {
                    holder.binding.openIssueLayout.visibility = View.VISIBLE
                    //retailAudit.lineStatus = "OPEN"
                    if (context is SingleRACDetail) {
                        val activity = context as SingleRACDetail
                        val position = holder.adapterPosition

                        activity.updateLineStatus(Constant.RAC_BUSINESS,position, "OPEN");
                    }
                }
            }
        }

        holder.binding.btnRemark.setOnClickListener {
            if (retailAudit.lineStatus.equals("Open", true)) {
                if (holder.binding.etRemark.visibility == View.VISIBLE) {
                    if (context is SingleRACDetail) {
                        val activity = context as SingleRACDetail
                        Utility.hideKeyboard(activity)
                    }
                    //holder.binding.etRemark.visibility = View.GONE
                    Handler().postDelayed({
                        val remark = holder.binding.etRemark.text.toString()
                        if (remark.isNotEmpty()) {
                            //retailAudit.auditRemarks = remark
                            if (context is SingleRACDetail) {
                                val activity = context as SingleRACDetail
                                val position = holder.adapterPosition
                                activity.saveRemarks(Constant.RAC_BUSINESS, position, remark);
                            }
                            //holder.binding.etRemark.isEnabled = false
                            holder.binding.etRemark.isFocusable = false
                        }
                    }, 10)
                } else
                    holder.binding.etRemark.visibility = View.VISIBLE
            }
        }

        holder.binding.btnShowRemarks.setOnClickListener {
            if (holder.binding.etRemark.visibility == View.GONE) {
                holder.binding.openIssueLayout.visibility = View.VISIBLE
                holder.binding.etComment.visibility = View.VISIBLE
                holder.binding.etRemark.visibility = View.VISIBLE

                holder.binding.etRemark.isFocusable = false
                holder.binding.etComment.isFocusable = false
            }else {
                holder.binding.openIssueLayout.visibility = View.GONE
                holder.binding.etComment.visibility = View.GONE
                holder.binding.etRemark.visibility = View.GONE

                holder.binding.etRemark.isFocusable = false
                holder.binding.etComment.isFocusable = false
            }

        }

        holder.binding.btnComment.setOnClickListener {
            if (retailAudit.lineStatus.equals("Open", true)) {
                if (holder.binding.etComment.visibility == View.VISIBLE) {
                    if (context is SingleRACDetail) {
                        val activity = context as SingleRACDetail
                        Utility.hideKeyboard(activity)
                    }
                    //holder.binding.etComment.visibility = View.GONE
                    Handler().postDelayed({
                        val comment = holder.binding.etComment.text.toString()
                        if (comment.isNotEmpty()) {
                            // retailAudit.responseRemarks = comment
                            if (context is SingleRACDetail) {
                                val activity = context as SingleRACDetail
                                val position = holder.adapterPosition
                                activity.saveComments(Constant.RAC_BUSINESS, position, comment);
                            }
                            //holder.binding.etComment.isEnabled = false
                            holder.binding.etComment.isFocusable = false
                        }
                    }, 10)
                } else
                    holder.binding.etComment.visibility = View.VISIBLE
            }
        }

        holder.binding.btnSaveUserId.setOnClickListener {
            val userId = holder.binding.etUserId.text.toString()
            if (userId.length >= 7) {
                //retailAudit.taskAssigned = userId
                if (context is SingleRACDetail) {
                    val activity = context as SingleRACDetail
                    Utility.hideKeyboard(activity)

                    val position = holder.adapterPosition
                    activity.saveAssigneName(Constant.RAC_BUSINESS, position, userId);
                }
                holder.binding.etUserId.clearFocus()
            } else {
                if (context is SingleRACDetail) {
                    val activity = context as SingleRACDetail
                    CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                }
            }
        }


        holder.binding.root.setOnClickListener {
           /* val intent = Intent(context,SinlgePlanogramActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("planogram", planogram)
            intent.putExtras(bundle)
            context.startActivity(intent)*/
        }
    }


}

class SingleBusinessViewHolder(val binding: SingleUpdateAuditDetailNewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(retailAudit: GetRACLineItem) {
        binding.retailAudit = retailAudit
       // binding.inputDF = inputDateFormat
       // binding.outputDF = dateFormat
       // binding.tvBrand.isSelected = true
        //binding.userId = PreferenceUtils.getUserId()
    }
}