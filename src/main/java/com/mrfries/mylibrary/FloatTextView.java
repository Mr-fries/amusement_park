package com.mrfries.mylibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * author:  Mr.fries
 * update:  2022/05/19
 * version: 1.0.0
 */

public class FloatTextView extends View {
    private final String TAG = "FunTextView";

    /**
     * 文字排列方向
     */
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    /**
     * 文字定位
     */
    private static final int AXIS_SPECIFIED = 0x0001;
    private static final int AXIS_BEFORE = 0x0002;
    private static final int AXIS_AFTER = 0x0004;
    private static final int X_SHIFT = 0;
    private static final int Y_SHIFT = 4;

    public static final int POSITION_LEFT = (AXIS_BEFORE|AXIS_SPECIFIED) << X_SHIFT;
    public static final int POSITION_RIGHT = (AXIS_AFTER|AXIS_SPECIFIED) << X_SHIFT;
    public static final int POSITION_TOP = (AXIS_BEFORE|AXIS_SPECIFIED) << Y_SHIFT;
    public static final int POSITION_BOTTOM = (AXIS_AFTER|AXIS_SPECIFIED) << Y_SHIFT;

    public static final int POSITION_CENTER_HORIZONTAL = AXIS_SPECIFIED << X_SHIFT;
    public static final int POSITION_CENTER_VERTICAL = AXIS_SPECIFIED << Y_SHIFT;

    public static final int CENTER = POSITION_CENTER_HORIZONTAL|POSITION_CENTER_VERTICAL;

    /**
     * 背景色
     */
    private int bgColor = Color.parseColor("#00FFFFFF");

    /**
     * 文本内容（默认 = ""）
     */
    private String text = "";

    /**
     * 字体大小（默认 = 16）
     */
    private float textSize = 16f;

    /**
     * 文字颜色
     */
    private int textColor = Color.BLACK;

    /**
     * 文本画笔
     */
    private TextPaint mTextPaint = null;

    /**
     * 文本方向（默认 = HORIZONTAL）
     */
    private int textDirection = HORIZONTAL;

    /**
     * 文字定位（默认 = 0）
     */
    private int textPosition = 0;

    /**
     * 文字定位 x轴偏移量
     */
    private int offsetX = 0;

    /**
     * 文字定位 y轴偏移量
     */
    private int offsetY = 0;

    /**
     * 内边距定位 默认都为0
     */
    private int leftPadding = 0;
    private int topPadding = 0;
    private int rightPadding = 0;
    private int bottomPadding = 0;

    /**
     * 是否滚动
     */
    private boolean canScroll = false;

    /**
     * 滚动文本前后间隔（默认 = 10）
     */
    private float textSpacing = 10;

    /**
     * 滚动速度（每秒移动距离）（默认 = 20）
     */
    private float scrollSpeed = 20;

    /**
     * 自定义动画事件
     */
    private Runnable animationRunnable = null;

    /**
     * x轴滚动偏移
     * 正值为右，负值为左
     */
    private float scrollX = 0f;

    /**
     * y轴滚动偏移
     * 正值为下，负值为上
     */
    private float scrollY = 0f;

    /**
     * 滚动方向（默认 = horizontal）
     * horizontal=0 vertical=1
     */
    private int scrollDirection = HORIZONTAL;

    /**
     * 滚动进度（%）
     * min = 0 max = 100
     */
    private float progress = 0f;

    /**
     * 帧速率，默认 60 帧/秒
     */
    private final int FPS = 60;

    public FloatTextView(Context context) {
        super(context);
    }

    public FloatTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public FunTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initAttrs(attrs);
//    }

