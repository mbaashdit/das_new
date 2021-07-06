package com.aashdit.distautosystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.model.ClosureData;
import com.aashdit.distautosystem.model.InitiationUploaded;
import com.aashdit.distautosystem.model.UploadedPhotoDetail;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
public class ClosurePhotoUploadAdapter extends RecyclerView.Adapter<ClosurePhotoUploadAdapter.ChangeLocationHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
 private double latitude;
 private double longitude;
    //we are storing all the products in a list
    private List<InitiationUploaded> photosList;

    //getting the context and product list with constructor
    public ClosurePhotoUploadAdapter(Context mCtx, List<InitiationUploaded> photosList) {
        this.mCtx = mCtx;
        this.photosList = photosList;
    }

    @Override
    public ChangeLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
       // LayoutInflater.from(parent.getContext()).inflate(R.layout.card_listitem, parent, false);
//        View view = inflater.inflate(R.layout.photosuploadedlist, null,false);
        View view = inflater.inflate(R.layout.cell_project_uploaded, null,false);
        return new ChangeLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(ChangeLocationHolder holder, int position) {
        //getting the product of the specified position
        InitiationUploaded photoDTO1 = photosList.get(position);
        holder.mTvProjectName.setText(photoDTO1.projectName);
        holder.mTvProjectName.setSelected(true);
        holder.mTvAgencyName.setText(photoDTO1.agencyName);
        bindData(holder.mRvImages,photoDTO1,photoDTO1.uploadedPhotoDetailArrayList);


        //binding the data with the viewholder views
       // holder.changeLocationTxt.setText(address.getAddress());
//
//        holder.tendername.setText("Tender ID: "+photoDTO1.getTenderCode());
//        holder.tenderagencyName.setText(photoDTO1.getAgencyName());
//        holder.tenderprojectname.setText(photoDTO1.getProjectName());
//        holder.tendercode.setVisibility(View.GONE);//.setText("Tender Code: "+photoDTO1.getTenderCode());
//        holder.tenderprojectcode.setText(photoDTO1.getProjectCode());
//        holder.tenderaggrementDate.setText(photoDTO1.getAggrementDate());
//        holder.tenderaggrementValue.setText(photoDTO1.getAggrementValue());
//        holder.tendertimeline.setText(photoDTO1.getTimeLine());
//        holder.tenderprojectid.setText(photoDTO1.getProjectId());
//        holder.mTvLatitude.setText(photoDTO1.latitude);
//        holder.mTvLongitude.setText(photoDTO1.longitude);
//        holder.mTvRemark.setText(photoDTO1.remark);
//        if(!photoDTO1.photoURL.equals(null) ) {
//            String photourl = photoDTO1.photoURL;
//
//            //display image in glide
//            Glide.with(mCtx)
//                    .load(photourl)
//                    .override(300, 200)
//                    .into(holder.displayimage);
//        }
    }

    //get latitude and longitude from ADdress


    private void bindData(RecyclerView recyclerView, InitiationUploaded uploadedData, ArrayList<UploadedPhotoDetail> imageUrl) {
        InitiationProjImagesAdapter adapter = new InitiationProjImagesAdapter(mCtx, uploadedData, imageUrl);
//        adapter.setFullImageListener(new StatusImageAdapter.FullImageListener() {
//            @Override
//            public void showfullscreenImage(String url) {
//                ImageFullScreenDialog imageFullScreenDialog = new ImageFullScreenDialog(context,images.get())
//            }
//        });
//        adapter.setFullImageListener(new StatusImageAdapter.FullImageListener() {
//            @Override
//            public void showfullscreenImage(String url, int pos) {
//                new CloseDialog(context,url);
//            }
//        });
        recyclerView.setAdapter(adapter);
    }



    @Override
    public int getItemCount() {
        return photosList.size();
    }

    class ChangeLocationHolder extends RecyclerView.ViewHolder {

        TextView tendername;
        TextView tenderagencyName;
        TextView tenderprojectname;
        TextView tendercode;
        TextView tenderprojectcode;
        TextView tenderaggrementDate;
        TextView tenderaggrementValue;
        TextView tendertimeline;
        TextView tenderprojectid;
        ImageView displayimage;
        TextView mTvLatitude;
        TextView mTvLongitude;
        TextView mTvRemark;
       // CardView addressCardView;

        TextView mTvProjectName, mTvAgencyName;
        RecyclerView mRvImages;
        ImageView mIvImage;

        public ChangeLocationHolder(View itemView) {
            super(itemView);

            mTvProjectName = itemView.findViewById(R.id.tv_project_name);
            mTvAgencyName = itemView.findViewById(R.id.tv_agency_name);
            mRvImages = itemView.findViewById(R.id.rv_fund_phases);
            mRvImages.setLayoutManager(new LinearLayoutManager(mCtx, RecyclerView.HORIZONTAL, false));


            tendername = itemView.findViewById(R.id.tendername);
            tendername = itemView.findViewById(R.id.tendername);
            tenderagencyName = itemView.findViewById(R.id.tenderagencyName);
            tenderprojectname= itemView.findViewById(R.id.tenderprojectname);
            tendercode= itemView.findViewById(R.id.tendercode);
            tenderprojectcode= itemView.findViewById(R.id.tenderprojectcode);
            tenderaggrementDate= itemView.findViewById(R.id.tenderaggrementDate);
            tenderaggrementValue= itemView.findViewById(R.id.tenderaggrementValue);
            tendertimeline= itemView.findViewById(R.id.tendertimeline);
            tenderprojectid= itemView.findViewById(R.id.tenderprojectid);
            displayimage = itemView.findViewById(R.id.displayimage);

            mTvLatitude = itemView.findViewById(R.id.tv_latitude);
            mTvLongitude = itemView.findViewById(R.id.tv_longitude);
            mTvRemark = itemView.findViewById(R.id.tv_remarks);

      //      addressCardView = itemView.findViewById(R.id.addressCardView);

        }
    }
}
