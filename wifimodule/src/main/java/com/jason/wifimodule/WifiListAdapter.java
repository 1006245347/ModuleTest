package com.jason.wifimodule;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WifiListAdapter extends BaseQuickAdapter<WifiBean, BaseViewHolder> {
    public WifiListAdapter(int layoutResId, @Nullable List<WifiBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, WifiBean item) {
        helper.setText(R.id.tv_wifi, item.getWifiName())
                .setText(R.id.tv_encrypt,"加密方式:"+item.getEncrypt());
    }
}