    /**
     * 初始化Attrs，通过xml文件静态属性引入
     */
    private void initAttrs(AttributeSet attrs) {
        mTextPaint = new TextPaint();
        mTextPaint.setSubpixelText(true);
        if (attrs != null) {
            // 获取AttributeSet中所有的XML属性的数量
            int count = attrs.getAttributeCount();
            Log.i("date511", "count:"+count);
            // 遍历AttributeSet中的XML属性
            for (int i = 0; i < count; i++){
                // 获取attr的资源ID
                int attrResId = attrs.getAttributeNameResource(i);
                if (attrResId == R.attr.text) {// 文本内容 属性
                    // 如果读取不到，那就用 空 作为默认值
//                        text = attrs.getAttributeValue(i);
                    this.text = attrs.getAttributeValue(i);
                } else if (attrResId == R.attr.textColor) {// 文本颜色 属性
                    // 如果读取不到，那就用 #333333 作为默认值
                    String c = attrs.getAttributeValue(i);
                    textColor = c.equals("") ? Color.BLACK : Color.parseColor(c);
                    mTextPaint.setColor(textColor);
                } else if (attrResId == R.attr.textSize) {// 文本字体大小 属性
                    // 如果读取不到，那就用 16(sp) 作为默认值
                    textSize = attrs.getAttributeFloatValue(i, 16f);
                    mTextPaint.setTextSize(attrs.getAttributeFloatValue(i, 16f));
                } else if (attrResId == R.attr.textPosition) {
                    setTextPosition(attrs.getAttributeIntValue(i, 0));
                } else if (attrResId == R.attr.canScroll) {// 是否滚动属性
                    canScroll = attrs.getAttributeBooleanValue(i, false);
                } else if (attrResId == R.attr.textSpacing) {// 滚动文本间隔
                    textSpacing = attrs.getAttributeFloatValue(i, 10);
                } else if (attrResId == R.attr.scrollSpeed) {// 滚动速度 属性
                    // 如果读取不到对应的值，那么就用20作为默认值
                    scrollSpeed = attrs.getAttributeFloatValue(i, 20);
                } else if (attrResId == R.attr.scrollDirection) {// 滚动方向 属性
                    // 如果读取不到对应的方向值，那么就用水平方向作为默认滚动方向
                    scrollDirection = attrs.getAttributeIntValue(i, HORIZONTAL);
                } else if (attrResId == R.attr.leftPadding) {// 内边距-左
                    leftPadding = attrs.getAttributeIntValue(i, 0);
                } else if (attrResId == R.attr.topPadding) {// 内边距-上
                    topPadding = attrs.getAttributeIntValue(i, 0);
                } else if (attrResId == R.attr.rightPadding) {// 内边距-右
                    rightPadding = attrs.getAttributeIntValue(i, 0);
                } else if (attrResId == R.attr.bottomPadding) {// 内边距-下
                    bottomPadding = attrs.getAttributeIntValue(i, 0);
                } else if (attrResId == R.attr.bgColor) {// 背景颜色
                    String b = attrs.getAttributeValue(i);
                    bgColor = b.equals("") ? Color.parseColor("#00FFFFFF") : Color.parseColor(b);
                }
            }
        }
    }

    /**
     * 初始化滚动动画Runnable
     */
    private void initRunnable() {
        // 清空之前的
        removeCallbacks(animationRunnable);

        // 重置滚动
        scrollX = 0;
        scrollY = 0;

        // 判断是否允许滚动
        if (!canScroll) {
            animationRunnable = () -> { };
            return;
        }

        // 判断是否满足滚动条件（文字长度超出边界）
        if (scrollDirection==HORIZONTAL) {
            if (!isOutOfBound(HORIZONTAL)) {
                animationRunnable = () -> { };
                return;
            }
        } else if (scrollDirection==VERTICAL) {
            if (!isOutOfBound(VERTICAL)) {
                animationRunnable = () -> { };
                return;
            }
        } else {
            animationRunnable = () -> { };
            Log.e(TAG, "非法的滚动方向！初始化滚动失败！");
            return;
        }

        // 初始化滚动事件
        if (scrollDirection==HORIZONTAL) {
            // 水平滚动
            animationRunnable = () -> {
                scrollX += scrollSpeed/FPS; // 每帧走过的距离
                if (scrollSpeed > 0) {
                    if (scrollX >= getTextBound().right-getTextBound().left+textSpacing){
                        scrollX = 0;
                    }
                } else {
                    if (-scrollX >= getTextBound().right-getTextBound().left+textSpacing) {
                        scrollX = 0;
                    }
                }
                invalidate();
                postDelayed(animationRunnable, (1000/FPS));
            };
        } else if (scrollDirection==VERTICAL) {
            // 垂直滚动
            animationRunnable = () -> {
                scrollY += scrollSpeed/FPS;
                if (scrollY >= getTextBound().bottom-getTextBound().top+textSpacing){
                    scrollY = 0;
                }
                invalidate();
                postDelayed(animationRunnable, (1000/FPS));
            };
        }
    }

