import React from 'react';
import Loading from '../loading';
import './index.less';
import picDefault from './emptyProject.svg';

const Empty = ({ style, loading, pic, title, description, extra }) => (
  loading ? <Loading /> : (
    <div
      className="infra-empty-page"
      style={style}
    >
      <div
        className="infra-empty-page-content"
      >
        <div className="infra-empty-page-imgWrap">
          <img src={pic || picDefault} alt="" className="infra-empty-page-imgWrap-img" />
        </div>
        <div
          className="infra-empty-page-textWrap"
        >
          <h1 className="infra-empty-page-title">
            {title || ''}
          </h1>
          <div className="infra-empty-page-description">
            {description || ''}
          </div>
          <div style={{ marginTop: 10 }}>
            {extra}
          </div>
        </div>
      </div>
    </div>
  )
);
export default Empty;
