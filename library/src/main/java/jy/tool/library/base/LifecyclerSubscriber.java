package jy.tool.library.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.banzhi.rxhttp.exception.ApiException;
import io.reactivex.observers.DisposableObserver;

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/12.
 * @desciption :
 * @version :
 * </pre>
 */
public abstract class LifecyclerSubscriber<T> extends DisposableObserver<T> implements LifecycleObserver {

    public LifecyclerSubscriber(Lifecycle lifecycle) {
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void onNext(T t) {

    }


    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(ApiException.handleException(e));
        }
        onComplete();
    }

    @Override
    public void onComplete() {

    }

    public abstract void onError(ApiException e);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestory() {
        if (!isDisposed()) {
            dispose();
        }
    }
}
