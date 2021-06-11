package jy.tool.library.base;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/9.
 * @desciption :
 * @version :
 * </pre>
 */
public class BindingViewHolder extends RecyclerView.ViewHolder {

    public BindingViewHolder(ViewDataBinding dataBinding) {
        super(dataBinding.getRoot());
    }

    public BindingViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
