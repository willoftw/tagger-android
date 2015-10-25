package com.bas.tagger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.Editable;
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

import com.bas.tagger.util.Settings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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


        //SUPER HAX FOR SPEED
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //nodes.add(new Node(R.mipmap.ic_launcher,"loading",null));
    }

    public void onItemClick(AdapterView parent, View v, final int position, long id)
    {



        final EditText txtUrl = new EditText(context);

        new AlertDialog.Builder(context)
                .setTitle("Your near : " + nodes.get(position).uuid)
                .setMessage("What would you like to add to it??")
                //.setView(txtUrl)
                .setPositiveButton("Use", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Node node = nodes.get(position);
                        Toast.makeText(context, "adding use to " + node.nodeid, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        final EditText edittext = new EditText(context);
                        alert.setMessage("Enter Your Use For this space");
                        alert.setTitle("Use");

                        alert.setView(edittext);

                        alert.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                try {
                                    new HTTPGet().execute(Settings.SERVERURL + "add_use?nodeid=" + node.nodeid + "&use=" + edittext.getText().toString().replaceAll(" ", "_")).get();
                                    node.messages =  new HTTPGet().execute(Settings.SERVERURL + "messages?nodeid=" + node.nodeid).get();
                                    node.uses =  new HTTPGet().execute(Settings.SERVERURL + "uses?nodeid=" + node.nodeid).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        alert.show();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .setNegativeButton("Message", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Node node = nodes.get(position);
                        Toast.makeText(context, "adding message to " + node.uuid, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        final EditText edittext = new EditText(context);
                        alert.setMessage("Enter Your Message For this space");
                        alert.setTitle("Comment");

                        alert.setView(edittext);

                        alert.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                try {
                                    new HTTPGet().execute(Settings.SERVERURL + "add_message?nodeid=" + node.nodeid + "&message=" + edittext.getText().toString().replaceAll(" ", "_")).get();

                                    node.messages =  new HTTPGet().execute(Settings.SERVERURL + "messages?nodeid=" + node.nodeid).get();
                                    node.uses =  new HTTPGet().execute(Settings.SERVERURL + "uses?nodeid=" + node.nodeid).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        alert.show();

                    }
                })
                .show();

    }

    Node temp_obj;
    @Override
    public void add(Node object) {
        super.add(object);
        Log.d("BEACONADAPTER", Settings.SERVERURL + "messages?nodeid=" + object.nodeid);
        try {
            object.messages =   "Messages: " + new HTTPGet().execute(Settings.SERVERURL + "messages?nodeid=" + object.nodeid).get();
            object.uses = "Uses: " + new HTTPGet().execute(Settings.SERVERURL + "uses?nodeid=" + object.nodeid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        holder.nodeuses.setText(node.uses);
        holder.nodecomments.setText(node.messages);
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


    class HTTPGet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... f_url) {

                final HttpClient httpclient = new DefaultHttpClient();
                final HttpGet httpget = new HttpGet(f_url[0]);
                String result=null;
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        InputStream inputstream = entity.getContent();
                        BufferedReader bufferedreader =
                                new BufferedReader(new InputStreamReader(inputstream));
                        StringBuilder stringbuilder = new StringBuilder();

                        String currentline = null;
                        while ((currentline = bufferedreader.readLine()) != null) {
                            stringbuilder.append(currentline + "\n");
                        }
                        result = stringbuilder.toString();
                        Log.v("HTTP REQUEST",result);
                        inputstream.close();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
        }

        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String result) {
            //playMusic();
        }
    }



}
