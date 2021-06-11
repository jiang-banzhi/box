package jy.tool.box.recycler;

import androidx.databinding.ObservableField;
import com.junfa.growthcompass.terminal.base.BaseViewModel;
import jy.tool.library.base.ItemViewModel;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/3/26.
 * @desciption :
 * @version :
 * </pre>
 */
public class RecyclerItemViewModel extends ItemViewModel {
    public RecyclerItemViewModel(BaseViewModel viewModel) {
        super(viewModel);
    }
    public ObservableField<String> info = new ObservableField<>();


    public void setArticle(String info) {
        this.info.set(info);
    }

    public String getArticle() {
        return this.info.get();
    }
}
