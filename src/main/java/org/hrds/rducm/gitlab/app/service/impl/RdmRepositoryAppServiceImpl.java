package org.hrds.rducm.gitlab.app.service.impl;

import org.hrds.rducm.gitlab.app.service.RdmRepositoryAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.AppServiceRepVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
@Service
public class RdmRepositoryAppServiceImpl implements RdmRepositoryAppService {
    @Autowired
    private RdmRepositoryRepository rdmRepositoryRepository;
    @Autowired
    private DevOpsServiceFeignClient devOpsServiceFeignClient;

    /**
     * 查询所有[已启用]的服务
     * todo 临时使用,需对接外围接口
     *
     * @return
     */
    @Override
    public List<RdmRepository> listByActive(Long projectId) {
        ResponseEntity<List<AppServiceRepVO>> feignVO = devOpsServiceFeignClient.listRepositoriesByActive(projectId);

        List<RdmRepository> rdmRepositories = feignVO.getBody().stream().map(vo -> {
            RdmRepository rdmRepository = new RdmRepository();
            rdmRepository.setRepositoryId(vo.getId())
                    .setRepositoryName(vo.getName())
                    .setProjectId(vo.getProjectId())
                    .setGlProjectId(Math.toIntExact(vo.getGitlabProjectId()));
            return rdmRepository;
        }).collect(Collectors.toList());

//        List<RdmRepository> gitlabRepositories = rdmRepositoryRepository.selectAll();
        return rdmRepositories;
    }
}
