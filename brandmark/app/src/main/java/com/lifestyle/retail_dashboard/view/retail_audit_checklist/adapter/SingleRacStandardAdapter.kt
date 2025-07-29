package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleUpdateAuditDetailNewBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.SingleRACDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACLineItem
import java.util.*


class SingleRacStandardAdapter(val context: Context, var planogramList: MutableList<GetRACLineItem>, var dataChangeListener: DataChangeListener?) : RecyclerView.Adapter<SingleStandardViewHolder>() {

    private var filterationList = planogramList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleStandardViewHolder {
        val binding: SingleUpdateAuditDetailNewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_update_audit_detail_new, parent, false)
        return SingleStandardViewHolder(binding)
    }

    interface DataChangeListener {
        fun onValueChanged(arg1: String?, qty: String?, position: Int)
    }

    override fun getItemCount(): Int {
        return planogramList.size
    }

    override fun onBindViewHolder(holder: SingleStandardViewHolder, position: Int) {
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
            holder.binding.btnShowRemarks.visibility = View.GONE
            //holder.binding.btnRemark.visibility = View.VISIBLE
            holder.binding.etComment.visibility = View.VISIBLE
            holder.binding.etRemark.visibility = View.VISIBLE
            holder.binding.rbOpen.visibility = View.VISIBLE
        } else {
            holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#02B509"))
            holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))
            holder.binding.btnShowRemarks.visibility = View.VISIBLE
           // holder.binding.btnRemark.visibility = View.GONE
            holder.binding.etComment.visibility = View.GONE
            holder.binding.etRemark.visibility = View.GONE
            //holder.binding.rbClosed.visibility = View.INVISIBLE
            holder.binding.rbOpen.visibility = View.GONE
            holder.binding.etComment.isFocusable = false
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

                        activity.updateLineStatus(Constant.RAC_STANDARD, position, "OPEN");
                    }
                }
            }
        }

        holder.binding.btnShowRemarks.setOnClickListener {
            if (holder.binding.etRemark.visibility == View.GONE) {
                holder.binding.openIssueLayout.visibility = View.VISIBLE
                holder.binding.etComment.visibility = View.VISIBLE
                holder.binding.etRemark.visibility = View.VISIBLE
                //holder.binding.etRemark.isFocusable = false
                //holder.binding.etComment.isFocusable = false
            } else {
                holder.binding.openIssueLayout.visibility = View.GONE
                holder.binding.etComment.visibility = View.GONE
                holder.binding.etRemark.visibility = View.GONE
                //holder.binding.etRemark.isFocusable = false
                //holder.binding.etComment.isFocusable = false
                if (retailAudit.lineStatus.equals("Open", true)) {
                } else {
                    holder.binding.etComment.isFocusable = false
                }
            }
        }

        holder.binding.etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val position = holder.adapterPosition
                val comment = holder.binding.etComment.text.toString()
                if (!TextUtils.isEmpty(comment)) {
                    dataChangeListener!!.onValueChanged("ChangeResponseComments", comment, position)
                    // holder.binding.etComment.clearFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        holder.binding.root.setOnClickListener {
            /* val intent = Intent(context,SinlgePlanogramActivity::class.java)
             val bundle = Bundle()
             bundle.putParcelable("planogram", planogram)
             intent.putExtras(bundle)
             context.startActivity(intent)*/
        }
    }
}

class SingleStandardViewHolder(val binding: SingleUpdateAuditDetailNewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(retailAudit: GetRACLineItem) {
        binding.retailAudit = retailAudit
        // binding.inputDF = inputDateFormat
        // binding.outputDF = dateFormat
        // binding.tvBrand.isSelected = true
        //binding.userId = PreferenceUtils.getUserId()
    }
}