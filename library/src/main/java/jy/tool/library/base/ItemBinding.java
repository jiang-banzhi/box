package jy.tool.library.base;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/9.
 * @desciption :
 * @version :
 * </pre>
 */
public class ItemBinding<T> {

    /**
     * Use this constant as the variable id to not bind the item in the collection to the layout if
     * no data is need, like a static footer or loading indicator.
     */
    public static final int VAR_NONE = 0;
    private static final int VAR_INVALID = -1;
    private static final int LAYOUT_NONE = 0;

    /**
     * Constructs an instance with the given variable id and layout.
     */
    @NonNull
    public static <T> ItemBinding<T> of(int variableId, @LayoutRes int layoutRes) {
        return new ItemBinding<T>(null).set(variableId, layoutRes);
    }

    @NonNull
    public static <T> ItemBinding<T> of(int variableId, @LayoutRes int layoutRes, int itemType) {
        return new ItemBinding<T>(null).set(variableId, layoutRes, itemType);
    }

    /**
     * Constructs an instance with the given callback. It will be called for each item in the
     * collection to set the binding info.
     *
     * @see OnItemBind
     */
    @NonNull
    public static <T> ItemBinding<T> of(@NonNull OnItemBind<T> onItemBind) {
        //noinspection ConstantConditions
        if (onItemBind == null) {
            throw new NullPointerException("onItemBind == null");
        }
        return new ItemBinding<>(onItemBind);
    }

    @Nullable
    private final OnItemBind<T> onItemBind;
    private int variableId;
    @LayoutRes
    private int layoutRes;
    private int itemType = RecyclerViewBindingAdapter.TYPE_NORMAL;
    private SparseArray<Object> extraBindings;

    private ItemBinding(@Nullable OnItemBind<T> onItemBind) {
        this.onItemBind = onItemBind;
    }

    /**
     * Set the variable id and layout. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, T)}.
     */
    @NonNull
    public final ItemBinding<T> set(int variableId, @LayoutRes int layoutRes) {
        this.variableId = variableId;
        this.layoutRes = layoutRes;
        return this;
    }

    @NonNull
    public final ItemBinding<T> set(int variableId, @LayoutRes int layoutRes, int itemType) {
        this.variableId = variableId;
        this.layoutRes = layoutRes;
        this.itemType = itemType;
        return this;
    }

    /**
     * Set the variable id. This is normally called in {@link OnItemBind#onItemBind(ItemBinding,
     * int, T)}.
     */
    @NonNull
    public final ItemBinding<T> variableId(int variableId) {
        this.variableId = variableId;
        return this;
    }

    /**
     * Set the layout. This is normally called in {@link OnItemBind#onItemBind(ItemBinding, int,
     * T)}.
     */
    @NonNull
    public final ItemBinding<T> layoutRes(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }

    /**
     * Bind an extra variable to the view with the given variable id. The same instance will be
     * provided to all views the binding is bound to.
     */
    @NonNull
    public final ItemBinding<T> bindExtra(int variableId, Object value) {
        if (extraBindings == null) {
            extraBindings = new SparseArray<>(1);
        }
        extraBindings.put(variableId, value);
        return this;
    }

    /**
     * Clear all extra variables. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, T)}.
     */
    @NonNull
    public final ItemBinding<T> clearExtras() {
        if (extraBindings != null) {
            extraBindings.clear();
        }
        return this;
    }

    /**
     * Remove an extra variable with the given variable id. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, T)}.
     */
    @NonNull
    public ItemBinding<T> removeExtra(int variableId) {
        if (extraBindings != null) {
            extraBindings.remove(variableId);
        }
        return this;
    }

    /**
     * Returns the current variable id of this binding.
     */
    public final int variableId() {
        return variableId;
    }

    /**
     * Returns the current layout fo this binding.
     */
    @LayoutRes
    public final int layoutRes() {
        return layoutRes;
    }

    public final int itemType() {
        return itemType;
    }

    /**
     * Returns the current extra binding for the given variable id or null if one isn't present.
     */
    @Nullable
    public final Object extraBinding(int variableId) {
        if (extraBindings == null) {
            return null;
        }
        return extraBindings.get(variableId);
    }

    /**
     * Updates the state of the binding for the given item and position. This is called internally
     * by the binding collection adapters.
     */
    public void onItemBind(int position, T item) {
        if (onItemBind != null) {
            variableId = VAR_INVALID;
            layoutRes = LAYOUT_NONE;
            onItemBind.onItemBind(this, position, item);
            if (variableId == VAR_INVALID) {
                throw new IllegalStateException("variableId not set in onItemBind()");
            }
            if (layoutRes == LAYOUT_NONE) {
                throw new IllegalStateException("layoutRes not set in onItemBind()");
            }
        }
    }


    public boolean bind(@NonNull ViewDataBinding binding, T item) {
        if (variableId == VAR_NONE) {
            return false;
        }
        boolean result = binding.setVariable(variableId, item);
        if (!result) {
            Context context = binding.getRoot().getContext();
            Resources resources = context.getResources();
            String layoutName = resources.getResourceName(layoutRes);
            String bindingVariableName = DataBindingUtil.convertBrIdToString(variableId);
            throw new IllegalStateException("Could not bind variable '" + bindingVariableName + "' in layout '" + layoutName + "'");
        }
        if (extraBindings != null) {
            for (int i = 0, size = extraBindings.size(); i < size; i++) {
                int variableId = extraBindings.keyAt(i);
                Object value = extraBindings.valueAt(i);
                if (variableId != VAR_NONE) {
                    binding.setVariable(variableId, value);
                }
            }
        }
        return true;
    }
}
