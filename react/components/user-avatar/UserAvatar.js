import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Tooltip } from 'choerodon-ui';
import './UserAvatar.less';

function getFirst(str) {
  if (!str) {
    return '';
  }
  const re = /[\u4E00-\u9FA5]/g;
  for (let i = 0, len = str.length; i < len; i += 1) {
    if (re.test(str[i])) {
      return str[i];
    }
  }
  return str[0];
}
const UserAvatar = memo(({
  user,
  color,
  size,
  hiddenText,
  style,
  className,
  type,
  showToolTip,
  placement,
  title,
  ...restProps
}) => {
  const iconSize = size || '.20rem';
  const {
    id,
    loginName,
    realName,
    avatar,
    imageUrl,
    email,
    ldap = true,
    name,
  } = user;
  const img = avatar || imageUrl;
  const renderTooltip = () => {
    if (title) {
      return title;
    }
    if (name) {
      return name;
    } else {
      return ldap ? `${realName}(${loginName})` : `${realName}(${email})`;
    }
  };

  const renderContent = () => (
    <div
      className={`c7n-userHead ${className}`}
      style={{
        display: (id || loginName) ? 'flex' : 'none',
        maxWidth: '1.08rem',
        ...style,
      }}
      {...restProps}
    >
      {
        type === 'datalog' ? (
          <div
            style={{
              width: '.4rem',
              height: '.4rem',
              background: '#b3bac5',
              color: '#fff',
              overflow: 'hidden',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              textAlign: 'center',
              borderRadius: '.04rem',
              flexShrink: 0,
            }}
          >
            {
              img ? (
                <img src={img} alt="" style={{ width: '100%' }} />
              ) : (
                <span
                  style={{
                    width: '.4rem',
                    height: '.4rem',
                    lineHeight: '.4rem',
                    textAlign: 'center',
                    color: '#fff',
                    fontSize: '.12rem',
                  }}
                  className="user-Head-Title"
                >
                  {getFirst(realName)}
                </span>
              )
            }
          </div>
        ) : (
          <div
            style={{
              width: iconSize,
              height: iconSize,
              background: '#c5cbe8',
              overflow: 'hidden',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              marginRight: '.05rem',
              textAlign: 'center',
              borderRadius: '50%',
              flexShrink: 0,
            }}
            className="c7n-infra-user-avatar"
          >
            {
                img ? (
                  <img
                    src={img}
                    alt=""
                    style={{
                      width: iconSize,
                      height: iconSize,
                    }}
                  />
                ) : (
                  <span
                    style={{
                      width: iconSize,
                      height: iconSize,
                      lineHeight: iconSize,
                      textAlign: 'center',
                    }}
                  >
                    {getFirst(realName)}
                  </span>
                )
              }
          </div>
        )
      }
      {
        hiddenText ? null : (
          <span
            style={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              // fontSize: '13px',
              lineHeight: iconSize,
              color: color || 'rgba(0, 0, 0, 0.65)',
            }}
          >
            {`${realName || loginName}`}
          </span>
        )
      }
    </div>
  );
  return showToolTip
    ? (
      <Tooltip placement={placement} title={renderTooltip()} mouseEnterDelay={0.5}>
        {renderContent()}
      </Tooltip>
    )
    : renderContent();
});

UserAvatar.propTypes = {
  user: PropTypes.object,
  color: PropTypes.string,
  size: PropTypes.string,
  hiddenText: PropTypes.bool,
  style: PropTypes.object,
  className: PropTypes.string,
  type: PropTypes.string,
  showToolTip: PropTypes.bool,
  title: PropTypes.string,
  placement: PropTypes.string,
};

UserAvatar.defaultProps = {
  className: '',
  showToolTip: true,
};

export default UserAvatar;
