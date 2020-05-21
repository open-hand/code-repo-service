package org.hrds.rducm.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * 服务路由配置
 */
@ChoerodonExtraData
public class RouteExtraDataManager implements ExtraDataManager {

    @Autowired
    private Environment environment;

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(environment.getProperty("hzero.service.current.name", "hrds-code-repo"));
        choerodonRouteData.setPath(environment.getProperty("hzero.service.current.path", "/rducm/**"));
        choerodonRouteData.setServiceId(environment.getProperty("hzero.service.current.service-name", "hrds-code-repo"));
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
