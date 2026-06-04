package org.gms.util;

import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 【类型】RequireUtil（class），包 `org.gms.util`。
 * <p>参数校验工具类，提供各种断言方法，用于参数合法性检查</p>
 */
public class RequireUtil {

    /**
     * 要求对象必须为 null
     * @param obj 待检查对象
     */
    public static void requireNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象必须为 null
     * @param obj 待检查对象
     * @param msg 错误信息
     */
    public static void requireNull(Object obj, String msg) {
        if (obj == null) {
            return;
        }
        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为空（非 null）
     * @param obj 待检查对象
     */
    public static void requireNotNull(Object obj) {
        requireNotNull(obj, null);
    }

    /**
     * 要求对象不能为空（非 null）
     * @param obj 待检查对象
     * @param msg 错误信息
     */
    public static void requireNotNull(Object obj, String msg) {
        if (obj != null) {
            return;
        }
        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为空（非 null 且非空）
     * @param obj 待检查对象
     */
    public static void requireNotEmpty(Object obj) {
        requireNotEmpty(obj, null);
    }

    /**
     * 要求对象不能为空（非 null 且非空）
     * @param obj 待检查对象
     * @param msg 错误信息
     */
    public static void requireNotEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            return;
        }

        // 有无错误信息
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求条件必须为 true
     * @param b   条件表达式
     * @param msg 错误信息
     */
    public static void requireTrue(boolean b, String msg) {
        if (!b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 要求条件必须为 false
     * @param b   条件表达式
     * @param msg 错误信息
     */
    public static void requireFalse(boolean b, String msg) {
        if (b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 判断对象是否为空
     * <p>支持 String、Iterable、数组、Map、Iterator 等类型</p>
     * @param obj 待检查对象
     * @return true=空, false=非空
     */
    public static boolean isEmpty(Object obj) {
        boolean empty = false;
        if (obj == null) {
            empty = true;
        } else if (obj instanceof String str) {
            empty = str.trim().isEmpty();
        } else if (obj instanceof Iterable<?> iter) {
            empty = !iter.iterator().hasNext();
        } else if (obj.getClass().isArray()) {
            empty = Array.getLength(obj) == 0;
        } else if (obj instanceof Map<?, ?> map) {
            empty = map.isEmpty();
        } else if (obj instanceof Iterator<?> iter) {
            empty = !iter.hasNext();
        }
        return empty;
    }

    /**
     * 判断数字是否为零
     * @param obj 待检查数字
     * @return true=为零, false=不为零
     */
    public static boolean isZero(Number obj) {
        if (obj == null) {
            return false;
        }
        return obj.doubleValue() == 0;
    }

    /**
     * 对象为空时执行指定操作
     * @param obj      待检查对象
     * @param runnable 要执行的操作
     */
    public static void requireNotEmptyOrElse(Object obj, Runnable runnable) {
        if (!isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 对象不为空时执行指定操作
     * @param obj      待检查对象
     * @param runnable 要执行的操作
     */
    public static void requireNotEmptyAndThen(Object obj, Runnable runnable) {
        if (isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 两个对象都不为空时执行指定操作
     * @param t        第一个对象
     * @param r        第二个对象
     * @param consumer 要执行的操作
     */
    public static <T, R> void requireNotEmptyAndThen(T t, R r, BiConsumer<T, R> consumer) {
        if (isEmpty(t) || isEmpty(r)) {
            return;
        }
        consumer.accept(t, r);
    }
}