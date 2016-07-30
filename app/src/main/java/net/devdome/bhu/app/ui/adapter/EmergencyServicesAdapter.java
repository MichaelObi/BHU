package net.devdome.bhu.app.ui.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.devdome.bhu.app.model.Contact;
import net.devdome.bhu.app.utility.PermissionUtils;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import java.util.ArrayList;

public class EmergencyServicesAdapter extends RecyclerView.Adapter<EmergencyServicesAdapter.ViewHolder> {


    private Context context;
    private ArrayList<Contact> contacts;
    private Activity activity;

    public EmergencyServicesAdapter(Activity activity, ArrayList<Contact> contacts) {
        this.activity = activity;
        this.context = (Context) activity;
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(contacts.get(position).getContactName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = contacts.get(position).getContactPhone();
                final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                makeCall(intent);
            }
        });
    }

    private void makeCall(final Intent intent) {

        final PermissionCallback callback = new PermissionCallback() {
            @Override
            public void permissionGranted() {
                makeCall(intent);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(context, "Could not complete phone call because permission was denied", Toast.LENGTH_SHORT).show();
            }
        };

        if (Nammu.checkPermission(Manifest.permission.CALL_PHONE)) {
            context.startActivity(intent);
        } else if (Nammu.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {

            PermissionUtils.showRationale(context, "You need to give permission to make calls.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Nammu.askForPermission(activity, Manifest.permission.CALL_PHONE, callback);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            });

        } else {
            Nammu.askForPermission(activity, Manifest.permission.CALL_PHONE, callback);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(android.R.id.text1);
        }

    }
}
