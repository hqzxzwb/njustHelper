package com.njust.helper.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.njust.helper.AccountActivity;
import com.njust.helper.CaptchaActivity;
import com.njust.helper.R;
import com.njust.helper.model.CaptchaData;
import com.njust.helper.tools.DataBindingHolder;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.ProgressAsyncTask;
import com.zwb.commonlibs.ui.DividerItemDecoration;
import com.zwb.commonlibs.utils.JsonUtils;
import com.zwb.commonlibs.utils.MemCacheManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class MyListActivity<T, U extends ViewDataBinding> extends ProgressActivity implements SwipeRefreshLayout.OnRefreshListener {
    protected RecyclerView recyclerView;

    protected ListRecycleAdapter<T, U> adapter;

    private String mCacheName;

    @Override
    protected void prepareViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        loadId();
        mCacheName = buildCacheName();
    }

    @NonNull
    protected abstract ListRecycleAdapter<T, U> onCreateAdapter();

    protected void loadId() {

    }

    @Override
    protected void firstRefresh() {
        if (mCacheName == null) return;
        //noinspection unchecked
        ArrayList<T> list = MemCacheManager.get(mCacheName);
        if (list == null) {
            onRefresh();
        } else {
            adapter.setData(list);
        }
    }

    @Override
    protected void setupPullLayout(SwipeRefreshLayout layout) {
        layout.setOnRefreshListener(this);
    }

    protected abstract String buildCacheName();

    @Override
    protected int layoutRes() {
        return R.layout.activity_my_list;
    }

    protected abstract String getResponse() throws Exception;

    protected abstract Class<T> getItemClass();

    protected int getAccountRequest() {
        return AccountActivity.REQUEST_JWC;
    }

    protected int getNoResultText() {
        return R.string.message_no_result;
    }

    protected int getServerErrorText() {
        return R.string.message_server_error;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CaptchaActivity.REQUEST_CAPTCHA && resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        attachAsyncTask(new ListTask());
    }

    protected boolean emptyParam() {
        return false;
    }

    public abstract static class ListRecycleAdapter<T, S extends ViewDataBinding> extends RecyclerView.Adapter<DataBindingHolder<S>> {
        private List<T> data = new ArrayList<>();

        public ListRecycleAdapter() {
        }

        protected T getItem(int position) {
            return data.get(position);
        }

        @LayoutRes
        protected abstract int getLayoutRes();

        @Override
        public DataBindingHolder<S> onCreateViewHolder(ViewGroup parent, int viewType) {
            S s = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutRes(), parent, false);
            return new DataBindingHolder<>(s);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<T> data) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    private class ListTask extends ProgressAsyncTask<Void, List<T>> {
        public ListTask() {
            super(MyListActivity.this);
        }

        @Override
        protected JsonData<List<T>> doInBackground(Void... params) {
            if (emptyParam()) {
                return JsonData.newLogFailedInstance();
            }
            try {
                String string = getResponse();
                return new JsonData<List<T>>(string) {
                    @Override
                    protected List<T> parseData(JSONObject jsonObject) throws Exception {
                        return JsonUtils.parseArray(jsonObject.getJSONArray("content"), getItemClass());
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onCancelled(JsonData<List<T>> result) {
            if (result != null && result.isValid() && mCacheName != null) {
                MemCacheManager.put(mCacheName, result.getData());
            }
        }

        @Override
        protected void onNetError() {
            showSnack(R.string.message_net_error);
        }

        @Override
        protected void onCaptchaError(CaptchaData captchaData) {
            CaptchaActivity.startCaptcha(MyListActivity.this, getAccountRequest() == AccountActivity.REQUEST_JWC ? 0 : 1);
        }

        @Override
        protected void onServerError() {
            showSnack(getServerErrorText());
        }

        @Override
        protected void onSuccess(List<T> ts) {
            if (mCacheName != null) {
                MemCacheManager.put(mCacheName, ts);
            }
            adapter.setData(ts);
            if (ts.size() == 0) {
                showSnack(getNoResultText());
            }
        }

        @Override
        protected void onLogFailed() {
            changeAccount(getAccountRequest());
        }
    }
}
