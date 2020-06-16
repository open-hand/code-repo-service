import React from 'react';
import { Spin } from 'choerodon-ui';
import './Loading.less';

const Loading = ({
  loading,
}) => (     
  <Spin spinning={loading} wrapperClassName="infra-loading-page">
    <div style={{ width: '100%', height: '100%' }} />
  </Spin>   
);

Loading.propTypes = {

};

export default Loading;
