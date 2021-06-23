import React, { useEffect, useMemo } from "react";
import { observer } from "mobx-react-lite";
import { Content, TabPage, Page, Choerodon } from "@choerodon/boot";
import { CustomTabs } from "@choerodon/components";
import { Row, Col, Icon, Tooltip } from "choerodon-ui";
import CodeManagerHeader from "../../header";
import Modals from "../modals";
import "../index.less";
import { usPsManagerStore } from "../stores";
import AppSelector from "../app-service-selector";
import { usePermissionStore } from "./stores";
import PSsetTable from "./components/psSet";
import PSapply from "./components/psApply";
import { isNil } from "lodash";
import TimeAgo from "timeago-react";
import PsAudit from "./components/psAudit";
import "./index.less";
import PsApproval from "./components/psApproval";

const intlPrefixAudit = "infra.codeManage.ps";

const PsAll = () => {
  const { psAuditDs, executionDate } = usPsManagerStore();

  const { customTabsData, psAllStore, formatMessage } = usePermissionStore();

  const { setSelectedTab, selectedTabkey } = psAllStore;

  const psMap = new Map([
    ["psSet", <PSsetTable />],
    ["applyView", <PSapply />],
    ["psApproval", <PsApproval />],
    ["psAudit", <PsAudit />],
  ]);

  const { prefixCls } = usPsManagerStore();

  const subPrefixCls = useMemo(() => `${prefixCls}-psAll`, [prefixCls]);

  function handleCustomTabChange(
    e: React.MouseEvent<HTMLDivElement, MouseEvent>,
    name: string | number,
    value: any,
    number: number
  ) {
    setSelectedTab(value);
  }

  function renderTime() {
    const date = isNil(executionDate) ? (
      formatMessage({ id: "none" })
    ) : (
      <Tooltip title={executionDate}>
        <TimeAgo
          datetime={executionDate}
          locale={Choerodon.getMessage("zh_CN", "en")}
        />
      </Tooltip>
    );
    return date;
  }

  return (
    <TabPage>
      <Modals type={selectedTabkey} />
      <CodeManagerHeader />
      <Content className={`${subPrefixCls}`}>
        <Page>
          <div className={`${subPrefixCls}-head`}>
            <AppSelector showAllOption />
            <CustomTabs
              onChange={handleCustomTabChange}
              className={`${subPrefixCls}-customTabs`}
              data={customTabsData}
              selectedTabValue={selectedTabkey}
            />
            {selectedTabkey === "psAudit" && (
              <div style={{flex: 1, marginLeft: '10px'}}>
                <Row className="c7n-infra-code-management-ps-audit-tip-text">
                  <Col span={12}>
                    <Icon
                      type="date_range"
                      style={{ marginRight: ".03rem", marginBottom: ".04rem" }}
                    />
                    <span>
                      {formatMessage({
                        id: `${intlPrefixAudit}.model.executionDate`,
                      })}
                      <span className="c7n-infra-code-management-ps-audit-tip-text-date">
                        {renderTime()}
                      </span>
                    </span>
                  </Col>
                  <Col span={12}>
                    <Icon type="compare" style={{ marginRight: ".03rem" }} />
                    <span>
                      {formatMessage({
                        id: `${intlPrefixAudit}.model.diffCount`,
                      })}
                      <span className="c7n-infra-code-management-ps-audit-tip-text-number">
                        {psAuditDs.totalCount || 0}
                      </span>
                    </span>
                  </Col>
                </Row>
              </div>
            )}
          </div>
          <div className={`${subPrefixCls}-tableContainer`}>
            {psMap.get(selectedTabkey)}
          </div>
        </Page>
      </Content>
    </TabPage>
  );
};

export default observer(PsAll);
