package com.njust.helper.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.njust.helper.AccountActivity;
import com.njust.helper.BackgroundService;
import com.njust.helper.BuildConfig;
import com.njust.helper.LinksActivity;
import com.njust.helper.R;
import com.njust.helper.activity.BaseActivity;
import com.njust.helper.classroom.ClassroomActivity;
import com.njust.helper.classroom.CourseQueryActivity;
import com.njust.helper.course.CourseActivity;
import com.njust.helper.course.CourseManager;
import com.njust.helper.databinding.ActivityMainBinding;
import com.njust.helper.grade.ExamsActivity;
import com.njust.helper.grade.GradeActivity;
import com.njust.helper.grade.GradeLevelActivity;
import com.njust.helper.library.LibSearchActivity;
import com.njust.helper.library.mylib.LibBorrowActivity;
import com.njust.helper.library.mylib.LibCollectionActivity;
import com.njust.helper.model.Course;
import com.njust.helper.model.UpdateInfo;
import com.njust.helper.settings.AboutActivity;
import com.njust.helper.settings.SettingsActivity;
import com.njust.helper.settings.UpdateActivity;
import com.njust.helper.settings.UpdateLogDialog;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.http.NetState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final int RESULT_COURSE_REFRESH = 2;
    public static final int REQUEST_COURSE_REFRESH = 0;

    private BroadcastReceiver receiver;
    ProgressDialog checkUpdateDialog;

    private MainViewModel viewModel = new MainViewModel();

    @Override
    protected void layout() {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setVm(viewModel);
        binding.setClickHandler(this);
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

        checkUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    private void checkUpdate() {
        // 如果距离上次刷新超过一天,检查更新
        long now = System.currentTimeMillis();
        long last_time = Prefs.getLastCheckUpdateTime(this);
        long time = now - last_time;
        if (time < 0 || time > 24 * 3600 * 1000L) {
            if (NetState.getNetworkState(this)) {
                startService(new Intent(this, BackgroundService.class)
                        .putExtra("action", "checkUpdate"));
            }
        }
        // 删除更新文件并弹出更新日志
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
    }

    @Override
    protected int layoutRes() {
        return 0;
    }

    public void openLibBorrowActivity(View view) {
        startActivity(LibBorrowActivity.class);
    }

    public void openLibCollectionActivity(View view) {
        startActivity(LibCollectionActivity.class);
    }

    public void openLibSearchActivity(View view) {
        startActivity(LibSearchActivity.class);
    }

    public void openCourseQueryActivity(View view) {
        startActivity(CourseQueryActivity.class);
    }

    public void openGradeLevelActivity(View v) {
        startActivity(GradeLevelActivity.class);
    }

    public void openLinksActivity(View view) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(SettingsActivity.class);
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
                                        .setPositiveButton("立即查看", (dialog, which) -> startActivity(new Intent(MainActivity.this, UpdateActivity.class).putExtra("updateInfo", updateInfo)))
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

    public void openCourseActivity(View view) {
        startActivityForResult(CourseActivity.class, REQUEST_COURSE_REFRESH);
    }

    public void openClassroomActivity(View view) {
        startActivity(ClassroomActivity.class);
    }

    public void openExamsActivity(View view) {
        startActivity(ExamsActivity.class);
    }

    public void openGradeActivity(View v) {
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
            viewModel.setCourses(null);
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
            viewModel.setCourses(strings);
        }
    }
}
