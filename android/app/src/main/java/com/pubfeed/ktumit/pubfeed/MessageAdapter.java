package com.pubfeed.ktumit.pubfeed;

/**
 * Created by ktumit on 18/05/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    private LayoutInflater inflater;
    private ArrayList<Message> results;
    private Context context;

    public MessageAdapter(Context context, int resource, ArrayList<Message> list) {
        super(context, resource, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        results = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Message message = results.get(position);
        if (convertView == null && inflater != null) {
            convertView = inflater.inflate(R.layout.message_item, null);

            holder = new ViewHolder();
            holder.nameTV = (TextView) convertView.findViewById(R.id.user_name);
            holder.messageTV = (TextView) convertView.findViewById(R.id.user_message);
            holder.avatarIV =  convertView.findViewById(R.id.user_avatar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.avatarIV.setBackgroundColor(Color.parseColor(message.avatar));
        holder.nameTV.setText(message.name);
        holder.messageTV.setText(message.message);
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class ViewHolder {
        TextView nameTV;
        TextView messageTV;
        View avatarIV;
    }
}