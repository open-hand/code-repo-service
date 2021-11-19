import {
  Form, TextField, Modal, message,
} from 'choerodon-ui/pro';
import { omit } from 'lodash';
import React, { } from 'react';
import { observer } from 'mobx-react-lite';
import { CaptchaField } from '@choerodon/components/lib/index.js';
import { Icon } from 'choerodon-ui';
import { useSaaSApproveFormStore } from './stores';
import { ShowHelp } from 'choerodon-ui/pro/lib/field/enum';
import { LabelLayoutType } from 'choerodon-ui/pro/lib/form/Form';
import { siteOpenApi } from '@choerodon/master';
import './index.less';
import { MIDDLE } from '@/common/getModalWidth';
import ApprovePswForm from '../approve-psw-form';

const approvePswFormKey = Modal.key();

const ApproveFormContent = () => {
  const {
    prefixCls,
    emailApproveDataSet,
    modal,
    refresh
  } = useSaaSApproveFormStore();

  const emailApproveDataSetSubmit = async () => {
    const res = await emailApproveDataSet.validate();
    if (res) {
      const postData = omit(emailApproveDataSet.current?.toData(),
        ['validateSuccess', '__dirty']);
      try {
        const ajaxRes = await siteOpenApi.bindUser(postData);

        if (ajaxRes) {
          message.success('认证绑定成功');
          refresh()
          return true;
        }
          Modal.open({
            key: approvePswFormKey,
            title: '请输入密码',
            children: <ApprovePswForm refresh={refresh} captcha={emailApproveDataSet.current?.get('captcha')} email={emailApproveDataSet.current?.get('email')} id={emailApproveDataSet.current?.get('user_id')} />,
            style: {
              width: MIDDLE,
            },
            okText: '保存',
          });
        return true;
      } catch (error) {
        console.log(error);
      }
    }
    return false;
  };

  modal.handleOk(emailApproveDataSetSubmit);

  return (
    <div className={prefixCls}>
      <div className={`${prefixCls}-tips`}>
        <Icon type="info" className={`${prefixCls}-icon`} />
        认证绑定可绑定现有猪齿鱼邮箱地址或汉得焱牛开放平台邮箱地址，绑定之后可用邮箱和
        密码登录汉得焱牛开放平台查看工单详情
      </div>
      <Form
        columns={2}
        labelLayout={'float' as LabelLayoutType}
        dataSet={emailApproveDataSet}
      >
        <TextField
          colSpan={2}
          name="email"
          label="邮箱"
          help="若有汉得焱牛开放平台邮箱可填写开放平台邮箱地址"
          showHelp={'tooltip' as ShowHelp}
          type="eamil"
        />
        <CaptchaField
          // @ts-ignore
          colSpan={2}
          type="email"
          dataSet={emailApproveDataSet}
          ajaxRequest={siteOpenApi.getCaptcha}
          maxAge={600}
        />
      </Form>
    </div>
  );
};

export default observer(ApproveFormContent);
