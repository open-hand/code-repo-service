import React, { useState, useImperativeHandle } from 'react';
import { Form, Input, message } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';

const FormItem = Form.Item;
const intlPrefix = 'user.changepwd';
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 9 },
  },
};

function EditPassword(props) {
  const [confirmDirty, setConfirmDirty] = useState(undefined);
  const { form, intl, useStore, refresh, userId, loginName } = props;


  const compareToFirstPassword = (rule, value, callback) => {
    if (value && value !== form.getFieldValue('password')) {
      callback(intl.formatMessage({ id: `${intlPrefix}.twopwd.pattern.msg` }));
    } else {
      callback();
    }
  };

  const validateToNextPassword = (rule, value, callback) => {
    if (value && confirmDirty) {
      form.validateFields(['rePassword'], { force: true });
    }
    if (value.indexOf(' ') !== -1) {
      callback(intl.formatMessage({ id: 'infra.personal.validate.password' }));
    }
    callback();
  };

  const handleConfirmBlur = (e) => {
    const { value } = e.target;
    setConfirmDirty(confirmDirty || !!value);
  };

  const handleSubmit = () => {
    props.form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          userId,
          loginName,
        };
        useStore.updateProdPassword(params)
          .then((res) => {
            if (res.failed) {
              message.error(res.message);
            } else {
              form.resetFields();
              refresh();
              message.success(intl.formatMessage({ id: 'modify.success' }));
              props.modal.close();
            }
          })
          .catch((error) => {
            Choerodon.handleResponseError(error);
          });
      }
    });
  };
  useImperativeHandle(props.forwardref, () => (
    {
      handleSubmit,
    }));


  const render = () => {
    const { getFieldDecorator } = form;
    return (
      <div className="ldapContainer">
        <Form layout="vertical">
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('oldPassword', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.oldpassword.require.msg` }),
              }, {
                validator: validateToNextPassword,
              }],
              validateTrigger: 'onBlur',
            })(<Input
              autoComplete="off"
              label={<FormattedMessage id={`${intlPrefix}.oldpassword`} />}
              type="password"
              showPasswordEye
            />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('password', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.newpassword.require.msg` }),
              }, {
                validator: validateToNextPassword,
              }],
              validateTrigger: 'onBlur',
              validateFirst: true,
            })(<Input
              autoComplete="off"
              label={<FormattedMessage id={`${intlPrefix}.newpassword`} />}
              type="password"
              showPasswordEye
            />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('rePassword', {
              rules: [{
                required: true,
                message: intl.formatMessage({ id: `${intlPrefix}.confirmpassword.require.msg` }),
              }, {
                validator: compareToFirstPassword,
              }],
              validateTrigger: 'onBlur',
              validateFirst: true,
            })(<Input
              autoComplete="off"
              label={<FormattedMessage id={`${intlPrefix}.confirmpassword`} />}
              type="password"
              onBlur={handleConfirmBlur}
              showPasswordEye
            // disabled={user.ldap}
            />)}
          </FormItem>
        </Form>
      </div>
    );
  };
  return render();
}
export default Form.create({})(observer(EditPassword));
