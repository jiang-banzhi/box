package jy.tool.library.base;

import com.junfa.growthcompass.terminal.base.BaseViewModel;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/10.
 * @desciption :
 * @version :
 * </pre>
 */
public class ItemViewModel<VM extends BaseViewModel> {
    protected VM viewModel;

    public ItemViewModel(VM viewModel) {
        this.viewModel = viewModel;
    }
}
