package pl.eastsidemandala.nyndrovnik;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;

/**
 * Created by konrad on 20.06.2013.
 */
public class NyndroProgressView extends View {

    // Computations constants
    private static final int VIEW_ROWS = 6;
    private static final int VIEW_COLS = 20;
    private static final float DESIRED_HEIGHT_FACTOR = (float) VIEW_ROWS / (float) VIEW_COLS;
    private static final HashMap<Practice, Integer> PRACTICE_PROGRESS_ROW_VALUE_MAP = new HashMap<>();

    // Other constants
    private static final float STROKE_WIDTH_DP = 1;

    // Static init
    {
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.SHORT_REFUGE, 2000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.PROSTRATIONS, 20000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.DIAMOND_MIND, 20000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.MANDALA_OFFERING, 20000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.GURU_YOGA, 20000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.AMITABHA, 20000);
        PRACTICE_PROGRESS_ROW_VALUE_MAP.put(Practice.LOVING_EYES, 200000);
    }

    // Below Instance members etc.

    private Paint mPaint;
    private Paint mFillPaint;
    private float mStrokeWidth;

    private Practice mPractice;
    private int mCurrentPracticeCount;
    private int mCellValue;
    private int mRowsNeeded;
    private int mLastRowCols;

    private float mCellSize;
    private float mCircleRadius;

    public NyndroProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setARGB(0xFF, 0xCC, 0, 0);
        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, getResources().getDisplayMetrics());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mFillPaint = new Paint(mPaint);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setPractice(Practice practice) {
        this.mPractice = practice;
    }

    public void setCurrentPracticeCount(int value) {
        mCurrentPracticeCount = value;
        int rowValue = PRACTICE_PROGRESS_ROW_VALUE_MAP.get(mPractice);
        mCellValue = rowValue / VIEW_COLS;
        mRowsNeeded = (mPractice.getRepetitionsMax() / rowValue) + 1;
        mLastRowCols = (mPractice.getRepetitionsMax() % rowValue) / mCellValue;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCellSize = (float) w / (float) VIEW_COLS;
        mCircleRadius = mCellSize / 3.0f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int reqWidth = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = (int) (reqWidth * DESIRED_HEIGHT_FACTOR); // (view width / cols) * rows
        setMeasuredDimension(reqWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas c) {

        int counter = 0;
        RectF rectF = new RectF();

        // this is where most of the magic happens!
        for (float y = mCircleRadius + mStrokeWidth; y < mCellSize * mRowsNeeded; y += mCellSize) {
            for (float x = mCircleRadius + mStrokeWidth; x < mCellSize * mLastRowCols || (y < mCellSize * (mRowsNeeded - 1) && x <= mCellSize * VIEW_COLS); x += mCellSize) {
                rectF.set(x - mCircleRadius, y - mCircleRadius, x + mCircleRadius, y + mCircleRadius);
                RectF clip = new RectF();
                clip.set(rectF);
                if (counter < mCurrentPracticeCount / mCellValue) {
                    // draw filled circles
                    c.drawOval(rectF, mFillPaint);
                } else if (counter >= (mCurrentPracticeCount / mCellValue) + 1) {
                    // draw empty circles
                    c.clipRect(0, 0, c.getWidth(), c.getHeight(), Region.Op.REPLACE);
                    c.drawOval(rectF, mPaint);
                } else {
                    // draw a partially filled circle
                    c.drawOval(rectF, mPaint);
                    double fillLevel = (mCurrentPracticeCount % mCellValue) / (float) mCellValue;
//                    scale it to be less linear
//                    fillLevel = ((fillLevel - 0.5f)*Math.abs(fillLevel-0.5f)*2) + 0.5f;
//                    fillLevel = (fillLevel - 0.5)*Math.sqrt(Math.abs(fillLevel-0.5))*Math.sqrt(2) + 0.5;
//                    this nicely approximates filling a cyllindrical tank: x^(0.768+x^2)
//                    fillLevel = Math.pow(fillLevel, 0.768+Math.pow(fillLevel, 2));
//                    and this is the formula for a spherical tank: (3-2x)(2x)^2/4
//                    fillLevel = (3-2*fillLevel)*Math.pow(2*fillLevel, 2)/4;
                    fillLevel = Math.pow(fillLevel, 0.57 + fillLevel);
                    clip.bottom += 2 * mCircleRadius - fillLevel * 2 * mCircleRadius;
                    clip.top += 2 * mCircleRadius - fillLevel * 2 * mCircleRadius;
                    c.clipRect(clip);
                    c.drawOval(rectF, mFillPaint);
                }
                counter++;
            }
        }
    }
}
