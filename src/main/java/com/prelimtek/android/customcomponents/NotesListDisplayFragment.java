package com.prelimtek.android.customcomponents;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 *
 * This class can be called from a dialog or activity.
 *
 * @author kaniundungu
 * **/
public class NotesListDisplayFragment extends Fragment {

    public static String TAG = Class.class.getSimpleName();

    public enum LayoutManagerDirection {

        RECYCLER_HORIZONTAL(RecyclerView.HORIZONTAL),
        RECYCLER_VERTICAL(RecyclerView.VERTICAL);

        private int direction = -1;

        LayoutManagerDirection(int recVal) {
            this.direction = recVal;
        }

    }

    public final static String MODEL_ID_KEY = "modelIdKey";

    private LayoutManagerDirection layout_manager_direction = LayoutManagerDirection.RECYCLER_HORIZONTAL;

    public interface OnNoteSelectedListener {
        public void onNoteClicked(NotesModel note);
    }

    public int viewedItems = 0;

    @NonNull
    private TextDAOInterface dbHelper;

    @Nullable
    private OnNoteSelectedListener noteSelectedLister;

    public void setDBHelper(TextDAOInterface localDao) {
        dbHelper = localDao;
    }

    public void setNoteSelectedListener(OnNoteSelectedListener selectedNoteListener) {
        this.noteSelectedLister = selectedNoteListener;
    }

    public void setLayoutManagerDirection(LayoutManagerDirection _layout_manager_direction) {
        this.layout_manager_direction = _layout_manager_direction == null ? this.layout_manager_direction : _layout_manager_direction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.notes_recycler_view_layout, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final Context currentContext = this.getActivity();

        //get data object passed by previous activity
        final String modelId = getArguments().getString(MODEL_ID_KEY);

        // isEditable = getArguments().getBoolean(IMAGE_IS_EDITABLE_BOOL_KEY,false);

        final Date afterDate = decrementDate(new Date(), 10);

        List<NotesModel> notesList = modelId == null ? null : dbHelper.getNotes(modelId, null, afterDate.getTime(), NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE, 0);
        viewedItems = notesList == null ? 0 : notesList.size();

        //Now bind the list of Images using an adapter

        LinearLayoutManager layoutManager = new LinearLayoutManager(currentContext, layout_manager_direction.direction, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.noteslist_recyclerview);

        NotesTextRecyclerViewAdapter dataAdapter = new NotesTextRecyclerViewAdapter(
                currentContext,
                notesList,
                R.layout.notes_list_layout,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Snackbar.make(v, "Selected note for Details is still under construction.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        NotesModel note = (NotesModel) v.getTag();
                        if (noteSelectedLister != null) noteSelectedLister.onNoteClicked(note);

                    }
                }
        );

        recyclerView.setAdapter(dataAdapter);
        recyclerView.setOnFlingListener(new PageFlingListerner(layoutManager) {

            boolean isLoading = false;

            @Override
            public void loadNextPage() {

                int offset = viewedItems == 0 ? 0 : viewedItems;

                queryDS(NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE, offset);

                showFirstPage();
            }

            @Override
            public void loadPreviousPage() {

                int offset = viewedItems - 2 * NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE;
                offset = offset < 0 ? 0 : offset;

                queryDS(NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE, offset);

                showLastPage();

            }

            private void queryDS(int rowcount, int offset) {

                if (isLoading) return;

                showProgress(isLoading = true);

                try {

                    List<NotesModel> notesList = dbHelper.getNotes(modelId, null, afterDate.getTime(), rowcount, offset);

                    if (notesList == null || notesList.isEmpty()) return;

                    dataAdapter.setRowItems(notesList);
                    dataAdapter.notifyDataSetChanged();//TODO use different notifyItemRangeChanged, itemRageInserted etc
                    //dataAdapter.notifyItemChanged(position);
                    //dataAdapter.notify();;
                    //dataAdapter.notifyAll();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    viewedItems = offset + (notesList == null ? 0 : notesList.size());
                    showProgress(isLoading = false);
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void showProgress(boolean show) {
                ProgressBar progress = (ProgressBar) view.findViewById(R.id.load_notes_progress);
                if (show)
                    progress.setVisibility(ProgressBar.VISIBLE);
                else
                    progress.setVisibility(ProgressBar.GONE);
            }
        });

        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //this passes the activity and not the parent fragment
    }

    /**
     * This is setter is called by parent fragment which is child aware
     * <p>
     * public void setCallback(OnImageDeletedListener callback){
     * //mCallback = callback;
     * // This makes sure that the container activity has implemented
     * // the callback interface. If not, it throws an exception.
     * <p>
     * }
     */


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //dbHelper.close();
        //mCallback = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //removing this to resolve Illegal state exception due to commit transaction in activity
        //super.onSaveInstanceState(outState);

    }

    public static Date decrementDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, (-1) * days);
        return cal.getTime();
    }



    abstract class PageFlingListerner extends RecyclerView.OnFlingListener{

        LinearLayoutManager layoutManager = null;
        public PageFlingListerner(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public boolean onFling(int velocityX, int velocityY) {

            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            int count = layoutManager.getInitialPrefetchItemCount();

            if(velocityX>10000 || velocityY>10000 ){
                if(first==last && last==count ) {
                    loadNextPage();
                    return true;
                }
            }else if(velocityX < -10000 || velocityY< -10000){
                if(first==0 && last == 0 && count > 0){
                    loadPreviousPage();
                    return true;
                }
            }

            return false;
        }

        public void showFirstPage(){
            int count = layoutManager.getInitialPrefetchItemCount();
            if(count>0)
                layoutManager.scrollToPosition(0);
        }

        public void showLastPage(){
            int count = layoutManager.getInitialPrefetchItemCount();
            if(count>0)
                layoutManager.scrollToPosition(count-1);
        }

        abstract public void loadNextPage();
        abstract public void loadPreviousPage();
        abstract public boolean isLoading();
        abstract public void showProgress(boolean show);

    }
}