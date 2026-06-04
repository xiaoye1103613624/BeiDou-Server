package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】Trio（class），包 `org.gms.util`。
 * <p>三元组数据容器，用于存储三个关联的值</p>
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trio<A, B, C> {
    /** 第一个元素 */
    private A first;
    /** 第二个元素 */
    private B second;
    /** 第三个元素 */
    private C third;
}