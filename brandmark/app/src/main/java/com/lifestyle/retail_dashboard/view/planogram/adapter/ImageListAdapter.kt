package com.lifestyle.retail_dashboard.view.planogram.adapter

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.DialogShowImageBinding
import com.lifestyle.retail_dashboard.databinding.SingleImageDisplayBinding
import com.lifestyle.retail_dashboard.utils.DialogBox
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram


class ImageListAdapter(val context: Context, private val imageList: List<Planogram>): RecyclerView.Adapter<FilterAttendanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAttendanceViewHolder {
        val binding: SingleImageDisplayBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_image_display, parent, false)
        return FilterAttendanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: FilterAttendanceViewHolder, position: Int) {
        val planogram = imageList[position]
        holder.bindItems(planogram)
        holder.binding.root.setOnClickListener {
            //showDialog(context, planogram.plgImgId)
            /*val path = Uri.parse(planogram.filePath)
            val intent = Intent(Intent.ACTION_VIEW, path)
            //intent.setDataAndType(path, "application/pdf")
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //context.startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.d("Abrar","Exception: ${e.localizedMessage}")
                intent.setPackage(null)
                context.startActivity(intent)
            }*/

            // open image in dialog..

            DialogBox.showSviItemImageDialog(context, planogram.filePath)

        }
    }
}

fun showDialog(context: Context, imageUrl: String?) {
    val dialog = Dialog(context)
    //val window: Window? = dialog.window
    //window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    val binding: DialogShowImageBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_show_image, null, false)
    dialog.setContentView(binding.root)
    binding.imageUrl = imageUrl
    binding.btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

class FilterAttendanceViewHolder(val binding: SingleImageDisplayBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(image: Planogram) {
        binding.image = image
    }
}