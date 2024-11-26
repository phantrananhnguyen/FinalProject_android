package com.example.finalproject_android.afterlogin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject_android.models.Feature;

public class MapViewModel extends ViewModel {
    private final MutableLiveData<Feature> feature = new MutableLiveData<>();

    public LiveData<Feature> getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature.setValue(feature);
    }
}