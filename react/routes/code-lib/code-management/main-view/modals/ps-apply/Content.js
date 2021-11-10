/* eslint-disable */
import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, TextField } from 'choerodon-ui/pro';
import { Choerodon, axios } from '@choerodon/boot';
import { useAddMemberStore } from './stores';
import { message } from 'choerodon-ui';
import './index.less';

export default observer(() => {
  const {
    formDs,
    modal,
    refresh,
    projectId,
    organizationId,
  } = useAddMemberStore();

  const record = formDs.current;
  if (!record) return;

  async function handleOk () {
    try {
      const res = await formDs.submit();
      if (res && res?.failed) {
        message.error(res?.message);
        return false;
      }
      refresh();
      return true;
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  }

  modal.handleOk(handleOk);

  // eslint-disable-next-line
  function getClusterOptionProp ({ record }) {
    return {
      disabled: Number(record.data.value.substring(1)) >= 50 || record.data.value === formDs.current.get('oldAccessLevel'),
    };
  }

  function rendererOpt (data) {
    return (
      <div style={{ width: '100%' }}>
        {data.text && <span className="code-lib-management-ps-apply-old-level" >{formDs.current.get('applicantType') === 'MEMBER_PERMISSION_CHANGE' ? `${formDs.current.getField('oldAccessLevel').getText()} -> ` : ''}</span>}
        <span>{data.text}</span>
      </div>);
  }

  function getLevelOption (data) {
    return (
      // <Tooltip placement="left" title={`${record.get('email')}`}>
      rendererOpt(data)
      // </Tooltip>
    );
  }

  async function handleSelect (value) {
    await axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants/self/detect-applicant-type?repositoryId=${value}`)
      .then((response) => {
        if (response.failed) {
          Choerodon.prompt(response.message);
          formDs.current.set('applicantType', '');
          formDs.current.set('oldAccessLevel', '');
          return;
        }
        formDs.current.set('applicantType', response.applicantType);
        formDs.current.set('oldAccessLevel', response.oldAccessLevel ? `L${response.oldAccessLevel}` : '');
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
      });
  }
  // eslint-disable-next-line no-shadow
  function optionsFilter (record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50 || record.data.value === formDs.current.get('oldAccessLevel'));
    return flag;
  }

  // eslint-disable-next-line no-shadow
  function searchMatcher ({ record, text, textField }) {
    const isTrue = record.get(textField).indexOf(text) !== -1 || record.get('repositoryCode').indexOf(text) !== -1;
    return isTrue;
  }

  return (
    <Form dataSet={formDs}>
      <TextField name="applicantUserName" disabled />
      <Select name="repositoryId" clearButton={false} onChange={handleSelect} searchable searchMatcher={searchMatcher} />
      <Select name="applicantType" disabled />
      <Select
        name="accessLevel"
        clearButton={false}
        onOption={getClusterOptionProp}
        optionRenderer={getLevelOption}
        renderer={rendererOpt}
        optionsFilter={optionsFilter}
      />
    </Form>
  );
});
