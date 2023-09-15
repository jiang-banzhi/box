package jy.tool.box.menu;

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
public class MenuItemViewModel extends ItemViewModel {
    public MenuItemViewModel(BaseViewModel viewModel) {
        super(viewModel);
    }

    public ObservableField<MenuInfo> menus = new ObservableField<>();


    public void setMenu(MenuInfo menu) {
        this.menus.set(menu);
    }

    public String getMenu() {
        return this.menus.get().getMenuName();
    }
}
