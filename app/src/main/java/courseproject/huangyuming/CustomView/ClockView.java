package courseproject.huangyuming.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import courseproject.huangyuming.wordsdividedreminder.R;

/**
 * Created by huangyuming on 17-1-3.
 */

public class ClockView extends View {
    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private int width;
    private int height;
    private int radius;

    private int hourPointerLength;
    private int minutePointerLength;

    private int hour = 3;
    private int minute = 50;
//    private boolean am = true;

    private Paint boardPaint = new Paint();
    private Paint pointerPaint = new Paint();

    private void init() {
        boardPaint.setColor(0xFFFFFFFF);// 设置颜色
        boardPaint.setAntiAlias(true);
        boardPaint.setStrokeWidth(4);
        pointerPaint.setColor(getResources().getColor(R.color.mainColor));// 设置颜色
        pointerPaint.setAntiAlias(true);
        pointerPaint.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean am = true;
        double hourRad;
        double minuteRad;
        if (hour >= 12) {
            am = false;
        }

        if (am) {
            boardPaint.setColor(0xFFFFFFFF);
            pointerPaint.setColor(getResources().getColor(R.color.mainColor));

            hourRad = Math.PI/2-2*Math.PI/12*hour;
            minuteRad = Math.PI/2-2*Math.PI/60*minute;
        }
        else {
            boardPaint.setColor(0xFF000000);
            pointerPaint.setColor(0xFFFFFFFF);

            hourRad = Math.PI/2-2*Math.PI/12*(hour-12);
            minuteRad = Math.PI/2-2*Math.PI/60*minute;
        }

        // 绘制的顺序一定不能调换！！！！！！！！！！！！！！
        canvas.drawCircle(width/2, height/2, radius, boardPaint);// 小圆
        canvas.drawLine(width/2, height/2, (float) ( width/2+hourPointerLength*Math.cos(hourRad) ), (float) ( height/2-hourPointerLength*Math.sin(hourRad) ), pointerPaint);
        canvas.drawLine(width/2, height/2, (float) ( width/2+minutePointerLength*Math.cos(minuteRad) ), (float) ( height/2-minutePointerLength*Math.sin(minuteRad) ), pointerPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        radius = width < height ? width/2 : height/2;

        hourPointerLength = radius/2;
        minutePointerLength = radius*2/3;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }
}
