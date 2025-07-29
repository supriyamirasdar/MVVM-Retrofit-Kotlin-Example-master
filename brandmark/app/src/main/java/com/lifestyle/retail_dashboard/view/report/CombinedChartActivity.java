package com.lifestyle.retail_dashboard.view.report;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.databinding.ActivityCombinedBinding;

import java.util.ArrayList;

public class CombinedChartActivity extends DemoBase implements AdapterView.OnItemSelectedListener {

    private CombinedChart chart;
    private final int count = 12;
    private ActivityCombinedBinding binding;
    private int maxIndex;
    private String chartType = "Budget Ach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_combined);

        setTitle("CombinedChartActivity");


        chart = findViewById(R.id.combinedChart);
       /* chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        */

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        chart.animateX(600);
       /* MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);*/

        // draw bars behind lines
        /*chart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });*/

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return months[(int) value % months.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        //data.setData(generateBubbleData());
        //data.setData(generateScatterData());
        data.setData(generateCandleData());
        data.setValueTypeface(tfLight);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        chart.setData(data);
        chart.invalidate();

        Highlight high = new Highlight(5f, 5f, 4);
        high.setDataIndex(7);
        chart.highlightValue(high, false);

        /*for (int i = 0; i < chart.getXAxis().getLabelCount();i++){
            Log.d("Abrar","P: "+i+ " X "+chart.getXAxis().getFormattedLabel(i));
            List<ILineDataSet> sets = chart.getData().getLineData().getDataSets();
            for (ILineDataSet set: sets){
                if (chart.getXAxis().getFormattedLabel(i).equalsIgnoreCase("Jul")) {
                    set.getEntryForIndex(i).setIcon(getResources().getDrawable(R.drawable.star));
                }
            }
        }*/

        //chart.getAxisLeft().setAxisMaximum((maxIndex * 1.25).toFloat());

        initSaleTypeSpinner();
        initGraphTypeSpinner();
    }

    private void initGraphTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options, R.layout.spinner_simple_text);
        adapter.setDropDownViewResource(R.layout.spinner_simple_text);
        binding.spOptions.setAdapter(adapter);
        binding.spOptions.setPadding(0, 0, 0, 0);

        binding.spOptions.setOnItemSelectedListener(this);
    }


    private void initSaleTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chartTypes, R.layout.spinner_simple_text);
        adapter.setDropDownViewResource(R.layout.spinner_simple_text);
        binding.spGraphType.setAdapter(adapter);
        binding.spGraphType.setPadding(0, 0, 0, 0);

/*        binding.spLayout.setOnClickListener(v -> binding.spGraphType.performClick());

        graphInterval.add("1W - By Day");
        graphInterval.add("1M - By Day");
        graphInterval.add("1Q - By Week");
        graphInterval.add("1H - By Week");
        graphInterval.add("1Y - By Month");

        DashSpinnerAdapter customAdapter = new DashSpinnerAdapter(this, graphInterval);
        binding.spGraphType.setAdapter(customAdapter);*/
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < count; index++)
            entries.add(new Entry(index + 0.5f, getRandom(15, 5)));

        LineDataSet set = new LineDataSet(entries, "Budget Ach");
        set.setColor(getResources().getColor(R.color.lineChart));
        set.setLineWidth(2.5f);
        set.setCircleColor(getResources().getColor(R.color.lineChart));
        set.setCircleRadius(5f);
        set.setFillColor(getResources().getColor(R.color.lineChart));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(getResources().getColor(R.color.lineChart));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.setValueTextColor(Color.YELLOW);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();


        for (int index = 0; index < count; index++) {
            entries1.add(new BarEntry(index + 0.5f, getRandom(25, 25)));

            // stacked
            entries2.add(new BarEntry(0, getRandom(13, 12)));
        }

        //maxIndex = Collections.max(entries1);

        BarDataSet set1 = new BarDataSet(entries1, "Sale");
        if (chartType.equalsIgnoreCase("Sale"))
            set1.setColor(getResources().getColor(R.color.barchart));
        else if (chartType.equalsIgnoreCase("Sale Qty"))
            set1.setColor(getResources().getColor(R.color.lineChart));
        else
            set1.setColor(Color.GREEN);

        set1.setValueTextColor(Color.BLACK);
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarDataSet set2 = new BarDataSet(entries2, "Plan Sale");
        set2.setColor(Color.rgb(61, 165, 255));
        set2.setValueTextColor(Color.BLACK);
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);


        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset

        BarData d = new BarData(set1,set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    private ScatterData generateScatterData() {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (float index = 0; index < count; index += 0.5f)
            entries.add(new Entry(index + 0.25f, getRandom(10, 55)));

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    private CandleData generateCandleData() {

        CandleData d = new CandleData();

        ArrayList<CandleEntry> entries = new ArrayList<>();

        for (int index = 0; index < count; index ++)
            entries.add(new CandleEntry(index + 0.5f , 90, 70, 85, 75f));

        CandleDataSet set = new CandleDataSet(entries, "");
        set.setDecreasingColor(Color.GREEN);
        set.setIncreasingColor(Color.RED);
        set.setShadowColor(Color.WHITE);
        set.setBarSpace(0.3f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        d.addDataSet(set);

        return d;
    }

    @Override
    public void saveToGallery() { /* Intentionally left empty */ }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("Abrar","Result "+binding.spOptions.getSelectedItem().toString());
        chartType  = binding.spOptions.getSelectedItem().toString();
        //chart.invalidate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
