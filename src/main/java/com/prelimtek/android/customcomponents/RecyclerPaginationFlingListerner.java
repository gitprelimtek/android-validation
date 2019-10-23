package com.prelimtek.android.customcomponents;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerPaginationFlingListerner extends RecyclerView.OnFlingListener{

    LinearLayoutManager layoutManager = null;
    public RecyclerPaginationFlingListerner(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {

        int first = layoutManager.findFirstCompletelyVisibleItemPosition();//findFirstVisibleItemPosition();
        int last = layoutManager.findLastCompletelyVisibleItemPosition();//findLastVisibleItemPosition();
        int count = layoutManager.getItemCount();//getInitialPrefetchItemCount();

        if(velocityX>5000 || velocityY>5000 ){
            if( last==count-1 ) {
                loadNextPage();
                return true;
            }
        }else if(velocityX < -5000 || velocityY< -5000){
            if(first==0 && count > 0){
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