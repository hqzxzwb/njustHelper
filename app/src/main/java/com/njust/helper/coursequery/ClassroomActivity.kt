package com.njust.helper.coursequery

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.activity.BaseActivity
import com.njust.helper.databinding.ActivityClassroomBinding
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.launch

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

        val time = (System.currentTimeMillis() - RemoteConfig.getTermStartTime()) % TimeUtil.ONE_DAY
        val captions = resources.getStringArray(R.array.sections)
        checkBoxes.forEachIndexed { index, checkBox -> checkBox.text = captions[index] }

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
        val sections = checkBoxes
                .foldIndexed(0) { index, acc, checkBox ->
                    if (checkBox.isChecked) {
                        acc or (1 shl index)
                    } else {
                        acc
                    }
                }
        if (sections == 0) {
            showSnack(R.string.toast_cr_choose_one_section)
            return
        }
        val dayId = when (binding.dateGroup.checkedRadioButtonId) {
            R.id.radio0 -> 0
            R.id.radio1 -> 1
            else -> 2
        }
        val dateLong = System.currentTimeMillis() + TimeUtil.ONE_DAY * dayId
        val termStart = RemoteConfig.getTermStartTime()
        val dayIndex = ((dateLong - termStart) / TimeUtil.ONE_DAY).toInt()
        val week = dayIndex / 7 + 1
        val day = dayIndex % 7
        val buildingIndex = when (binding.buildingGroup.checkedRadioButtonId) {
            R.id.radio3 -> 0
            R.id.radio4 -> 1
            else -> 2
        }
        val building = BUILDING_VALUE[buildingIndex]
        binding.loading = true
        lifecycleScope.launch {
            try {
                val dao = CourseQueryDao.getInstance(this@ClassroomActivity)
                val allRooms = dao.queryClassroomSet(building)
                val ruledOutRooms = dao.queryClassroom(building, week, day, sections)
                val result = (allRooms - ruledOutRooms)
                        .fold(StringBuilder()) { acc, it ->
                            acc.append(it.replace('-', '_')).append("  ")
                        }
                        .toString()
                binding.loading = false
                if (result == "") {
                    binding.text = getString(R.string.text_classroom_no_info)
                } else {
                    binding.text = result
                }
            } catch (e: Exception) {
                binding.loading = false
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
