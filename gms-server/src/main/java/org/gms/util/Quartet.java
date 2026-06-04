package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】Quartet（class），包 `org.gms.util`。
 * <p>四元组数据容器，用于存储四个关联的值</p>
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 * @param <D> 第四个元素类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quartet<A, B, C, D> {
    /** 第一个元素 */
    private A first;
    /** 第二个元素 */
    private B second;
    /** 第三个元素 */
    private C third;
    /** 第四个元素 */
    private D fourth;
}