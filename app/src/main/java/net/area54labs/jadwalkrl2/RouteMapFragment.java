package net.area54labs.jadwalkrl2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class RouteMapFragment extends Fragment {

    WebView mapView;

    public RouteMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_map, container, false);

        mapView = (WebView) rootView.findViewById(R.id.map_image);

        mapView.loadUrl("file:///android_asset/map.jpg");
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setLoadWithOverviewMode(true);
        mapView.getSettings().setUseWideViewPort(true);
        mapView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mapView.getSettings().setBuiltInZoomControls(true);

        return rootView;
    }
}