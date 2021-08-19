package me.codego.delegate;

import android.view.View;

import java.util.List;

/**
 * @author mengxn
 * @date 2020/5/6
 */
public interface IShareViewSelector {

    /**
     * convert transition name to view
     *
     * @param transitionNameList transition name list
     * @return
     */
    View[] selectShareView(List<String> transitionNameList);

}
