package com.leejangyoun.photoview.photo.picker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leejangyoun.photoview.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.utils.MediaStoreHelper;

public class PhotoPickerActivity extends AppCompatActivity {

    public final static String EXTRA_MAX_COUNT      = "EXTRA_MAX_COUNT";
    public final static String SELECTED_PHOTOS      = "SELECTED_PHOTOS";
    public final static String SELECTED_ONE_PHOTO   = "SELECTED_ONE_PHOTO";

    private static int DEFAULT_MAX_COUNT        = 10;
    private static int COL_CNT                  = 3;


    private Context mContext;

    private Toolbar mToolbar;

    private int maxCount = DEFAULT_MAX_COUNT;

    private List<PhotoDirectory> mDirectories;
    private PhotoGridAdapter     mPhotoGridAdapter;

    private TextView txtPicCnt;

    private ArrayList<Photo> selectedPhotos;

    //=======================================================================
    // METHOD : onCreate
    //=======================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        mContext = getApplicationContext();

        // 전달 파라미터 받아 옴 (max count = 1일때 한장만 선택)
        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        Log.i("TEST", "maxCount : " + maxCount);

        //여러장 선택할 경우, 기존의 선택한 이미지를 불러옴
        if(maxCount != 1) selectedPhotos = (ArrayList<Photo>) getIntent().getSerializableExtra(SELECTED_PHOTOS);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setDisplayShowTitleEnabled(false);

        mToolbar.findViewById(R.id.layout_multi_choose).setVisibility( maxCount==1 ? View.GONE : View.VISIBLE);
        txtPicCnt = (TextView) mToolbar.findViewById(R.id.txt_pic_cnt);
        txtPicCnt.setText(selectedPhotos==null ? ("0/" + maxCount) : (selectedPhotos.size()+"/"+maxCount));

        mToolbar.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<String> path = new ArrayList();
                for (Photo photo : mPhotoGridAdapter.getSelectedPhotos()) {
                    Log.i("TEST", "selectedPhoto : " + photo.getId() + ", " + photo.getPath());
                    //path.add(photo.getPath());
                }

                List<Photo> path = mPhotoGridAdapter.getSelectedPhotos();
                Intent i = new Intent();
                //i.putStringArrayListExtra(PATH, path);
                i.putExtra(SELECTED_PHOTOS, (Serializable) path);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        mDirectories      = new ArrayList<>();
        mPhotoGridAdapter = new PhotoGridAdapter(mContext, mDirectories, COL_CNT, maxCount);
        mPhotoGridAdapter.setSelectedPhoto(selectedPhotos);
        mPhotoGridAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, boolean isCheck, int selectedItemCount) {

                int total = selectedItemCount + (isCheck ? -1 : 1);
                //menuDoneItem.setEnabled(total > 0);

                Log.i("TEST", "total : " + total + " / maxCount : " + maxCount);
                ArrayList<String> arr = mPhotoGridAdapter.getSelectedPhotoPaths();
                for (String s: arr) Log.i("TEST", "s : " + s);

                if(maxCount <= 0) {
                    List<Photo> photos = mPhotoGridAdapter.getSelectedPhotos();
                    if (!photos.contains(photo)) {
                        photos.clear();
                        mPhotoGridAdapter.notifyDataSetChanged();
                    }
                    return true;

                } else if(maxCount == 1) {
                    //Intent i = new Intent(mContext, PhotoCropperActivity.class);
                    //i.putExtra(PhotoCropperActivity.SELECTED_PHOTO_PATH, photo.getPath());
                    //startActivity(i);
                    //finish();

                    Intent i = new Intent();
                    //i.putStringArrayListExtra(PATH, path);
                    i.putExtra(SELECTED_ONE_PHOTO, photo.getPath());
                    setResult(RESULT_OK, i);
                    finish();

                } else {

                }

                if (total > maxCount) {
                    Log.i("TEST", "maxCount : " + maxCount);
                    Toast.makeText(mContext, "사진은 최대 " + DEFAULT_MAX_COUNT + "장까지 올릴 수 있습니다.", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount), LENGTH_LONG).show();
                    return false;
                }

                txtPicCnt.setText(total + "/" + maxCount);
                //menuDoneItem.setTitle(getString(R.string.__picker_done_with_count, total, maxCount));
                return true;
            }
        });

        Bundle mediaStoreArgs = new Bundle();

        MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        mDirectories.clear();
                        mDirectories.addAll(dirs);
                        mPhotoGridAdapter.notifyDataSetChanged();
                    }
                });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(COL_CNT, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mPhotoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //=======================================================================
    // METHOD : onOptionsItemSelected
    //=======================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

}