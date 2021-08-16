/* eslint-disable max-len */
import React, { createContext, useCallback, useContext, useMemo } from "react";
import { DataSet } from "choerodon-ui/pro";
import { injectIntl } from "react-intl";
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

      const psAllStore = useStore();

      const { hasMemberPermission, hasPermission } = usPsManagerStore();

      const getCustomsTabData = useCallback(() => {
        let customTabsData: {
          tip?: any;
          name: string;
          value: string;
        }[] = [];
        if (hasPermission && hasMemberPermission) {
          customTabsData = [psSetTabData, psApprovalTabData, psAuditTabData];
        } else if (!hasPermission && hasMemberPermission) {
          customTabsData.push(applyViewTabData);
        }
        return customTabsData;
      }, [hasPermission, hasMemberPermission]);

      const value = {
        ...props,
        formatMessage,
        projectId,
        customTabsData: getCustomsTabData(),
        psAllStore,
      };
      return <Store.Provider value={value}>{children}</Store.Provider>;
    })
  )
);