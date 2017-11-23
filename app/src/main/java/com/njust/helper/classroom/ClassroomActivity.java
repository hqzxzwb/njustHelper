package com.njust.helper.classroom;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.njust.helper.R;
import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.Prefs;
import com.njust.helper.tools.ProgressAsyncTask;
import com.zwb.commonlibs.http.HttpMap;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * 自习室查询
 *
 * @author zwb
 */
public class ClassroomActivity extends ProgressActivity {
    private static final String[] BUILDING_VALUE = {"Ⅳ", "II", "I"};

    private CheckBox[] checkBoxes;

    @BindView(R.id.radioGroup1)
    RadioGroup dateGroup;
    @BindView(R.id.radioGroup2)
    RadioGroup buildingGroup;
    @BindView(R.id.textView1)
    TextView textView;
    @BindView(R.id.button1)
    FloatingActionButton button;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void prepareViews() {
        checkBoxes = new CheckBox[5];
        checkBoxes[0] = findViewById(R.id.checkBox1);
        checkBoxes[1] = findViewById(R.id.checkBox2);
        checkBoxes[2] = findViewById(R.id.checkBox3);
        checkBoxes[3] = findViewById(R.id.checkBox4);
        checkBoxes[4] = findViewById(R.id.checkBox5);

        long time = (System.currentTimeMillis() - Prefs.getTermStartTime(this)) % Constants.MILLIS_IN_ONE_DAY;
        String[] captions = getResources().getStringArray(R.array.sections);
        int i = 0;
        for (; i < 5; i++) {
            checkBoxes[i].setText(captions[i]);
        }

        for (i = 0; i < 5; i++) {
            if (time < Constants.SECTION_END[i]) {
                break;
            }
        }
        if (i < 5) {
            checkBoxes[i].setChecked(true);
            if (i < 4 && 2 * time > Constants.SECTION_START[i] + Constants.SECTION_END[i]) {
                checkBoxes[i + 1].setChecked(true);
            }
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_classroom;
    }

    public void onClick(View view) {
        int sections = 0;
        for (int i = 0; i < 5; i++) {
            if (checkBoxes[i].isChecked()) {
                sections |= 1 << i;
            }
        }
        if (sections == 0) {
            showSnack(R.string.toast_cr_choose_one_section);
            return;
        }
        int i = dateGroup.getCheckedRadioButtonId();
        i = i == R.id.radio0 ? 0 : (i == R.id.radio1 ? 1 : 2);
        long dateLong = System.currentTimeMillis() + 86400000 * i;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date(dateLong);
        String dateString = format.format(date);
        i = buildingGroup.getCheckedRadioButtonId();
        i = i == R.id.radio3 ? 0 : (i == R.id.radio4 ? 1 : 2);
        String building = BUILDING_VALUE[i];
        attachAsyncTask(new ClassRoomTask(), dateString, building, Integer.toString(sections));
    }

    @Override
    public void setRefreshing(boolean b) {
        super.setRefreshing(b);
        button.setEnabled(!b);
    }

    @Override
    protected void setupActionBar() {
        setSupportActionBar(toolbar);
        super.setupActionBar();
    }

    @Override
    protected View getViewForSnackBar() {
        return coordinatorLayout;
    }

    private class ClassRoomTask extends ProgressAsyncTask<String, String> {
        public ClassRoomTask() {
            super(ClassroomActivity.this);
        }

        @Override
        protected JsonData<String> doInBackground(String... params) {
            HttpMap data = new HttpMap();
            data.addParam("date", params[0])
                    .addParam("building", params[1])
                    .addParam("timeofday", params[2]);
            try {
                String string = new AppHttpHelper().getPostResult("classroom.php", data);
                return new JsonData<String>(string) {
                    @Override
                    protected String parseData(JSONObject jsonObject) throws Exception {
                        return jsonObject.getString("content");
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JsonData.newNetErrorInstance();
        }

        @Override
        protected void onNetError() {
            textView.setText(R.string.text_classroom_fail);
        }

        @Override
        protected void onSuccess(String s) {
            if (s.equals("")) {
                textView.setText(R.string.text_classroom_no_info);
            } else {
                textView.setText(s);
            }
        }
    }
}
