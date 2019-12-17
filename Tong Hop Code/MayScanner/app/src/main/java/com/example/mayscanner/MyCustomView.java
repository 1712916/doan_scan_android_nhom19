package com.example.mayscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;


@SuppressLint("AppCompatCustomView")
public class MyCustomView extends ImageView {
    private int color=Color.BLACK;
    private float sizePaint=20;
    private Paint mPaint=new Paint();
    private Path mPath=new Path();
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Bitmap altBitmap;
    Context context;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Paint> paints = new ArrayList<Paint>();
    public MyCustomView(Context context) {
        super(context);
        this.context=context;
        init();

    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();

    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        altBitmap = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(altBitmap);
    }

    private Paint init(){
        mPaint=new Paint();
        mPath=new Path();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setColor(color);
        mPaint.setStrokeWidth(sizePaint);
        return mPaint;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(mPath,mPaint);


    }


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

    }

    private void touch_move(float x, float y) {


        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            /*Khi di chuyển tay khi chạm trên màn hình thì:
             * điểm đầu: là lúc chạm tay
             * điểm cuối là lúc nhấc tay ra
             * Hàm lineTo bên dưới nối hai điểm từ điểm cuối với điểm đầu
             * nên hàm quadTo xác định(điểm cuối) trong suốt quá trình tay mình rê trên màn hình*/
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

     //       circlePath.reset();
       //     circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
 /*
*/
        mPath.lineTo(mX, mY); //nối hai điểm
        //circlePath.reset();
        // commit the path to our offscreen //ghi nhận những gì đã vẽ, có nghĩa khi vẽ xong nó không có mất đi
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw

        paths.add(mPath);
        paints.add(mPaint);
        mPath = new Path();
        mPath.reset();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);

                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);

                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();

                    break;
            }


        invalidate();
        return true;
    }

    public void onClickUndo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);
            invalidate();
        } else {

        }
        //toast the user
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        setImageBitmap(this.mBitmap);
    }

    public  Bitmap getNewImage(){

        Bitmap result;

        setDrawingCacheEnabled(true);
        destroyDrawingCache();
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        result=getDrawingCache();

        return result;

    }

    public void reductionStrokeWidth(float a){
        if(sizePaint>5)
        {
            //sizePaint=sizePaint-3;
            sizePaint=a;
            mPaint =  init();
        }
    }
    public void increaseStrokeWidth(float a){
        //if(sizePaint<50)
        {
            //sizePaint=sizePaint+3;
            sizePaint=a*100;
            mPaint =  init();
        }
    }
    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int altcolor) {

                color = altcolor;
                mPaint = init();

            }
        });
        colorPicker.show();
    }


}
