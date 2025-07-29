package com.lifestyle.buddydetagging.view.detagging.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.ImageZoom;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.detagging.model.ItemList;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDetailDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class OrderItemDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    //private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<OrderDetailDto> mFilteredList;
    private SviOrderClickLisner sviOrderClickLisner;
    private Context context;
    private Activity activity;
    boolean isChecked1 = false;

    public OrderItemDetailAdapter(Context context, List<OrderDetailDto> mFilteredList) {
        this.activity = activity;//added SUPRIYA FOR IMAGE
        this.context = context;
        this.mFilteredList = mFilteredList;

    }

    public OrderItemDetailAdapter(Context context, List<OrderDetailDto> mFilteredList, SviOrderClickLisner sviOrderClickLisner) {
        this.activity = activity;//added SUPRIYA FOR IMAGE
        this.context = context;
        this.mFilteredList = mFilteredList;
        this.sviOrderClickLisner = sviOrderClickLisner;

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dt_order_item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mFilteredList != null ? mFilteredList.size() : 0;
    }


    private OrderDetailDto getItem(int position) {
        return mFilteredList.get(position);
    }

    public class ViewHolder extends BaseViewHolder {

        private CardView itemDetailCV;


        private TextView itemCodeValTV;
        private TextView eanCodeValTV;
        private TextView itemDescValTV;

        private TextView itemColorTV;
        private TextView itemMrpTV;
        private TextView itemRspTV;
        private TextView itemSizeTV;
        private TextView orderTagStatusValTV;

        //supriya added image for LS 05-10-2023
        private ImageView itemImage;
        private TextView imageMessage;
        LinearLayout imageforls;
        CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            itemDetailCV = itemView.findViewById(R.id.itemDetailCV);
            orderTagStatusValTV = itemView.findViewById(R.id.orderTagStatusValTV);

            itemCodeValTV = itemView.findViewById(R.id.itemCodeValTV);
            eanCodeValTV = itemView.findViewById(R.id.eanCodeValTV);
            itemDescValTV = itemView.findViewById(R.id.itemDescValTV);

            itemColorTV = itemView.findViewById(R.id.itemColorTV);
            itemMrpTV = itemView.findViewById(R.id.itemMrpTV);
            itemRspTV = itemView.findViewById(R.id.itemRspTV);
            itemSizeTV = itemView.findViewById(R.id.itemSizeTV);

            //added supriya 05-10-2023
            itemImage =itemView.findViewById(R.id.itemImage);
            imageMessage =itemView.findViewById(R.id.imageMessage);
            imageforls =itemView.findViewById(R.id.imageforls);
            checkbox =itemView.findViewById(R.id.checkbox);
          //boolean isChecked1 = checkbox.isChecked();

            if(PreferenceUtils.getOuCode().equalsIgnoreCase("Max")){
                imageforls.setVisibility(View.GONE);
                itemImage.setVisibility(View.GONE);
                checkbox.setVisibility(View.GONE);
            }else{

                checkbox.setVisibility(View.VISIBLE);
                imageMessage.setVisibility(View.VISIBLE);
            }
            ////////////////////////////////////////////

        }

        protected void clearView() {

        }


        @SuppressLint("SetTextI18n")
        public void onBind(final int position) {
            super.onBind(position);
            try {
                final OrderDetailDto orders = mFilteredList.get(position);
                ItemList itemData = orders.getItemList();
                itemCodeValTV.setText("" + itemData.getItemID());
                eanCodeValTV.setText("" + itemData.getItemEAN());

                itemDescValTV.setText("Desc: " + itemData.getItemDesc());
                itemColorTV.setText("Color: " + itemData.getItemColour());

                itemMrpTV.setText("MRP: " + itemData.getSellPrice());
                itemRspTV.setText("RSP: " + itemData.getExtSellPrice());
                itemSizeTV.setText("Size: " + itemData.getItemSize());


                orderTagStatusValTV.setText("" + orders.getTagStatusVal());

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    isChecked1 = isChecked; // Update the isChecked1 variable when the checkbox state changes
                    itemData.setCheckboxvalue(isChecked);
                    orders.getItemList().checkboxvalue=isChecked;
                    // isChecked1 will be true when the checkbox is checked
                });

                if (orders.getTagStatusVal().equals("Tag")) {
                    orderTagStatusValTV.setTextColor(context.getResources().getColor(R.color.red));
                    checkbox.setEnabled(true);
                } else {
                    orderTagStatusValTV.setTextColor(context.getResources().getColor(R.color.green));
                }
                Log.e("Log Status", position+ "_"+ orders.getTagStatus());
                if (orders.getTagStatus() == 0) {
                    Log.e("get status", position+"_"+"_"+orders.getItemList().checkboxvalue);
                    if(orders.getItemList().checkboxvalue)
                    {
                        checkbox.setChecked(true);
                    }else{
                        checkbox.setChecked(false);
                    }
                    checkbox.setEnabled(true);
                    itemDetailCV.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else if (orders.getTagStatus() == 1) {
                    itemDetailCV.setCardBackgroundColor(context.getResources().getColor(R.color.light_green));
                    //checkbox.isChecked();
                    checkbox.setChecked(true);
                    checkbox.setEnabled(false);


                    //supriya added after detag we cant able to uncheck the chekbox
                }





                //supriya added for image
                new Handler().postDelayed(() -> {
                    if (itemData.getImageURL() != null) {
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.lifestyle);
                        requestOptions.error(R.drawable.lifestyle);

                        Glide.with(context).setDefaultRequestOptions(requestOptions)
                                .load(itemData.getImageURL())
                                .into(itemImage);
                        if(!PreferenceUtils.getOuCode().equalsIgnoreCase("Max")) {
                            checkbox.setVisibility(View.VISIBLE);
                            imageMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                       // itemImage.setImageResource(R.drawable.no_image_new);
                       // if(itemData.getImageURL() != null && itemData.getImageURL().equalsIgnoreCase("https://i1.lmsin.net/website_images/static-pages/brand_exp/brand2images/loadingimages/lifestyle/in/en/new_loading_150.svg")) {
                            itemImage.setImageResource(R.drawable.lifestyle);
                            imageMessage.setVisibility(View.GONE);
                             checkbox.setVisibility(View.GONE);
                       // }
                    }
                }, 100);

                itemImage.setOnClickListener(v -> {
                    try {
                        if (itemData.getImageURL() != null) {
                            itemImage.setEnabled(false);
                            showSviItemImageDialog(context, itemData.getImageURL(), itemImage);
                        } else {
                            CommonUtility.showSnackBarr(context, "No Image found!!!");
                        }
                    } catch (Exception e) {
                        Log.d("Abrar", e.getLocalizedMessage());
                    }
                });
                ///////////////////////////

                itemView.setOnClickListener(v -> {
                    try {
                        sviOrderClickLisner.onOrderClick(position, orders);
                    } catch (Exception e) {
                        Log.d("Abrar", e.getLocalizedMessage());
                    }
                });
            } catch (Exception e) {
                Log.d("Abrar", e.getLocalizedMessage());
            }
        }
    }

    //check box checked method supriya 12-10-2023
    public boolean isCheckboxChecked() {
        return isChecked1;
    }


    public List<OrderDetailDto> getData() {
        return mFilteredList;
    }

    public interface SviOrderClickLisner {
        void onOrderClick(int position, OrderDetailDto orders);
    }



    ///////////////////////supriya added for image zoomin zoom out 05-10-2023
    @SuppressLint({"ClickableViewAccessibility", "CheckResult"})
    public static void showSviItemImageDialog(Context ctx, String imageUrl, final ImageView btnImage) {
        final View dialogView = View.inflate(ctx, R.layout.image_popup, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.pauseDialogAnimation;
        dialog.show();
        dialog.setOnCancelListener(dialog1 -> {
            dialog1.dismiss();
            btnImage.setEnabled(true);
        });

        ImageView iv_expand_fullImage = dialog.findViewById(R.id.expand_image_Id);
        ImageView imvCrossIcon = dialog.findViewById(R.id.cross_icon);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_image_placeholder);
        requestOptions.error(R.drawable.ic_image_placeholder);
        Log.e("SVI Dialog", "imageUrl :: " + imageUrl);
        Glide.with(ctx).setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(iv_expand_fullImage);

        iv_expand_fullImage.setOnTouchListener((v, event) -> {
            ImageView view = (ImageView) v;
            //view.bringToFront();
            ImageZoom.getInstance().viewTransformation(view, event);
            return true;
        });

        imvCrossIcon.setOnClickListener(view -> {
            dialog.dismiss();
            btnImage.setEnabled(true);
        });
    }
    /////////////////////////////////////////////////////////////////////



}
