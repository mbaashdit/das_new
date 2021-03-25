package com.aashdit.distautosystem.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.model.TagData;
import com.aashdit.distautosystem.model.Uploaded;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjImagesAdapter extends RecyclerView.Adapter<ProjImagesAdapter.ProjectListHolder> {

    private static final String TAG = "ProjectListAdapter";

    private Context mContext;
    private Uploaded p;
    private String imageUrl, currPhaseCode, currStageCode, stageCode;

    public ProjImagesAdapter(Context mContext, Uploaded projects, String imageUrl) {
        this.mContext = mContext;
        this.p = projects;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ProjectListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cell_image, parent, false);
        return new ProjectListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListHolder holder, int position) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

//        Uploaded p = projects.get(position);

        Geocoder gc = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(Double.parseDouble(p.latitude), Double.parseDouble(p.longitude), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                if (address.getAddressLine(0) != null)
                    holder.mTvAddress.setText("Address : "+address.getAddressLine(0));

                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.mTvLat.setText("Lat : "+p.latitude);
        holder.mTvLong.setText("Long : "+p.longitude);
//        holder.mTvAmount.setText("Amount : "+p.aggrementValue);
        holder.mTvRemarks.setText("Remarks : "+p.remark);
//        holder.mTvDateTime.setText("Date : "+p.aggrementDate);

        Glide.with(mContext).load(imageUrl)
                .thumbnail(0.5f)
                .placeholder(R.drawable.avatardefault)
                .error(R.drawable.avatardefault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvImage);

//        if(currStageCode.equals(stageCode) && (currPhaseCode.equals("BEFORE_GEO_TAG") || currPhaseCode.equals("GEO_TAG_REVERTED"))){
//            holder.mIvDelete.setVisibility(View.VISIBLE);
//        }else{
//            holder.mIvDelete.setVisibility(View.GONE);
//        }

//        holder.mIvZoom.setOnClickListener(v -> imagesListener.onImageZoom(position,p.projectGeoTaggingId));
//        holder.mIvDelete.setOnClickListener(v -> imagesListener.onImageDelete(position, p.projectGeoTaggingId));

    }

    @Override
    public int getItemCount() {
        return 1;//projects.size();
    }

    public static class ProjectListHolder extends RecyclerView.ViewHolder {

        TextView mTvLat, mTvLong, mTvAddress, mTvDateTime,mTvAmount,mTvRemarks;
        ImageView mIvImage, mIvDelete, mIvZoom;

        public ProjectListHolder(@NonNull View itemView) {
            super(itemView);
            mTvLat = itemView.findViewById(R.id.cell_tv_lat);
            mTvLong = itemView.findViewById(R.id.cell_tv_long);
            mTvAddress = itemView.findViewById(R.id.cell_tv_address);
            mIvImage = itemView.findViewById(R.id.cell_image);
            mIvDelete = itemView.findViewById(R.id.iv_delete);
            mIvZoom = itemView.findViewById(R.id.iv_zoom);
            mTvDateTime = itemView.findViewById(R.id.cell_tv_dt);
            mTvAmount = itemView.findViewById(R.id.cell_tv_amount);
            mTvRemarks = itemView.findViewById(R.id.cell_tv_remarks);
        }
    }
    ImagesListener imagesListener;

    public void setImagesListener(ImagesListener imagesListener) {
        this.imagesListener = imagesListener;
    }

    public interface ImagesListener{
        void onImageZoom(int position, Long imgId);
        void onImageDelete(int position, Long imgId);
    }
}
