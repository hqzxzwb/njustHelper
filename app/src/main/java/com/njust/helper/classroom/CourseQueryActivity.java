package com.njust.helper.classroom;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.njust.helper.R;
import com.njust.helper.activity.ProgressActivity;

import butterknife.BindView;

public class CourseQueryActivity extends ProgressActivity {
    private static final SparseIntArray MAP1 = new SparseIntArray(6);
    private static final SparseIntArray MAP2 = new SparseIntArray(8);

    static {
        MAP1.put(R.id.radio1, -1);
        MAP1.put(R.id.radio2, 0);
        MAP1.put(R.id.radio3, 1);
        MAP1.put(R.id.radio4, 2);
        MAP1.put(R.id.radio5, 3);
        MAP1.put(R.id.radio6, 4);

        MAP2.put(R.id.radio7, -1);
        MAP2.put(R.id.radio8, 0);
        MAP2.put(R.id.radio9, 1);
        MAP2.put(R.id.radio10, 2);
        MAP2.put(R.id.radio11, 3);
        MAP2.put(R.id.radio12, 4);
        MAP2.put(R.id.radio13, 5);
        MAP2.put(R.id.radio14, 6);
    }

    @BindView(R.id.button1)
    FloatingActionButton button;
    @BindView(R.id.editText1)
    EditText classnameText;
    @BindView(R.id.editText2)
    EditText teacherText;
    @BindView(R.id.radioGroup1)
    RadioGroup sectionGroup;
    @BindView(R.id.radioGroup2)
    RadioGroup dayOfWeekGroup;

    @Override
    protected int layoutRes() {
        return R.layout.activity_course_query;
    }

    public void onClick(View view) {
        int timeofday = MAP1.get(sectionGroup.getCheckedRadioButtonId());
        int dayofweek = MAP2.get(dayOfWeekGroup.getCheckedRadioButtonId());
        Intent intent = new Intent(this, CourseQueryResultActivity.class);
        intent.putExtra("section", Integer.toString(timeofday < 0 ? -1 : 1 << timeofday));
        intent.putExtra("day", Integer.toString(dayofweek < 0 ? -1 : 1 << dayofweek));
        intent.putExtra("name", classnameText.getText().toString());
        intent.putExtra("teacher", teacherText.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void prepareViews() {

    }
}
