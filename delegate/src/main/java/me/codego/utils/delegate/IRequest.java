package me.codego.utils.delegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author mengxn 2017/6/20
 */

public interface IRequest {

    /**
     * 附带参数
     *
     * @param key   键值
     * @param value 键值
     * @return IRequest
     */
    IRequest with(String key, Object value);

    /**
     * 附带参数
     *
     * @param bundle 附加参数
     * @return IRequest
     */
    IRequest with(Bundle bundle);

    /**
     * 场景转换动画
     *
     * @param enterResId 进入动画
     * @param exitResId  退出动画
     * @return IRequest
     */
    IRequest transition(int enterResId, int exitResId);

    /**
     * 场景转换动画
     *
     * @param resId 场景动画
     * @return IRequest
     */
    IRequest scene(int resId);

    /**
     * 共享元素动画
     *
     * @param view 共享 View
     * @param name 共享名字
     * @return IRequest
     */
    IRequest share(View view, String name);

    /**
     * 增加flag标识
     *
     * @param flag 标识
     * @return IRequest
     */
    IRequest flag(int flag);

    /**
     * 跳转到指定界面
     *
     * @param action 跳转 action
     */
    void to(String action);

    /**
     * 跳转到指定界面
     *
     * @param action 跳转 action
     * @param requestCode 跳转请求值
     * @return IRequest
     */
    IRequest to(String action, int requestCode);

    /**
     * 跳转到指定界面
     *
     * @param cls 跳转对象
     */
    void to(Class cls);

    /**
     * 跳转到指定界面
     *
     * @param cls         跳转对象
     * @param requestCode 跳转请求值
     * @return IRequest
     */
    IRequest to(Class cls, int requestCode);

    /**
     * 回调结果处理
     *
     * @param callback 回调
     */
    void result(IRequest.Callback callback);

    /**
     * 是否保留上一界面
     *
     * @param isKeep 是否保留
     * @return IRequest
     */
    IRequest keep(boolean isKeep);

    /**
     * 关闭当前界面
     */
    void finish();

    interface Callback {
        /**
         * 成功回调
         *
         * @param data 回调参数
         */
        void onResult(Intent data);
    }

    interface BiCallback extends Callback {
        /**
         * 取消回调
         */
        void onCancel();
    }
}
