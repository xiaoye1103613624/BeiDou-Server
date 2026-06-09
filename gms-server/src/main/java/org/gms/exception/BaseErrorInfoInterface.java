/**
 * 错误信息接口
 * 定义错误信息必须提供的方法：获取状态码和获取错误消息
 */
package org.gms.exception;

public interface BaseErrorInfoInterface {
    /**
     * 获取结果状态码
     *
     * @return 状态码
     */
    Integer getResultCode();

    /**
     * 获取结果消息
     *
     * @return 错误消息
     */
    String getResultMsg();
}