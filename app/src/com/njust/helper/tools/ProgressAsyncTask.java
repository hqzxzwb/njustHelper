package com.njust.helper.tools;

import com.njust.helper.activity.ProgressActivity;

public abstract class ProgressAsyncTask<Params, Result> extends JsonTask<Params, Result> {
    private ProgressActivity mActivity;

    public ProgressAsyncTask(ProgressActivity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        mActivity.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(JsonData<Result> resultJsonData) {
        super.onPostExecute(resultJsonData);

        mActivity.setRefreshing(false);
    }
}
