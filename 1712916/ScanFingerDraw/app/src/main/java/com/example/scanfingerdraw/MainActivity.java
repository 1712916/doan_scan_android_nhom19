package com.example.scanfingerdraw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends Activity {

    private Bundle savedInstanceState;


    DrawingView dv ; //Nắm giữ các thuộc tính khi vẽ ở nơi được "chọn"
    private Paint mPaint;
    Button btnChooseColor;
    int colorOfPen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        
		dv = new DrawingView(this);
		setContentView(R.layout.activity_main);
        LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.view_drawing_pad); //xác định vị trí được "chọn" để vẽ
        mDrawingPad.addView(dv);
		colorOfPen= ContextCompat.getColor(MainActivity.this,R.color.colorPrimaryDark);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(colorOfPen);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);

        btnChooseColor=(Button)findViewById(R.id.btnChooseColor);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

    }

    public void openColorPicker(){
        AmbilWarnaDialog colorPicker=new AmbilWarnaDialog(this, colorOfPen, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colorOfPen=color;
                mPaint.setColor(colorOfPen);
            }
        });
        colorPicker.show();
    }
    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;//chỉ thị vị trí khi chạm vào màn hình "cái hình tròn"
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
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

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);//vẽ xong nó sẽ có một độ dời(0,0)
            canvas.drawCircle(50,50,50,mBitmapPaint); //new add
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
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
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
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
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
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

    }
}
