package com.njust.helper.grade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.njust.helper.BR
import com.njust.helper.BuildConfig
import com.njust.helper.R
import com.njust.helper.account.AccountActivity
import com.njust.helper.activity.BaseActivity
import com.njust.helper.api.LoginErrorException
import com.njust.helper.api.ParseErrorException
import com.njust.helper.api.ServerErrorException
import com.njust.helper.api.jwc.GradeItem
import com.njust.helper.api.jwc.JwcApi
import com.njust.helper.databinding.ActivityGradeBinding
import com.njust.helper.tools.Prefs
import com.zwb.commonlibs.binding.BaseDataBindingHolder
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat

class GradeActivity : BaseActivity() {
    private lateinit var binding: ActivityGradeBinding
    private lateinit var adapter: GradeAdapter

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        refresh()
    }

    private fun refresh() {
        lifecycleScope.launch {
            try {
                val result = JwcApi.grade(Prefs.getId(this@GradeActivity), Prefs.getJwcPwd(this@GradeActivity))
                onDataReceived(result)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun onDataReceived(data: Map<String, List<GradeItem>>) {
        binding.loading = false
        if (data.isEmpty()) {
            showSnack(R.string.message_no_result_exam)
        } else {
            adapter.vm = GradeVm(data)
        }
    }

    private fun onError(throwable: Throwable) {
        binding.loading = false
        when (throwable) {
            is ServerErrorException -> showSnack(R.string.message_server_error)
            is LoginErrorException -> AccountActivity.alertPasswordError(this, AccountActivity.REQUEST_JWC)
            is IOException -> showSnack(R.string.message_net_error)
            is ParseErrorException -> showSnack(R.string.message_parse_error)
            else -> {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                    throw throwable
                }
                Crashlytics.logException(throwable)
            }
        }
    }

    override fun layoutRes(): Int = 0

    override fun layout() {
        val binding = DataBindingUtil.setContentView<ActivityGradeBinding>(this, R.layout.activity_grade)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        this.binding = binding
        GradeAdapter().let {
            adapter = it
            binding.recyclerView.adapter = it
        }
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh)
    }
}

private class GradeAdapter : RecyclerView.Adapter<BaseDataBindingHolder>() {
    var vm: GradeVm? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataBindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BaseDataBindingHolder(DataBindingUtil.inflate(inflater, viewType, parent, false))
    }

    override fun getItemCount(): Int {
        return vm.let { if (it == null) 0 else it.terms.size + 1 }
    }

    override fun onBindViewHolder(holder: BaseDataBindingHolder, position: Int) {
        val vm = vm ?: return
        holder.binding.let {
            it.setVariable(BR.vm, if (position < vm.terms.size) vm.terms[position] else vm.mean)
            it.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val vm = vm ?: return 0
        return if (position < vm.terms.size) {
            R.layout.item_grade_term
        } else {
            R.layout.item_grade_summary
        }
    }
}

class MeanGradeValues {
    var hasUnrecognizedGrade = false
    var totalWeight = 0.0
    var totalPoint = 0.0
    var totalGrade = 0.0
    var requiredWeight = 0.0
    var requiredPoint = 0.0
    var requiredGrade = 0.0
}

class MeanGradeVm(values: MeanGradeValues) {
    val hasUnrecognizedGrade = values.hasUnrecognizedGrade
    val totalWeight: String = values.totalWeight.roundToString()
    val totalPoint: String = (values.totalPoint / values.totalWeight).roundToString()
    val totalGrade: String = (values.totalGrade / values.totalWeight).roundToString()
    val requiredWeight: String = values.requiredWeight.roundToString()
    val requiredPoint: String = (values.requiredPoint / values.requiredWeight).roundToString()
    val requiredGrade: String = (values.requiredGrade / values.requiredWeight).roundToString()

    private fun Double.roundToString(): String {
        return if (this.isNaN()) {
            toString()
        } else {
            DecimalFormat("#.##").format(this)
        }
    }
}

class GradeVm(data: Map<String, List<GradeItem>>) {
    val terms = arrayListOf<GradeTermVm>()

    val mean: MeanGradeVm

    init {
        val total = MeanGradeValues()
        val terms = this.terms
        data.forEach { entry ->
            val termName = entry.key
            val items = entry.value
            val loop = MeanGradeValues()
            items.forEach {
                foldToTripleDouble(total, it)
                foldToTripleDouble(loop, it)
            }
            terms += GradeTermVm(items, termName, MeanGradeVm(loop))
        }
        terms.sortByDescending { it.termName }
        mean = MeanGradeVm(total)
    }

    private fun foldToTripleDouble(acc: MeanGradeValues, gradeItem: GradeItem) {
        if (gradeItem.grade < 0) {
            acc.hasUnrecognizedGrade = true
            return
        }
        val weight = gradeItem.weight
        val grade = weight * gradeItem.grade
        val point = weight * gradeItem.point
        acc.totalWeight += weight
        acc.totalGrade += grade
        acc.totalPoint += point
        if (gradeItem.type == "必修") {
            acc.requiredWeight += weight
            acc.requiredGrade += grade
            acc.requiredPoint += point
        }
    }
}

class GradeTermVm(
        val items: List<GradeItem>,
        val termName: String,
        val mean: MeanGradeVm
) {
    val brId: Int = BR.vm
}
