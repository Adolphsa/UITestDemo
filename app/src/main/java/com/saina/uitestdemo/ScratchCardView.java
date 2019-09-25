package com.saina.uitestdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author lucas
 * @package com.saina.uitestdemo
 * @fileName ScratchCardView
 * @date on 2019/9/18 13:56
 * @describe 刮刮卡
 */
public class ScratchCardView extends View {

    private Paint mPaint;
    private Bitmap mDstBmp, mSrcBmp, mTxtBmp;
    private Path mPath;

    private float mEventX, mEventY;

    public ScratchCardView(Context context) {
        this(context, null);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(80);

        //禁用硬件加速，某些API不支持硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //初始化图像对象
        mTxtBmp = BitmapFactory.decodeResource(getResources(), R.drawable.result);
        mSrcBmp = BitmapFactory.decodeResource(getResources(), R.drawable.eraser);
        mDstBmp = Bitmap.createBitmap(mSrcBmp.getWidth(), mSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);

        //路径（贝赛尔曲线）
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制刮奖结果
        canvas.drawBitmap(mTxtBmp, 0, 0, mPaint);

        //使用离屏绘制
        int layerID = canvas.saveLayer(0,0,getWidth(),getHeight(),mPaint);

        //先将路径绘制到 bitmap上
        Canvas dstCanvas = new Canvas(mDstBmp);
        dstCanvas.drawPath(mPath, mPaint);

        //绘制目标图像
        canvas.drawBitmap(mDstBmp,0,0,mPaint);
        //设置模式为SRC_OUT,擦橡皮区域为交集区域需要清掉像素
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //绘制源图像
        canvas.drawBitmap(mSrcBmp,0,0,mPaint);

        mPaint.setXfermode(null);

        canvas.restoreToCount(layerID);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mEventX = event.getX();
                mEventY = event.getY();
                mPath.moveTo(mEventX,mEventY);
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = (event.getX() - mEventX) / 2 + mEventX;
                float endY = (event.getY() - mEventY) / 2 + mEventY;
                //画二阶贝塞尔曲线
                mPath.quadTo(mEventX, mEventY, endX, endY);
                mEventX = event.getX();
                mEventY = event.getY();
                break;
        }
        invalidate();
        return true;
    }
}
