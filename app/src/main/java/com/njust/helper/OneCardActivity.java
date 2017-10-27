package com.njust.helper;

import android.support.annotation.NonNull;

import com.njust.helper.activity.MyListActivity;
import com.njust.helper.databinding.ItemCardBinding;
import com.njust.helper.model.CardItem;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.DataBindingHolder;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpMap;

import java.util.List;

public class OneCardActivity extends MyListActivity<CardItem, ItemCardBinding> {
    private String stuid;

    @NonNull
    @Override
    protected ListRecycleAdapter<CardItem, ItemCardBinding> onCreateAdapter() {
        return new OneCardAdapter();
    }

    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
    }

    @Override
    protected String buildCacheName() {
        return "cardDetail_" + stuid;
    }

    @Override
    protected String getResponse() throws Exception {
        HttpMap data = new HttpMap();
        data.addParam("stuid", stuid);
        return new AppHttpHelper().getPostResult("cardDetail.php", data);
    }

    @Override
    protected Class<CardItem> getItemClass() {
        return CardItem.class;
    }

    @Override
    protected void changeAccount(int account_request) {
        showSnack("明细查询失败");
    }

    public static class OneCardAdapter extends ListRecycleAdapter<CardItem, ItemCardBinding> {
        @Override
        protected int getLayoutRes() {
            return R.layout.item_card;
        }

        @Override
        public void onBindViewHolder(DataBindingHolder<ItemCardBinding> holder, int position) {
            holder.getDataBinding().setItem(getItem(position));
        }

        @Override
        public void setData(List<CardItem> data) {
            super.setData(data);
        }
    }
}
