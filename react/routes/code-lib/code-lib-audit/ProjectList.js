/*eslint-disable*/
import React, { useCallback } from 'react';
import { Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { Spin, TextField } from 'choerodon-ui/pro';
import { useStore } from './stores'
 
const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  border: '1px solid rgba(104,135,232,1)',
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '16px',
  textAlign: 'center',
  border: '1px solid rgba(104,135,232,1)',
};

const ProjectList = ({
  projectListDs, activeProject, onClickProject,
}) => {

  const {
    formatClient,
  } = useStore();

  const record = projectListDs.toJSONData();

  const handleSearch = useCallback((e) => {
    projectListDs.setQueryParameter('name', e.target.value);
    projectListDs.query();
  });

  const getProjectIcon = useCallback((imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" style={imgStyle} />;
    }
    return <div style={iconStyle}>{name[0]}</div>;
  });

  const projectList = () => {
    const liArr = record.length > 0 ? record.map(o =>
      (
        <li
          key={o.id}
          className={activeProject.id === o.id ? 'project-active' : ''}
          onClick={() => onClickProject({ id: o.id, name: o.name })}
        >
          {getProjectIcon(o.imageUrl, o.name)}<span>{o.name}</span>
        </li>
      )) : [];
    // eslint-disable-next-line
    liArr.unshift(
      <li
        key="all"
        className={activeProject.id === 'all' ? 'project-active' : ''}
        onClick={() => onClickProject({ id: 'all', name: '所有项目' })}
      >
        <span className="project-all-icon">
          <Icon type="project_filled" style={{ color: 'rgba(255, 255, 255, 1)', fontSize: '0.14rem' }} />
        </span>
        <span>所有项目</span>
      </li>);
    return liArr;
  };

  return (
    <div className="code-lib-audit-project-list">
      <div className="code-lib-audit-project-list-top">
        <TextField
          style={{ width: '200px' }}
          prefix={<Icon type="search" style={{ fontSize: '0.2rem' }} />}
          placeholder= { formatClient({id: 'list.search'})}
          onKeyUp={handleSearch}
        />
      </div>
      <Spin dataSet={projectListDs}>
        <div className="code-lib-audit-project-list-main">
          <ul>
            {projectList()}
          </ul>
        </div>
      </Spin>
    </div>
  );
};

export default observer(ProjectList);
