package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.backup

import android.content.Context
import android.graphics.Color
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
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.CreateNewRACActivity
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.RetailAudit
import java.util.ArrayList


class RacBusinessAdapter(val context: Context, var planogramList: MutableList<RetailAudit>): RecyclerView.Adapter<BusinessViewHolder>() {

    private var filterationList = planogramList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val binding: SingleAuditDetailItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_audit_detail_item, parent, false)
        return BusinessViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return planogramList.size
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val planogram = planogramList[position]
        holder.bindItems(planogram)
        //holder.binding.deleteListner = deletePlanoListner

        val assignToList: ArrayList<String> = arrayListOf()
        val ratingList: ArrayList<String> = arrayListOf()
        ratingList.addAll(context.resources.getStringArray(R.array.number_1_to_10).toList())
        assignToList.add("BM")
        assignToList.add("CM")
        assignToList.add("Other")

        // set assign list adapter..
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.spinner_simple_text_black, assignToList)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        holder.binding.spName.adapter = adapter
        holder.binding.spName.setPadding(0, 0, 0, 0)
        holder.binding.spName.setSelection(0, false)


        // set rating adapter..
        val numAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.spinner_simple_text_black, ratingList)
        numAdapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        holder.binding.spRating.adapter = numAdapter
        holder.binding.spRating.setPadding(0, 0, 0, 0)

        val selectedRating = holder.binding.spRating.selectedItem.toString()
        //retailAudit.rating = selectedRating
        if (context is CreateNewRACActivity) {
            val activity = context as CreateNewRACActivity
            val position = holder.adapterPosition
            activity.saveRatings(Constant.RAC_BUSINESS, position, selectedRating);
        }

        // by default.. status will be open..
        holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#807F7F"))// grey
        holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))// red
        holder.binding.openIssueLayout.visibility = View.VISIBLE
        holder.binding.btnShowRemarks.visibility= View.GONE
        holder.binding.btnRemark.visibility= View.VISIBLE

        holder.binding.etComment.visibility= View.VISIBLE
        holder.binding.etRemark.visibility= View.VISIBLE
        //holder.binding.rbClosed.visibility = View.INVISIBLE
        //holder.binding.rbOpen.visibility = View.GONE
        if (context is CreateNewRACActivity) {
            val activity = context as CreateNewRACActivity
            val position = holder.adapterPosition
            activity.radioCheckListener(Constant.RAC_BUSINESS, position, "OPEN", "BM");
        }

        holder.binding.radioGroupBT.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            if (checkedId == R.id.rbClosed) {
                holder.binding.openIssueLayout.visibility = View.GONE
                /* retailAudit.status = "CLOSED"
                 //retailAudit.action = "No"
                 retailAudit.taskAssigned = null*/

                holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#02B509"))// green
                holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))// red

                holder.binding.btnShowRemarks.visibility= View.VISIBLE
                holder.binding.btnRemark.visibility= View.GONE

                holder.binding.etComment.visibility= View.GONE
                holder.binding.etRemark.visibility= View.GONE
                holder.binding.etRemark.isFocusable = false
                holder.binding.etComment.isFocusable = false

                holder.binding.rbOpen.visibility = View.GONE


                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    val position = holder.adapterPosition
                    activity.radioCheckListener(Constant.RAC_BUSINESS, position, "CLOSED", null);
                }
            } else if (checkedId == R.id.rbOpen) {
                holder.binding.openIssueLayout.visibility = View.VISIBLE
                /* retailAudit.status = "OPEN"
                 //retailAudit.action = "Yes"
                 retailAudit.taskAssigned = "BM"*/

                holder.binding.rbClosed.setBackgroundColor(Color.parseColor("#807F7F"))// grey
                holder.binding.rbOpen.setBackgroundColor(Color.parseColor("#FF0000"))// red
                holder.binding.btnShowRemarks.visibility= View.GONE
                holder.binding.btnRemark.visibility= View.VISIBLE

                holder.binding.etComment.visibility= View.VISIBLE
                holder.binding.etRemark.visibility= View.VISIBLE
                //holder.binding.rbClosed.visibility = View.INVISIBLE
                //holder.binding.rbOpen.visibility = View.GONE

                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    val position = holder.adapterPosition
                    activity.radioCheckListener(Constant.RAC_BUSINESS, position, "OPEN", "BM");
                }
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

        holder.binding.btnRemark.setOnClickListener {
            if (holder.binding.etRemark.visibility == View.VISIBLE) {
                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    Utility.hideKeyboard(activity)
                }
                //holder.binding.etRemark.visibility = View.GONE
                val remark = holder.binding.etRemark.text.toString()
                if (remark.isNotEmpty()) {
                    //retailAudit.brndFreeText = remark

                    if (context is CreateNewRACActivity) {
                        val activity = context as CreateNewRACActivity
                        val position = holder.adapterPosition
                        activity.saveRemarks(Constant.RAC_BUSINESS, position, remark);
                    }
                    //holder.binding.etRemark.isEnabled = false
                    holder.binding.etRemark.isFocusable = false
                }

                /*Handler().postDelayed({
                    val remark = holder.binding.etRemark.text.toString()
                    if (remark.isNotEmpty()) {
                        retailAudit.brndFreeText = remark
                    }
                },10)*/
            } else
                holder.binding.etRemark.visibility = View.VISIBLE
        }

        holder.binding.btnComment.setOnClickListener {
            if (holder.binding.etComment.visibility == View.VISIBLE) {
                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    Utility.hideKeyboard(activity)
                }
                //holder.binding.etComment.visibility = View.GONE
                val comment = holder.binding.etComment.text.toString()
                if (comment.isNotEmpty()) {
                    // retailAudit.lsFreeText = comment

                    if (context is CreateNewRACActivity) {
                        val activity = context as CreateNewRACActivity
                        val position = holder.adapterPosition
                        activity.saveComments(Constant.RAC_BUSINESS, position, comment);
                    }
                    //holder.binding.etComment.isEnabled = false
                    holder.binding.etComment.isFocusable = false
                }
                /*Handler().postDelayed({
                    val comment = holder.binding.etComment.text.toString()
                    if (comment.isNotEmpty()) {
                        retailAudit.lsFreeText = comment
                    }
                }, 10)*/
            } else
                holder.binding.etComment.visibility = View.VISIBLE
        }

        holder.binding.spName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(prt: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                if (prt?.id == R.id.spName) {
                    val assignTo = holder.binding.spName.selectedItem.toString()
                    //retailAudit.taskAssigned = assignTo
                    if (context is CreateNewRACActivity) {
                        val activity = context as CreateNewRACActivity
                        val position = holder.adapterPosition
                        activity.saveAssigneName(Constant.RAC_BUSINESS, position, assignTo);
                    }
                    if (holder.binding.spName.selectedItem.toString().equals("Other", true))
                        holder.binding.userIdLayout.visibility = View.VISIBLE
                    else
                        holder.binding.userIdLayout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }


        holder.binding.spRating.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(prt: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                if (prt?.id == R.id.spRating) {
                    val rating = holder.binding.spRating.selectedItem.toString()
                    //retailAudit.rating = rating
                    if (context is CreateNewRACActivity) {
                        val activity = context as CreateNewRACActivity
                        val position = holder.adapterPosition
                        activity.saveRatings(Constant.RAC_BUSINESS, position, rating);
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        holder.binding.btnSaveUserId.setOnClickListener {
            val userId = holder.binding.etUserId.text.toString()
            if (userId.length >= 7) {
                // retailAudit.taskAssigned = userId
                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    Utility.hideKeyboard(activity)
                }

                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
                    val position = holder.adapterPosition
                    activity.saveUserIds(Constant.RAC_BUSINESS, position, userId);
                }

                holder.binding.etUserId.clearFocus()
            } else {
                if (context is CreateNewRACActivity) {
                    val activity = context as CreateNewRACActivity
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

class BusinessViewHolder(val binding: SingleAuditDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(retailAudit:   RetailAudit) {
        binding.retailAudit = retailAudit
       // binding.inputDF = inputDateFormat
       // binding.outputDF = dateFormat
       // binding.tvBrand.isSelected = true
        //binding.userId = PreferenceUtils.getUserId()
    }
}