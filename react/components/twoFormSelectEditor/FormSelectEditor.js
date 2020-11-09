import React, { useRef, useContext, useEffect } from 'react';
import { runInAction } from 'mobx';
import { Form, DataSet, Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from './stores';
import './index.less';

export default observer(({
  name,
  optionDataSetConfig,
  optionDataSet,
  record,
  children,
  addButton,
  formDs,
}) => {
  const formElement = useRef(null);

  async function handleSubmit() {
    const result = await formElement.current.checkValidity();
    return result;
  }
  useEffect(() => {
    if (formDs) {
      formDs.addEventListener('submit', handleSubmit);
    }
    return () => {
      if (formDs) {
        formDs.removeEventListener('submit', handleSubmit);
      }
    };
  });

  if (!record[0] || !record[1]) { return null; }
  const { dsStore } = useContext(Store);
  const valueField = [
    record[0] && record[0].fields.get(name[0]).get('valueField'),
    record[1] && record[1].fields.get(name[1]).get('valueField'),
  ];
  const textField = [
    record[0] && record[0].fields.get(name[0]).get('textField'),
    record[1] && record[1].fields.get(name[1]).get('textField'),
  ];

  function handleCreatOther() {
    runInAction(() => {
      record[0].set(name[0], (record[0].get(name[0]) || []).concat(''));
      record[1].set(name[1], (record[1].get(name[1]) || []).concat(''));
      record[1].set(name[2], (record[2].get(name[2]) || []).concat(undefined));
    });
  }

  const handleChange = [(e, index) => {
    const changedValue = record[0].get(name[0]);
    changedValue[index] = e;
    record[0].set(name[0], changedValue);
  }, (e, index) => {
    const changedValue = record[1].get(name[1]);
    changedValue[index] = e;
    record[1].set(name[1], changedValue);
  }, (e, index) => {
    const changedValue = record[2].get(name[2]);
    changedValue[index] = e;
    record[2].set(name[2], changedValue);
  }];

  function handleDeleteItem(index) {
    const arr = [record[0].get(name[0]) || [], record[1].get(name[1]) || [], record[2].get(name[2]) || []];
    arr[0].splice(index, 1);
    arr[1].splice(index, 1);
    arr[2].splice(index, 1);
    runInAction(() => {
      dsStore[0].splice(index, 1);
      dsStore[1].splice(index, 1);
      dsStore[2].splice(index, 1);
      record[0].set(name[0], arr[0].slice());
      record[1].set(name[1], arr[1].slice());
      record[2].set(name[2], arr[2].slice());
    });
  }

  return (
    <React.Fragment>
      <Form ref={formElement} className="two-form-select-editor" columns={13}>
        {(record[0].get(name[0]) || []).map((v, index) => {
          const value = [v, record[1].get(name[1])[index], record[2].get(name[2])[index]];
          if (!dsStore[0][index]) {
            dsStore[0][index] = new DataSet(optionDataSetConfig[0]);
          }
          return [
            React.createElement(children[0], {
              onChange: (text) => handleChange[0](text, index),
              value: value[0],
              options: dsStore[0][index],
              textField: textField[0],
              valueField: valueField[0],
              allowClear: false,
              clearButton: false,
              colSpan: 4,
              label: record[0].fields.get(name[0]).get('label'),
              required: record[0].fields.get(name[0]).get('required'),
            }),
            React.createElement(children[1], {
              onChange: (text) => handleChange[1](text, index),
              value: value[1],
              options: optionDataSet[1],
              textField: textField[1],
              valueField: valueField[1],
              allowClear: false,
              clearButton: false,
              colSpan: 4,
              label: record[1].fields.get(name[1]).get('label'),
              required: record[1].fields.get(name[1]).get('required'),
            }),
            React.createElement(children[2], {
              onChange: (text) => handleChange[2](text, index),
              value: value[2],
              allowClear: false,
              colSpan: 4,
              label: record[2].fields.get(name[2]).get('label'),
              required: false,
            }),
            <Button
              // colSpan={1}
              className="two-form-select-editor-button"
              disabled={(record[0].get(name[0]) || []).length <= 1}
              onClick={() => handleDeleteItem(index)}
              icon="delete"
            />,
          ];
        })}

      </Form>
      <Button
        colSpan={12}
        color="blue"
        onClick={handleCreatOther}
        style={{ textAlign: 'left', marginTop: '-0.04rem' }}
        icon="add"
      >
        {addButton || '添加'}
      </Button>
    </React.Fragment>
  );
});
