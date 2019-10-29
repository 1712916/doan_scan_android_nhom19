package com.example.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class DrawingView extends View {


    public int width;
    public int height;
    private int mcolor = Color.BLACK;
    Context context;
    public Paint mPaint = initPaint(mcolor); //doi tuong de ve
    private Path mPath;
    private Bitmap mBitmap; //luu giu be mat duoc ve len
    private Canvas mCanvas;

    //  private Paint   mBitmapPaint;

    private Paint circlePaint;//chỉ thị vị trí khi chạm vào màn hình "cái hình tròn"
    private Path circlePath;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Paint> paints = new ArrayList<Paint>();

    public DrawingView(Context c) {
        super(c);
        context = c;
        mPath = new Path();
        //mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //Khoi tao cac gia tri ban dau cua but ve

        //******///

        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);//chỉnh màu của cái "hình tròn" khi chạm tay vào màn hình
        circlePaint.setStyle(Paint.Style.STROKE);//Tô màu cho cái hình tròn bên trên.
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(9f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);//vẽ xong nó sẽ có một độ dời(0,0)
        //canvas.drawCircle(50,50,50,mBitmapPaint); //new add
        //canvas.drawBitmap( mBitmap, 0, 0, mPaint);

        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
           /* for (Path p : paths){
                canvas.drawPath(p, mPaint);
            }*/
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

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {

        mPath.lineTo(mX, mY); //nối hai điểm
        circlePath.reset();
        // commit the path to our offscreen //ghi nhận những gì đã vẽ, có nghĩa khi vẽ xong nó không có mất đi
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw

        paths.add(mPath);
        paints.add(mPaint);
        mPath = new Path();
        //mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
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

    public Paint initPaint(int color) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        return mPaint;
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, mcolor, new OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                mcolor = color;
                mPaint = initPaint(mcolor);
            }
        });
        colorPicker.show();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }
}

