import React, { useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { inject } from 'mobx-react';
import moment from 'moment';
import { DataSet, Form, SelectBox, DatePicker, TextArea } from 'choerodon-ui/pro';
import BatchApproveServices from '@/routes/code-lib/code-management/main-view/ps-approval/services';


export default inject('AppState')(observer(({
  selects, modal, AppState, func,
}) => {
  /**
   * 提交事件
   * @returns {Promise<void>}
   */
  const handleOk = async () => {
    const res = await BatchDs.validate();
    // 如果通过
    if (res) {
      const list = selects.map(i => ({
        id: i.get('id'),
        objectVersionNumber: i.get('objectVersionNumber'),
      }));
      const { organizationId, projectId } = AppState.menuType;
      // 如果是批量通过
      if (BatchDs.current.get('result') === 'pass') {
        const expiresAt = moment(BatchDs.current.get('overdueTime')).format('YYYY-MM-DD HH:mm:ss');
        try {
          await BatchApproveServices
            .axiosPostBatchPass(organizationId, projectId, expiresAt, list);
          func();
          return true;
        } catch (e) {
          return false;
        }
      } else {
        // 如果是批量驳回
        const approvalMessage = BatchDs.current.get('rejectReason');
        try {
          await BatchApproveServices
            .axiosPostBatchReject(organizationId, projectId, approvalMessage, list);
          func();
          return true;
        } catch (e) {
          return false;
        }
      }
    }
    return false;
  };

  modal.handleOk(handleOk);

  const BatchDs = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'result',
      type: 'string',
      label: '权限审批结果',
      options: new DataSet({
        data: [{
          text: '批量通过',
          value: 'pass',
        }, {
          text: '批量驳回',
          value: 'reject',
        }],
      }),
      textField: 'text',
      valueField: 'value',
      defaultValue: 'pass',
    }, {
      name: 'overdueTime',
      type: 'date',
      label: '过期时间',
    }, {
      name: 'rejectReason',
      type: 'string',
      label: '批量驳回原因',
      dynamicProps: {
        required: ({ record }) => record.get('result') === 'reject',
      },
    }],
  }), []);
  return (
    <Form dataSet={BatchDs}>
      <SelectBox name="result" />
      {
        BatchDs?.current?.get('result') === 'pass' ? (
          <DatePicker name="overdueTime" />
        ) : (
          <TextArea name="rejectReason" />
        )
      }
    </Form>
  );
}));
