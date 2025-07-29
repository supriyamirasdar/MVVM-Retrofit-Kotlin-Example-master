package com.lifestyle.buddydetagging.view.detagging.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clearView();

    public void onBind(int position) {
        mCurrentPosition = position;
        clearView();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

}
