import React, { Component } from 'react';
import { stores, axios, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import FileSaver from 'file-saver';

const { AppState } = stores;
@observer
class ExportAuthority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  componentDidMount() {
    const { loading } = this.state;
    const { modal } = this.props;
    modal.update({
      onOk: this.exportExcel,
      confirmLoading: loading,
    });
  }

  /**
   * 输出 excel
   */
  exportExcel = () => {
    const organizationId = AppState.currentMenuType.id;
    const { formatMessage, modal } = this.props;
    this.setState({
      loading: true,
    });
    axios.get(`/rducm/v1/organizations/${organizationId}/projects/gitlab/repositories/members/export`, {
      responseType: 'blob',
      params: {
        projectIds: this.props.activeProject.id === 'all' ? undefined : this.props.activeProject.id?.toString(),
        ...this.props.psViewDs.queryDataSet.toData()[0],
        exportType: 'DATA',
      },
    })
      .then((blob) => {
        const fileName = '权限记录.xlsx';
        FileSaver.saveAs(blob, fileName);

        Choerodon.prompt(formatMessage({ id: 'success.export', defaultMessage: '导出成功' }));
        modal.update({ closable: false });
      }).finally(() => {
        this.setState({
          loading: false,
        });
      });
  };

  render() {
    const organizationName = AppState.currentMenuType.name;
    const { formatMessage } = this.props;
    return (
      <div style={{ margin: '10px 0' }}>
        {formatMessage({ id: 'infra.docManage.message.confirm.export' })}
        {' '}
        <span style={{ fontWeight: 500 }}>{organizationName}</span>
        {' '}
        {formatMessage({ id: 'dir.path.permission' }, { dirData: '' })}
      </div>
    );
  }
}

ExportAuthority.propTypes = {

};

export default ExportAuthority;
