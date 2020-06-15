import React, { useEffect, useCallback, useState, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { Page, Header, Breadcrumb, Content, Permission } from '@choerodon/boot';
import { DataSet, Button, Modal, Stores } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
// import MainView from './main-view';
import { useApplyStore } from './stores';
import CreateApply from './modals/create-apply';
import ApplyDetail from './modals/apply-detail/ApplyDetail';
import ListView from './list';
import ApplyDetailDS from './stores/ApplyDetailDS';

import './index.less';

const modalKey = Modal.key();
const createRef = React.createRef();
const updateRef = React.createRef();

const ClusterApply = () => {
  const {
    AppState,
    organizationId,
    projectId,
    formatMessage,
    intlPrefix,
    prefixCls,
    permissions,
    applyListDs,
    applyStore,
  } = useApplyStore();

  const [purposeList, setPurposeList] = useState([]);

  async function getPurposeList() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUEM.CAF_PURPOSE');
    setPurposeList(lookupData);
  }

  const getPurposeName = (code) => {
    const item = purposeList.find(o => o.value === code);
    return item && item.meaning;
  };

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    const res = await applyListDs.query(page);
    const records = applyStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        applyListDs.unshift(...records);
      }
      applyStore.setOldOptsRecord(applyListDs.records);
      applyStore.setLoadMoreBtn(res.hasNextPage);
      return res;
    } else {
      return false;
    }
  }, [applyListDs]);

  useEffect(() => {
    getPurposeList();
    loadData();
  }, []);

  // 创建
  function handleCreateApply() {
    Modal.open({
      title: formatMessage({ id: `${intlPrefix}.view.createApply.title`, defaultMessage: '创建集群申请单' }),
      children: <CreateApply
        modalRef={createRef}
        refresh={loadData}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
      />,
      key: modalKey,
      drawer: true,
      style: { width: '7.4rem' },
      destroyOnClose: true,
      className: 'cluster-apply-sider',
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button
            color="primary"
            type="submit"
            funcType="raised"
            onClick={() => {
              createRef.current.handleSubmit();
              return false;
            }}
          >
            {formatMessage({ id: 'commit' })}
          </Button>
          {cancelBtn}
        </div>
      ),
      okText: formatMessage({ id: 'save' }),
    });
  }
  // 编辑
  function handleUpdate(id, applicationNo) {
    Modal.open({
      title: formatMessage({ id: `${intlPrefix}.view.updateApply.title`, defaultMessage: `修改集群申请单：${applicationNo}` }, { applicationNo }),
      children: <CreateApply
        modalRef={updateRef}
        refresh={loadData}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        id={id}
      />,
      key: modalKey,
      drawer: true,
      style: { width: '7.4rem' },
      destroyOnClose: true,
      className: 'cluster-apply-sider',
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button
            color="primary"
            type="submit"
            funcType="raised"
            onClick={() => {
              updateRef.current.handleSubmit();
              return false;
            }}
          >
            {formatMessage({ id: 'commit' })}
          </Button>
          {cancelBtn}
        </div>
      ),
      okText: formatMessage({ id: 'save' }),
    });
  }
  // 查看详情
  function handleViewDetail(id, applicationNo) {
    const applyDetailDs = new DataSet(ApplyDetailDS({ intlPrefix, formatMessage, projectId, id }));
    const tagPros = {
      AppState,
      applicationNo,
      formatMessage,
      intlPrefix,
      getPurposeName,
      dataSet: applyDetailDs,
    };

    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.detail.title`, defaultMessage: `集群申请单：${applicationNo}` }, { applicationNo }),
      style: {
        width: '7.4rem',
      },
      className: 'cluster-apply-detail-sider',
      children: <ApplyDetail {...tagPros} />,
      footer: (okBtn) => okBtn,
      okText: formatMessage({ id: 'close' }),
    });
  }
  async function handleDelete(id) {
    const deleData = {
      organizationId,
      projectId,
      id,
    };
    await applyStore.deleteDocument(deleData).then(res => {
      if (res.failed) {
        message.error(res.message);
      } else {
        message.success(formatMessage({ id: 'success.delete' }));
        loadData();
      }
    });
  }

  const listProps = useMemo(() => ({
    intlPrefix,
    prefixCls,
    formatMessage,
    getPurposeName,
    isMore: applyStore.getLoadMoreBtn,
    purposeList,
    loadData,
    onDelete: handleDelete,
    onUpdate: handleUpdate,
    onViewDetail: handleViewDetail,
    applyListDs,
  }), [applyStore.getLoadMoreBtn, purposeList, loadData, applyListDs]);


  return (
    <Page service={permissions}>
      <Header className={`${prefixCls}-page-header`}>
        <Permission service={[]}>
          <Button
            onClick={handleCreateApply}
            icon="playlist_add"
          >
            {formatMessage({ id: `${intlPrefix}.view.createApply`, defaultMessage: '创建申请' })}
          </Button>
        </Permission>
        <Button
          onClick={() => loadData(1)}
          icon="refresh"
        >
          {formatMessage({ id: 'refresh' })}
        </Button>
      </Header>
      <Breadcrumb />
      <Content className={`${prefixCls}-page-content`}>
        <ListView {...listProps} />
      </Content>
    </Page>
  );
};

export default observer(ClusterApply);
