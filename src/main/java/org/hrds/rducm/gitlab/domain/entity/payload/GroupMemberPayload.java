package org.hrds.rducm.gitlab.domain.entity.payload;

import java.util.List;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;

/**
 * Created by wangxiang on 2021/8/18
 */
public class GroupMemberPayload {

    private Integer gGroupId;

    private List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS;

    public Integer getgGroupId() {
        return gGroupId;
    }

    public void setgGroupId(Integer gGroupId) {
        this.gGroupId = gGroupId;
    }

    public List<RdmMemberBatchDTO.GitlabMemberCreateDTO> getGitlabMemberCreateDTOS() {
        return gitlabMemberCreateDTOS;
    }

    public void setGitlabMemberCreateDTOS(List<RdmMemberBatchDTO.GitlabMemberCreateDTO> gitlabMemberCreateDTOS) {
        this.gitlabMemberCreateDTOS = gitlabMemberCreateDTOS;
    }
}
