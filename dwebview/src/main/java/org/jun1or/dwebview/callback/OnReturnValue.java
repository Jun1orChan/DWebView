package org.jun1or.dwebview.callback;

/**
 * @author cwj
 * @date 16/12/31
 */

public interface OnReturnValue<T> {
    /**
     * 返回值
     *
     * @param retValue
     */
    void onValue(T retValue);
}
