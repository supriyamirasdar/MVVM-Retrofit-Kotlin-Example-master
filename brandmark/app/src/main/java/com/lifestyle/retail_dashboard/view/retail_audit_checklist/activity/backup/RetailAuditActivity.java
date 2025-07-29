package com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.backup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.databinding.ActivityRetailAuditBinding;
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.StandardDataAdapter;
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.AuditList;
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACLineItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RetailAuditActivity extends AppCompatActivity {

    ActivityRetailAuditBinding binding;
    private StandardDataAdapter standardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_retail_audit);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_retail_audit);
        setToolBarData();
        initialization();
    }

    private void setToolBarData() {
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        binding.toolbarLayout.tvToolBarTitle.setText("Retail Audit");
        binding.toolbarLayout.tvToolBarSubtitle.setVisibility(View.GONE);
    }

    private List<String> taxTypeList = new ArrayList();
    String selRegionST = "", selCityST = "", conceptST = "", auditByST;
    String storeName = "", auditDateST = "", brandNameST = "", managerNameST = "";

    private void initialization() {

        List<GetRACLineItem> retailAuditList = new ArrayList<>();
        List<GetRACLineItem> displayStandardList = new ArrayList();

        List<GetRACLineItem> businessCMList = new ArrayList();
        List<GetRACLineItem> backStoreList = new ArrayList();

        for (int i = 0; i < retailAuditList.size(); i++) {
            GetRACLineItem getRACLineItem = retailAuditList.get(i);

            if (i <= 3) {
                displayStandardList.add(getRACLineItem);
            } else if (i >= 4 && i <= 8) {
                businessCMList.add(getRACLineItem);
            } else {
                backStoreList.add(getRACLineItem);
            }
        }


        ArrayList<String> RegionList = new ArrayList<>();
        RegionList.add("Select Region");
        //arrayList.add("OPEN");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RegionList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.regionSP.setAdapter(arrayAdapter);

        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Select city");
        //arrayList.add("OPEN");
        //cityList.add("CLOSED");
        //cityList.add("SUSPENDED");
        ArrayAdapter<String> cityarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityList);
        cityarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.citySP.setAdapter(cityarrayAdapter);

        ArrayList<String> storeNameList = new ArrayList<>();
        storeNameList.add("Select Store name");
        storeNameList.add("Adarsh - Bangalore");
        storeNameList.add("Axis Mall - Kolkata");
        storeNameList.add("City Center - Mangalore");
        storeNameList.add("LS - Vijayawada");
        ArrayAdapter<String> storearrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, storeNameList);
        storearrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.storeNameSP.setAdapter(storearrayAdapter);


        ArrayList<String> conceptarrayList = new ArrayList<>();
        conceptarrayList.add("Select Concept");
        //arrayList.add("OPEN");
        conceptarrayList.add("APM");
        conceptarrayList.add("T-Base");
        ArrayAdapter<String> conceptarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, conceptarrayList);
        conceptarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.conceptSP.setAdapter(conceptarrayAdapter);

        ArrayList<String> auditarrayList = new ArrayList<>();
        auditarrayList.add("Select Audit for");
        //arrayList.add("OPEN");
        auditarrayList.add("CM");
        auditarrayList.add("BM");
        auditarrayList.add("Area Manager");
        auditarrayList.add("TCM");
        ArrayAdapter<String> auditarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, auditarrayList);
        auditarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.doAuditSP.setAdapter(auditarrayAdapter);


        binding.regionSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selRegionST = null;
                    return;
                } else {
                    selRegionST = RegionList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.citySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selCityST = null;
                    return;
                } else {
                    selCityST = cityList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.storeNameSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    brandNameST = null;
                    return;
                } else {
                    brandNameST = storeNameList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        binding.conceptSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    conceptST = null;
                    return;
                } else {
                    conceptST = conceptarrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.doAuditSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    auditByST = null;
                    return;
                } else {
                    auditByST = auditarrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.auditDateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(1);
            }
        });

        storeName = binding.storenameET.getText().toString().trim();
        brandNameST = binding.brandNameET.getText().toString().trim();
        managerNameST = binding.managerNameET.getText().toString().trim();

        binding.displayStandardLL.setVisibility(View.GONE);
        binding.businessCMLL.setVisibility(View.GONE);
        binding.backstoreLL.setVisibility(View.GONE);

        binding.displayStandardTV.setOnClickListener(view -> onClickDisplayStandard());
        binding.businessCMTV.setOnClickListener(view -> onClickBusinessCM());
        binding.backstoreTV.setOnClickListener(view -> onClickBackStore());


    }

    public void onClickDisplayStandard() {
        binding.displayStandardLL.setVisibility(View.VISIBLE);
        binding.businessCMLL.setVisibility(View.GONE);
        binding.backstoreLL.setVisibility(View.GONE);

        binding.displayStandardRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.displayStandardRV.setLayoutManager(layoutManager);
        binding.displayStandardRV.setItemAnimator(new DefaultItemAnimator());

        List<String> standardList = new ArrayList<>();
        standardList.add("Display as per planogram");
        standardList.add("Dust levels");
        standardList.add("Ironing on all hangings");
        standardList.add("Size stickers- ascending order");
        standardList.add(" Offers/POD well highlighted");
        standardList.add("Props");
        standardList.add("Signage/Talkers - Internal/External");
        standardList.add(" Hangers");
        standardList.add("GVP display and communication");
        standardList.add("Aisle space between fixtures (4-Ft)");
        standardList.add(" Brand fixtures manitenance- SIS/Towers");

        List<AuditList> auditListList = new ArrayList<>();
        for (int i = 0; i < standardList.size(); i++) {
            AuditList auditList = new AuditList();
            auditList.setId(i);
            auditList.setTextLabel(standardList.get(i));
            auditList.setChecked(false);
            if (i == 0) {
                auditList.setShowYes(true);
                auditList.setShowNo(false);
            } else if (i == 1) {
                auditList.setShowYes(false);
                auditList.setShowNo(true);
            } else if (i == 2) {
                auditList.setShowYes(false);
                auditList.setShowNo(true);
            } else {
                auditList.setShowYes(true);
                auditList.setShowNo(true);
            }
            auditListList.add(auditList);
        }

        standardListAdapter = new StandardDataAdapter(RetailAuditActivity.this, auditListList, new StandardDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int item) {
                AuditList auditList = auditListList.get(item);

            }
        });
        binding.displayStandardRV.setAdapter(standardListAdapter);

    }

    public void onClickBusinessCM() {
        binding.displayStandardLL.setVisibility(View.GONE);
        binding.businessCMLL.setVisibility(View.VISIBLE);
        binding.backstoreLL.setVisibility(View.GONE);

        binding.businessCMRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.businessCMRV.setLayoutManager(layoutManager);
        binding.businessCMRV.setItemAnimator(new DefaultItemAnimator());

        List<String> businessCMList = new ArrayList<>();

        businessCMList.add("Discuss focus brands STD/MTD/YTD");
        businessCMList.add("Discuss catchment competition report");
        businessCMList.add("Promo/Offers feedback");
        businessCMList.add("Feedback on FMS - SMS");
        businessCMList.add("Damages / Shrinkages");
        businessCMList.add("Returns/Consolidations as per expiry dates");
        businessCMList.add("DC Options VS Actual");
        businessCMList.add("CM Checklist");
        businessCMList.add("Attendance Brand staff");
        businessCMList.add("Attrition");
        businessCMList.add("Target Card");
        businessCMList.add("Basic / Advanced Product Knowledge training / New season/ Range / POG/Supplier/Mystery Audit and others");
        businessCMList.add("Inventory health check/ timeliness");


        List<AuditList> auditListList = new ArrayList<>();
        for (int i = 0; i < businessCMList.size(); i++) {
            AuditList auditList = new AuditList();
            auditList.setId(i);
            auditList.setTextLabel(businessCMList.get(i));
            auditList.setChecked(false);
            auditList.setShowYes(true);
            auditList.setShowNo(true);
            auditListList.add(auditList);
        }

        StandardDataAdapter businessCmListAdapter = new StandardDataAdapter(RetailAuditActivity.this, auditListList, new StandardDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int item) {
                AuditList auditList = auditListList.get(item);

            }
        });
        binding.businessCMRV.setAdapter(businessCmListAdapter);

    }

    public void onClickBackStore() {
        binding.displayStandardLL.setVisibility(View.GONE);
        binding.businessCMLL.setVisibility(View.GONE);
        binding.backstoreLL.setVisibility(View.VISIBLE);

        binding.backstoreRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.backstoreRV.setLayoutManager(layoutManager);
        binding.backstoreRV.setItemAnimator(new DefaultItemAnimator());

        List<String> backStoreList = new ArrayList<>();
        backStoreList.add("Is the backstore  organized - Style / Size , with shoes properly packed in boxes");
        backStoreList.add("Bags in proper poly pack");
        backStoreList.add("Are the non-trading items stacked properly in the backstore");
        backStoreList.add("Loose pair details / Odd Pair details");
        backStoreList.add("Customer Returns details filled in  Register and action taken");
        backStoreList.add("Maintenace of all Registers ( Damage, Hash total, Repair etc.)");
        backStoreList.add("Stacking 2 ft below sprinklers");
        backStoreList.add("Use of ladder /bend in racks");


        List<AuditList> auditListList = new ArrayList<>();
        for (int i = 0; i < backStoreList.size(); i++) {
            AuditList auditList = new AuditList();
            auditList.setId(i);
            auditList.setTextLabel(backStoreList.get(i));
            auditList.setChecked(false);
            auditList.setShowYes(true);
            auditList.setShowNo(true);
            auditListList.add(auditList);
        }

        StandardDataAdapter backStoreListAdapter = new StandardDataAdapter(RetailAuditActivity.this, auditListList, new StandardDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int item) {
                AuditList auditList = auditListList.get(item);

            }
        });
        binding.backstoreRV.setAdapter(backStoreListAdapter);

    }

    public void showDatePickerDialog(final int type) {
        int mYear, mMonth, mDay;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String monthOfYearST, dayOfMonthST;
                        if (monthOfYear < 9) {
                            monthOfYearST = "0" + (monthOfYear + 1);
                        } else {
                            monthOfYearST = "" + (monthOfYear + 1);
                        }

                        if (dayOfMonth < 10) {
                            dayOfMonthST = "0" + dayOfMonth;
                        } else {
                            dayOfMonthST = "" + dayOfMonth;
                        }

                        auditDateST = dayOfMonthST + "-" + (monthOfYearST) + "-" + year;
                        //auditDateST = year + "-" + (monthOfYearST) + "-" + dayOfMonthST;


                        binding.auditDateTV.setText(auditDateST);

                    }
                }, mYear, mMonth, mDay);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -90);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        onBackButtonPressed();
    }

    public void onBackButtonPressed() {
        super.onBackPressed();
    }
}