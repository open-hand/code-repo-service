import React, { useState, useImperativeHandle } from 'react';
import { Form, Input } from 'choerodon-ui';
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
  const { form, intl, UserInfoStore, organizationId, userName, name, setEnablePwd } = props;


  const compareToFirstPassword = (rule, value, callback) => {
    if (value && value !== form.getFieldValue('userPassword')) {
      callback(intl.formatMessage({ id: `${intlPrefix}.twopwd.pattern.msg` }));
    } else {
      callback();
    }
  };

  const validateToNextPassword = (rule, value, callback) => {
    if (value && confirmDirty) {
      form.validateFields(['reUserPassword'], { force: true });
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

  const handleSubmit = (callback) => {
    props.form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          userName,
          name,
        };
        UserInfoStore.updatePassword(organizationId, params)
          .then((res) => {
            if (res.failed) {
              Choerodon.prompt(res.message);
            } else {
              form.resetFields();
              setEnablePwd(res);
              Choerodon.prompt(intl.formatMessage({ id: 'modify.success' }));
              callback();
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
            {getFieldDecorator('oldUserPassword', {
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
            {getFieldDecorator('userPassword', {
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
            {getFieldDecorator('reUserPassword', {
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
