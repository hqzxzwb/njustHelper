package com.njust.helper.library;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.njust.helper.R;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.library.mylib.LibraryDatabaseManager;
import com.njust.helper.model.LibDetailItem;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.JsonTask;
import com.zwb.commonlibs.http.HttpMap;
import com.zwb.commonlibs.injection.IntentInjection;
import com.zwb.commonlibs.utils.JsonUtils;
import com.zwb.commonlibs.utils.MemCacheManager;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;

public class LibDetailActivity extends ProgressActivity implements SwipeRefreshLayout.OnRefreshListener {
    @IntentInjection("id")
    private String idString;

    private LibraryDatabaseManager manager;
    private String title;
    private boolean isCollected = false;
    private LibDetailAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private String code;

    private Intent resultIntent = new Intent();

    public static void showLibDetail(Context context, String idString) {
        Intent intent = new Intent(context, LibDetailActivity.class);
        intent.putExtra("id", idString);
        context.startActivity(intent);
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_lib_detail;
    }

    @Override
    protected void prepareViews() {
        manager = LibraryDatabaseManager.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new LibDetailAdapter(this);
        recyclerView.setAdapter(adapter);

        resultIntent.putExtra("id", idString);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    protected void firstRefresh() {
        LibDetailData data = MemCacheManager.get(getCacheName());
        if (data == null) {
            onRefresh();
        } else {
            notifyData(data);
        }
    }

    @Override
    protected void setupPullLayout(SwipeRefreshLayout layout) {
        layout.setOnRefreshListener(this);
    }

    private void notifyData(LibDetailData data) {
        String[] strings = data.getHead().split("\n");
        if (strings.length > 1) {
            title = strings[1];
        }
        List<LibDetailItem> list = data.getStates();
        code = list.size() == 0 ? "" : list.get(0).getCode();
        adapter.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lib_detail, menu);
        MenuItem item = menu.findItem(R.id.item_collect);
        if (manager.checkCollect(idString)) {
            item.setIcon(R.drawable.ic_action_important);
            isCollected = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_collect:
                if (title == null) {
                    showSnack("收藏失败，请刷新后重试");
                } else if (isCollected) {
                    manager.removeCollect(idString);
                    showSnack("已取消收藏");
                    isCollected = false;
                    item.setIcon(R.drawable.ic_action_not_important);
                } else if (manager.addCollect(idString, title, code)) {
                    showSnack("收藏成功");
                    isCollected = true;
                    item.setIcon(R.drawable.ic_action_important);
                } else {
                    showSnack("收藏失败,这本书已经收藏 ");
                    isCollected = true;
                    item.setIcon(R.drawable.ic_action_important);
                }
                resultIntent.putExtra("isCollected", isCollected);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getCacheName() {
        return "libDetail_" + idString;
    }

    @Override
    public void onRefresh() {
        attachAsyncTask(new LibDetailTask());
    }

    public static class LibDetailData {
        private List<LibDetailItem> states;
        private String head;

        public List<LibDetailItem> getStates() {
            return states;
        }

        public void setStates(List<LibDetailItem> states) {
            this.states = states;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }
    }

    private class LibDetailTask extends JsonTask<Void, LibDetailData> {
        @Override
        protected void onNetError() {
            showSnack(getString(R.string.message_net_error));
        }

        @Override
        protected void onServerError() {
            showSnack(getString(R.string.message_server_error_lib));
        }

        @Override
        protected void onSuccess(LibDetailData libDetailData) {
            MemCacheManager.put(getCacheName(), libDetailData);
            notifyData(libDetailData);
        }

        @Override
        protected void onCancelled(JsonData<LibDetailData> libDetailDataJsonData) {
            if (libDetailDataJsonData != null && libDetailDataJsonData.isValid()) {
                MemCacheManager.put(getCacheName(), libDetailDataJsonData.getData());
            }
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected JsonData<LibDetailData> doInBackground(Void... params) {
            HttpMap data = new HttpMap();
            data.addParam("id", idString);
            try {
                String string = new AppHttpHelper().getPostResult("libDetail.php", data);
                return new JsonData<LibDetailData>(string) {
                    @Override
                    protected LibDetailData parseData(JSONObject jsonObject) throws Exception {
                        LibDetailData libDetailData = new LibDetailData();
                        libDetailData.setHead(jsonObject.getString("head"));
                        libDetailData.setStates(JsonUtils.parseArray(
                                jsonObject.getJSONArray("content"), LibDetailItem.class));
                        return libDetailData;
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onPostExecute(JsonData<LibDetailData> libDetailDataJsonData) {
            super.onPostExecute(libDetailDataJsonData);
            setRefreshing(false);
        }
    }
}