package com.bas.tagger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by willo on 25/10/2015.
 */
public class BeaconListAdapter extends ArrayAdapter<Node> implements AdapterView.OnItemClickListener {

    Context context;
    int layoutResourceId;
    ArrayList<Node> nodes = new ArrayList<Node>();

    public BeaconListAdapter(Context _context, int _layoutResourceId) {
        super(_context, _layoutResourceId);
        this.layoutResourceId = _layoutResourceId;
        this.context = _context;
        //nodes.add(new Node(R.mipmap.ic_launcher,"loading",null));
    }

    public void onItemClick(AdapterView parent, View v, final int position, long id)
    {



        final EditText txtUrl = new EditText(context);

// Set the default text to a link of the Queen
        txtUrl.setHint("http://www.librarising.com/astrology/celebs/images2/QR/queenelizabethii.jpg");

        new AlertDialog.Builder(context)
                .setTitle("Your near : " + nodes.get(position).uuid)
                .setMessage("What would you like to add to it??")
                //.setView(txtUrl)
                .setPositiveButton("Use", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Node node = nodes.get(position);
                        Toast.makeText(context, "adding use to " + node.uuid, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .setNegativeButton("Comment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Node node = nodes.get(position);
                        Toast.makeText(context, "adding comment to " + node.uuid, Toast.LENGTH_SHORT).show();

                    }
                })
                .show();

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
            holder.nodeid = (TextView)row.findViewById(R.id.nodeid);
            holder.nodeuses = (TextView)row.findViewById(R.id.nodeuses);
            holder.nodecomments = (TextView)row.findViewById(R.id.nodecomments);

            row.setTag(holder);
        }
        else
        {
            holder = (NodeHolder)row.getTag();
        }

        Node node = nodes.get(position);
        holder.nodeid.setText(node.nodeid);
        holder.nodeuses.setText(node.uses.toString());
        holder.nodecomments.setText(node.messages.toString());
        holder.imgIcon.setImageResource(node.icon);

        return row;
    }

    static class NodeHolder
    {
        ImageView imgIcon;
        TextView nodeid;
        TextView nodeuses;
        TextView nodecomments;
    }

}
