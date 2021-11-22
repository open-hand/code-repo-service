import {
    Button, Modal,message
} from 'choerodon-ui/pro';
import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import './index.less';
import { Page, Content, Breadcrumb } from '@choerodon/boot';
//@ts-ignore
import logo from './assets/logo.svg';
import { StatusTag } from "@choerodon/components";
import { ButtonColor } from 'choerodon-ui/pro/lib/button/enum';
import ApproveForm from './approve-form'
import { MIDDLE } from '@/common/getModalWidth';
import { siteOpenApi } from '@choerodon/master';
import { inject } from 'mobx-react';
import { cloneDeep } from 'lodash';
import { injectIntl } from 'react-intl';

const ApproveFormKey = Modal.key()

const PageContent = (props: any) => {

    const { AppState } = props

    const prefix = 'c7n-personal-setting-thirdPartServiceBind'

    const [listData, setListData] = useState([
        {
            title: '汉得焱牛开放平台',
            icon: logo,
            desc: '绑定开放平台后，您可以提交工单，并进入开放平台查看工单详情。',
            bind: true
        },
    ])

    const getIfBindOpenPlatform = async () => {
        const res = await siteOpenApi.getIfBindOpenPlatform();
        let cloneData = cloneDeep(listData)
        cloneData[0].bind = res
        setListData(cloneData)
    }

    useEffect(() => {
        getIfBindOpenPlatform()
    }, [])


    const goAction = async (item: any) => {
        if (item.bind) {
            try {
                await siteOpenApi.removeBind({
                    user_id: AppState.getUserId
                })
                message.success('解绑汉得焱牛开放平台成功！')
                getIfBindOpenPlatform()
            } catch (error) {
                console.log(error)
            }
        } else {
            Modal.open({
                key: ApproveFormKey,
                title: '认证绑定',
                children: <ApproveForm refresh={getIfBindOpenPlatform} />,
                style: {
                    width: MIDDLE,
                },
                okText: '绑定',
            });
        }
    }

    return (
        <Page>
            <Breadcrumb />
            <Content>
                {listData.map(item => {
                    return (
                        <div className={`${prefix}-list-item`}>
                            <img src={logo} className={`${prefix}-list-item-logo`} alt="" />
                            <p className={`${prefix}-list-item-title`}>
                                {item.title}
                                <StatusTag
                                    type="default"
                                    colorCode={item.bind ? 'success' : 'pending'}
                                    name={item.bind ? '已绑定' : '未绑定'}
                                    style={{ lineHeight: "16px", marginLeft: 12, width: "42px" }}
                                />
                            </p>
                            <p className={`${prefix}-list-item-desc`}>{item.desc}</p>
                            <Button onClick={() => { goAction(item) }} color={item.bind ? 'default' as ButtonColor : 'primary' as ButtonColor} className={`${prefix}-list-item-btn`}>{item.bind ? '解绑' : '绑定'}</Button>
                        </div>
                    )
                })}
            </Content>
        </Page>
    );
};

export default injectIntl(inject('AppState')(observer(PageContent)));
