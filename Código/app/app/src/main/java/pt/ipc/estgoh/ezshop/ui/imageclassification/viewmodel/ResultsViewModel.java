package pt.ipc.estgoh.ezshop.ui.imageclassification.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.mlkit.vision.label.ImageLabel;

import java.io.File;
import java.util.List;

import kotlin.Triple;

public class ResultsViewModel extends ViewModel {
    /*
  when you don't want your data to be modified use LiveData
  If you want to modify your data later use MutableLiveData
  */
    private final MutableLiveData<Triple<Long, List<ImageLabel>, File>> listMutableLiveData = new MutableLiveData<>();

    public void setResults(final long aListId, final List<ImageLabel> aImageLabels, final File image) {
        this.listMutableLiveData.setValue(new Triple<>(aListId, aImageLabels, image));
    }

    public LiveData<Triple<Long, List<ImageLabel>, File>> getResults() {
        return this.listMutableLiveData;
    }
}
