package org.gms.util;

import com.mybatisflex.core.paginate.Page;
import org.gms.model.dto.BasePageDTO;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 对于数据库的查询分页，直接用mybatis-flex
 * 这里仅针对特殊情况，在已经有数据的情况下分页
 * 不需要过滤，不需要转换的情况下，直接 BasePageUtil.create(list, request).page();
 */
public class BasePageUtil<T> {
    /** 数据流，支持链式的过滤和排序操作 */
    private Stream<T> data;
    /** 分页参数（页码、每页条数、是否只查总数、是否不分页） */
    private final BasePageDTO basePageDTO;

    /**
     * 私有构造器，使用默认分页参数
     *
     * @param data 数据集合
     */
    private BasePageUtil(Collection<T> data) {
        this(data, null);
    }

    /**
     * 私有构造器，初始化数据流和分页参数
     *
     * @param data        数据集合
     * @param basePageDTO 分页参数，为null时使用默认值
     */
    private BasePageUtil(Collection<T> data, BasePageDTO basePageDTO) {
        // 空集合转为空流，避免NPE
        if (data == null || data.isEmpty()) {
            this.data = Stream.of();
        } else {
            this.data = data.stream();
        }
        if (basePageDTO == null) {
            basePageDTO = new BasePageDTO();
        }
        if (basePageDTO.getPageNo() == null) {
            // 默认页码
            basePageDTO.setPageNo(1);
        }
        if (basePageDTO.getPageSize() == null) {
            // 默认每页条数
            basePageDTO.setPageSize(20);
        }
        this.basePageDTO = basePageDTO;
    }

    /**
     * 起始必须调create创建分页对象
     *
     * @param data 列表数据
     * @param <T>  类型
     * @return PageUtil对象
     */
    public static <T> BasePageUtil<T> create(Collection<T> data) {
        return new BasePageUtil<>(data);
    }

    /**
     * 根据分页参数创建分页工具
     *
     * @param data         列表数据
     * @param basePageDTO  分页参数
     * @param <T>          数据类型
     * @return BasePageUtil对象
     */
    public static <T> BasePageUtil<T> create(Collection<T> data, BasePageDTO basePageDTO) {
        return new BasePageUtil<>(data, basePageDTO);
    }

    /**
     * 指定页码和每页大小创建分页工具
     *
     * @param data      列表数据
     * @param pageNo    页码
     * @param pageSize  每页大小
     * @param <T>       数据类型
     * @return BasePageUtil对象
     */
    public static <T> BasePageUtil<T> create(Collection<T> data, Integer pageNo, Integer pageSize) {
        return new BasePageUtil<>(data, BasePageDTO.builder().pageNo(pageNo).pageSize(pageSize).build());
    }

    /**
     * 创建分页工具，可指定只查询总数或不分页
     *
     * @param data      列表数据
     * @param onlyTotal 只查询总数
     * @param notPage   不分页，返回全部数据
     * @param <T>       数据类型
     * @return BasePageUtil对象
     */
    public static <T> BasePageUtil<T> create(Collection<T> data, boolean onlyTotal, boolean notPage) {
        return new BasePageUtil<>(data, BasePageDTO.builder().onlyTotal(onlyTotal).notPage(notPage).build());
    }

    /**
     * 如有过滤数据的需要，可以调用这个方法
     *
     * @param predicate 过滤条件
     * @return PageUtil对象
     */
    public BasePageUtil<T> filter(Predicate<T> predicate) {
        this.data = this.data.filter(predicate);
        return this;
    }

    /**
     * 对数据进行排序
     *
     * @param comparator 比较器
     * @return BasePageUtil对象
     */
    public BasePageUtil<T> sorted(Comparator<? super T> comparator) {
        this.data = this.data.sorted(comparator);
        return this;
    }

    /**
     * 构建分页对象
     *
     * @return 分页对象
     */
    public Page<T> page() {
        if (this.basePageDTO.isNotPage()) {
            // 不分页模式：返回全部数据
            List<T> list = this.data.toList();
            return new Page<>(list, 1, list.size(), list.size());
        } else if (this.basePageDTO.isOnlyTotal()) {
            // 仅返回总数，不返回数据列表
            Page<T> page = new Page<>();
            page.setTotalRow(this.data.toList().size());
            return page;
        } else {
            // 标准分页：先收集全部数据，再skip和limit截取当前页
            List<T> totalList = this.data.toList();
            List<T> list = totalList.stream()
                    .skip((long) (this.basePageDTO.getPageNo() - 1) * this.basePageDTO.getPageSize())
                    .limit(this.basePageDTO.getPageSize())
                    .toList();
            return new Page<>(list, this.basePageDTO.getPageNo(), this.basePageDTO.getPageSize(), totalList.size());
        }
    }

    /**
     * 如果不以原对象返回，需要构建新对象，用这个
     *
     * @param mapper 数据转换函数
     * @param <R>    目标类型
     * @return 分页对象
     */
    public <R> Page<R> page(Function<T, R> mapper) {
        if (this.basePageDTO.isNotPage()) {
            // 不分页模式：转换后返回全部数据
            List<R> list = this.data.map(mapper).toList();
            return new Page<>(list, 1, list.size(), list.size());
        } else if (this.basePageDTO.isOnlyTotal()) {
            // 仅返回总数，不返回数据列表
            Page<R> page = new Page<>();
            page.setTotalRow(this.data.toList().size());
            return page;
        } else {
            // 标准分页：先收集全部数据，skip/limit截取后再转换
            List<T> totalList = this.data.toList();
            List<R> list = totalList.stream()
                    .skip((long) (this.basePageDTO.getPageNo() - 1) * this.basePageDTO.getPageSize())
                    .limit(this.basePageDTO.getPageSize())
                    .map(mapper)
                    .toList();
            return new Page<>(list, this.basePageDTO.getPageNo(), this.basePageDTO.getPageSize(), totalList.size());
        }
    }
}