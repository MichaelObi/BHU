package net.devdome.bhu.app.ui.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.model.Contact;
import net.devdome.bhu.app.ui.adapter.EmergencyServicesAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampusServicesFragment extends Fragment implements View.OnClickListener {

    TextView emergency, professor, restaurants;
    Context context;

    public CampusServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_campus_services, container, false);
        context = getActivity();
        emergency = (TextView) v.findViewById(R.id.emergency_services);
        professor = (TextView) v.findViewById(R.id.my_professor);
        restaurants = (TextView) v.findViewById(R.id.restaurants);

        emergency.setOnClickListener(this);
        professor.setOnClickListener(this);
        restaurants.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_professor:
                String url = "http://binghamuni.edu.ng/myprofessor/";
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.primary));
                intentBuilder.setShowTitle(true);
                intentBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
                intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                CustomTabsIntent intent = intentBuilder.build();
                intent.launchUrl(getActivity(), Uri.parse(url));
                break;
            case R.id.restaurants:
                break;
            case R.id.emergency_services:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("EmergencyServicesDialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                EmergencyServicesDialog emergencyServicesDialog = EmergencyServicesDialog.getInstance();

                emergencyServicesDialog.show(ft, "EmergencyServicesDialog");
                break;
            default:
        }
    }

    public static class EmergencyServicesDialog extends DialogFragment {
        RecyclerView recyclerView;

        static EmergencyServicesDialog getInstance() {
            EmergencyServicesDialog dialog = new EmergencyServicesDialog();
            return dialog;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.emergency_numbers, container, false);
            recyclerView = (RecyclerView) v.findViewById(R.id.rv_emergency_contacts);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new EmergencyServicesAdapter(getActivity(), getEmergencyContacts()));
            return v;
        }

        private ArrayList<Contact> getEmergencyContacts() {
            ArrayList<Contact> contacts = new ArrayList<>();
            contacts.add(new Contact("Campus Security", "080674928297"));
            contacts.add(new Contact("Clinic", "080674928297"));
            contacts.add(new Contact("Principal Security Officer", "080674928297"));
            return contacts;
        }
    }

}
