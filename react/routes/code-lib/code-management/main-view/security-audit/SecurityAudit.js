/**
 * 安全审计
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/04/01
 * @copyright 2020 ® HAND
 */
/* eslint-disable */
import React, { useEffect } from 'react';
import { Page } from '@choerodon/boot';
import { useFormatMessage } from "@choerodon/master";
import { Table, Modal, DataSet } from 'choerodon-ui/pro';
import { Slider } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import UserAvatar from '@/components/user-avatar';
import Tips from '@/components/new-tips';
import { usPsManagerStore } from '../stores';
import SecurityDetailDS from '../stores/SecurityDetailDS';
import DetailTable from './DetailTable';
import './index.less';

const { Column } = Table;

const modalKey = Modal.key();

const PsSet = observer(() => {
  const {
    intlPrefix,
    // prefixCls,
    intl: { formatMessage },
    // appServiceDs,
    securityAuditDs,
    appId,
    AppState: { currentMenuType: { id: projectId, organizationId } },

  } = usPsManagerStore();

  const format = useFormatMessage('c7ncd.codeLibManagement');

  function refresh() {
    securityAuditDs.query();
  }

  useEffect(() => {
    refresh();
  }, [appId]);

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function openDetail(record) {
    const securityDetailDs = new DataSet(SecurityDetailDS({
      formatMessage, intlPrefix, userId: record.get('user').userId, projectId, organizationId,
    }));
    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: 'infra.codeManage.ps.message.psDetail' }, { name: record.get('user').realName }),
      style: {
        width: '7.4rem',
      },
      children: <DetailTable dataSet={securityDetailDs} />,
      footer: okBtn => okBtn,
      okText: formatMessage({ id: 'close' }),
    });
  }

  function renderName({ record }) {
    const avatar = (
      <UserAvatar
        user={{
          id: record.get('user').userId,
          loginName: record.get('user').loginName,
          realName: record.get('user').realName,
          imageUrl: record.get('user').imageUrl,
          email: record.get('user').email,
        }}
        hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
        <span className="c7n-infra-code-management-table-name" onClick={() => openDetail(record)}>{record.get('user').realName}</span>
      </div>
    );
  }
  function renderLoginName({ record }) {
    return (
      <span>{record.get('user').loginName}</span>
    );
  }
  function renderLevel({ record }) {
    const marks = {
      24: '',
      44: '',
      65: '',
      85: '',
    };
    // const rateValue = 100;
    const realValenzuela = !isNil(record.get('allRepositoryCount')) ? (record.get('authorizedRepositoryCount') * 100) / record.get('allRepositoryCount') : 0;
    const rateValue = realValenzuela.toFixed(1);
    const domStr = (
      // eslint-disable-next-line no-nested-ternary
      <div className={rateValue === 100 ? 'code-lib-management-security-audio-icon-wrapper-full' : rateValue > 50 ? 'code-lib-management-security-audio-icon-wrapper-warning' : 'code-lib-management-security-audio-icon-wrapper'} style={{ display: 'flex' }}>
        <Slider value={rateValue} marks={marks} style={{ width: '80%' }} disabled />
        <span>{`${rateValue}%`}</span>
      </div>
    );
    return domStr;
    // return <Progress name="authorizedRate" value={rateValue} format={() => format(rateValue)} status={rateValue > 50 ? 'exception' : 'success'} />;
  }
  function renderRole({ value }) {
    return value && value.join();
  }


  return (
    <Page
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-owner',
      ]}
      className="c7n-infra-code-management-table"
    >
      <Table
        dataSet={securityAuditDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
        className="code-lib-management-security-audio"
      >
        <Column
          name="realName"
          renderer={renderName}
          width={200}
        />
        <Column name="loginName" renderer={renderLoginName} />
        <Column name="roleNames" renderer={renderRole} />
        <Column name="authorizedRepositoryCount" />
        <Column name="allRepositoryCount" />
        <Column header={<Tips title={format({ id: 'PercentageServices' })} helpText={formatMessage({ id: `${intlPrefix}.authorizedRateTips` })} />} name="authorizedRate" renderer={renderLevel} width={150} />
      </Table>
    </Page>
  );
});

export default PsSet;
