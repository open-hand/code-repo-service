import React from 'react';
import { Tooltip } from 'choerodon-ui';

export default function renderFullName({ text }) {
  return (
    <Tooltip title={text}>
      {text}
    </Tooltip>
  );
}
