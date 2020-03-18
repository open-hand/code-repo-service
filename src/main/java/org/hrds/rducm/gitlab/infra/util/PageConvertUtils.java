package org.hrds.rducm.gitlab.infra.util;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public class PageConvertUtils {
    /**
     * hzero分页转换为猪齿鱼分页
     *
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> convert(Page<T> page) {
        com.github.pagehelper.Page<T> c7nPage = new com.github.pagehelper.Page<>(page.getNumber() + 1, page.getSize());
        c7nPage.addAll(page.getContent());
//        c7nPage.setTotal(page.getTotalElements());

        PageInfo<T> pageInfo = new PageInfo<>(c7nPage);
        pageInfo.setTotal(page.getTotalElements());

        return pageInfo;
    }
}
