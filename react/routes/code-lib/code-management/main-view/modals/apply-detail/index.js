import React, { useState } from 'react';
import { SelectBox, Row, Col, DatePicker, TextArea } from 'choerodon-ui/pro';
import { Tooltip } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { isNil } from 'lodash';
import { Choerodon } from '@choerodon/boot';
import UserAvatar from '@/components/user-avatar';
import './index.less';

const { Option } = SelectBox;

const intlPrefix = 'infra.codeManage.ps';

export default observer(({ record, modal, formatMessage, overStores, organizationId, projectId, psSetDs }) => {
  const { dataSet } = record;
  const approvalState = record.get('approvalState');
  const approvalMessage = record.get('approvalMessage');
  const objectVersionNumber = record.get('objectVersionNumber');
  const [commitMsg, setCommitFlag] = useState({ canCommit: false, message: '' });
  const [other, setOther] = useState('');
  const [expiresAt, setExpiresAt] = useState(undefined);

  function handleChange(value) {
    setCommitFlag({ canCommit: false, message: '' });
    setOther(null);
    record.set('approvalState', value);
    record.set('isApproval', true);
  }
  function handleChangeMes(value) {
    setCommitFlag({ canCommit: false, message: '' });
    setOther(value);
  }

  async function handleOk() {
    if (record.getPristineValue('approvalState') !== 'PENDING') {
      return true;
    } else if (!record.get('isApproval')) {
      setCommitFlag({ canCommit: true, message: formatMessage({ id: `${intlPrefix}.message.noApprove` }) });
      return false;
    } else if (record.get('approvalState') === 'REJECTED' && isNil(other)) {
      setCommitFlag({ canCommit: true, message: formatMessage({ id: `${intlPrefix}.message.noReason` }) });
      return false;
    }
    const formatExpDate = expiresAt ? moment(expiresAt).format('YYYY-MM-DD 00:00:00') : null;
    const commitMethod = record.get('approvalState') === 'APPROVED' ? overStores.approvalPass(organizationId, projectId, record.get('id'), objectVersionNumber, formatExpDate) : overStores.approvalRefuse(organizationId, projectId, record.get('id'), objectVersionNumber, other);
    const result = await commitMethod
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
          return false;
        } else {
          Choerodon.prompt(formatMessage({ id: `${intlPrefix}.message.approveSuccess` }));
          dataSet.query();
          psSetDs.query();
          return true;
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
    return result;
  }
  modal.handleOk(handleOk);
  modal.handleCancel(() => dataSet.reset());

  function renderApprovalDetail() {
    return (
      <div className="code-lib-ps-approval-sider-content">
        <h3 className="code-lib-ps-approval-sider-title">{formatMessage({ id: `${intlPrefix}.model.approveInfo` })}</h3>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: `${intlPrefix}.model.approveResult` })}</span>
          </Col>
          <Col span={16}>
            <span className={`code-lib-ps-approval-sider-value ${approvalState === 'APPROVED' ? 'approved' : 'rejected'}`}>{approvalState === 'APPROVED' ? formatMessage({ id: `${intlPrefix}.model.approved` }) : formatMessage({ id: `${intlPrefix}.model.rejected` })}</span>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: `${intlPrefix}.model.approveTime` })}</span>
          </Col>
          <Col span={16}>
            <span className="code-lib-ps-approval-sider-value">{record.get('approvalDate')}</span>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: `${intlPrefix}.model.approveUser` })}</span>
          </Col>
          <Col span={16} style={{ display: 'inline-flex' }}>
            <UserAvatar
              user={{
                id: record.get('approvalUser').userId,
                loginName: record.get('approvalUser').loginName,
                realName: record.get('approvalUser').realName,
                imageUrl: record.get('approvalUser').imageUrl,
                email: record.get('approvalUser').email,
              }}
              hiddenText
              showToolTip={false}
            />
            <div
              style={{
                display: 'inline-block',
                maxWidth: '2.3rem',
                overflow: 'hidden',
                whiteSpace: 'nowrap',
                textOverflow: 'ellipsis',
              }}
              className="code-lib-ps-approval-sider-value"
            >
              <Tooltip title={`${record.get('approvalUser').realName}(${record.get('approvalUser').loginName})` || formatMessage({ id: 'none' })}>
                {`${record.get('approvalUser').realName}(${record.get('approvalUser').loginName})` || formatMessage({ id: 'none' })}
              </Tooltip>
            </div>
          </Col>
        </Row>
        {approvalState === 'REJECTED' && (
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: `${intlPrefix}.model.rejectedReason` })}</span>
          </Col>
          <Col span={16}>
            <span className="code-lib-ps-approval-sider-value">{approvalMessage}</span>
          </Col>
        </Row>
        )}
      </div>
    );
  }

  return (
    <div className="code-lib-ps-approval-sider-form">
      <div className="code-lib-ps-approval-sider-content">
        <h3 className="code-lib-ps-approval-sider-title">{formatMessage({ id: `${intlPrefix}.model.applyInfo` })}</h3>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: 'infra.approval.fields.applicantUserName' })}</span>
          </Col>
          <Col span={16} style={{ display: 'inline-flex' }}>
            <UserAvatar
              user={{
                id: record.get('applicantUser').userId,
                loginName: record.get('applicantUser').loginName,
                realName: record.get('applicantUser').realName,
                imageUrl: record.get('applicantUser').imageUrl,
                email: record.get('applicantUser').email,
              }}
              hiddenText
              showToolTip={false}
            />
            <div
              style={{
                display: 'inline-block',
                maxWidth: '2.3rem',
                overflow: 'hidden',
                whiteSpace: 'nowrap',
                textOverflow: 'ellipsis',
              }}
              className="code-lib-ps-approval-sider-value"
            >
              <Tooltip title={`${record.get('applicantUser').realName}(${record.get('applicantUser').loginName})` || formatMessage({ id: 'none' })}>
                {`${record.get('applicantUser').realName}(${record.get('applicantUser').loginName})` || formatMessage({ id: 'none' })}
              </Tooltip>
            </div>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: 'infra.approval.fields.repositoryName' })}</span>
          </Col>
          <Col span={16} style={{ display: 'inline-flex' }}>
            <span className="code-lib-ps-approval-sider-value">{record.get('repositoryName') || formatMessage({ id: 'none' })}</span>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: 'infra.approval.fields.applicantType' })}</span>
          </Col>
          <Col span={16} style={{ display: 'inline-flex' }}>
            <span className="code-lib-ps-approval-sider-value">{record.getField('applicantType').getText() || formatMessage({ id: 'none' })}</span>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="code-lib-ps-approval-sider-label">{formatMessage({ id: 'infra.approval.fields.accessLevel' })}</span>
          </Col>
          <Col span={16} style={{ display: 'inline-flex' }}>
            <span className="code-lib-ps-approval-sider-value">
              {record.get('applicantType') === 'MEMBER_PERMISSION_CHANGE' ? (
                `${record.getField('oldAccessLevel').getText()} -> ${record.getField('accessLevel').getText()}`
               ) : (record.getField('accessLevel').getText() || formatMessage({ id: 'none' }))}
            </span>
          </Col>
        </Row>
      </div>
      <div className={`devider ${record.getPristineValue('approvalState') === 'PENDING' ? '' : 'mb-20'}`} />
      <div className="code-lib-ps-approval-sider-content">
        {record.getPristineValue('approvalState') === 'PENDING' && (
        <React.Fragment>
          <h3 className="code-lib-ps-approval-sider-title">{formatMessage({ id: `${intlPrefix}.model.select` })}</h3>
          <SelectBox label={formatMessage({ id: `${intlPrefix}.message.wetherToPass` })} onChange={value => handleChange(value)} value={approvalState} labelLayout="float">
            <Option value="APPROVED">{formatMessage({ id: 'yes' })}</Option>
            <Option value="REJECTED">{formatMessage({ id: 'no' })}</Option>
          </SelectBox>
        </React.Fragment>
        )}
      </div>
      {approvalState === 'APPROVED' && record.getPristineValue('approvalState') === 'PENDING' && (
        <React.Fragment>
          <div className="code-lib-ps-approval-sider-content">
            <DatePicker
              className="code-lib-ps-approval-sider-content-input-box"
              min={moment().add(1, 'days').format('YYYY-MM-DD')}
              label={formatMessage({ id: `${intlPrefix}.model.expDate` })}
              value={expiresAt}
              onChange={setExpiresAt}
              labelLayout="float"
            />
          </div>
        </React.Fragment>
      )}
      {approvalState === 'REJECTED' && record.getPristineValue('approvalState') === 'PENDING' && (
        <React.Fragment>
          <div className="code-lib-ps-approval-sider-content">
            <TextArea
              className="code-lib-ps-approval-sider-content-input-box"
              resize="vertical"
              rows={5}
              label={formatMessage({ id: `${intlPrefix}.message.enterReason` })}
              // placeholder="请输入原因"
              value={other}
              onChange={value => handleChangeMes(value)}
              labelLayout="float"
              cols={40}
            />
          </div>
        </React.Fragment>
      )}
      { commitMsg.canCommit && (
        <div className="code-lib-ps-approval-sider-failed">{commitMsg.message}</div>
      )
      }
      {record.getPristineValue('approvalState') !== 'PENDING' && renderApprovalDetail()}
    </div>
  );
});
