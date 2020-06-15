import React from 'react';
import { isNil } from 'lodash';
import { Table } from 'choerodon-ui/pro';
import TimePopover from '@/components/time-popover/TimePopover';
import './index.less';

const cssPrefix = 'code-lib-management-security-audit';
const { Column } = Table;

const DetailTable = ({ dataSet }) => {
  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }
  return (
    <Table className={`${cssPrefix}-childTable`} dataSet={dataSet} queryBar="none">
      <Column name="repositoryName" width={200} />
      <Column name="glAccessLevel" />
      <Column name="glExpiresAt" />
      <Column name="lastUpdateDate" renderer={renderTime} />
    </Table>
  );
};

export default DetailTable;
