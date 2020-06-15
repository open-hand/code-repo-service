import React, { useEffect, useState, useCallback, useRef } from 'react';
import { axios, stores } from '@choerodon/boot';
import { Radio, TextArea, Form, DataSet, Select } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import uuidv4 from 'uuid/v4';
import ApplicationForm from '../DoneList/ApplicationForm';
import './index.less';

const intlPrefix = 'infra.env.approval';

const ApproveForm = ({ data, modal, formatMessage, record }) => {
  const [detail, setDetail] = useState({});
  const [approve, setApprove] = useState('YES');
  const { currentMenuType: { projectId, organizationId } } = stores.AppState;
  const ds = useRef(new DataSet({
    autoCreate: true,
    fields: [
      {
        name: 'clusterId',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.model.clusterId`, defaultMessage: '集群' }),
        lookupUrl: `/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/approval/clusters`,
        textField: 'name',
        valueField: 'id',
      },
      {
        name: 'comment',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.comment`, defaultMessage: '审批意见' }),
        required: true,
        maxLength: 30,
      },
      {
        name: 'others',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.others`, defaultMessage: '其他' }),
      },
    ],
  })).current;

  // eslint-disable-next-line
  const renderTextarea = (infrastructure) => {
    return (detail.applicationList[infrastructure] || []).map((o, index) =>
      <TextArea key={uuidv4()} label={`${o.name}连接信息`} required onChange={val => { detail.applicationList[infrastructure][index].approvalDesc = val; }} />);
  };

  const validateDetail = useCallback(() => {
    if (approve === 'YES') {
      if (detail.applicationList.infrastructure.map(o => o.approvalDesc).filter(Boolean).length < detail.applicationList.infrastructure.length
        ||
        detail.applicationList.platformService.map(o => o.approvalDesc).filter(Boolean).length < detail.applicationList.platformService.length
      ) {
        message.error('请输入全部连接信息');
        return false;
      }
    }
    return true;
  }, [detail, approve]);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await ds.current.validate(true) && validateDetail();
      if (validate) {
        try {
          if (approve === 'YES') {
            await axios.post(`/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/approval/pass`, detail);
          } else {
            await axios.post(`/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/approval/reject`, detail);
          }
          record.set('state', 'approving');
          // todoListDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [modal, validateDetail, approve]);

  const handleSelectBoxChange = (val) => {
    setApprove(val);
    if (val === 'YES') {
      ds.fields.get('clusterId').set('required', true);
    } else {
      ds.fields.get('clusterId').set('required', false);
    }
  };

  return (
    <React.Fragment>
      <ApplicationForm data={data} sendToParent={setDetail} />
      <section className="env-application-approve-form">
        <title className="env-application-approve-form-title">审批信息</title>
        <label className="env-application-approve-form-label">是否通过审批</label>
        <div style={{ marginTop: '8px' }}>
          <Radio name="base" value="YES" defaultChecked onChange={handleSelectBoxChange}>是</Radio>
          <Radio name="base" value="NO" style={{ marginLeft: '60px' }} onChange={handleSelectBoxChange}>否</Radio>
        </div>
        {approve === 'YES' &&
          <React.Fragment>
            <Form dataSet={ds} style={{ marginTop: '10px' }}>
              <Select name="clusterId" onChange={val => { detail.clusterId = val; }} />
              <TextArea name="comment" onChange={val => { detail.comment = val; }} />
            </Form>

            <title className="env-application-approve-form-title">基础设施</title>
            <Form>
              {detail.applicationList && renderTextarea('infrastructure')}
            </Form>
            <title className="env-application-approve-form-title">平台服务</title>
            <Form>
              {detail.applicationList && renderTextarea('platformService')}
            </Form>
            <title className="env-application-approve-form-title">其他</title>
            <Form dataSet={ds}>
              <TextArea
                name="others"
                onChange={val => {
                  if (!detail.applicationList.others) {
                    detail.applicationList.others = { approvalDesc: val };
                  } else {
                    detail.applicationList.others.approvalDesc = val;
                  }
                }}
              />
            </Form>
          </React.Fragment>
        }
        {approve === 'NO' &&
          <Form style={{ marginTop: '15px' }} dataSet={ds}>
            <TextArea name="comment" onChange={val => { detail.comment = val; }} />
          </Form>
        }
      </section>
    </React.Fragment>
  );
};

export default observer(ApproveForm);
