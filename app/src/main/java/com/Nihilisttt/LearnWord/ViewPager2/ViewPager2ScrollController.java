package com.Nihilisttt.LearnWord.ViewPager2;

import android.view.MotionEvent;

import androidx.viewpager2.widget.ViewPager2;

/**
 * ViewPager2 滚动控制器
 * 功能：通过手势识别控制 ViewPager2 的滚动行为，支持垂直或水平方向控制
 * <p>
 * 使用示例：
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * setContentView(R.layout.activity_learn_page);
 * int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
 * viewPager2 = findViewById(R.id.learn_page_vp2_container);
 * <p>
 * // 初始化控制器（选择方向：VERTICAL 或 HORIZONTAL）
 * scrollController = new ViewPager2ScrollController(
 * viewPager2,
 * touchSlop,
 * 3.0f,
 * ViewPager2ScrollController.ScrollDirection.VERTICAL
 * );
 * setupViewPager();
 * }
 * @Override public boolean dispatchTouchEvent(MotionEvent ev) {
 * boolean handled = super.dispatchTouchEvent(ev);
 * scrollController.handleTouchEvent(ev); // 传递触摸事件
 * return handled;
 * }
 */
public class ViewPager2ScrollController {

    // 滚动方向枚举
    public enum ScrollDirection {
        VERTICAL,   // 垂直方向
        HORIZONTAL  // 水平方向
    }

    // 关联的ViewPager2实例
    private final ViewPager2 viewPager2;
    // 系统识别滑动的最小距离阈值
    private final int touchSlop;
    // 主/次方向滑动距离比例阈值
    private final float scrollRatioThreshold;
    // 当前设置的滚动方向
    private final ScrollDirection scrollDirection;

    // 触摸起始坐标
    private float startX, startY;
    // 标记是否已触发滚动
    private boolean isScrollTriggered;
    // 标记是否应处理滚动事件
    private boolean shouldHandleScroll;

    /**
     * 构造函数
     *
     * @param viewPager2           要控制的ViewPager2实例
     * @param touchSlop            系统触摸阈值（通过ViewConfiguration获取）
     * @param scrollRatioThreshold 触发滑动的距离比例阈值
     * @param direction            控制的滚动方向（VERTICAL/HORIZONTAL）
     */
    public ViewPager2ScrollController(ViewPager2 viewPager2, int touchSlop,
            float scrollRatioThreshold, ScrollDirection direction) {
        this.viewPager2 = viewPager2;
        this.touchSlop = touchSlop;
        this.scrollRatioThreshold = scrollRatioThreshold;
        this.scrollDirection = direction;
        // 初始状态：不处理滚动
        setState(false);
    }

    /**
     * 处理触摸事件
     *
     * @param ev 触摸事件对象
     */
    public void handleTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 记录触摸起始点
                startX = ev.getX();
                startY = ev.getY();
                // 重置状态
                setState(false);
                break;

            case MotionEvent.ACTION_MOVE:
                // 仅当未处理滚动且未触发时检测
                if (!shouldHandleScroll && !isScrollTriggered) {
                    float dx = Math.abs(ev.getX() - startX); // X轴偏移量
                    float dy = Math.abs(ev.getY() - startY); // Y轴偏移量

                    boolean shouldTrigger;

                    if (scrollDirection == ScrollDirection.VERTICAL) {
                        // 垂直方向控制：当垂直滑动距离 > 水平滑动距离×阈值
                        shouldTrigger = (dy > scrollRatioThreshold * dx) && (dy > touchSlop);
                    } else {
                        // 水平方向控制：当水平滑动距离 > 垂直滑动距离×阈值
                        shouldTrigger = (dx > scrollRatioThreshold * dy) && (dx > touchSlop);
                    }

                    if (shouldTrigger) {
                        setState(true); // 标记为已触发状态
                        viewPager2.setUserInputEnabled(true); // 启用ViewPager2滚动
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 触摸结束：重置状态并禁用滚动
                setState(false);
                viewPager2.setUserInputEnabled(false);
                break;
        }
    }

    /**
     * 更新控制器状态
     *
     * @param state true: 处理滚动, false: 不处理滚动
     */
    private void setState(boolean state) {
        shouldHandleScroll = state;
        isScrollTriggered = state;
    }

    /**
     * 获取当前设置的滚动方向
     *
     * @return 当前滚动方向
     */
    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }
}
