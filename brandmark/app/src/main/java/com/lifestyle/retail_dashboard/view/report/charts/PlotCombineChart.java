package com.lifestyle.retail_dashboard.view.report.charts;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.lifestyle.retail_dashboard.utils.DashboardUtils.decimalFormat1;
import static com.lifestyle.retail_dashboard.utils.DashboardUtils.floatingDecimalFormate1;

public class PlotCombineChart {
    private final Typeface typeface;
    private CombinedChart combinedChart;
    private List<String> xValues = new ArrayList<>();
    private YAxis leftYAxis;
    private String chartType;
    private Activity activity;

    public PlotCombineChart(Activity activity, CombinedChart combinedChart){
        this.combinedChart = combinedChart;
        this.activity = activity;

        typeface = Typeface.createFromAsset(activity.getAssets(), "font/calibri_bold.ttf");
        CombinedData combineData = new CombinedData();
        combineData.setValueTypeface(typeface);
    }


    public void plotChart(List<String> xValues,String chartType){
        this.xValues.clear();
        this.xValues.addAll(xValues);
        this.chartType = chartType;
        if (xValues.size() > 1)
            initializingChart();
    }

    private void initializingChart() {
        //combinedChart.setBackgroundColor(activity.getResources().getColor(R.color.darkblack));
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setTouchEnabled(true);
        combinedChart.setDragEnabled(true);
        combinedChart.setScaleEnabled(true);
        combinedChart.setPinchZoom(true);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.animateX(300);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.CANDLE
        });

        //MyMarkerView mv = new MyMarkerView(activity, R.layout.graph_marker_view,xValues);
        //mv.setChartView(combinedChart);
        //combinedChart.setMarker(mv);

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setTextColor(Color.BLACK);
        l.setTextSize(10f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        /*if (activity instanceof ProductChartActivity)
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        else*/
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis;
        {
            xAxis = combinedChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularityEnabled(true);
            xAxis.setTypeface(typeface);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setAxisLineColor(Color.BLACK);
            xAxis.setDrawGridLines(false);
            xAxis.setCenterAxisLabels(true);
            xAxis.setAxisMinimum(0f);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if (xValues.size() > 0 && value>= 0)
                        return xValues.get((int) value % xValues.size());
                    else
                        return "";
                }
            });
        }

        {
            leftYAxis = combinedChart.getAxisLeft();
            leftYAxis.setTypeface(typeface);
            leftYAxis.setTextColor(Color.BLACK);
            leftYAxis.setAxisLineColor(Color.BLACK);
            leftYAxis.setGranularityEnabled(true);
            leftYAxis.setAxisMinimum(0f);
            if(chartType.equalsIgnoreCase("Sale Qty")) {
                leftYAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return decimalFormat1.format(value);
                    }
                });
            }else{
                leftYAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return floatingDecimalFormate1.format(value);
                    }
                });
            }

            leftYAxis.setDrawGridLines(false);
        }

        YAxis rightYAxis;
        {
            rightYAxis = combinedChart.getAxisRight();
            rightYAxis.setTypeface(typeface);
            rightYAxis.setTextColor(Color.BLACK);
            rightYAxis.setAxisLineColor(Color.BLACK);
            rightYAxis.setGranularityEnabled(true);
            rightYAxis.setAxisMinimum(0f);
            if(chartType.equalsIgnoreCase("Sale Qty")) {
                rightYAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return decimalFormat1.format(value);
                    }
                });
            }else{
                rightYAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return floatingDecimalFormate1.format(value);
                    }
                });
            }
            rightYAxis.setDrawGridLines(false);
            rightYAxis.setEnabled(false);
        }

        combinedChart.getViewPortHandler().setMaximumScaleX(2f);
        combinedChart.getViewPortHandler().setMaximumScaleY(2f);
    }

    //LineChart Data
    public LineDataSet generateLineChart(String label, List<Double> yValues, String yAxisPosition, int color) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < xValues.size(); index++)
            entries.add(new Entry(index + 0.5f, Float.parseFloat(String.valueOf(yValues.get(index)))));

        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(2f);
        set.setCircleColor(color);
        set.setCircleRadius(3f);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawValues(true);
        set.setValueTextSize(8f);
        //set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);
        set.setDrawValues(true);
        if(label.equalsIgnoreCase("Sale Qty") || label.equalsIgnoreCase("Budget Ach%")
                ||label.equalsIgnoreCase("Growth%")) {
            set.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return decimalFormat1.format(value)+"%";
                }
            });
        }else{
            set.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return floatingDecimalFormate1.format(value);
                }
            });
        }

        if (yAxisPosition.equalsIgnoreCase("left"))
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        else
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return set;
    }

    public LineDataSet generateCircleLineChart(String label, List<Double> yValues, String yAxisPosition, int color) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < xValues.size(); index++)
            entries.add(new Entry((index), Float.parseFloat(String.valueOf(yValues.get(index)))));

        LineDataSet set = new LineDataSet(entries, label);
        /*set.setColor(color);
        set.setLineWidth(2f);
        set.setCircleColor(color);
        set.setCircleRadius(5f);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);
        set.setDrawValues(false);*/

        set.setColor(color);
        set.setLineWidth(2.5f);
        set.setCircleColor(color);
        set.setCircleRadius(5f);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawValues(false);
        //set.setValueTextSize(10f);
        set.setValueTextColor(Color.BLACK);

        if (yAxisPosition.equalsIgnoreCase("left"))
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        else
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return set;
    }

    //LineChart Data For Cummulative
    public LineDataSet generateCumalativeLineChart(String label, List<Double> yValues, String yAxisPosition, int color) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < yValues.size(); index++)
            entries.add(new Entry((index), Float.parseFloat(String.valueOf(yValues.get(index)))));

        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(2f);
        set.setCircleColor(color);
        set.setCircleRadius(5f);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setDrawCircles(false);
        set.setValueTextColor(Color.BLACK);
        set.setDrawValues(false);

        if (yAxisPosition.equalsIgnoreCase("left"))
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        else
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return set;
    }

    public void drawYAxisLine(float value,String lable){
        LimitLine limitLine = new LimitLine(value, lable);
        limitLine.setLineWidth(1f);
        limitLine.setLineColor(Color.GREEN);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextColor(Color.BLACK);
        limitLine.setTextSize(10f);
        limitLine.setTypeface(typeface);
        leftYAxis.addLimitLine(limitLine);
    }

    //BarChart Data
    public BarData generateBarChart(String label, List<Double> yValues, String yAxisPosition, int color) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int index = 0; index < xValues.size(); index++)
            entries.add(new BarEntry(index + 0.5f, Float.parseFloat(String.valueOf(yValues.get(index)))));

        BarDataSet set = new BarDataSet(entries, label);
        set.setColor(color);
        //set.setBarBorderWidth(10f);
        set.setDrawValues(true);
        //set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        //set.setHighLightColor(activity.getResources().getColor(R.color.green));
        //set.setValueTextSize(15f);
        set.setValueTextColor(Color.WHITE);

        if (yAxisPosition.equalsIgnoreCase("left"))
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        else
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarData d = new BarData();
        d.setBarWidth(0.4f);
        d.addDataSet(set);

        return d;
    }


    public BarData generateStackBarChart(String label1, List<Double> yValues1,int firstBarColor,String label2, List<Double> yValues2, int secondBarColor, String yAxisPosition) {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        Log.d("Abrar","Sale: "+yValues1);
        Log.d("Abrar","Cummulative Sale: "+yValues2);

        for (int index = 0; index < xValues.size(); index++) {
            entries1.add(new BarEntry(index + 0.5f, Float.parseFloat(String.valueOf(yValues1.get(index)))));
            entries2.add(new BarEntry(0, Float.parseFloat(String.valueOf(yValues2.get(index)))));
        }

        Log.d("Abrar","entries1: "+entries1);
        Log.d("Abrar","entries2: "+entries2);

        BarDataSet set1 = new BarDataSet(entries1, label1);
        set1.setColor(firstBarColor);
        set1.setDrawValues(true);
        set1.setValueTextColor(Color.BLACK);
        set1.setValueTextSize(8f);
        //set1.setDrawValueAboveBar(true);

        if(label1.equalsIgnoreCase("Sale Qty")) {
            set1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return decimalFormat1.format(value);
                }
            });
        }else{
            set1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return floatingDecimalFormate1.format(value);
                }
            });
        }

        BarDataSet set2 = new BarDataSet(entries2, label2);
        set2.setColor(secondBarColor);
        set2.setDrawValues(true);
        set2.setValueTextColor(Color.BLACK);
        set2.setValueTextSize(8f);
        //set2.setDrawValueAboveBar(true);
        if(label2.equalsIgnoreCase("Cumulative Sale Qty")) {
            set2.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return decimalFormat1.format(value);
                }
            });
        }else{
            set2.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return floatingDecimalFormate1.format(value);
                }
            });
        }

        if (yAxisPosition.equalsIgnoreCase("left")) {
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        }
        else {
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        }

        float groupSpace = 0.08f;
        float barSpace = 0.02f;
        float barWidth = 0.45f;

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0
        return d;
    }

    //CandelStick Data
    public CandleData generateCandleData(String lable, List<Double> yValues, String yAxisPosition) {

        CandleData d = new CandleData();
        ArrayList<CandleEntry> entries = new ArrayList<>();

        for (int index = 0; index < xValues.size(); index ++) {
            float val = Float.parseFloat(String.valueOf(yValues.get(index)));
            if (val > 0)
                entries.add(new CandleEntry(index + 0.5f,val,0,val,0));
            else {
                entries.add(new CandleEntry(index + 0.5f,val,0 ,0,-val));
            }
        }

        CandleDataSet set = new CandleDataSet(entries, lable);
        //set.setShadowColor(activity.getResources().getColor(R.color.darkblack));
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(Color.GREEN);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(Color.RED);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(Color.GREEN);
        set.setValueTextColor(Color.BLACK);
        set.setBarSpace(0.2f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat mFormat = new DecimalFormat("#,##,###.##");
                return mFormat.format(value);
            }
        });

        if (yAxisPosition.equalsIgnoreCase("left"))
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        else
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);
        return d;
    }
}