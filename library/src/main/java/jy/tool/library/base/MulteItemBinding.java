package jy.tool.library.base;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/3/12.
 * @desciption :
 * @version :
 * </pre>
 */
public class MulteItemBinding<T> {
    Map<Integer, ItemBinding<T>> itemBindings = new HashMap<Integer, ItemBinding<T>>();

    private MulteItemBinding() {
    }

    @NonNull
    public static <T> MulteItemBinding<T> ofEmpty() {
        return new MulteItemBinding<T>();
    }

    @NonNull
    public static <T> MulteItemBinding<T> of(ItemBinding<T>... itemBindings) {
        return new MulteItemBinding<T>().addItemBinding(itemBindings);
    }

    @NonNull
    public static <T> MulteItemBinding<T> of(ItemBinding<T> itemBinding) {
        return new MulteItemBinding<T>().addItemBinding(itemBinding);
    }

    @NonNull
    public static <T> MulteItemBinding<T> of(Map<Integer, ItemBinding<T>> itemBindings) {
        return new MulteItemBinding<T>().addItemBinding(itemBindings);
    }

    @NonNull
    public static <T> MulteItemBinding<T> of(MulteItemBinding<T> itemBindings) {
        return new MulteItemBinding<T>().addItemBinding(itemBindings.itemBindings);
    }

    public MulteItemBinding<T> addItemBinding(ItemBinding<T> itemBinding) {
        itemBindings.put(itemBinding.itemType(), itemBinding);
        return this;
    }

    public MulteItemBinding<T> addItemBinding(ItemBinding<T>... itemBindings) {
        for (ItemBinding<T> itemBinding : itemBindings) {
            addItemBinding(itemBinding);
        }
        return this;
    }

    public MulteItemBinding<T> addItemBinding(Map<Integer, ItemBinding<T>> itemBindings) {
        this.itemBindings.putAll(itemBindings);
        return this;
    }


    public void binding(ViewDataBinding binding, T item) {
        Set<Map.Entry<Integer, ItemBinding<T>>> entries = itemBindings.entrySet();
        for (Map.Entry<Integer, ItemBinding<T>> entry : entries) {
            entry.getValue().bind(binding, item);
        }
    }

    public void onItemBind(int position, T item) {
        Set<Map.Entry<Integer, ItemBinding<T>>> entries = itemBindings.entrySet();
        for (Map.Entry<Integer, ItemBinding<T>> entry : entries) {
            entry.getValue().onItemBind(position, item);
        }
    }


    public boolean bind(@NotNull ViewDataBinding binding, @NotNull T item, int itemType) {
        return itemBindings.get(itemType).bind(binding, item);
    }

    public ItemBinding<T> getItemBinding(int itemType) {
        return itemBindings.get(itemType);
    }
}
