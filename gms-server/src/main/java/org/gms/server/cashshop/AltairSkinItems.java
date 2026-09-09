package org.gms.server.cashshop;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Canonical 111 Altair anime cap skins (1008900-1009999 sparse IDs).
 * Keep in sync with scripts-zh-CN/BeiDouSpecial/xy/阿尔泰动漫皮肤.js ComicList.
 */
public final class AltairSkinItems {
    private AltairSkinItems() {
    }

    private static final int[] IDS = {
            1008956, 1008955, 1008953, 1008950, 1008948, 1008946, 1008960, 1008942, 1008940, 1008939,
            1008933, 1008959, 1008927, 1008926, 1008923, 1008918, 1008913, 1008910, 1008901, 1008900,
            1008906, 1008929, 1009911, 1009912, 1009913, 1009914, 1009915, 1009916, 1009917, 1009918,
            1009919, 1009920, 1009921, 1009923, 1009930, 1009943, 1009922, 1009924, 1009927, 1009937,
            1009938, 1009939, 1009928, 1009929, 1009931, 1009932, 1009925, 1009926, 1009934, 1009933,
            1009935, 1009936, 1009940, 1009941, 1009942, 1009944, 1009945, 1009946, 1009947, 1009948,
            1009949, 1009950, 1009951, 1009952, 1009953, 1009954, 1009955, 1009956, 1009957, 1009958,
            1009959, 1009960, 1009961, 1009962, 1009963, 1009964, 1009965, 1009966, 1009967, 1009968,
            1009969, 1009970, 1009971, 1009972, 1009973, 1009974, 1009975, 1009976, 1009977, 1009978,
            1009979, 1009980, 1009981, 1009982, 1009983, 1009984, 1009985, 1009986, 1009987, 1009988,
            1009989, 1009990, 1009991, 1009992, 1009993, 1009994, 1009995, 1009996, 1009997, 1009998,
            1009999,
    };

    private static final Set<Integer> ID_SET;

    static {
        final LinkedHashSet<Integer> set = new LinkedHashSet<>();
        for (int id : IDS) {
            set.add(id);
        }
        ID_SET = Collections.unmodifiableSet(set);
    }

    public static Set<Integer> allIds() {
        return ID_SET;
    }

    public static boolean isAltairCapSkin(int itemId) {
        return ID_SET.contains(itemId);
    }
}
