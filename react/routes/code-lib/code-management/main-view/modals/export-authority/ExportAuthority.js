import React, { Component } from 'react';
import { stores, axios, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import { Modal } from 'choerodon-ui';
import FileSaver from 'file-saver';

// const RadioGroup = Radio.Group;
const { AppState } = stores;
// const radioStyle = {
//   display: 'block',
//   height: '30px',
//   lineHeight: '30px',
// };
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
          // repositoryIds: appServiceDs.toData()[0].repositoryIds.map(o => o.repositoryId).join(','),
          ...psSetDs.queryDataSet.toData()[0],
        },
      },
    )
      .then((blob) => {
        const fileName = '权限记录.xlsx';
        FileSaver.saveAs(blob, fileName);

        // const tempUrl = URL.createObjectURL(data);
        // const a = document.createElement('a');
        // a.href = tempUrl;
        // a.download = '权限记录.xlsx';
        // a.style.position = 'absolute';
        // a.style.opacity = 0;
        // document.body.appendChild(a);
        // a.click();
        // document.body.removeChild(a);

        Choerodon.prompt(formatMessage({ id: 'success.export', defaultMessage: '导出成功' }));
        setExportModalVisible(false);
      }).finally(() => {
        this.setState({
          loading: false,
        });
      });
  };

  render() {
    const { loading } = this.state;
    const { exportModalVisible, setExportModalVisible, formatMessage } = this.props;
    const projectName = AppState.currentMenuType.name;
    return (
      <Modal
        title={formatMessage({ id: 'exportModal.confirm.title', defaultMessage: '权限导出确认' })}
        visible={exportModalVisible}
        onOk={this.exportExcel}
        onCancel={() => setExportModalVisible(false)}
        confirmLoading={loading}
      >
        <div style={{ margin: '10px 0' }}>
          {formatMessage({ id: 'infra.docManage.message.confirm.export' })}
          {' '}
          <span style={{ fontWeight: 500 }}>{projectName}</span>
          {' '}
          {formatMessage({ id: 'dir.path.permission' }, { dirData: '' })}
        </div>
        {/* <RadioGroup onChange={this.handleExportChange} value={mode}>
          <Radio style={radioStyle} value="show">当前页面显示字段</Radio>
          <Radio style={radioStyle} value="all">全部字段</Radio>
        </RadioGroup> */}
      </Modal>
    );
  }
}

ExportAuthority.propTypes = {

};

export default ExportAuthority;
