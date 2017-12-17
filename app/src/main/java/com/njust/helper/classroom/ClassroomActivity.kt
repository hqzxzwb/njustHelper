package com.njust.helper.classroom

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.BindView
import com.njust.helper.R
import com.njust.helper.activity.ProgressActivity
import com.njust.helper.tools.*
import com.zwb.commonlibs.http.HttpMap
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : ProgressActivity() {
    private lateinit var checkBoxes: Array<CheckBox>

    @BindView(R.id.radioGroup1)
    lateinit var dateGroup: RadioGroup
    @BindView(R.id.radioGroup2)
    lateinit var buildingGroup: RadioGroup
    @BindView(R.id.textView1)
    lateinit var textView: TextView
    @BindView(R.id.button1)
    lateinit var button: FloatingActionButton
    @BindView(R.id.coordinatorLayout)
    lateinit var coordinatorLayout: CoordinatorLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun prepareViews() {
        checkBoxes = arrayOf(
                findViewById(R.id.checkBox1),
                findViewById(R.id.checkBox2),
                findViewById(R.id.checkBox3),
                findViewById(R.id.checkBox4),
                findViewById(R.id.checkBox5)
        )

        val time = (System.currentTimeMillis() - Prefs.getTermStartTime(this)) % Constants.MILLIS_IN_ONE_DAY
        val captions = resources.getStringArray(R.array.sections)
        var i = 0
        while (i < Constants.COURSE_SECTION_COUNT) {
            checkBoxes[i].text = captions[i]
            i++
        }

        i = 0
        while (i < Constants.COURSE_SECTION_COUNT) {
            if (time < Constants.SECTION_END[i]) {
                break
            }
            i++
        }
        if (i < Constants.COURSE_SECTION_COUNT) {
            checkBoxes[i].isChecked = true
            if (i < 4 && 2 * time > Constants.SECTION_START[i] + Constants.SECTION_END[i]) {
                checkBoxes[i + 1].isChecked = true
            }
        }
    }

    override fun layoutRes(): Int {
        return R.layout.activity_classroom
    }

    fun onClick(view: View) {
        var sections = 0
        (0 until Constants.COURSE_SECTION_COUNT)
                .asSequence()
                .filter { checkBoxes[it].isChecked }
                .forEach { sections = sections or (1 shl it) }
        if (sections == 0) {
            showSnack(R.string.toast_cr_choose_one_section)
            return
        }
        val day = when (dateGroup.checkedRadioButtonId) {
            R.id.radio0 -> 0
            R.id.radio1 -> 1
            else -> 2
        }
        val dateLong = System.currentTimeMillis() + 86400000 * day
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = Date(dateLong)
        val dateString = format.format(date)
        val buildingIndex = when (buildingGroup.checkedRadioButtonId) {
            R.id.radio3 -> 0
            R.id.radio4 -> 1
            else -> 2
        }
        val building = BUILDING_VALUE[buildingIndex]
        attachAsyncTask(ClassRoomTask(), dateString, building, Integer.toString(sections))
    }

    override fun setRefreshing(b: Boolean) {
        super.setRefreshing(b)
        button.isEnabled = !b
    }

    override fun setupActionBar() {
        setSupportActionBar(toolbar)
        super.setupActionBar()
    }

    override fun getViewForSnackBar(): View {
        return coordinatorLayout
    }

    private inner class ClassRoomTask : ProgressAsyncTask<String, String>(this@ClassroomActivity) {
        override fun doInBackground(vararg params: String): JsonData<String> {
            val data = HttpMap()
            data.addParam("date", params[0])
                    .addParam("building", params[1])
                    .addParam("timeofday", params[2])
            try {
                val string = AppHttpHelper().getPostResult("classroom.php", data)
                return object : JsonData<String>(string) {
                    @Throws(Exception::class)
                    override fun parseData(jsonObject: JSONObject): String {
                        return jsonObject.getString("content")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return JsonData.newNetErrorInstance()
        }

        override fun onNetError() {
            textView.setText(R.string.text_classroom_fail)
        }

        override fun onSuccess(s: String) {
            if (s == "") {
                textView.setText(R.string.text_classroom_no_info)
            } else {
                textView.text = s
            }
        }
    }

    companion object {
        private val BUILDING_VALUE = arrayOf("Ⅳ", "II", "I")
    }
}
