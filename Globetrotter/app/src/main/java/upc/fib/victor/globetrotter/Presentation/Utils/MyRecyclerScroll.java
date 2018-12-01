package upc.fib.victor.globetrotter.Presentation.Utils;

import android.support.v7.widget.RecyclerView;


public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    private int scrollDist = 0;
    private static final float MINIMUM = 25;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(dy > 0) {
            if(scrollDist > MINIMUM) {
                hide();
                scrollDist = 0;
            }
            scrollDist += dy;
        }
    }
    public void show() {
        scrollDist = 0;
    }
    public abstract void hide();
}
