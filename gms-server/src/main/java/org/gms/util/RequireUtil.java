package org.gms.util;

import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 参数校验工具类
 * 提供各种参数校验方法，校验失败时抛出业务异常
 */
public class RequireUtil {
    /**
     * 要求对象必须为null
     *
     * @param obj 待校验对象
     */
    public static void requireNull(Object obj) {
        requireNull(obj, null);
    }

    /**
     * 要求对象必须为null
     *
     * @param obj 待校验对象
     * @param msg 校验失败时的错误信息
     */
    public static void requireNull(Object obj, String msg) {
        if (obj == null) {
            return;
        }
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为null
     *
     * @param obj 待校验对象
     */
    public static void requireNotNull(Object obj) {
        requireNotNull(obj, null);
    }

    /**
     * 要求对象不能为null
     *
     * @param obj 待校验对象
     * @param msg 校验失败时的错误信息
     */
    public static void requireNotNull(Object obj, String msg) {
        if (obj != null) {
            return;
        }
        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求对象不能为空
     * 支持String、Iterable、数组、Map、Iterator类型的空值判断
     *
     * @param obj 待校验对象
     */
    public static void requireNotEmpty(Object obj) {
        requireNotEmpty(obj, null);
    }

    /**
     * 要求对象不能为空
     * 支持String、Iterable、数组、Map、Iterator类型的空值判断
     *
     * @param obj 待校验对象
     * @param msg 校验失败时的错误信息
     */
    public static void requireNotEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            return;
        }

        if (msg == null) {
            throw new IllegalArgumentException();
        } else {
            throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
        }
    }

    /**
     * 要求条件必须为true
     *
     * @param b   条件表达式
     * @param msg 校验失败时的错误信息
     */
    public static void requireTrue(boolean b, String msg) {
        if (!b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 要求条件必须为false
     *
     * @param b   条件表达式
     * @param msg 校验失败时的错误信息
     */
    public static void requireFalse(boolean b, String msg) {
        if (b) throw new BizException(BizExceptionEnum.ILLEGAL_PARAMETERS.getResultCode(), msg);
    }

    /**
     * 判断对象是否为空
     * 支持String、Iterable、数组、Map、Iterator类型的空值判断
     *
     * @param obj 待判断对象
     * @return 如果对象为空返回true，否则返回false
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
     * 判断数字是否为0
     *
     * @param obj 待判断的数字对象
     * @return 如果数字为null或0返回true，否则返回false
     */
    public static boolean isZero(Number obj) {
        if (obj == null) {
            return false;
        }
        return obj.doubleValue() == 0;
    }

    /**
     * 如果对象为空，则执行指定的操作
     *
     * @param obj      待判断对象
     * @param runnable 对象为空时执行的操作
     */
    public static void requireNotEmptyOrElse(Object obj, Runnable runnable) {
        if (!isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 如果对象不为空，则执行指定的操作
     *
     * @param obj      待判断对象
     * @param runnable 对象不为空时执行的操作
     */
    public static void requireNotEmptyAndThen(Object obj, Runnable runnable) {
        if (isEmpty(obj)) {
            return;
        }
        runnable.run();
    }

    /**
     * 如果两个对象都不为空，则执行指定的操作
     *
     * @param t        第一个待判断对象
     * @param r        第二个待判断对象
     * @param consumer 两个对象都不为空时执行的操作
     */
    public static <T, R> void requireNotEmptyAndThen(T t, R r, BiConsumer<T, R> consumer) {
        if (isEmpty(t) || isEmpty(r)) {
            return;
        }
        consumer.accept(t, r);
    }
}