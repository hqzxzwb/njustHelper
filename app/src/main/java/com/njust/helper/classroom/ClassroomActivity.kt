package com.njust.helper.classroom

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.njust.helper.R
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityClassroomBinding
import com.njust.helper.tools.AppHttpHelper
import com.njust.helper.tools.Constants
import com.njust.helper.tools.JsonData
import com.njust.helper.tools.Prefs
import com.zwb.commonlibs.http.HttpMap
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : BaseActivity() {
    private lateinit var checkBoxes: Array<CheckBox>

    lateinit var binding: ActivityClassroomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val time = (System.currentTimeMillis() - Prefs.getTermStartTime(this)) % Constants.MILLIS_IN_ONE_DAY
        val captions = resources.getStringArray(R.array.sections)
        (0 until checkBoxes.size)
                .forEach { checkBoxes[it].text = captions[it] }

        var i = 0
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

    override fun layoutRes(): Int = 0

    override fun layout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_classroom)

        checkBoxes = arrayOf(
                binding.checkBox1,
                binding.checkBox2,
                binding.checkBox3,
                binding.checkBox4,
                binding.checkBox5
        )

        binding.button1.setOnClickListener { onClickQueryButton() }
    }

    private fun onClickQueryButton() {
        var sections = 0
        (0 until Constants.COURSE_SECTION_COUNT)
                .asSequence()
                .filter { checkBoxes[it].isChecked }
                .forEach { sections = sections or (1 shl it) }
        if (sections == 0) {
            showSnack(R.string.toast_cr_choose_one_section)
            return
        }
        val day = when (binding.dateGroup.checkedRadioButtonId) {
            R.id.radio0 -> 0
            R.id.radio1 -> 1
            else -> 2
        }
        val dateLong = System.currentTimeMillis() + 86400000 * day
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = Date(dateLong)
        val dateString = format.format(date)
        val buildingIndex = when (binding.buildingGroup.checkedRadioButtonId) {
            R.id.radio3 -> 0
            R.id.radio4 -> 1
            else -> 2
        }
        val building = BUILDING_VALUE[buildingIndex]
        binding.loading = true
        Observable
                .fromCallable { loadClassRoom(dateString, building, sections) }
                .subscribeOn(Schedulers.io())
                .subscribe(this::onResult, {}, {
                    binding.loading = false
                })
    }

    private fun loadClassRoom(date: String, building: String, sections: Int): JsonData<String> {
        val data = HttpMap()
        data.addParam("date", date)
                .addParam("building", building)
                .addParam("timeofday", Integer.toString(sections))
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
        return JsonData.newNetErrorInstance<String>()
    }

    private fun onResult(result: JsonData<String>) {
        when (result.status) {
            JsonData.STATUS_SUCCESS -> {
                val s = result.data
                if (s == "") {
                    binding.text = getString(R.string.text_classroom_no_info)
                } else {
                    binding.text = s
                }
            }
            JsonData.STATUS_NET_ERROR -> {
                binding.text = getString(R.string.text_classroom_fail)
            }
        }
    }

    override fun getViewForSnackBar(): View {
        return binding.coordinatorLayout
    }

    companion object {
        private val BUILDING_VALUE = arrayOf("Ⅳ", "II", "I")
    }
}
