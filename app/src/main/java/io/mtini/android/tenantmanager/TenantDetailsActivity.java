package io.mtini.android.tenantmanager;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.mtini.android.tenantmanager.databinding.ActivityDetailsTenantBinding;
import io.mtini.model.AppDAO;
import io.mtini.model.AppDAOInterface;
import io.mtini.model.TenantModel;

public class TenantDetailsActivity extends AppCompatActivity {

    public static String TAG = Class.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_tenant);

        AppDAOInterface dbHelper = null;
        try {
            dbHelper = AppDAO.builder().open(this);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        //EstateModel estate = (EstateModel)getIntent().getSerializableExtra("estate");
        TenantModel tenant = (TenantModel)getIntent().getSerializableExtra("tenant");
        //String tenantName = tenant.getName();
        //This binds TenantModel to the UI as an estate object. Allows for ${tenant.name} data extraction
        ActivityDetailsTenantBinding  binding = DataBindingUtil.setContentView(this,R.layout.activity_details_tenant);
        binding.setTenant(tenant);
        //binding.setEstate(estate);

        //TODO add estate setting on back button

        /*
        int count = tenantList.size();
        estate.setTenantCount(count);
        TextView estateSizeTxtView = (TextView)findViewById(R.id.textEstateDetails);
        estateSizeTxtView.setText(estateName+" has "+count+" tenants.");

        //This binds EstateModel to the UI as an estate object. Allows for ${estate.description} data extraction
        ActivityDetailsEstateBinding  binding = DataBindingUtil.setContentView(this,R.layout.activity_details_estate);
        binding.setEstate(estate);

        //No bind the list of Tenants using an adapter
        ListView listView = (ListView) findViewById(R.id.tenantListView);
        TenantListBindingAdapter dataAdapter = new TenantListBindingAdapter(
        this,tenantList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TenantDetailsActivity.this, TenantDetailsActivity.class);
                        EstateModel tag = (EstateModel)v.getTag();
                        //Object h = v.getHandler();
                        intent.putExtra("estate",tag);
                        startActivity(intent);
                    }
                });
        listView.setAdapter(dataAdapter);
        */
    }


}
