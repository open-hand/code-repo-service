/**
 * 权限总览
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from 'react';
import { isNil } from 'lodash';
import { Page, Choerodon } from '@choerodon/boot';
import { Table } from 'choerodon-ui/pro';
// import ClickText from '@/components/click-text';
import TimePopover from '@/components/time-popover/TimePopover';
import renderFullName from '@/utils/renderer';
// import MouseOverWrapper from '@/components/mouseover-wrapper';
// import ClickText from '@/components/click-text';
import { usPsManagerStore } from '../stores';
// import ShareRule from './modals/share-rule';


const { Column } = Table;

const PsOverView = () => {
  const {
    projectId,
    psOverViewDs,
    appId,
    overStores,
  } = usPsManagerStore();

  function refresh() {
    psOverViewDs.query();
  }


  useEffect(() => {
    refresh();
  }, [appId]);

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }
  function linkToMerge(record) {
    overStores.loadServiceUrl(projectId, record.get('repositoryId'))
      .then((data) => {
        if (data && data.failed) {
          Choerodon.prompt(data.message);
        } else {
          const url = `${data}/merge_requests`;
          window.open(url);
        }
      }).catch((error) => {
        Choerodon.handleResponseError(error);
      });
  }
  function renderName({ text, record }) {
    return <span className="c7n-infra-code-management-table-ps-overview-number-color" onClick={() => linkToMerge(record)} >{text}</span>;
  }

  return (
    <Page
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-member',
        'choerodon.code.project.infra.code-lib-management.ps.project-owner',
      ]}
      className="c7n-infra-code-management-table"
    >
      <Table
        dataSet={psOverViewDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
      // editMode="inline"
      >
        <Column name="repositoryName" width={170} renderer={renderFullName} />
        <Column name="managerCount" width={120} style={{ paddingRight: '.20rem' }} />
        <Column name="developerCount" width={120} />
        <Column name="defaultBranch" />
        <Column name="visibility" />
        <Column name="lastCommittedDate" renderer={renderTime} />
        <Column name="openedMergeRequestCount" renderer={renderName} width={150} />
        <Column name="repositoryCreationDate" renderer={renderFullName} />
        {/* <Column header="操作" width={60} command={['delete']} lock="right" /> */}
      </Table>
    </Page>
  );
};

export default PsOverView;
