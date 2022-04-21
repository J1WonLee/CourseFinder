package com.example.coursefinder.exercise;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coursefinder.MainCategory;
import com.example.coursefinder.R;


public class ListViewAdapter extends BaseAdapter {

    Context context;

    int[] courseImg;
    String[] courseName;
    String[] coursePlace;
    String[] courseScore;

    public ListViewAdapter(Context context, int[] courseImg, String[] courseName, String[] coursePlace, String[] courseScore) {
        this.context = context;
        this.courseImg = courseImg;
        this.courseName = courseName;
        this.coursePlace = coursePlace;
        this.courseScore = courseScore;
    }

    LayoutInflater inflater;


    @Override
    public int getCount() {
        return courseName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.exercise_list_single_item, null);
        }

        ImageView courseimgIv = convertView.findViewById(R.id.courseImg);
        TextView courseNameTv = convertView.findViewById(R.id.courseName);
        TextView coursePlaceTv = convertView.findViewById(R.id.coursePlace);
        TextView courseScoreTv = convertView.findViewById(R.id.courseScore);

        courseimgIv.setImageResource(courseImg[i]);
        courseNameTv.setText(courseName[i]);
        coursePlaceTv.setText(coursePlace[i]);
        courseScoreTv.setText(courseScore[i]);


        ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.detailBtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainCategory.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

}