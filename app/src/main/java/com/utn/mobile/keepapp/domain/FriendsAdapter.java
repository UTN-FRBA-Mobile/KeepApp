package com.utn.mobile.keepapp.domain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.utn.mobile.keepapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ldzisiuk on 13/11/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private JSONArray mDataset;
    private Context context;
    private Set<String> selectedIds = new HashSet<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvAmigo;
        public CheckBox checkAmigo;
        public String fbId;

        public ViewHolder(View v) {
            super(v);
            tvAmigo = (TextView) v.findViewById(R.id.tvAmigo);
            checkAmigo = (CheckBox) v.findViewById(R.id.checkAmigo);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsAdapter(JSONArray myDataset, Context context) {
        this.mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_sharewithfriends_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            JSONObject model = (JSONObject) mDataset.get(position);
            holder.tvAmigo.setText(model.getString("name"));
            holder.fbId = model.getString("id");
            holder.checkAmigo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        selectedIds.add(holder.fbId);
                    }else{
                        selectedIds.remove(holder.fbId);
                    }
                }
            });
        }catch (Exception e){
            Log.e("FriendsAdapter", "Error en adapter: "+e.getMessage());
            Toast.makeText(context, "Error en formato de JSON", Toast.LENGTH_LONG).show();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }



    public List<String> getSelectedIds(){
        return new ArrayList<>(selectedIds);
    }
}
