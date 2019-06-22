package io.mtini.android.adaptor;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Log;

import java.util.List;

import io.mtini.android.tenantmanager.BR;

@Deprecated
public class GenericItemListBindingAdapter<T>{
    public static final String TAG = "ListBindingAdapters";

    //@BindingAdapter({"app:entries", "app:layout"})
    @BindingAdapter({"entries", "layout"})
    public static <T> void setEntries(ViewGroup viewGroup, ObservableList<T> entries, int layoutId){
                   viewGroup.removeAllViews();
                   if (entries != null) {
                       LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                       for (int i = 0; i < entries.size(); i++) {
                           T entry = entries.get(i);
                           ViewDataBinding binding = DataBindingUtil
                                   .inflate(inflater, layoutId, viewGroup, true);
                           binding.setVariable(BR.estates , entry);
                       }
                   }
    }

    private static ViewDataBinding bindLayout(LayoutInflater inflater,
                                              ViewGroup parent, int layoutId, Object entry) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater,
                layoutId, parent, false);
        if (!binding.setVariable(BR.estates, entry)) {
            String layoutName = parent.getResources().getResourceEntryName(layoutId);
            Log.w(TAG, "There is no variable 'estates' in layout " + layoutName);
        }
        return binding;
    }


    private static void resetViews(ViewGroup parent, int layoutId,
                                   List entries) {
        parent.removeAllViews();
        if (layoutId == 0) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < entries.size(); i++) {
            Object entry = entries.get(i);
            ViewDataBinding binding = bindLayout(inflater, parent,
                    layoutId, entry);
            parent.addView(binding.getRoot());
        }
    }

    /**
     * Starts a transition only if on KITKAT or higher.
     *
     * @param root The scene root
     */
    private static void startTransition(ViewGroup root) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root);
        }
    }

}
