import React, { Component } from 'react';
import { stores, axios, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import { Modal } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import FileSaver from 'file-saver';
import './index.less';

const { AppState } = stores;
@observer
class ExportAuthority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // mode: 'all',
      loading: false,
    };
  }


  /**
   * 输出 excel
   */
  exportExcel = () => {
    const projectId = AppState.currentMenuType.id;
    const { currentMenuType: { organizationId } } = AppState;
    const { setExportModalVisible, psSetDs, formatMessage } = this.props;
    this.setState({
      loading: true,
    });
    axios.get(
      `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/export`,
      {
        responseType: 'blob',
        params: {
          exportType: 'DATA',
          ...psSetDs.queryDataSet.toData()[0],
        },
      },
    )
      .then((blob) => {
        const fileName = '权限记录.xlsx';
        FileSaver.saveAs(blob, fileName);
        Choerodon.prompt(formatMessage({ id: 'success.export', defaultMessage: '导出成功' }));
        setExportModalVisible(false);
      }).finally(() => {
        this.setState({
          loading: false,
        });
      });
  };

  renderButtons = () => (
    <div>
      <Button onClick={() => this.props.setExportModalVisible(false)}>
        取消
      </Button>
      <Button onClick={this.exportExcel} color="primary" funcType="raised">
        确定
      </Button>
    </div>
  )

  render() {
    const { loading } = this.state;
    const { exportModalVisible, setExportModalVisible, formatMessage } = this.props;
    const projectName = AppState.currentMenuType.name;
    return (
      <Modal
        title={formatMessage({ id: 'exportModal.confirm.title', defaultMessage: '权限导出确认' })}
        visible={exportModalVisible}
        onOk={this.exportExcel}
        confirmLoading={loading}
        closable={false}
        className="c7ncd-exportModal"
        footer={this.renderButtons()}
      >
        <div style={{ margin: '10px 0' }}>
          {formatMessage({ id: 'infra.docManage.message.confirm.export' })}
          {' '}
          <span style={{ fontWeight: 500 }}>{projectName}</span>
          {' '}
          {formatMessage({ id: 'dir.path.permission' }, { dirData: '' })}
        </div>
      </Modal>
    );
  }
}

ExportAuthority.propTypes = {

};

export default ExportAuthority;
