package com.dramabite.thebes.app.mvvmRepo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dramabite.thebes.app.applicationPreference.ControlPreference;
import com.dramabite.thebes.app.engineBox.model.episdata.EpisodeListModel;
import com.dramabite.thebes.app.mvvmRepo.repo.PopularSeriesRepository;

public class PopularSeriesViewModel extends ViewModel {

    private PopularSeriesRepository repository;
    private MutableLiveData<EpisodeListModel> popularSeriesLiveData;

    public PopularSeriesViewModel() {
        // Repository created lazily — EngineClient needs Remote Config base URL first.
    }

    private String currentLang = "";

    private PopularSeriesRepository repository() {
        if (repository == null) {
            repository = new PopularSeriesRepository();
        }
        return repository;
    }

    public LiveData<EpisodeListModel> getPopularSeries() {
        String lang = ControlPreference.getLanguage();
        if (popularSeriesLiveData == null || !currentLang.equals(lang)) {
            currentLang = lang;
            try {
                popularSeriesLiveData = repository().getPopularSeries(lang, 1, 10);
            } catch (IllegalStateException e) {
                popularSeriesLiveData = new MutableLiveData<>();
                popularSeriesLiveData.setValue(null);
            }
        }
        return popularSeriesLiveData;
    }
}