import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TagNoProtectDS from './TagNoProtectDS';
import TagFormDataSet from './TagFormDataSet';

const Store = createContext();

export function useAddTagStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId } },
    intl: { formatMessage },
    branchAppId,
  } = props;

  const tagOptions = useMemo(() => new DataSet(TagNoProtectDS(projectId, branchAppId)), [projectId, branchAppId]);
  const tagFormDs = useMemo(() => new DataSet(TagFormDataSet(formatMessage, projectId, branchAppId, tagOptions)), [formatMessage, projectId, branchAppId, tagOptions]);

  useEffect(() => {
    tagFormDs.create();
  }, []);

  const value = {
    ...props,
    tagFormDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

