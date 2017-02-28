package com.njust.helper;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.njust.helper.activity.ProgressActivity;
import com.njust.helper.classroom.ClassroomActivity;
import com.njust.helper.classroom.CourseQueryActivity;
import com.njust.helper.course.CourseActivity;
import com.njust.helper.course.CourseHomeView;
import com.njust.helper.course.CourseManager;
import com.njust.helper.grade.ExamsActivity;
import com.njust.helper.grade.GradeActivity;
import com.njust.helper.grade.GradeLevelActivity;
import com.njust.helper.library.LibSearchActivity;
import com.njust.helper.library.mylib.LibBorrowActivity;
import com.njust.helper.library.mylib.LibCollectionActivity;
import com.njust.helper.model.Course;
import com.njust.helper.model.UpdateInfo;
import com.njust.helper.settings.AboutActivity;
import com.njust.helper.settings.SettingsActivityV11;
import com.njust.helper.settings.UpdateActivity;
import com.njust.helper.settings.UpdateLogDialog;
import com.njust.helper.tools.AppHttpHelper;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.JsonData;
import com.njust.helper.tools.Prefs;
import com.njust.helper.tools.ProgressAsyncTask;
import com.zwb.commonlibs.http.HttpHelper;
import com.zwb.commonlibs.injection.ViewInjection;
import com.zwb.commonlibs.utils.MemCacheManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ProgressActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final int RESULT_COURSE_REFRESH = 2;
    public static final int REQUEST_COURSE_REFRESH = 0;
    private static final String ONE_CARD_CACHE_NAME = "OCCN";

    @ViewInjection(R.id.courseHomeView)
    private CourseHomeView courseHomeView;
//    @ViewInjection(R.id.tvCardBalance)
//    private TextView cardBalanceView;

    private BroadcastReceiver receiver;
    private ProgressDialog checkUpdateDialog;

    @Override
    protected void prepareViews() {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String id = Prefs.getId(this);
        if (id.equals("")) {
            startActivity(AccountActivity.class);
        } else {
            updateCourse();
        }

        if (receiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(receiver, new IntentFilter(BackgroundService.ACTION_UPDATE_INFO));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    @Override
    protected void firstRefresh() {
        // 如果距离上次刷新超过五小时,检查更新
        long now = System.currentTimeMillis();
        long last_time = Prefs.getLastCheckUpdateTime(this);
        long time = now - last_time;
        if (time < 0 || time > 18000000L) {
            if (HttpHelper.getNetworkState(this)) {
                startService(new Intent(this, BackgroundService.class)
                        .putExtra("action", "checkUpdate"));
            }
        }
        //每学期更新
        int preVersion = Prefs.getVersion(this);
        if (BuildConfig.VERSION_CODE != preVersion) {
            File file = getExternalCacheDir();
            if (file != null) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.toString().endsWith(".apk")) {
                            //尝试删除之前下载到缓存目录的更新文件，并不关心删除是否成功
                            //noinspection ResultOfMethodCallIgnored
                            f.delete();
                        }
                    }
                }
            }

            UpdateLogDialog.showUpdateDialog(this);
            Prefs.putVersion(this, BuildConfig.VERSION_CODE);
            if (preVersion == 0) {
                if (BuildConfig.DEBUG) {
                    Prefs.putIdValues(this,
                            getString(R.string.testStuid),
                            getString(R.string.testJwcPwd),
                            getString(R.string.testLibPwd));
                }
            }
        }
//        //刷新一卡通余额
//        String string = MemCacheManager.get(ONE_CARD_CACHE_NAME);
//        if (string != null) {
//            setCardBalance(string);
//        } else {
//            setCardBalance("");
//            onRefresh();
//        }
    }

