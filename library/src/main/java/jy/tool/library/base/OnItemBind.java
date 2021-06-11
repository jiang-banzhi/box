package jy.tool.library.base;

import androidx.annotation.NonNull;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/9.
 * @desciption :
 * @version :
 * </pre>
 */
public interface OnItemBind<T> {
    void onItemBind(@NonNull ItemBinding itemBinding, int position, T item);
}
