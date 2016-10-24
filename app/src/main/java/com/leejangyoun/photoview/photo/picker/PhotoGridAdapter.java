package com.leejangyoun.photoview.photo.picker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leejangyoun.photoview.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.adapter.SelectableAdapter;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnItemCheckListener;

public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

  private final static int COL_NUM         = 3;
  public  final static int ITEM_TYPE_PHOTO = 101;

  private Context mContext;

  private int mImageSize;
  private int mMaxCnt;

  //=======================================================================
  // METHOD : PhotoGridAdapter
  //=======================================================================
  public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories) {
    this.photoDirectories = photoDirectories;
    this.mContext = context;
    setColumnNumber(context, COL_NUM);
  }

  //=======================================================================
  // METHOD : PhotoGridAdapter
  //=======================================================================
  public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories, int colNum, int maxCnt) {
    this(context, photoDirectories);
    setColumnNumber(context, COL_NUM);
    this.mMaxCnt = maxCnt;
  }

  //=======================================================================
  // METHOD : setColumnNumber
  //=======================================================================
  private void setColumnNumber(Context context, int columnNumber) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    int widthPixels = metrics.widthPixels;
    mImageSize = widthPixels / columnNumber;
  }

  //=======================================================================
  // METHOD : onOptionsItemSelected
  //=======================================================================
  @Override
  public int getItemViewType(int position) {
    return ITEM_TYPE_PHOTO;
  }

  //=======================================================================
  // METHOD : onCreateViewHolder
  //=======================================================================
  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
    PhotoViewHolder holder = new PhotoViewHolder(itemView);
    return holder;
  }

  //=======================================================================
  // METHOD : onBindViewHolder
  //=======================================================================
  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    List<Photo> photos = getCurrentPhotos();
    final Photo photo;
    photo = photos.get(position);

    //ALog.i("onBindViewHolder > photo.getPath() : " + photo.getPath());

    Glide.with(mContext)
            .load(new File(photo.getPath()))
            .centerCrop()
            .dontAnimate()
            .thumbnail(0.5f)
            .override(mImageSize, mImageSize)
            .into(holder.ivPhoto);


    holder.vSelected.setVisibility( mMaxCnt==1 ? View.GONE : View.VISIBLE);

    final boolean isChecked = isSelected(photo);
    holder.ivPhoto.setSelected(isChecked);
    holder.vSelected.setSelected(isChecked);

    holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (mMaxCnt==1) {
          onItemCheckListener.OnItemCheck(position, photo, isChecked, getSelectedPhotos().size());

        } else {
          boolean isEnable = true;
          if (onItemCheckListener != null)
            isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked, getSelectedPhotos().size());

          if (isEnable) {
            toggleSelection(photo);
            notifyItemChanged(position);
          }
        }


      }
    });

  }

  //=======================================================================
  // METHOD : getItemCount
  //=======================================================================
  @Override
  public int getItemCount() {
    int photosCount = photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
    return photosCount;
  }

  //=======================================================================
  // METHOD : PhotoViewHolder
  //=======================================================================
  public static class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivPhoto;
    private ImageView vSelected;

    public PhotoViewHolder(View itemView) {
      super(itemView);

      ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
      vSelected = (ImageView) itemView.findViewById(R.id.v_selected);
    }

  }

  //=======================================================================
  // METHOD : getSelectedPhotoPaths
  //=======================================================================
  public ArrayList<String> getSelectedPhotoPaths() {
    ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

    for (Photo photo : selectedPhotos)
      selectedPhotoPaths.add(photo.getPath());

    return selectedPhotoPaths;
  }

  //=======================================================================
  // METHOD : getSelectedPhotoPaths
  //=======================================================================
  public void setSelectedPhoto(ArrayList<Photo> arrayList) {

    if(selectedPhotos == null)
      return;

    if(arrayList == null)
      return;

    for (Photo photo : arrayList)
      selectedPhotos.add(photo);
  }

  //=======================================================================
  // METHOD : setOnItemCheckListener
  //=======================================================================
  private OnItemCheckListener onItemCheckListener = null;
  public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
    this.onItemCheckListener = onItemCheckListener;
  }

}
