package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 四元组数据结构
 * 用于存储四个相关联的对象
 *
 * @param <A> 第一个元素的类型
 * @param <B> 第二个元素的类型
 * @param <C> 第三个元素的类型
 * @param <D> 第四个元素的类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quartet<A, B, C, D> {
    /**
     * 第一个元素
     */
    private A first;

    /**
     * 第二个元素
     */
    private B second;

    /**
     * 第三个元素
     */
    private C third;

    /**
     * 第四个元素
     */
    private D fourth;
}