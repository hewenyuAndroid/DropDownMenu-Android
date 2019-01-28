package com.hwy.dropdownmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hwy.dropdownmenu.adapter.BaseDropDownAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: hewenyu
 * 日期: 2018/11/28 9:54
 * 说明: 仿美团下拉选择控件
 */
public class DropDownMenu extends LinearLayout implements View.OnClickListener {

    protected Context mContext;

    protected BaseDropDownAdapter mAdapter;

    /**
     * 指示器的容器
     */
    protected LinearLayout mMenuContainer;

    /**
     * 详情的最外部的布局
     */
    protected FrameLayout mDetailWrapper;

    /**
     * 用于显示阴影的控件
     */
    protected View mMaskView;

    /**
     * 自定义内容详情的容器
     */
    protected FrameLayout mDetailContainer;

    /**
     * 缓存每个页面的高度
     */
    protected List<Integer> mDetailHeights;

    /**
     * 当前选中的位置
     */
    protected int mCurrentPosition = -1;

    /**
     * 默认遮罩阴影的颜色
     */
    protected int mMaskColor = 0x88888888;

    /**
     * 默认动画的时长
     */
    protected int mAnimatorDuration = 200;

    /**
     * 打开详情页动画的插值器
     */
    protected Interpolator mOpenInterpolator;

    /**
     * 关闭详情页动画的插值器
     */
    protected Interpolator mCloseInterpolator;

    /**
     * 切换详情页动画的插值器
     */
    protected Interpolator mUpdateInterpolator;

    /**
     * 用于判断动画是否正在执行
     */
    protected boolean mIsAnimatorExecute;

    /**
     * 详情页面允许的最大高度和容器的高度的比例
     */
    protected float mDetailHeightMaxRatio = 0.65f;

    /**
     * 是否是初始化适配器
     */
    protected boolean mIsInitAdapter = false;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        if (array.hasValue(R.styleable.DropDownMenu_maskViewColor)) {
            mMaskColor = array.getColor(R.styleable.DropDownMenu_maskViewColor, mMaskColor);
        }
        if (array.hasValue(R.styleable.DropDownMenu_detailHeightMaxRatio)) {
            mDetailHeightMaxRatio = array.getFloat(R.styleable.DropDownMenu_detailHeightMaxRatio, mDetailHeightMaxRatio);
        }
        array.recycle();

        mContext = context;
        mDetailHeights = new ArrayList<>();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 清空所有的孩子
        removeAllViews();
        // 垂直布局
        setOrientation(VERTICAL);
        initLayout();
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        mMenuContainer = new LinearLayout(mContext);
        mMenuContainer.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        addView(mMenuContainer);

        mDetailWrapper = new FrameLayout(mContext);
        mDetailWrapper.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        addView(mDetailWrapper);

        mMaskView = new View(mContext);
        mMaskView.setAlpha(0f);
        mMaskView.setBackgroundColor(mMaskColor);
        mMaskView.setOnClickListener(this);
        mDetailWrapper.addView(mMaskView);

        mDetailContainer = new FrameLayout(mContext);
        mDetailContainer.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        mDetailContainer.setClickable(true);
        mDetailWrapper.addView(mDetailContainer);
        mDetailWrapper.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int maxHeight = (int) ((getMeasuredHeight() - mMenuContainer.getMeasuredHeight()) * mDetailHeightMaxRatio);
        int count = mDetailContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mDetailContainer.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = params.height > maxHeight ? maxHeight : params.height;
            view.setLayoutParams(params);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDetailHeights.clear();
        int count = mDetailContainer.getChildCount();
        int maxHeight = (int) ((getMeasuredHeight() - mMenuContainer.getMeasuredHeight()) * mDetailHeightMaxRatio);
        for (int i = 0; i < count; i++) {
            // 获取每个详情页面的高度
            View view = mDetailContainer.getChildAt(i);
            mDetailHeights.add(view.getMeasuredHeight() > maxHeight ? maxHeight : view.getMeasuredHeight());
            if (mIsInitAdapter) {
                view.setTranslationY(-mDetailHeights.get(i));
                view.setVisibility(GONE);
                if (i == count - 1) {
                    mDetailWrapper.setVisibility(GONE);
                }
            }
        }
        mIsInitAdapter = false;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(BaseDropDownAdapter adapter) {
        if (adapter == null) {
            throw new RuntimeException("BaseIndicatorDetailAdapter is null object");
        }

        mAdapter = adapter;
        mIsInitAdapter = true;
        reset();
        int count = mAdapter.getCount();
        // 这里解决父容器为GONE时无法获取子View的宽高的问题
        mDetailWrapper.setVisibility(VISIBLE);
        for (int i = 0; i < count; i++) {
            View menuView = mAdapter.getMenuView(i, mMenuContainer);
            mMenuContainer.addView(menuView);
            LinearLayout.LayoutParams menuParams = (LayoutParams) menuView.getLayoutParams();
            menuParams.weight = 1;
            menuView.setLayoutParams(menuParams);
            setMenuClick(menuView, i);

            View detailView = mAdapter.getDetailView(i, mDetailContainer);
            mDetailContainer.addView(detailView);
        }

    }

