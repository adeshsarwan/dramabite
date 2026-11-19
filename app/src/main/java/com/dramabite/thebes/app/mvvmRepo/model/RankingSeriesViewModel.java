package com.dramabite.thebes.app.mvvmRepo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dramabite.thebes.app.applicationPreference.ControlPreference;
import com.dramabite.thebes.app.engineBox.model.rank.RankSeriesModel;
import com.dramabite.thebes.app.mvvmRepo.repo.RankingSeriesRepository;


public class RankingSeriesViewModel extends ViewModel {

    private RankingSeriesRepository repository;
    private MutableLiveData<RankSeriesModel> popularSeriesLiveData;

    public RankingSeriesViewModel() {
        // Repository created lazily — EngineClient needs Remote Config base URL first.
    }

    private String currentLang = "";

    private RankingSeriesRepository repository() {
        if (repository == null) {
            repository = new RankingSeriesRepository();
        }
        return repository;
    }

    public LiveData<RankSeriesModel> getRankingSeries() {
        String lang = ControlPreference.getLanguage();
        if (popularSeriesLiveData == null || !currentLang.equals(lang)) {
            currentLang = lang;
            try {
                popularSeriesLiveData = repository().getRankingSeries(lang, 1, 10);
            } catch (IllegalStateException e) {
                popularSeriesLiveData = new MutableLiveData<>();
                popularSeriesLiveData.setValue(null);
            }
        }
        return popularSeriesLiveData;
    }
}