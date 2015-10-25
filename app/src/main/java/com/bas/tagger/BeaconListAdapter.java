package com.bas.tagger;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by willo on 25/10/2015.
 */
public class BeaconListAdapter extends ArrayAdapter<Node> {

    Context context;
    int layoutResourceId;
    ArrayList<Node> nodes = new ArrayList<Node>();

    public BeaconListAdapter(Context _context, int _layoutResourceId) {
        super(_context, _layoutResourceId);
        this.layoutResourceId = _layoutResourceId;
        this.context = _context;
        nodes.add(new Node(R.mipmap.ic_launcher,"loading",null));
    }

    @Override
    public void add(Node object) {
        if (nodes.contains(object))
            return;
        super.add(object);
        nodes.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NodeHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NodeHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.icon);
            holder.txtTitle = (TextView)row.findViewById(R.id.firstLine);
            holder.txtSubtitle = (TextView)row.findViewById(R.id.secondLine);

            row.setTag(holder);
        }
        else
        {
            holder = (NodeHolder)row.getTag();
        }

        Node node = nodes.get(position);
        holder.txtTitle.setText(node.nodeid);
       // holder.txtSubtitle.setText(node.messages);
        holder.imgIcon.setImageResource(node.icon);

        return row;
    }

    static class NodeHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtSubtitle;
    }

}