    /**
     * 绘制总调度
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        this.setBackgroundColor(bgColor);
        setTextPosition(textPosition);
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initRunnable();
        post(animationRunnable);
    }

    /**
     * 绘制内容
     * 画两段，模拟滚动的首尾相连
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        Rect rect = new Rect(leftPadding, topPadding, canvas.getWidth()-rightPadding, canvas.getHeight()-bottomPadding);
//        canvas.drawRect(rect, mTextPaint);
//        canvas.clipRect(rect);
//        int[] colors = {Color.parseColor("#00000000"),
//                Color.parseColor("#00000000"),
//                textColor,
//                textColor,
//                Color.parseColor("#00000000"),
//                Color.parseColor("#00000000")};
        canvas.translate(leftPadding, topPadding);
        canvas.translate(offsetX, offsetY);

        if (canScroll && !(scrollSpeed==0) && isOutOfBound()) {
            if (scrollDirection==HORIZONTAL && isOutOfBound(HORIZONTAL)) {
                if (scrollSpeed > 0) { // 横向正向滚动时
//                    float[] positions = {0, leftPadding-scrollX>0?leftPadding-scrollX:0, leftPadding-scrollX>0?leftPadding-scrollX+15:15, 100, 100, 100};
//                    mTextPaint.setShader(new LinearGradient(0, 0, 1f*canvas.getWidth(), 0f, colors, positions, Shader.TileMode.CLAMP));
                    canvas.drawText(text, scrollX, scrollY, mTextPaint);
                    canvas.drawText(text, scrollX - textSpacing - (getTextBound().right-getTextBound().left), scrollY, mTextPaint);
                } else { // 横向负向滚动时
//                    float[] positions = {0, 0, 0, 0, 0, 0};
//                    mTextPaint.setShader(new LinearGradient(0, 0, 1f*canvas.getWidth(), 0f, colors, positions, Shader.TileMode.CLAMP));
                    canvas.drawText(text, scrollX, scrollY, mTextPaint);
                    canvas.drawText(text, scrollX + textSpacing + (getTextBound().right-getTextBound().left), scrollY, mTextPaint);
                }
            } else if (scrollDirection==VERTICAL && isOutOfBound(VERTICAL)) {

            }
        } else {
            canvas.drawText(text, scrollX, scrollY, mTextPaint);
        }
    }

    /**
     * 绘制子控件
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 绘制前景遮罩
     * @param canvas
     */
    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
    }

    /**
     * 获取自行边界
     * @return the bound rect
     */
    private Rect getTextBound() {
        Rect boundRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), boundRect);
        return boundRect;
    }

    /**
     * 测量文本是否超出边界
     * @return if any side of the text out of bound
     */
    private boolean isOutOfBound() {
        Rect boundRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), boundRect);
        int tWidth = boundRect.right-boundRect.left;
        int tHeight = boundRect.bottom-boundRect.top;
        int vWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
        int vHeight = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();
        if (tWidth>vWidth || tHeight>vHeight) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 测量文本是否超出边界
     * @param mode horizontal / vertical
     * @return
     */
    private boolean isOutOfBound(int mode) {
        Rect boundRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), boundRect);
