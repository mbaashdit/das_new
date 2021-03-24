package com.aashdit.distautosystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.model.NotUploaded;

import java.util.List;

public class PhotoNotUploadAdapter extends RecyclerView.Adapter<PhotoNotUploadAdapter.PhotoNotUploadedHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
 private double latitude;
 private double longitude;
    //we are storing all the products in a list
    private List<NotUploaded> photosList;

    //getting the context and product list with constructor
    public PhotoNotUploadAdapter(Context mCtx, List<NotUploaded> photosList) {
        this.mCtx = mCtx;
        this.photosList = photosList;
    }

    @Override
    public PhotoNotUploadedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
       // LayoutInflater.from(parent.getContext()).inflate(R.layout.card_listitem, parent, false);
        View view = inflater.inflate(R.layout.cell_project_not_uploaded, null,false);
        return new PhotoNotUploadedHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoNotUploadedHolder holder, int position) {
        //getting the product of the specified position
        NotUploaded item = photosList.get(position);
        holder.mTvProjectName.setText("Project Name : "+item.projectName);
        holder.mTvProjectName.setSelected(true);
        holder.mTvAgencyName.setText("Agency Name : "+item.agencyName);
        holder.mTvReleaseDate.setText("Released Date : "+item.releaseDate);
        holder.mTvReleaseAmount.setText("Released Amount : "+item.releaseAmount);

        holder.mIvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notUploadedListener.notUploaded(item);
            }
        });


        //binding the data with the viewholder views

//          holder.tendername.setText("Tender Code: "+photoDTO1.getTenderCode());
//        holder.tenderagencyName.setText(photoDTO1.getAgencyName());
//        holder.tenderprojectname.setText(photoDTO1.getProjectName());
//        holder.tendercode.setVisibility(View.GONE);//.setText("Tender Code: "+photoDTO1.getTenderCode());
//        holder.tenderprojectcode.setText(photoDTO1.getProjectCode());
//        holder.tenderaggrementDate.setText(photoDTO1.getAggrementDate());
//        holder.tenderaggrementValue.setText(photoDTO1.getAggrementValue());
//        holder.tendertimeline.setText(photoDTO1.getTimeLine());
//        holder.tenderprojectid.setText(photoDTO1.getProjectId());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resultResponse photoDTO1 = photosList.get(position);
//                String tenderid=photoDTO1.getTenderId()+"";
//                RegPrefManager.getInstance(mCtx).setInitialTenderId(tenderid);
//
//                notUploadedListener.notUploaded(photoDTO1);
//                Intent intent =new Intent(mCtx, InitiationPhotoUploadActivity.class);
//                intent.putExtra("TENDER_ID",tenderid);
//                mCtx.startActivity(intent);
//            }
//        });
//        holder.uploadbtn.setVisibility(View.GONE);
//        holder.uploadbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resultResponse photoDTO1 = photosList.get(position);
//                String tenderid=photoDTO1.getTenderId()+"";
//                RegPrefManager.getInstance(mCtx).setInitialTenderId(tenderid);

//                notUploadedListener.notUploaded(photoDTO1);

//            }
//        });
    }

    //get latitude and longitude from ADdress

    NotUploadedListener notUploadedListener;

    public void setNotUploadedListener(NotUploadedListener notUploadedListener) {
        this.notUploadedListener = notUploadedListener;
    }

    public interface NotUploadedListener{
        void notUploaded(NotUploaded resultResponse);
    }


    @Override
    public int getItemCount() {
        return photosList.size();
    }

    class PhotoNotUploadedHolder extends RecyclerView.ViewHolder {

        TextView mTvAgencyName,mTvProjectName,mTvReleaseDate,mTvReleaseAmount;
        ImageView mIvUpload;
       // CardView addressCardView;

        public PhotoNotUploadedHolder(View itemView) {
            super(itemView);

            mTvAgencyName = itemView.findViewById(R.id.tv_agency_name);
            mTvProjectName= itemView.findViewById(R.id.tv_project_name);
            mTvReleaseDate= itemView.findViewById(R.id.tv_release_date);
            mTvReleaseAmount= itemView.findViewById(R.id.tv_release_amount);


            mIvUpload = itemView.findViewById(R.id.img_upload);



      //      addressCardView = itemView.findViewById(R.id.addressCardView);

        }
    }
}
