/* eslint-disable max-len */
import React, { createContext, useCallback, useContext, useMemo } from "react";
import { DataSet } from "choerodon-ui/pro";
import { injectIntl } from "react-intl";
import { useFormatMessage } from "@choerodon/master";
import { inject } from "mobx-react";
import { DataSetSelection } from "choerodon-ui/pro/lib/data-set/enum";
import useStore, { MainStoreProps } from "./useStore";
import { usPsManagerStore } from "../../stores";
import { observer } from "mobx-react-lite";
import {
  psApprovalTabData,
  psAuditTabData,
  psSetTabData,
  applyViewTabData,
} from "./TABMAP";

interface ContextProps {
  prefixCls: string;
  intlPrefix: string;
  formatMessage(arg0: object, arg1?: object): string;
  projectId: number;
  customTabsData: {
    name: string;
    value: string;
  }[];
  psAllStore: MainStoreProps;
}

const Store = createContext({} as ContextProps);

export function usePermissionStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(
  inject("AppState")(
    observer((props: any) => {
      const {
        children,
        intl: { formatMessage },
        AppState: {
          currentMenuType: { projectId },
        },
      } = props;

      const { hasMemberPermission, hasPermission } = usPsManagerStore();

      const format = useFormatMessage('c7ncd.codeLibManagement');

      const getCustomsTabData = useCallback(() => {
        let customTabsData: {
          tip?: any;
          name: string;
          value: string;
        }[] = [];
        if (hasPermission) {
          customTabsData = [psSetTabData(format), psApprovalTabData(format), psAuditTabData(format)];
        } else if (!hasPermission && hasMemberPermission) {
          customTabsData = [psSetTabData(format), applyViewTabData]
        }
        return customTabsData;
      }, [hasPermission, hasMemberPermission]);

      const customTabsData = getCustomsTabData();

      const psAllStore = useStore(customTabsData[0]?.value);

      const value = {
        ...props,
        formatMessage,
        projectId,
        customTabsData,
        psAllStore,
      };
      return <Store.Provider value={value}>{children}</Store.Provider>;
    })
  )
);
