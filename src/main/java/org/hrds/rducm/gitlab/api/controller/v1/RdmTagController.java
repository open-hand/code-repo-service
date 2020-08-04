package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.gitlab4j.api.models.ProtectedTag;
import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagQueryDTO;
import org.hrds.rducm.gitlab.app.service.RdmTagAppService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.RDM_TAG)
@RestController("rdmTagController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/tags")
public class RdmTagController extends BaseController {
    public static final String API_INFO_TAG_NAME = "标签名(可使用通配符)";
    public static final String API_INFO_CREATE_ACCESS_LEVEL = "是否允许创建-权限级别(0|30|40)";

    @Autowired
    private RdmTagAppService rdmTagAppService;

    @ApiOperation(value = "查询标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "excludeProtectedFlag", value = "是否排除保护标记", paramType = "query", dataType = "boolean"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<TagDTO>> getTags(@PathVariable Long projectId,
                                                @Encrypt @PathVariable Long repositoryId,
                                                TagQueryDTO tagQueryDTO) {
        return Results.success(rdmTagAppService.getTags(projectId, repositoryId, tagQueryDTO));
    }

    @ApiOperation(value = "查询保护标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/protected-tags")
    public ResponseEntity<List<ProtectedTagDTO>> getProtectedTags(@PathVariable Long projectId,
                                                                  @Encrypt @PathVariable Long repositoryId) {
        return Results.success(rdmTagAppService.getProtectedTags(projectId, repositoryId));
    }

    @ApiOperation(value = "创建保护标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "tagName", value = API_INFO_TAG_NAME, paramType = "query", required = true),
            @ApiImplicitParam(name = "createAccessLevel", value = API_INFO_CREATE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/protected-tags")
    public ResponseEntity<ProtectedTagDTO> createProtectedTag(@PathVariable Long projectId,
                                                              @Encrypt @PathVariable Long repositoryId,
                                                              @RequestParam String tagName,
                                                              @RequestParam Integer createAccessLevel) {
        return Results.created(rdmTagAppService.protectTag(projectId, repositoryId, tagName, createAccessLevel));
    }

    @ApiOperation(value = "修改保护标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "tagName", value = "标签名", paramType = "query", required = true),
            @ApiImplicitParam(name = "createAccessLevel", value = API_INFO_CREATE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/protected-tags")
    public ResponseEntity<ProtectedTagDTO> updateProtectedTag(@PathVariable Long projectId,
                                                              @Encrypt @PathVariable Long repositoryId,
                                                              @RequestParam String tagName,
                                                              @RequestParam Integer createAccessLevel) {
        return Results.created(rdmTagAppService.updateProtectedTag(projectId, repositoryId, tagName, createAccessLevel));
    }

    @ApiOperation(value = "删除保护标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "tagName", value = "标签名", paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/protected-tags")
    public ResponseEntity<ProtectedTag> deleteProtectedTag(@PathVariable Long projectId,
                                                           @Encrypt @PathVariable Long repositoryId,
                                                           @RequestParam String tagName) {
        rdmTagAppService.unprotectTag(projectId, repositoryId, tagName);
        return Results.success();
    }
}
