package com.njust.helper.classroom;

import android.support.annotation.NonNull;

import com.njust.helper.R;
import com.njust.helper.activity.MyListActivity;
import com.njust.helper.databinding.ItemCourseQueryBinding;
import com.njust.helper.model.CourseQuery;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.DataBindingHolder;
import com.zwb.commonlibs.http.HttpMap;
import com.zwb.commonlibs.injection.IntentInjection;
import com.zwb.commonlibs.utils.LogUtils;

public class CourseQueryResultActivity extends MyListActivity<CourseQuery, ItemCourseQueryBinding> {
    @IntentInjection("section")
    private String section;
    @IntentInjection("day")
    private String day;
    @IntentInjection("name")
    private String name;
    @IntentInjection("teacher")
    private String teacher;

    @NonNull
    @Override
    protected ListRecycleAdapter<CourseQuery, ItemCourseQueryBinding> onCreateAdapter() {
        return new CourseQueryAdapter();
    }

    @Override
    protected String buildCacheName() {
        String string = "courseQuery_" + section + "_" + day + "_" + name + "_" + teacher;
        LogUtils.i(this, string);
        return string;
    }

    @Override
    protected String getResponse() throws Exception {
        HttpMap data = new HttpMap();
        data.addParam("section", section)
                .addParam("day", day)
                .addParam("name", name)
                .addParam("teacher", teacher);
        return new AppHttpHelper().getPostResult("course_query.php", data);
    }

    @Override
    protected Class<CourseQuery> getItemClass() {
        return CourseQuery.class;
    }


    private static class CourseQueryAdapter extends ListRecycleAdapter<CourseQuery, ItemCourseQueryBinding> {
        @Override
        protected int getLayoutRes() {
            return R.layout.item_course_query;
        }

        @Override
        public void onBindViewHolder(DataBindingHolder<ItemCourseQueryBinding> holder, int position) {
            holder.getDataBinding().setCourse(getItem(position));
            holder.getDataBinding().setPosition(position);
        }
    }
}