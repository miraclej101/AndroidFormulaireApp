package com.example.tp2_formulaire;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contact> list;

    public MyAdapter(Context context, ArrayList<Contact> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ConstraintLayout layoutItem;
        LayoutInflater mlnflater = LayoutInflater.from(context);
        //(1) : Résutilisation du layout
        if (convertView == null)
            layoutItem = (ConstraintLayout) mlnflater.inflate(R.layout.item_layout, viewGroup, false);
        else
            layoutItem = (ConstraintLayout) convertView;
        ViewHolder viewHolder = (ViewHolder) layoutItem.getTag();
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.tvListNom = layoutItem.findViewById(R.id.tvListNom);
            viewHolder.tvListPrenom = layoutItem.findViewById(R.id.tvListPrenom);
            viewHolder.tvListEmail = layoutItem.findViewById(R.id.tvListEmail);
            viewHolder.img = layoutItem.findViewById(R.id.imageView);
            layoutItem.setTag(viewHolder);
        }
        viewHolder.tvListNom.setText(list.get(position).getNom());
        viewHolder.tvListPrenom.setText(list.get(position).getPrenom());
        viewHolder.tvListEmail.setText(list.get(position).getEmail());
     //   int resId = context.getResources().getIdentifier("ic_"+list.get(position).toLowerCase(), "mipmap", context.getPackageName());
        int resId = list.get(position).getResId();
        String imageUriStr = list.get(position).getImageUriStr();
        if (imageUriStr != null && !imageUriStr.equals("") && !imageUriStr.equals("null")) {
            Glide.with(context).load(Uri.parse(imageUriStr)).circleCrop().into(viewHolder.img);
        } else if (list.get(position).getGenre().equals("M")){
            viewHolder.img.setImageResource(R.mipmap.ic_launcher);
        } else {
            viewHolder.img.setImageResource(R.mipmap.ic_female);
        }

        return layoutItem;
    }
    private class ViewHolder {
        public TextView tvListNom;
        public TextView tvListPrenom;
        public TextView tvListEmail;
        public ImageView img;
    }
}