//        mTextPaint.set
        if (mode==HORIZONTAL) {
            int tWidth = boundRect.right-boundRect.left;
            int vWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
            if (tWidth > vWidth) {
                return true;
            } else {
                return false;
            }
        } else if (mode==VERTICAL) {
            int tHeight = boundRect.bottom-boundRect.top;
            int vHeight = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();
            if (tHeight > vHeight) {
                return true;
            } else {
                return false;
            }
        } else {
            Log.i(TAG, "非法滚动模式！");
            return false;
        }
    }

    /**
     * getters & setters
     */

    public int getTextPosition() {
        return textPosition;
    }
    public void setTextPosition(int position) {
        textPosition = position;
        String p = Integer.toBinaryString(position);
        String pVertical = "0";
        String pHorizontal = "0";
        if (p.length() > 4) {
            pVertical = p.substring(0, p.length()-4);
            pVertical = pVertical+"0000";
            pHorizontal = p.substring(p.length()-4);
        } else {
            pHorizontal = p;
        }


        int pVertical1 = Integer.valueOf(pVertical, 2);
        int pHorizontal1 = Integer.valueOf(pHorizontal, 2);

        Rect boundRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), boundRect);

        int textExactHeight = boundRect.bottom - boundRect.top;

        switch (pHorizontal1) {
            case POSITION_LEFT:
                offsetX = 0;
                break;
            case POSITION_CENTER_HORIZONTAL:
                offsetX = (getMeasuredWidth()-boundRect.right+boundRect.left-5)/2;
                break;
            case POSITION_RIGHT:
                offsetX = getMeasuredWidth()-boundRect.right+boundRect.left-5;
                break;
        }

        switch (pVertical1) {
            case POSITION_TOP:
                offsetY = textExactHeight;
                break;
            case POSITION_CENTER_VERTICAL:
                offsetY = (getMeasuredHeight() + textExactHeight - 5) / 2;
                break;
            case POSITION_BOTTOM:
                offsetY = getMeasuredHeight() - 5;
                break;
        }

        invalidate();
    }


    public String getText(){
        return text;
    }
    public void setText(String text) {
        if (! text.equals(this.text)) {
            this.text = text;
            setTextPosition(textPosition);
            initRunnable();
            post(animationRunnable);
        }
    }


    public int getTextColor() {
        return textColor;
    }
    public void setTextColor(int textColor) {
        Log.i(TAG, "setColor");
        this.textColor = textColor;
        this.mTextPaint.setColor(textColor);
        invalidate();
    }


    public float getScrollSpeed() {
        return scrollSpeed;
    }
    public void setScrollSpeed(float scrollSpeed) {
        this.scrollX = 0;   // 重置滚动进度
        this.scrollSpeed = scrollSpeed;
    }


    public float getTextSize() {
        return textSize;
    }
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(textSize);
//        invalidate();
        initRunnable();
        post(animationRunnable);
    }


    public int getBgColor() {
        return bgColor;
    }
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public int getLeftPadding() {
        return leftPadding;
    }
    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }


    public int getTopPadding() {
        return topPadding;
    }
    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }


    public int getRightPadding() {
        return rightPadding;
    }
    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }


    public int getBottomPadding() {
        return bottomPadding;
    }
    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }


    public boolean isCanScroll() {
        return canScroll;
    }
    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }


    public float getTextSpacing() {
        return textSpacing;
    }
    public void setTextSpacing(float textSpacing) {
        this.textSpacing = textSpacing;
    }


    public float getTextScrollX() {
        return scrollX;
    }
    public void setTextScrollX(float scrollX) {
        this.scrollX = scrollX;
    }

    public float getTextScrollY() {
        return scrollY;
    }
    public void setTextScrollY(float scrollY) {
        this.scrollY = scrollY;
    }

    public int getScrollDirection() {
        return scrollDirection;
    }
    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public int getFPS() {
        return FPS;
    }


    /***********
     * 控制方法 *
     ***********/

    
}
