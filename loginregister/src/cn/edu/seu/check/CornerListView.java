package cn.edu.seu.check;



import cn.edu.seu.main.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

public class CornerListView extends ListView {
    public CornerListView(Context context) {
        super(context);
    }

    public CornerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CornerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemnum = pointToPosition(x, y);

                if (itemnum == AdapterView.INVALID_POSITION)
                        break;                 
                else{
                	if(itemnum==0){
                        if(itemnum==(getAdapter().getCount()-1)){                                    
                            setSelector(R.drawable.background_view_rounded_single);
                        }else{
                            setSelector(R.drawable.background_view_rounded_top);
                        }
	                }else if(itemnum==(getAdapter().getCount()-1))
	                        setSelector(R.drawable.background_view_rounded_bottom);
	                else{                            
	                    setSelector(R.drawable.background_view_rounded_middle);
	                }
                }

                break;
        case MotionEvent.ACTION_UP:
                break;
        }
        
        return super.onInterceptTouchEvent(ev);
    }
}