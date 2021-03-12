
package com.aashdit.distautosystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.model.Uploaded;

import java.util.ArrayList;
import java.util.List;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.ChangeLocationHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
 private double latitude;
    private double longitude;
    //we are storing all the products in a list
    private ArrayList<Uploaded> photosList;

    //getting the context and product list with constructor
    public PhotoUploadAdapter(Context mCtx, ArrayList<Uploaded> photosList) {
        this.mCtx = mCtx;
        this.photosList = photosList;
    }

    @Override
    public ChangeLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
       // LayoutInflater.from(parent.getContext()).inflate(R.layout.card_listitem, parent, false);
        View view = inflater.inflate(R.layout.cell_project_uploaded, null, false);
        return new ChangeLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(ChangeLocationHolder holder, int position) {
        Uploaded upload = photosList.get(position);
        holder.mTvProjectName.setText(upload.projectName);
        holder.mTvProjectName.setSelected(true);
        holder.mTvAgencyName.setText(upload.agencyName);

        bindData(holder.mRvImages,upload,upload.photoURL);

    }


    private void bindData(RecyclerView recyclerView, Uploaded uploadedData, String imageUrl) {
        ProjImagesAdapter adapter = new ProjImagesAdapter(mCtx, uploadedData, imageUrl);
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

        TextView mTvProjectName, mTvAgencyName;
        RecyclerView mRvImages;

        public ChangeLocationHolder(View itemView) {
            super(itemView);

            mTvProjectName = itemView.findViewById(R.id.tv_project_name);
            mTvAgencyName = itemView.findViewById(R.id.tv_agency_name);
            mRvImages = itemView.findViewById(R.id.rv_images);
            mRvImages.setLayoutManager(new LinearLayoutManager(mCtx, RecyclerView.VERTICAL, false));

        }
    }
}