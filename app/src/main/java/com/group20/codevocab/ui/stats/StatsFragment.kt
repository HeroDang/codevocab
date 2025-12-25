package com.group20.codevocab.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.group20.codevocab.data.local.dao.DayProgress
import com.group20.codevocab.databinding.FragmentStatsBinding
import com.group20.codevocab.viewmodel.StatsViewModel
import com.group20.codevocab.viewmodel.StatsViewModelFactory

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        // ✅ Xử lý nút Back để quay lại màn Home
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewModel() {
        val factory = StatsViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[StatsViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.totalWordsLearned.observe(viewLifecycleOwner) { total: Int ->
            binding.tvFlashcardsValue.text = total.toString()
        }

        viewModel.weeklyProgress.observe(viewLifecycleOwner) { progressList: List<DayProgress> ->
            if (progressList.isNotEmpty()) {
                setupLineChart(progressList)
            }
        }
    }

    private fun setupLineChart(data: List<DayProgress>) {
        val entries = data.mapIndexed { index, day ->
            Entry(index.toFloat(), day.count.toFloat())
        }

        val dataSet = LineDataSet(entries, "Words Learned").apply {
            color = Color.parseColor("#5856D6")
            valueTextColor = Color.BLACK
            lineWidth = 2.5f
            circleRadius = 5f
            setCircleColor(Color.parseColor("#5856D6"))
            circleHoleColor = Color.WHITE
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = Color.parseColor("#5856D6")
            fillAlpha = 40
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(dataSet)
        binding.lineChart.apply {
            this.data = lineData
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(data.map { it.date.substring(5) }) // MM-DD
                granularity = 1f
                textColor = Color.parseColor("#64748B")
                yOffset = 10f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F1F5F9")
                axisMinimum = 0f
                textColor = Color.parseColor("#64748B")
                xOffset = 10f
            }

            axisRight.isEnabled = false
            animateX(800)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}