//    private void setCardBalance(String balance) {
//        cardBalanceView.setText(getString(R.string.text_home_card_balance, balance));
//    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    public void lib_borrow(View view) {
        startActivity(LibBorrowActivity.class);
    }

    public void lib_collection(View view) {
        startActivity(LibCollectionActivity.class);
    }

    public void lib_search(View view) {
        startActivity(LibSearchActivity.class);
    }

    public void id(View view) {
        startActivity(AccountActivity.class);
    }

    public void course_query(View view) {
        startActivity(CourseQueryActivity.class);
    }

    public void level_grades(View v) {
        startActivity(GradeLevelActivity.class);
    }

    public void links(View view) {
        startActivity(LinksActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COURSE_REFRESH &&
                resultCode == RESULT_COURSE_REFRESH) {
            updateCourse();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void setupActionBar() {
    }

//    @Override
//    protected void setupPullLayout(SwipeRefreshLayout layout) {
//        layout.setOnRefreshListener(this);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(SettingsActivityV11.class);
                return true;
            case R.id.item_about:
                startActivity(AboutActivity.class);
                return true;
            case R.id.item_account:
                startActivity(AccountActivity.class);
                return true;
            case R.id.item_update:
                checkUpdateDialog = ProgressDialog.show(this, "请稍候", "正在检查更新……", true, false);
                Intent intent = new Intent(this, BackgroundService.class);
                intent.putExtra("silentlyCheckUpdate", false)
                        .putExtra("action", "checkUpdate");
                if (receiver == null) {
                    receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            checkUpdateDialog.dismiss();
                            int status = intent.getIntExtra("updateStatus", BackgroundService.UPDATE_STATUS_FAIL);
                            if (status == BackgroundService.UPDATE_STATUS_FAIL) {
                                showSnack("检查更新失败，请检查网络后重试");
                            } else if (status == BackgroundService.UPDATE_STATUS_NO_UPDATE) {
                                showSnack("未检测到更新");
                            } else {
                                final UpdateInfo updateInfo = (UpdateInfo) intent.getSerializableExtra("updateInfo");
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("发现新版本")
                                        .setMessage(updateInfo.toString())
                                        .setPositiveButton("立即查看", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(MainActivity.this, UpdateActivity.class)
                                                        .putExtra("updateInfo", updateInfo));
                                            }
                                        })
                                        .setNegativeButton("以后再说", null)
                                        .show();
                            }
                        }
                    };
                    LocalBroadcastManager.getInstance(this)
                            .registerReceiver(receiver, new IntentFilter(BackgroundService.ACTION_UPDATE_INFO));
                }
                startService(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void course(View view) {
        startActivityForResult(CourseActivity.class, REQUEST_COURSE_REFRESH);
    }

    public void classroom(View view) {
        startActivity(ClassroomActivity.class);
    }

    public void exams(View view) {
        startActivity(ExamsActivity.class);
    }

    public void grade(View v) {
        startActivity(GradeActivity.class);
    }

    private void updateCourse() {
        long minus = System.currentTimeMillis() - Prefs.getTermStartTime(this);
        int day = (int) (minus / Constants.MILLIS_IN_ONE_DAY);
        if (minus < 0L) {
            day--;
        }
        CourseManager manager = CourseManager.getInstance(this);
        List<Course> list1 = manager.getCourses(day);
        List<Course> list2 = manager.getCourses(++day);
        List<Course> list3 = manager.getCourses(++day);
        if (list1.size() + list2.size() + list3.size() == 0) {
            courseHomeView.setData(null);
        } else {
            List<String> strings = new ArrayList<>();
            String[] timeList = getResources().getStringArray(R.array.section_start);
            int millisOfDay = (int) ((System.currentTimeMillis() - Prefs.getTermStartTime(this)) % Constants.MILLIS_IN_ONE_DAY);
            for (Course course : list1) {
                if (millisOfDay > Constants.SECTION_END[course.getSec1()]) continue;
                strings.add("今天" + timeList[course.getSec1()] + "/" + course.getClassroom() + "/" + course.getName());
            }
            for (Course course : list2) {
                strings.add("明天" + timeList[course.getSec1()] + "/" + course.getClassroom() + "/" + course.getName());
            }
            for (Course course : list3) {
                strings.add("后天" + timeList[course.getSec1()] + "/" + course.getClassroom() + "/" + course.getName());
            }
            courseHomeView.setData(strings);
        }
    }

    public void card(View view) {
        startActivity(OneCardActivity.class);
    }

    @Override
    public void onRefresh() {
//        attachAsyncTask(new ProgressAsyncTask<Void, String>(this) {
//            @Override
//            protected JsonData<String> doInBackground(Void... voids) {
//                HttpHelper.HttpMap map = new HttpHelper.HttpMap();
//                map.addParam("stuid", Prefs.getId(MainActivity.this));
//                try {
//                    String s = new AppHttpHelper().getPostResult("cardInfo.php", map);
//                    return new JsonData<String>(s) {
//                        @Override
//                        protected String parseData(JSONObject jsonObject) throws Exception {
//                            return jsonObject.getString("content");
//                        }
//                    };
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return JsonData.newNetErrorInstance();
//            }
//
//            @Override
//            protected void onPostExecute(JsonData<String> stringJsonData) {
//                super.onPostExecute(stringJsonData);
//
//                if (stringJsonData.isValid()) {
//                    String result = stringJsonData.getData();
//                    setCardBalance(result);
//                    MemCacheManager.put(ONE_CARD_CACHE_NAME, result);
//                } else {
//                    setCardBalance("查询失败");
//                }
//            }
//        });
    }
}
