package com.leejangyoun.photoview.write.modify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leejangyoun.photoview.R;
import com.leejangyoun.photoview.photo.picker.PhotoPickerActivity;

import java.util.ArrayList;

import me.iwf.photopicker.entity.Photo;

public class ModifyActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0;

    public static final int START_ACTIVITY_FOR_RESULT_BY_CHOOSE_PIC = 1000;
    private static final int AVAILABLE_MAX_CNT = 10;

    private Context mContext;


    LayoutInflater mLayoutInflater;
    LinearLayout mSelectedPhotoLayout;

    EditText mEdit;

    ArrayList<Photo> mSelectedPhotoArrList;
    ArrayList<Photo> mHttpPhotoArrList;

    //=======================================================================
    // METHOD : onCreate
    //=======================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        mContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mLayoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedPhotoLayout = (LinearLayout) findViewById(R.id.selected_photo_layout);

        mEdit = (EditText)findViewById(R.id.edit);

        mSelectedPhotoArrList = new ArrayList<>();
        mHttpPhotoArrList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://leejangyoun.com/android/dummy/PhotoView.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("TEST", "response : " + response);
                        Gson gson = new Gson();
                        JsonObject root = new JsonParser().parse(response).getAsJsonObject();

                        String writeContent = root.get("writeContent").getAsString();
                        mEdit.setText(writeContent);

                        JsonArray node = root.get("fileList").getAsJsonArray();
                        for (JsonElement jEle : node) {
                            Photo photo = gson.fromJson(jEle.getAsJsonObject(), Photo.class);
                            mHttpPhotoArrList.add(photo);
                        }

                        creatCellInPhotoArrView(mHttpPhotoArrList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(stringRequest);


    }

    //=======================================================================
    // METHOD : OnClickPic
    //=======================================================================
    public void OnClickPic(View v) {
        callPhotoActivity();
    }

    //=======================================================================
    // METHOD : OnClickPic
    //=======================================================================
    public void OnClickSend(View v) {

        String edit = mEdit.getText().toString();
        edit = edit.substring(0, 7).concat("...");

        String photoIds = "";
        for (Photo p : mSelectedPhotoArrList)
            photoIds += p.getId() + ", ";


        String httpIds = "";
        for (Photo p : mHttpPhotoArrList)
            httpIds += p.getId() + ", ";

        Toast.makeText(mContext, "EditText : " + edit + "\nphotoIds : " + photoIds + "\nhttpIds : " + httpIds, Toast.LENGTH_SHORT).show();
    }

    //=======================================================================
    // METHOD : callPhotoActivity
    //=======================================================================
    private void callPhotoActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ModifyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // request first time!!
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                return;

            } else {
                gotoPhotoPickerActivity();
                return;
            }
        } else {
            gotoPhotoPickerActivity();
            return;
        }
    }

    //=======================================================================
    // METHOD : onRequestPermissionsResult
    //=======================================================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST", "permissions : " + permissions[0]+ ", grantResults : " + grantResults[0]);
            switch (requestCode) {
                case REQUEST_READ_EXTERNAL_STORAGE:
                    gotoPhotoPickerActivity();
                    return;
            }
        }
    }

    //=======================================================================
    // METHOD : onActivityResult
    //=======================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TEST", "requestCode : " + requestCode  + ", resultCode : " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_ACTIVITY_FOR_RESULT_BY_CHOOSE_PIC:
                    mSelectedPhotoArrList = (ArrayList<Photo>) data.getSerializableExtra(PhotoPickerActivity.SELECTED_PHOTOS);

                    ArrayList<Photo> temp = new ArrayList<>();
                    temp.addAll(mHttpPhotoArrList);
                    temp.addAll(mSelectedPhotoArrList);
                    creatCellInPhotoArrView(temp);
                    break;
            }
        }
    }

    // =======================================================================
    // METHOD : creatCellInPhotoArrView
    // =======================================================================
    public void creatCellInPhotoArrView(ArrayList<Photo> arr) {
        mSelectedPhotoLayout.removeAllViews();
        for (Photo ph : arr) {
            View cell = mLayoutInflater.inflate(R.layout.cell_in_photo, null);
            cell.setTag(ph);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Photo deletePhoto = (Photo) view.getTag();
                    mSelectedPhotoArrList.remove(deletePhoto);
                    mHttpPhotoArrList.remove(deletePhoto);
                    mSelectedPhotoLayout.removeView(view);
                }
            });
            ImageView iv = (ImageView) cell.findViewById(R.id.img);
            Glide.with(mContext).load(ph.getPath()).into(iv);

            mSelectedPhotoLayout.addView(cell);
        }

    }

    // =======================================================================
    // METHOD : gotoPhotoPickerActivity
    // =======================================================================
    public void gotoPhotoPickerActivity() {
        Intent i = new Intent(mContext, PhotoPickerActivity.class);
        i.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, AVAILABLE_MAX_CNT - mHttpPhotoArrList.size());
        i.putExtra(PhotoPickerActivity.SELECTED_PHOTOS, mSelectedPhotoArrList);
        startActivityForResult(i, START_ACTIVITY_FOR_RESULT_BY_CHOOSE_PIC);
    }
}
