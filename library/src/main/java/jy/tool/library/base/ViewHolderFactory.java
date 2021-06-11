package jy.tool.library.base;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/10.
 * @desciption :
 * @version :
 * </pre>
 */
public interface ViewHolderFactory {

    @NonNull
    RecyclerView.ViewHolder createViewHolder(@NonNull ViewDataBinding binding);
}