    /**
     * 设置菜单的点击事件
     *
     * @param menuView
     * @param position
     */
    protected void setMenuClick(final View menuView, final int position) {
        menuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition == -1) {
                    // 打开对应的详情页面
                    openDetail(menuView, position);
                } else {
                    if (mCurrentPosition == position) {
                        // 关闭详情页面
                        closeDetail();
                    } else {
                        // 切换详情页面
                        updateDetail(position);
                    }
                }
            }
        });
    }

    /**
     * 切换详情页面
     *
     * @param position
     */
    protected void updateDetail(final int position) {
        if (mIsAnimatorExecute || mCurrentPosition == -1) {
            return;
        }
        // 获取原来打开的页面
        final View lastDetailView = mDetailContainer.getChildAt(mCurrentPosition);
        lastDetailView.setVisibility(GONE);
        // 将原来的页面移动到关闭时的状态
        ViewCompat.setTranslationY(lastDetailView, -mDetailHeights.get(mCurrentPosition));
        mAdapter.onMenuClose(mMenuContainer.getChildAt(mCurrentPosition));

        // 获取目标页面
        final View detailView = mDetailContainer.getChildAt(position);
        // 将目标页面移动到完全打开的位置
        ViewCompat.setTranslationY(detailView, 0);
        detailView.setVisibility(VISIBLE);

        // 执行上一个页面的高度到这个页面的高度的动画
        ValueAnimator animator = ValueAnimator.ofFloat(mDetailHeights.get(mCurrentPosition), mDetailHeights.get(position));
        animator.setDuration(mAnimatorDuration);
        if (mUpdateInterpolator != null) {
            animator.setInterpolator(mUpdateInterpolator);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempValue = (float) animation.getAnimatedValue();
                // 更新容器的高度
                updateDetailContainerHeight((int) (tempValue));

                // 动态的设置详情页面的高度
                ViewGroup.LayoutParams params = detailView.getLayoutParams();
                params.height = (int) tempValue;
                detailView.setLayoutParams(params);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimatorExecute = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimatorExecute = true;
                mCurrentPosition = position;
                updateDetailContainerHeight(position);
                mAdapter.onMenuOpen(mMenuContainer.getChildAt(position));
            }
        });

        animator.start();
    }

    /**
     * 关闭详情页面
     */
    public void closeDetail() {
        if (mIsAnimatorExecute || mCurrentPosition == -1) {
            return;
        }
        // 显示当前的DetailView
        final View detailView = mDetailContainer.getChildAt(mCurrentPosition);
        final float itemHeight = mDetailHeights.get(mCurrentPosition);

        // 配置关闭的属性动画
        ValueAnimator animator = ValueAnimator.ofFloat(0, -itemHeight);
        animator.setDuration(mAnimatorDuration);
        if (mCloseInterpolator != null) {
            animator.setInterpolator(mCloseInterpolator);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempValue = (float) animation.getAnimatedValue();
                // 设置位移动画
                ViewCompat.setTranslationY(detailView, tempValue);

                // 设置遮罩层的透明度
                float alpha = 1 - Math.abs(tempValue / itemHeight);
                if (alpha < 0) {
                    alpha = 0;
                }
                ViewCompat.setAlpha(mMaskView, alpha);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 隐藏遮罩层防止原来页面的点击事件
                mDetailWrapper.setVisibility(GONE);
                mIsAnimatorExecute = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimatorExecute = true;
                // 执行 Adapter 的回调函数
                mAdapter.onMenuClose(mMenuContainer.getChildAt(mCurrentPosition));
                mCurrentPosition = -1;
            }
        });
        animator.start();
    }

    /**
     * 打开详情页面
     *
     * @param menuView
     * @param position
     */
    public void openDetail(final View menuView, final int position) {
        if (mIsAnimatorExecute) {
            return;
        }
        // 显示遮罩层
        mDetailWrapper.setVisibility(VISIBLE);
        // 获取当前需要显示的 DetailView
        final View detailView = mDetailContainer.getChildAt(position);
        // 显示当前需要显示的的 DetailView
        detailView.setVisibility(VISIBLE);
        // 获取该页面的高度
        final float itemHeight = mDetailHeights.get(position);

        // 配置打开的属性动画
        ValueAnimator animator = ValueAnimator.ofFloat(-itemHeight, 0);
        animator.setDuration(mAnimatorDuration);
        if (mOpenInterpolator != null) {
            animator.setInterpolator(mOpenInterpolator);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempValue = (float) animation.getAnimatedValue();
                // 动态更新内容容器的高度
                updateDetailContainerHeight((int) (itemHeight + tempValue));
                // 设置位移动画
                ViewCompat.setTranslationY(detailView, tempValue);

                // 设置遮罩层的透明度
                float alpha = 1 - Math.abs(tempValue / itemHeight);
                if (alpha > 1) {
                    return;
                }
                ViewCompat.setAlpha(mMaskView, alpha);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimatorExecute = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mCurrentPosition = position;
                mIsAnimatorExecute = true;
                // 回调Adapter对应的方法
                mAdapter.onMenuOpen(menuView);
            }
        });
        animator.start();
    }

    /**
     * 动态更新内容详情容器的高度,打开详情/更新详情页面的时候使用
     *
     * @param height
     */
    private void updateDetailContainerHeight(int height) {
        ViewGroup.LayoutParams params = mDetailContainer.getLayoutParams();
        params.height = height;
        mDetailContainer.setLayoutParams(params);
    }

    private void reset() {
        mDetailHeights.clear();
        mDetailContainer.removeAllViews();
        mMenuContainer.removeAllViews();
    }


    @Override
    public void onClick(View v) {
        // 触摸阴影的位置关闭页面
        closeDetail();
    }

    // region --------- get/set ---------

    public void setAnimatorDuration(int duration) {
        this.mAnimatorDuration = duration;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        this.mOpenInterpolator = interpolator;
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        this.mCloseInterpolator = interpolator;
    }

    public void setUpdateInterpolator(Interpolator interpolator) {
        this.mUpdateInterpolator = interpolator;
    }

    public void setOpenAndCloseInterpolator(Interpolator interpolator) {
        setOpenInterpolator(interpolator);
        setCloseInterpolator(interpolator);
    }

    public void setDetailHeightMaxRatio(float maxRatio) {
        if (maxRatio < 0 || maxRatio > 1) {
            throw new RuntimeException("The maxRatio must be 0.0F <= a <= 1.0F");
        }
        this.mDetailHeightMaxRatio = maxRatio;
        requestLayout();
    }

    /**
     * 获取详情页面
     *
     * @param position
     * @return
     */
    public View getDetilView(int position) {
        return mDetailContainer.getChildAt(position);
    }

    /**
     * 获取所有的详情页面
     *
     * @return
     */
    public List<View> getDetailViews() {
        List<View> list = new ArrayList<>();
        for (int i = 0; i < mDetailContainer.getChildCount(); i++) {
            list.add(mDetailContainer.getChildAt(i));
        }
        return list;
    }

    /**
     * 获取按钮的容器
     *
     * @return
     */
    public ViewGroup getMenuContainer() {
        return this.mMenuContainer;
    }

    /**
     * 获取详情页面的容器
     *
     * @return
     */
    public ViewGroup getDetailContainer() {
        return this.mDetailContainer;
    }

    /**
     * 获取适配器
     *
     * @return
     */
    public BaseDropDownAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 详情页面是否打开
     *
     * @return
     */
    public boolean isOpen() {
        return mCurrentPosition != -1;
    }

    /**
     * 设置遮罩的颜色
     *
     * @param color
     */
    public void setMaskColor(int color) {
        this.mMaskColor = color;
        mMaskView.setBackgroundColor(mMaskColor);
    }

    // endregion -------------------------


}
