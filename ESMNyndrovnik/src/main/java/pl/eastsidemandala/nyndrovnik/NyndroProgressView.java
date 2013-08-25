package pl.eastsidemandala.nyndrovnik;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by konrad on 20.06.2013.
 */
public class NyndroProgressView extends View {
    private static final float STROKE = 2;

    private Paint mPaint, mFillPaint;
    float mWidth;
    float mHeight;
    float mRadius;
    int mCount;
    RectF mRect;



    public NyndroProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setARGB(0xFF, 0xCC, 0, 0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE);
        mFillPaint = new Paint(mPaint);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);


    }

    public void setCount(int c) {
        mCount = c;
        invalidate();
    }

    int getCount() {
        return mCount;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mRadius = (w/20.0f)/3.0f;
        mRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int reqWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = reqWidth * 6/20;
        setMeasuredDimension(reqWidth, height);
    }

    @Override
    protected void onDraw(Canvas c) {
        float r = mRadius;
        float s = mRadius*3;
        int counter = 0;
// this is where most of the magic happens!
        for (float y = r+STROKE; y < s*6; y += s) {
            for (float x = r+STROKE; x < s*11 || (y < s*5 && x <= s*20) ; x+= s) {
                mRect.set(x-r, y-r, x+r, y+r);
                RectF clip = new RectF();
                clip.set(mRect);
                if (counter < mCount/1000) {
                    // draw filled circles
                    c.drawOval(mRect, mFillPaint);
                } else {
                    if (counter >= (mCount/1000)+1) {
                        // draw empty circles
                        c.clipRect(0, 0, c.getWidth(), c.getHeight(), Region.Op.REPLACE);
                        c.drawOval(mRect, mPaint);
                    } else {
                        // draw a partially filled circle
                        c.drawOval(mRect, mPaint);
                        float fillLevel = (mCount % 1000)/1000.0f;
                        clip.bottom += 2*r - fillLevel*2*r;
                        clip.top += 2*r - fillLevel*2*r;
                        c.clipRect(clip);
                        c.drawOval(mRect, mFillPaint);
                    }
                }
                counter++;
            }
        }
    }
}
