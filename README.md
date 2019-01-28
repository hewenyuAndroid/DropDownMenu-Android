### DropDownMenu-Android
本项目时参考美团app中的下拉选择的功能的自定义组合控件，采用 Adapter 设计模式方便项目扩展使用。
![效果图1](https://github.com/hewenyuAndroid/DropDownMenu-Android/blob/master/screen/image1.gif?raw=true)
![效果图2](https://github.com/hewenyuAndroid/DropDownMenu-Android/blob/master/screen/image2.gif?raw=true)

GIF图片看上去有点卡顿，可以[点击这里](https://github.com/hewenyuAndroid/DropDownMenu-Android/raw/master/apk/app-debug.apk)下载安装包测试;

[Github地址](https://github.com/hewenyuAndroid/DropDownMenu-Android)

### 引入方式
> compile 'com.hewenyu:DropDownMenu:1.0.1'

### 思路分析
1. 整个控件分为指示器和详情页面两个模块，可以采用LinearLayout为最外层布局;
2. 指示器部分和内容部分每个项目定制的不一样，采用 `Adapter` 设计模式将UI控件和数据集分离；
3. 指示器部分可以是横向的LinearLayout，每个Item均分宽度；
4. 内容部分包含内容详情`FrameLayout`和阴影`View`部分,可以由一个`FrameLayout`包裹，如果打开时还需要将触摸事件拦截掉；
5. 内容部分包含三个功能：显示/隐藏/切换三个ValueAnimator;

### 使用方式
1. 按照如下代码放置布局文件:
```Java
<com.hwy.dropdownmenu.DropDownMenu
    android:id="@+id/drop_down_menu"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
2. 完成适配器类，这里提供了 `BaseDropDownAdapter` 和 `SimpleDropDownAdapter` 两个类，第二个类实际上是采用泛型的方式对 `BaseDropDownAdapter` 了的简单封装,这里我们需要完成几个主要的方法即可，具体参考 [DropMenuAdapter](https://github.com/hewenyuAndroid/DropDownMenu-Android/blob/master/app/src/main/java/com/hwy/dropdownmenu_android/DropMenuAdapter.java) 类；
```Java
/**
 * 设置菜单对象
 *
 * @param position
 * @param parent
 * @return
 */
public abstract View getMenuView(int position, ViewGroup parent);

/**
 * 设置内容对象
 *
 * @param position
 * @param parent
 * @return
 */
public abstract View getDetailView(int position, ViewGroup parent);

/**
 * 对应的页面被打开
 *
 * @param menuView
 */
public abstract void onMenuOpen(View menuView);

/**
 * 对应的位置被关闭
 *
 * @param menuView
 */
public abstract void onMenuClose(View menuView);
```

### 自定义属性
```XML
<!-- 遮罩层的颜色 -->
<attr name="maskViewColor" format="color" />
<!-- 内容详情允许的最大高度与内容容器高度的比例 -->
<attr name="detailHeightMaxRatio" format="float" />
```