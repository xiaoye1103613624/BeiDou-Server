package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】Trio（class），包 `org.gms.util`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trio<A, B, C> {
    private A first;
    private B second;
    private C third;
}
