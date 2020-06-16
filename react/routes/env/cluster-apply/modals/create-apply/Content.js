import React, { useImperativeHandle } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, Button, DatePicker, TextField, TextArea, Spin } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { Choerodon } from '@choerodon/boot';
import { map } from 'lodash';
import moment from 'moment';
import UserAvatar from '@/components/user-avatar';
import { useCreateApplyStore } from './stores';
import './index.less';

export default observer(() => {
  const {
    AppState,
    formDs,
    applicationListDs,
    infrastructureDs,
    platformServiceDs,
    intl: { formatMessage },
    modal,
    refresh,
    intlPrefix,
    createStore,
    modalRef,
  } = useCreateApplyStore();
  const { userInfo: { id, loginName, realName, imageUrl }, currentMenuType: { projectId, organizationId } } = AppState;


  // 提交
  async function handleSubmit() {
    const commitData = {
      organizationId,
      projectId,
      postData: formDs.toJSONData(),
    };
    const result = await formDs.validate();
    if (!result) return false;
    await createStore.commitDocument(commitData)
      .then((res) => {
        if (res.failed) {
          message.error(res.message);
          return false;
        } else {
          formDs.reset();
          applicationListDs.reset();
          infrastructureDs.reset();
          platformServiceDs.reset();
          modal.close();
          message.success(formatMessage({ id: 'commit.success' }));
          refresh();
          return true;
        }
      })
      .catch((error) => {
        message.error(error.message);
        return false;
      });
  }
  useImperativeHandle(modalRef, () => (
    {
      handleSubmit,
    }));

  modal.handleOk(async () => {
    try {
      if (await formDs.submit() !== false) {
        refresh();
        return true;
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  });

  function handleAddInfra() {
    infrastructureDs.create();
  }

  function handleRemoveInfra(removeRecord) {
    infrastructureDs.remove(removeRecord);
  }
  function handleAddPlat() {
    platformServiceDs.create();
  }

  function handleRemovePlat(removeRecord) {
    platformServiceDs.remove(removeRecord);
  }

  return (
    <Spin dataSet={formDs} >
      <div className="env-cluster-apply-modal">
        <div className="env-cluster-apply-modal-application-user">
          <div style={{ display: 'inline-flex', width: '3.5rem' }}>
            <span style={{ marginRight: '0.8rem' }}>
              {formatMessage({ id: 'infra.env.approval.model.user' })}
            </span>
            <UserAvatar
              user={{
                id,
                loginName,
                realName,
                imageUrl,
              }}
              // hiddenText
              color="rgba(0, 0, 0, 1)"
              size="0.2rem"
              style={{ maxWidth: '2rem' }}
            />
            {/* <span>{realName}</span> */}
          </div>
          <span style={{ marginRight: '0.8rem' }}>
            {formatMessage({ id: `${intlPrefix}.model.applicationDate` })}
          </span>
          <span style={{ color: 'rgba(0, 0, 0, 1)' }}>{moment().format('YYYY-MM-DD HH:mm:ss')}</span>
        </div>
        <Form dataSet={formDs} columns={12} className="env-cluster-apply-modal-item-padding">
          <Select
            name="applicationPurpose"
            colSpan={6}
          />
          <Select
            name="opsProjectId"
            searchable
            colSpan={6}
          />
          <DatePicker name="deadlineDate" min={moment().add(1, 'days').format('YYYY-MM-DD')} colSpan={6} />
          <TextArea name="applicationDesc" resize="vertical" rows={1} colSpan={6} style={{ minHeight: '0.36rem' }} />
        </Form>
        <div className="env-cluster-apply-modal-divider" />
        <div className="env-cluster-apply-modal-title">{formatMessage({ id: `${intlPrefix}.view.infrastructure` })}</div>
        {map(infrastructureDs.data, (infraRecord, index) => (
          <div className="env-cluster-apply-modal-item-padding">
            <Form record={infraRecord} columns={23} key={infraRecord.id} >
              <TextField name="name" colSpan={11} style={{ width: '3.2rem' }} />
              <TextArea name="desc" resize="vertical" rows={1} colSpan={11} style={{ minHeight: '0.36rem', width: '3.2rem' }} />
              <div style={{ position: 'relative' }}>
                <Button
                  funcType="flat"
                  icon="delete"
                  onClick={() => handleRemoveInfra(infraRecord)}
                  style={{
                    position: 'absolute',
                    top: '0.15rem',
                  }}
                />
              </div>
              <TextField name="version" placeholder="版本，例如：5.0" colSpan={11} style={{ width: '3.2rem' }} />
              <TextField name="configuration" placeholder="期望配置，例如：4核32G" colSpan={11} style={{ width: '3.2rem' }} />
            </Form>
            {index !== infrastructureDs.length - 1 && <div className="env-cluster-apply-modal-block-divider" />}
          </div>
        ))}
        <div className="env-cluster-apply-modal-item-padding">
          <Button
            funcType="flat"
            color="primary"
            icon="add"
            style={{ marginBottom: '0.2rem' }}
            onClick={handleAddInfra}
          >
            {formatMessage({ id: `${intlPrefix}.view.add.infrastructure` })}
          </Button>
        </div>
        <div className="env-cluster-apply-modal-divider" />
        <div className="env-cluster-apply-modal-title">{formatMessage({ id: `${intlPrefix}.view.platform` })}</div>
        {map(platformServiceDs.data, (platRecord, index) => (
          <div className="env-cluster-apply-modal-item-padding">
            <Form record={platRecord} columns={23} key={platRecord.id}>
              <TextField name="name" colSpan={11} style={{ width: '3.2rem' }} />
              <TextArea name="desc" resize="vertical" rows={1} colSpan={11} style={{ minHeight: '0.36rem', width: '3.2rem' }} />
              <div style={{ position: 'relative' }}>
                <Button
                  funcType="flat"
                  icon="delete"
                  onClick={() => handleRemovePlat(platRecord)}
                  style={{
                    position: 'absolute',
                    top: '0.15rem',
                  }}
                />
              </div>
              <TextField name="version" placeholder="版本，例如：1.3.1" colSpan={11} style={{ width: '3.2rem' }} />
              <TextField name="configuration" placeholder="期望配置，例如：4核32G" colSpan={11} style={{ width: '3.2rem' }} />
            </Form>
            {index !== platformServiceDs.length - 1 && <div className="env-cluster-apply-modal-block-divider" />}
          </div>
        ))}
        <div className="env-cluster-apply-modal-item-padding">
          <Button
            funcType="flat"
            color="primary"
            icon="add"
            style={{ marginBottom: '0.2rem' }}
            onClick={handleAddPlat}
          >
            {formatMessage({ id: `${intlPrefix}.view.add.platform` })}
          </Button>
        </div>
        <div className="env-cluster-apply-modal-divider" />
        <div className="env-cluster-apply-modal-title">{formatMessage({ id: `${intlPrefix}.view.others` })}</div>
        <Form dataSet={applicationListDs} columns={12} className="env-cluster-apply-modal-item-padding">
          <TextArea name="desc" resize="vertical" rows={2} colSpan={12} />
        </Form>
      </div>
    </Spin>
  );
});
