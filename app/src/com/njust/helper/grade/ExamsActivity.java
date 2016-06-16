package com.njust.helper.grade;

import android.support.annotation.NonNull;

import com.njust.helper.R;
import com.njust.helper.activity.MyListActivity;
import com.njust.helper.databinding.ItemExamBinding;
import com.njust.helper.model.Exam;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.DataBindingHolder;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.HttpHelper;

public class ExamsActivity extends MyListActivity<Exam, ItemExamBinding> {

    private String stuid, pwd;

    @Override
    protected void loadId() {
        stuid = Prefs.getId(this);
        pwd = Prefs.getJwcPwd(this);
    }

    @Override
    protected boolean emptyParam() {
        return stuid.equals("");
    }

    @NonNull
    @Override
    protected ListRecycleAdapter<Exam, ItemExamBinding> onCreateAdapter() {
        return new ExamAdapter();
    }

    @Override
    protected String buildCacheName() {
        return "exams_" + stuid;
    }

    @Override
    protected String getResponse() throws Exception {
        HttpHelper.HttpMap data = new HttpHelper.HttpMap();
        data.addParam("stuid", stuid).addParam("pwd", pwd);

        return new AppHttpHelper().getPostResult("exams2.php", data);
    }

    @Override
    protected int getNoResultText() {
        return R.string.message_no_exams_found;
    }

    @Override
    protected Class<Exam> getItemClass() {
        return Exam.class;
    }

    private static class ExamAdapter extends ListRecycleAdapter<Exam, ItemExamBinding> {
        @Override
        protected int getLayoutRes() {
            return R.layout.item_exam;
        }

        @Override
        public void onBindViewHolder(DataBindingHolder<ItemExamBinding> holder, int position) {
            holder.getDataBinding().setExam(getItem(position));
        }
    }
}
