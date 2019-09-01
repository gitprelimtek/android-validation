package com.prelimtek.android.customcomponents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 *
 * This class can be called from a dialog or activity. To include delete functionality an argument should
 * be passed to
 *
 * @author kaniundungu
 * **/
public class NotesListDisplayFragment extends Fragment {

    public static String TAG = Class.class.getSimpleName();


    public final static String MODEL_ID_KEY = "modelIdKey";
    //public final static String IMAGE_IS_EDITABLE_BOOL_KEY = "editable";


    @NonNull
    private TextDAOInterface dbHelper;

    //private boolean isEditable = false;

    public void setDBHelper(TextDAOInterface localDao) {
        dbHelper = localDao;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        View view =  inflater.inflate( R.layout.notes_recycler_view_layout , container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context currentContext = this.getActivity();

        try {
            //dbHelper =  AppDAO.builder().open(currentContext);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        //get data object passed by previous activity
        final String modelId = getArguments().getString(MODEL_ID_KEY);

       // isEditable = getArguments().getBoolean(IMAGE_IS_EDITABLE_BOOL_KEY,false);

        assert modelId!=null;

        Date afterDate = decrementDate(new Date(),10);
        NotesModel[] notesList = dbHelper.getNotes(modelId,afterDate.getTime(),NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE);
        //if(notesList!=null ){


            //Now bind the list of Images using an adapter

            LinearLayoutManager layoutManager = new LinearLayoutManager(currentContext,RecyclerView.HORIZONTAL,false);
            RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.noteslist_recyclerview) ;

            NotesTextRecyclerViewAdapter dataAdapter = new NotesTextRecyclerViewAdapter(
                    currentContext,
                    Arrays.asList(notesList),
                    R.layout.notes_list_layout,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String imageid = (String)v.getTag();

                            Snackbar.make(v, "Selected image for Details", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
            );
            recyclerView.setAdapter(dataAdapter);
            recyclerView.addOnScrollListener(
                    new PageScrollListener(layoutManager){

                        boolean isLoading = false;
                        @Override
                        public void loadNextPage(int position) {

                            showProgress(isLoading = true);

                            try {
                                NotesModel note = dataAdapter.getItem(position);
                                List<NotesModel> notesList = dbHelper.getNotes(modelId, note.getDate(), null, NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE);
                                dataAdapter.setRowItems(notesList);
                                //dataAdapter.notifyDataSetChanged();//TODO use different notifyItemRangeChanged, itemRageInserted etc
                                dataAdapter.notifyItemChanged(position);
                            }catch(RuntimeException e){
                                e.printStackTrace();
                            }finally {

                                showProgress(isLoading = false);
                            }
                        }

                        @Override
                        public boolean isLoading() {
                            return isLoading;
                        }

                        @Override
                        public void showProgress(boolean show) {
                            ProgressBar progress = (ProgressBar)view.findViewById(R.id.load_notes_progress) ;
                            if(show)
                                progress.setVisibility(ProgressBar.VISIBLE);
                            else
                                progress.setVisibility(ProgressBar.GONE);
                        }
                    }
            );
            recyclerView.setLayoutManager(layoutManager);
            //recyclerView.setLayoutManager(new LinearLayoutManager(currentContext,LinearLayout.HORIZONTAL,false));


        //}
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //this passes the activity and not the parent fragment
    }

    /**
     * This is setter is called by parent fragment which is child aware
     *
    public void setCallback(OnImageDeletedListener callback){
        //mCallback = callback;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.

    }*/


    @Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        //if (getFragmentManager().findFragmentById(R.id.fragment_tenant_list ) != null) {
        //    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //}
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

    public static Date decrementDate(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, (-1)*days);
        return cal.getTime();
    }


    abstract class PageScrollListener extends RecyclerView.OnScrollListener{

        LinearLayoutManager layoutManager = null;
        public PageScrollListener(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int count = layoutManager.getChildCount();
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();

            //NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE
            if( last == count ){
                //scroll right
                //load notes before last note date
                loadNextPage(last);

            }

            /*else if (first == 0){
                //scroll left

            }*/

        }

        abstract public void loadNextPage(int position);
        abstract public boolean isLoading();
        abstract public void showProgress(boolean show);
    }



}
