package org.hrds.rducm.migration.domain.facade;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;

import java.util.List;
import java.util.Map;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/30
 */
public interface MigDevopsServiceFacade {
    Map<Long, Long> listC7nAppServiceIdsMapOnProjectLevel(Long projectId);
}
