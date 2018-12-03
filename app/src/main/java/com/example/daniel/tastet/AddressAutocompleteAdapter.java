package com.example.daniel.tastet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AddressAutocompleteAdapter extends ArrayAdapter {
    /* Referenced: http://www.zoftino.com/google-places-auto-complete-android */

    public class Place {
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String newVal) {
            address = newVal;
        }
    }

    private ArrayList<Place> addressList;
    private Context mContext;
    private GeoDataClient geoDataClient;

    private AddressAutocompleteAdapter.AddressAutocompleteFilter listFilter;

    public AddressAutocompleteAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<Place>());
        mContext = context;

        geoDataClient = Places.getGeoDataClient(mContext);
        listFilter = new AddressAutocompleteFilter();
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Place getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        }

        TextView textOne = view.findViewById(android.R.id.text1);
        textOne.setText(addressList.get(position).getAddress());

        return view;
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class AddressAutocompleteFilter extends Filter {
        private Object lock = new Object();
        private Object lockTwo = new Object();
        private boolean placeResults = false;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            placeResults = false;
            final List<Place> matchList = new ArrayList<>();
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<Places>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                Task<AutocompletePredictionBufferResponse> task = getAddressList(searchStrLowerCase);

                task.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        if (task.isSuccessful()) {
                            AutocompletePredictionBufferResponse predictions = task.getResult();
                            Place match;
                            for (AutocompletePrediction prediction : predictions) {
                                match = new Place();
                                match.setAddress(prediction.getFullText(null).toString());
                                matchList.add(match);
                            }
                            predictions.release();
                        } else {
                            Log.i(NavigatorActivity.TAG, task.getException().toString());
                        }

                        placeResults = true;
                        synchronized (lockTwo) {
                            lockTwo.notifyAll();
                        }
                    }
                });

                while(!placeResults) {
                    synchronized (lockTwo) {
                        try {
                            lockTwo.wait();
                        } catch (InterruptedException e) {}
                    }
                }

                results.values = matchList;
                results.count = matchList.size();
                Log.i(NavigatorActivity.TAG, "Returned prediction list of size " + results.count);

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                addressList = (ArrayList<Place>) results.values;
            } else {
                addressList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        private Task<AutocompletePredictionBufferResponse> getAddressList(String query) {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();

            return geoDataClient.getAutocompletePredictions(query, null, filter);
        }


    }

}
