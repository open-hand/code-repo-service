# hrds-infra-front

统一代码库服务、统一文档库服务、制品库服务前端工程

## react目录结构

`assets` 存放`css` 文件和`images`

`components` 存放公共组件

`routes` 表示按菜单或路由划分的模块

`locale` 多语言处理

`utils` 公用工具函数等

## routes目录结构

`code-lib` 代码库相关页面

`doc-lib` 文档库相关页面

`personal-setting` 个人设置页面


## 依赖

* Node environment (6.9.0+)
* Git environment
* [@choerodon/boot](https://github.com/choerodon/choerodon-front-boot)
* [@choerodon/master](https://github.com/choerodon/choerodon-front-master)

## 安装与启动
1. 拉取代码到本地：

   ```sh
   git clone https://code.choerodon.com.cn/hzero-rdm01/hrds-infra-front.git
   ```

2. 本地启动

   ```sh
   npm install
   npm start
   ```

  启动后，访问 http://localhost:9090

## 分支
新建开发分支：feature-功能编号

新建bug修复分支：hotfix-功能编号

## git提交规范
[操作][:][空格][commit内容]

[commit内容]请详细填写具体的文件新增/修改/删除操作过程
```
fix：修复bug
feat：更新/新增文件/新特性
modify：重命名
delete：删除文件
docs: 文档调整补充
```


## 相关技术文档

* [React](https://reactjs.org)
* [Mobx](https://github.com/mobxjs/mobx)
* [webpack](https://webpack.docschina.org)
* [gulp](https://gulpjs.com)

## 更具体的开发规范，严格遵守猪齿鱼官方文档的[开发规范](http://choerodon.io/zh/docs/development-guide/front/demo/)
