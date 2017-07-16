package net.pocketmagic.android.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author radhoo
 *
 */
public class D2Control extends View implements OnTouchListener {
	Context m_Context;
	private int m_nViewWidthScaled,
				m_nViewHeightScaled,
				cx, cy,
				m_nFrontV, m_nFrontMax;
	private D2ControlEvents listner;
	
	public interface D2ControlEvents {
		public void onLeft();
		public void onForward();
		public void onRight();
		public void onBackward();
	}
	
	public void EnableUserInput() {
		this.setOnTouchListener(this);
	}
	
	public void DisableUserInput() {
		this.setOnTouchListener(null);
	}
	
	public void SetOnReceiveEvents(D2ControlEvents l) {
		listner = l;
	}
	
	public D2Control(Context context) {
		super(context);
		m_Context = context;
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
       	m_nViewWidthScaled = widthSize;
       	m_nViewHeightScaled = heightSize;
       	
       	cx = m_nViewWidthScaled / 2;
       	cy = m_nViewHeightScaled / 2;
       //	Log.e("XX", m_nViewWidthScaled + "x"+ m_nViewHeightScaled);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    public void updateFrontObstacle(int value, int max) {
    	m_nFrontV = value; m_nFrontMax = max;
    	invalidate();
    }
    
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		int w = m_nViewWidthScaled, h = m_nViewHeightScaled;
		
		// Draw some text
	    Paint p = new Paint();
	    p.setColor(Color.YELLOW);
		canvas.drawLine(w/3, 0, w/3, h, p);
		canvas.drawLine(2*w/3, 0, 2*w/3, h, p);
		canvas.drawLine(0, h/3, w, h/3, p);
		canvas.drawLine(0, 2*h/3, w,2*h/3, p);
		
		canvas.drawCircle(w/2,  h/2, 8, p);
		
		Paint p2 = new Paint();
		p2.setColor(Color.GREEN);
		canvas.drawCircle(cx,  cy, 10, p2);
		
		// Draw obstacles
		Paint p3 = new Paint();
		p3.setColor(Color.RED);
		if (m_nFrontMax > 0) {
			int obfront = (int)((float)(m_nFrontMax - m_nFrontV) /  (float)m_nFrontMax *  (float)h/2.0);
			canvas.drawLine(0, obfront, w, obfront, p3);
		}
	}
	
	
	//@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) event.getX(), y = (int) event.getY();
		//Log.e("XX", event.getAction()+ ""); // 0, 2, 2 , 2 ,2 , 2, 1
		if (event.getAction() == MotionEvent.ACTION_UP) {
			cx = x; cy = y;
			invalidate();
			
			// trigger events
			if (listner != null) {
				int w = m_nViewWidthScaled, h = m_nViewHeightScaled;
				if (x > w /3 && x< 2*w/3 && y>0 && y<h/3) listner.onForward();
				if (x > w /3 && x< 2*w/3 && y>2*h/3 && y<h) listner.onBackward();
				if (x > 0 && x< w/3 && y>h/3 && y<2*h/3) listner.onLeft();
				if (x > 2*w /3 && x< w && y>h/3 && y<2*h/3) listner.onRight();
			}
			
		}
		
		return true;
	}
	
	public void SetCursor(int x, int y) {
		cx = x; cy = y;
		invalidate();
	}

}
