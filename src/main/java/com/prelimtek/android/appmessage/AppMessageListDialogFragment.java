package com.prelimtek.android.appmessage;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.prelimtek.android.customcomponents.R;
import com.prelimtek.android.customcomponents.RecyclerItemTouchHelper;

import java.util.List;

public class AppMessageListDialogFragment extends DialogFragment {

    interface AppMessageDismissOrDeleteListener{
        void onMessageDeleted(AppMessageModel message);
        void onMessageDismissed(AppMessageModel message);
    }

    public AppMessageListDialogFragment(Context context, AppMessageDAOInterface dao){
        this.context = context;
        dbHelper = dao;
    }

    private Context context;
    private static final String TAG = AppMessageListDialogFragment.class.getSimpleName();

    private static int PAGE_BUFFER_SIZE = 4;

    private int viewedItems = 0;

    private AppMessageDismissOrDeleteListener callback;
    private AppMessageDAOInterface dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view  = inflater.inflate(R.layout.appmessage_item_list_recycler_layout,container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final Context currentContext = this.getActivity();

        try {
            //dbHelper =  AppDAO.builder().open(currentContext);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //set layout manager and listeners
        ObservableList<AppMessageModel> notesList = new ObservableArrayList<AppMessageModel>();
        notesList.addAll(queryForNotifications());
        viewedItems = notesList==null?0:notesList.size();


        View cancelbutton = view.findViewById(R.id.cancel_button) ;
        cancelbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(currentContext,LinearLayoutManager.VERTICAL,false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.item_swipe_recycler) ;

        AppMessageRecyclerViewAdapter dataAdapter = new AppMessageRecyclerViewAdapter(
                currentContext,
                notesList,
                R.layout.swipeable_deleteupdate_appmessage_item_layout,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Snackbar.make(v, "Selected note for Details is still under construction.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        AppMessageModel note = (AppMessageModel)v.getTag();
                        //if(noteSelectedLister!=null)noteSelectedLister.onNoteClicked(note);

                    }
                }
        );

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(dataAdapter);
        /*recyclerView.setOnFlingListener(new RecyclerPaginationFlingListerner(layoutManager) {

            boolean isLoading = false;
            @Override
            public void loadNextPage() {

                int offset = viewedItems==0?0:viewedItems;

                queryDS(NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE,offset);

                showFirstPage();
            }

            @Override
            public void loadPreviousPage() {

                int offset = viewedItems-2*NotesTextRecyclerViewAdapter.PAGE_BUFFER_SIZE;
                offset = offset < 0 ? 0 : offset;

                queryDS(PAGE_BUFFER_SIZE,offset);

                showLastPage();

            }

            private void queryDS(int rowcount, int offset){

                if(isLoading) return;

                showProgress(isLoading = true);

                try {

                    List<NotesModel> notesList = dbHelper.getNotes(modelId, null, afterDate==null?null:afterDate.getTime(), rowcount,offset );

                    if(notesList==null || notesList.isEmpty())return;

                    dataAdapter.setRowItems(notesList);
                    dataAdapter.notifyDataSetChanged();//TODO use different notifyItemRangeChanged, itemRageInserted etc

                }catch(RuntimeException e){
                    e.printStackTrace();
                }finally {
                    viewedItems = offset+(notesList==null?0:notesList.size());
                    showProgress(isLoading = false);
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void showProgress(boolean show) {
                ProgressBar progress = (ProgressBar)getActivity().findViewById(R.id.load_items_progress) ;
                if(show)
                    progress.setVisibility(ProgressBar.VISIBLE);
                else
                    progress.setVisibility(ProgressBar.GONE);
            }
        });*/
        recyclerView.setLayoutManager(layoutManager);
        RecyclerItemTouchHelper recyclerItemTouchHelper = new RecyclerItemTouchHelper(0,  ItemTouchHelper.LEFT){
            @Override
            public void onLeftActionActivated(RecyclerView.ViewHolder viewHolder) {
                int pos = viewHolder.getAdapterPosition();
                AppMessageModel noteTag =  (AppMessageModel)viewHolder.itemView.getTag();
                callback.onMessageDismissed(noteTag);

                //noteEditedCallBack.onNoteEdited(tag);
                //Do nothing
                /*
                FragmentManager fm = getFragmentManager();
                EditNotesDetailsDialogFragment editNameDialogFragment = new EditNotesDetailsDialogFragment();
                //NotesDetailsFragment editNameDialogFragment = new NotesDetailsFragment();
                Bundle args = new Bundle();
                args.putSerializable(EditNotesDetailsDialogFragment.ARG_NOTES_MODEL, noteTag);
                editNameDialogFragment.setArguments(args);
                editNameDialogFragment.setCancelable(false);
                editNameDialogFragment.show(fm, "fragment_edit_note");*/
            }

            @Override
            public void onRightActionActivated(RecyclerView.ViewHolder viewHolder) {
                int pos = viewHolder.getAdapterPosition();
                AppMessageModel tag =  (AppMessageModel)viewHolder.itemView.getTag();
                callback.onMessageDeleted(tag);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //Do nothing
                /*if(direction == ItemTouchHelper.LEFT ){
                    //display delete
                }else if(direction == ItemTouchHelper.RIGHT){
                    //display edit
                }*/

            }
        };
        new ItemTouchHelper(recyclerItemTouchHelper).attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                recyclerItemTouchHelper.onDraw(c);
            }
        });



    }

    private List<AppMessageModel> queryForNotifications(){

        return dbHelper.retrieveAllAppMessages();
        //return Arrays.asList();
    }


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            if(getParentFragment() instanceof AppMessageListDialogFragment){
                //callback = (AppMessageListDialogFragment)getParentFragment();
            }else {//if(getActivity() instanceof AppMessageListDialogFragment){
                //callback = (AppMessageListDialogFragment)getActivity();
            }
            //noteSelectedLister = (NotesListDisplayFragment.OnNoteSelectedListener) activity;
            //noteEditedCallBack = ((NotesDetailsFragment.OnEditNoteListener) getActivity());
        } catch (ClassCastException e) {
            Log.e(TAG,e.getMessage());
            //throw new ClassCastException(activity.toString() + " must implement OnTenantSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //remove callbacks
        //close connections
    }
}
