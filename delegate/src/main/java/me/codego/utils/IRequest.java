package me.codego.utils;

import android.os.Bundle;
import android.view.View;

/**
 * Created by mengxn on 2017/6/20.
 */

public interface IRequest {

    /**
     * 附带参数
     * @param key
     * @param value
     * @return
     */
    IRequest with(String key, Object value);

    /**
     * 附带参数
     * @param bundle
     * @return
     */
    IRequest with(Bundle bundle);

    /**
     * 场景转换动画
     * @param enterResId
     * @param exitResId
     * @return
     */
    IRequest transition(int enterResId, int exitResId);

    /**
     * 场景转换动画
     * @param resId
     * @return
     */
    IRequest scene(int resId);

    /**
     * 共享元素动画
     * @param view
     * @param name
     * @return
     */
    IRequest share(View view, String name);

    /**
     * 增加flag标识
     * @param flag
     * @return
     */
    IRequest flag(int flag);

    /**
     * 跳转到指定界面
     * @param action
     */
    void to(String action);

    /**
     * 跳转到指定界面
     * @param cls
     * @return
     */
    void to(Class cls);

    /**
     * 跳转到指定界面
     * @param cls
     * @param requestCode
     * @return
     */
    ActivityResponse to(Class cls, int requestCode);

    /**
     * 是否保留上一界面
     * @param isKeep
     * @return
     */
    IRequest keep(boolean isKeep);

    /**
     * 关闭当前界面
     */
    void finish();
}
