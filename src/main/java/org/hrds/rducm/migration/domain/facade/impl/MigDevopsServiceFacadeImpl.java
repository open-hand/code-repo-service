package org.hrds.rducm.migration.domain.facade.impl;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.migration.domain.facade.MigDevopsServiceFacade;
import org.hrds.rducm.migration.infra.feign.MigDevOpsServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/30
 */
@Component
public class MigDevopsServiceFacadeImpl implements MigDevopsServiceFacade {
    @Autowired
    private MigDevOpsServiceFeignClient migDevOpsServiceFeignClient;

    private List<C7nAppServiceVO> listC7nAppServiceOnProjectLevel(Long projectId) {
        // 将参数转换为json格式
        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = migDevOpsServiceFeignClient.pageAppServiceByOptions(projectId, null, false, false, 0, 0, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nAppServiceVO> c7nAppServiceVOS = responseEntity.getBody().getContent();
            return c7nAppServiceVOS;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<Long, Long> listC7nAppServiceIdsMapOnProjectLevel(Long projectId) {
        List<C7nAppServiceVO> list = this.listC7nAppServiceOnProjectLevel(projectId);

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream()
                    .collect(HashMap::new, (m, v) ->
                            m.put(v.getId(), v.getGitlabProjectId()), HashMap::putAll);
        } else {
            return Collections.emptyMap();
        }
    }
}
