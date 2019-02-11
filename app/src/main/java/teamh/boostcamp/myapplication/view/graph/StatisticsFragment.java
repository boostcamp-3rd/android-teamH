package teamh.boostcamp.myapplication.view.graph;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import teamh.boostcamp.myapplication.R;
import teamh.boostcamp.myapplication.data.local.room.AppDatabase;
import teamh.boostcamp.myapplication.data.model.EmotionHistory;
import teamh.boostcamp.myapplication.data.model.EmotionType;
import teamh.boostcamp.myapplication.data.repository.StatisticsRepositoryImpl;
import teamh.boostcamp.myapplication.databinding.FragmentGraphBinding;

public class StatisticsFragment extends Fragment implements StatisticsView {

    private StatisticsPresenter statisticsPresenter;
    private Context context;
    private FragmentGraphBinding binding;
    private String[] dates;
    private String[] emojis;
    private ArrayList<ILineDataSet> dataSets;

    public StatisticsFragment() {

    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        dataSets = new ArrayList<>();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        initPresenter();
        initData();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statisticsPresenter.loadStatisticsData();
    }


    @Override
    public void updateStatisticsData(@NonNull List<Pair<EmotionHistory, EmotionHistory>> emotionHistoryList) {
        int size = emotionHistoryList.size();
        List<Entry> selectedEmotionList = new ArrayList<>();
        List<Entry> analyzedEmotionList = new ArrayList<>();
        dates = new String[size];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < size; i++) {

            // 페어로 처리.
            Pair<EmotionHistory, EmotionHistory> p = emotionHistoryList.get(i);

            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
            Date date = emotionHistoryList.get(i).first.getDate();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            dates[i] = convertToDayOfWeek(dayOfWeek); // x축 표현

            int selectedEmotion = p.first.getEmotion().getEmotion(); // 선택
            int analyzedEmotion = p.second.getEmotion().getEmotion(); // 분석

            selectedEmotionList.add(new Entry(i, selectedEmotion));
            analyzedEmotionList.add(new Entry(i, analyzedEmotion));
        }


        LineDataSet selectedDataSet = new LineDataSet(selectedEmotionList, "선택");
        setLineData(selectedDataSet, EmotionType.selectedEmotion);

        LineDataSet analyzedDataSet = new LineDataSet(analyzedEmotionList, "분석");
        setLineData(analyzedDataSet, EmotionType.analyzedEmotion);

        final LineData lineData = new LineData(dataSets);
        binding.lcEmotionGraph.setData(lineData);

        drawGraph();

    }

    private String convertToDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "일";
            case 2:
                return "월";
            case 3:
                return "화";
            case 4:
                return "수";
            case 5:
                return "목";
            case 6:
                return "금";
            case 7:
                return "일";
            default:
                return "일";
        }
    }


    private void setLineData(LineDataSet lineDataSet, EmotionType emotionType) {
        lineDataSet.setLineWidth(2); // 곡률
        lineDataSet.setCircleRadius(5); // 원 색상 지정

        switch (emotionType) {
            case selectedEmotion:
                lineDataSet.setCircleColor(context.getResources().getColor(R.color.graphColor));
                lineDataSet.setCircleHoleColor(context.getResources().getColor(R.color.graphColor));
                lineDataSet.setColor(context.getResources().getColor(R.color.graphColor));
                break;
            case analyzedEmotion:
                lineDataSet.setCircleColor(context.getResources().getColor(R.color.graph_analyzed_color));
                lineDataSet.setCircleHoleColor(context.getResources().getColor(R.color.graph_analyzed_color));
                lineDataSet.setColor(context.getResources().getColor(R.color.graph_analyzed_color));
                break;
        }

        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);
        dataSets.add(lineDataSet);
    }

    @Override
    public void showLoadStatisticsDataSuccessMessage() {
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadStatisticsDataFailMessage() {
        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
    }

    void initPresenter() {
        statisticsPresenter = new StatisticsPresenter(StatisticsFragment.this,
                StatisticsRepositoryImpl.getInstance(AppDatabase.getInstance(
                        context.getApplicationContext())));
    }

    void initData() {
        emojis = context.getResources().getStringArray(R.array.graph_emojis);
    }

    private void drawGraph() {
        final XAxis xAxis;
        final YAxis yLeftAxis, yRightAxis;

        xAxis = binding.lcEmotionGraph.getXAxis();
        yLeftAxis = binding.lcEmotionGraph.getAxisLeft();
        yRightAxis = binding.lcEmotionGraph.getAxisRight();
        // X축 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(dates.length, true);
        xAxis.setTextColor(R.color.black);
        xAxis.setValueFormatter(new GraphAxisValueFormatter(dates));
        xAxis.enableGridDashedLine(14, 24, 0);

        // y축 왼쪽 설정
        yLeftAxis.setTextColor(R.color.black);
        // y축 텍스트 사이즈 지정.
        yLeftAxis.setTextSize(20f);
        yLeftAxis.setValueFormatter(new GraphAxisValueFormatter(emojis));
        // max 값
        yLeftAxis.setAxisMaximum(4.0f);
        // min 값
        yLeftAxis.setAxisMinimum(0.0f);
        yLeftAxis.setGranularityEnabled(true);
        // 증가 간격
        yLeftAxis.setGranularity(1.0f);
        yLeftAxis.setSpaceMax(500f);

        // y축 오른쪽 설정
        yRightAxis.setDrawLabels(false);
        yRightAxis.setDrawAxisLine(false);
        yRightAxis.setDrawGridLines(false);

        binding.lcEmotionGraph.setDescription(null);
        binding.lcEmotionGraph.setBackgroundColor(Color.TRANSPARENT);
        binding.lcEmotionGraph.setDoubleTapToZoomEnabled(false);
        binding.lcEmotionGraph.setDrawGridBackground(false);
        binding.lcEmotionGraph.animateY(2000, Easing.EaseInCubic);
        binding.lcEmotionGraph.invalidate();
    }
}
