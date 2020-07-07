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



}
