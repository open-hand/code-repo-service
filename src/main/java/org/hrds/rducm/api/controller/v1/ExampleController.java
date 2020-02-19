package org.hrds.rducm.api.controller.v1;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hrds.rducm.app.service.ExampleService;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.domain.entity.Example;
import org.hrds.rducm.domain.repository.ExampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * API接口
 */
@Api(tags = SwaggerTags.EXAMPLE)
@RestController("exampleController.v1")
@RequestMapping("/v1/example")
public class ExampleController extends BaseController {

    @Autowired
    private ExampleService exampleService;
    @Autowired
    private ExampleRepository exampleRepository;

    @ApiOperation(value = "根据ID获取")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Example> hello(@PathVariable Long id) {
        return Results.success(exampleRepository.selectByPrimaryKey(id));
    }


}