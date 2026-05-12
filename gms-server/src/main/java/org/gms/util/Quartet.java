package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】Quartet（class），包 `org.gms.util`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quartet<A, B, C, D> {
    private A first;
    private B second;
    private C third;
    private D fourth;
}
