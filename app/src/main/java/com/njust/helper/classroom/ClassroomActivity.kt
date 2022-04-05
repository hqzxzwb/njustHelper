package com.njust.helper.classroom

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.njust.helper.R
import com.njust.helper.RemoteConfig
import com.njust.helper.coursequery.CourseQueryDatabase
import com.njust.helper.tools.Constants
import com.njust.helper.tools.TimeUtil
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 自习室查询
 *
 * @author zwb
 */
class ClassroomActivity : AppCompatActivity(), KoinComponent {
  private val courseQueryDatabase: CourseQueryDatabase by inject()
  private val BUILDING_VALUE = arrayOf("Ⅳ-", "II-", "I-", "江阴")
  private val vm = ClassroomViewModel(
    onClickQuery = {
      lifecycleScope.launch {
        query()
      }
    },
    onClickHome = this::finish,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      ClassroomScreen(vm = this.vm)
    }

    val time = (System.currentTimeMillis() - RemoteConfig.getTermStartTime()) % TimeUtil.ONE_DAY

    var i = 0
    while (i < Constants.COURSE_SECTION_COUNT) {
      if (time < Constants.SECTION_END[i]) {
        break
      }
      i++
    }
    var selectedSections = 0
    if (i < Constants.COURSE_SECTION_COUNT) {
      selectedSections = selectedSections or (1 shl i)
      if (i < 4 && 2 * time > Constants.SECTION_START[i] + Constants.SECTION_END[i]) {
        selectedSections = selectedSections or (1 shl (i + 1))
      }
    }
    vm.selectedSections = selectedSections
  }

  suspend fun query() {
    val sections = vm.selectedSections
    if (sections == 0) {
      vm.noSectionChosenFlow.emit(Unit)
      return
    }
    vm.noSectionChosenFlow.emit(null)
    val dayId = vm.selectedDay
    val dateLong = System.currentTimeMillis() + TimeUtil.ONE_DAY * dayId
    val termStart = RemoteConfig.getTermStartTime()
    val dayIndex = ((dateLong - termStart) / TimeUtil.ONE_DAY).toInt()
    val week = dayIndex / 7 + 1
    val day = dayIndex % 7
    vm.isRefreshing = true
    vm.result = try {
      val dao = courseQueryDatabase.getDao()
      val building = BUILDING_VALUE[vm.selectedBuilding]
      val allRooms = dao.queryClassroomSet(building)
      val ruledOutRooms = dao.queryClassroom(building, week, day, sections).toSet()
      (allRooms - ruledOutRooms).ifEmpty {
        listOf(getString(R.string.text_classroom_no_info))
      }
    } catch (e: Exception) {
      listOf(getString(R.string.text_classroom_fail))
    }
    vm.isRefreshing = false
  }
}
