# rducm
## 表及数据初始化

### 初次使用

如果是初始化项目，请先创建相应的数据库，接着进行表结构及数据初始化。如：
```
CREATE DATABASE hzero_platform DEFAULT CHARACTER SET utf8mb4;
GRANT ALL PRIVILEGES ON hzero_platform.* TO hzero@'%';
FLUSH PRIVILEGES;
```

### <a target="_blank" href="https://code.choerodon.com.cn/hzero-hzero/hzero-resource/tree/0.11.0.RELEASE"><b>hzero-resource</b></a>
表结构及初始化数据在 <a target="_blank" href="https://code.choerodon.com.cn/hzero-hzero/hzero-resource/tree/1.2.0.RELEASE"><b>hzero-resource</b></a> 项目下。

* groovy：表结构 groovy 脚本
* init-data：初始化数据
* tool-jar：执行初始化的工具
* database-init.sh：执行初始化的脚本

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1563380297.jpg)

### database-init.sh

* 脚本内容如图

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1563380641.jpg)

* 需要替换以下配置

    - `spring.datasource.url`：数据库连接URL
    - `spring.datasource.username`：数据库名称
    - `spring.datasource.password`：数据库密码
    - `data.update.exclusion`:排除数据更新的表或列
    - `service`：要初始化或升级的服务，不同的服务需更改此处执行
    - `dir`：要初始化或升级的脚本路径，不同的数据库需更改此处执行

<blockquote class="note">
请注意：表结构和初始化数据是分开执行的，便于服务升级。如果更新表结构，设置 dir=groovy/$service 即可；如果更新数据，设置 dir=init-data/$service 即可。特别需要注意的是，hzero-iam 服务下的数据分 SaaS 版本和 OP 版本，请选择对应版本的数据初始化。
</blockquote>

### 默认登录用户

hzero-iam 的数据初始化好之后，默认的用户名密码为 [admin/Admin@123]，可使用此用户密码登陆系统。

### 数据处理

在 **部署后端服务** 完成后，需调用IAM服务的初始化接口进行一些初始化操作。

* 调用 `[GET /v1/init/menu-level-path]` 初始化菜单层级路径(`h_level_path`)，如果发现有些 level_path 不正确，可以直接将本列清掉并调用该接口重新生成。
* 调用 `[GET /v1/init/role-level-path]` 初始化角色层级路径(`h_level_path`、`h_inherit_level__path`)，如果发现有些 level_path 不正确，可以直接将这两列清掉并调用该接口重新生成。
* 调用 `[GET /v1/init/super-role-permission-sets]` 将权限集分别分配到平台超级管理员和租户超级管理员上。

![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1550746345.jpg)



## 服务初始化
<blockquote class="note"> 
服务初始化前，请先确定项目或产品是使用SaaS版还是OP版，SaaS 版本支持多租户，OP版本没有租户概念。
</blockquote>

* 正常情况下依赖于 hzero 的服务进行开发，hzero 服务发新版之后可自行决定是否升级服务。注：所有HZERO服务及组件在发布时都会统一升级一个版本，升级服务时可定一个稳定版本即可。
* 以 hzero-gateway 服务为例，首先创建一个空的工程，一般以产品或编码为前缀命名，如 demo-gateway。

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/demo_1550742871.jpg)

* 修改 `pom.xml`，可以 hzero-parent 为父 pom，也可自行建一个所属产品或项目的顶级 parent 项目进行统一版本管理，接着引入依赖的服务。

    > 引用服务坐标时，可在服务清单下找到服务的坐标，SaaS 版本的 artifactId 是以 -saas 结尾的，请注意区分。

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/demo_1550742991.jpg)
    
    ```xml
    <parent>
        <groupId>org.hzero</groupId>
        <artifactId>hzero-parent</artifactId>
        <version>0.11.0.RELEASE</version>
    </parent>

    <dependencies>
        <!-- hzero-gateway -->
        <dependency>
            <groupId>org.hzero</groupId>
            <artifactId>hzero-gateway</artifactId>
        </dependency>

        <!-- eureka-client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- config-client -->
        <dependency>
            <groupId>io.choerodon</groupId>
            <artifactId>choerodon-starter-config-client</artifactId>
        </dependency>
        <!-- mysql -->
        <dependency>
            <artifactId>mysql-connector-java</artifactId>
            <groupId>mysql</groupId>
        </dependency>

    </dependencies>
    ```

    

* 第一次使用需在 pom 中加入HZERO的Maven仓库地址：

    ```xml
    <repositories>
        <repository>
            <id>HandPublic</id>
            <name>Hand-Public Repository</name>
            <url>http://nexus.saas.hand-china.com/content/repositories/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>
        <repository>
            <id>HzeroRelease</id>
            <name>Hzero-Release Repository</name>
            <url>http://nexus.saas.hand-china.com/content/repositories/Hzero-Release/</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>HzeroSnapshot</id>
            <name>Hzero-Snapshot Repository</name>
            <url>http://nexus.saas.hand-china.com/content/repositories/Hzero-Snapshot/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    ```

* 从依赖包中复制出配置文件到 `resources/` 目录下，主要修改 application.yml 中的一些配置即可。

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/demo_1550745832.jpg)

* 修改启动类，加入对应的 `@EnableHZeroXxx` 注解，会自动扫描依赖服务的包。

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/demo_1550745930.jpg)


* **添加客户端依赖**

    需自行根据使用的注册中心、配置中心、数据库驱动不同，加入相应的依赖，具体的依赖或配置请参考 [客户端依赖](../../service-config/dependency/)


* 之后就可以运行服务测试了。  

## 部署后端服务

后端服务主要包括基础服务和平台通用服务，因为服务间有依赖，服务需按一定顺序安装，可根据下面的列表按装HZero平台必备服务。每个服务可按 **服务初始化** 中的流程创建自己产品或项目的服务。

服务|简码|默认端口|描述
---|---|---|---
hzero-register|HREG|8000|注册中心
hzero-config|HCFG|8010|配置服务
hzero-gateway|HGWY|8080|网关服务
hzero-oauth|HOTH|8020|认证服务
hzero-iam|HIAM|8030|IAM服务
hzero-swagger|HSWG|8050|Swagger测试服务（开发环境可装）
hzero-platform|HPFM|8100|平台服务

下面以注册中心为例，利用jenkins启动服务(**注意拉取自己创建服务的源码**)。

* 以SSH方式拉取项目源码：`# git clone git@xxxx/demo-register.git`
* 下载 [run.sh](/files/docs/installation-configuration/install/run.sh) 脚本放到 hzero-register 根目录下
* 修改 run.sh，将`JAR`设置为服务名，`MPORT`设置为配置文件中 `management.port` 的端口。该脚本会拉取最新代码、打包、停止原服务、启动新服务。

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546075203.jpg)

* 设置 run.sh 可执行：`# chmod +x run.sh`

* Jenkins 添加 SSH 服务器：在系统管理>系统配置下，用于连接部署的服务器

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546077096.jpg)

* 创建项目

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546077246.jpg)

* 基础配置

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546077358.jpg)

* 增加构建配置

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546077416.jpg)

* 构建并启动服务

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546077925.jpg)

* 按照服务列表一个个按上面的步骤部署服务即可。其中，`hzero-config`、`hzero-iam`、`hzero-asgard`、`hzero-swagger` 需要初始化表及数据，按照 `数据初始化` 操作即可。

* hzero_platform 的数据初始化完成后，请检查 `fd_organization`、`hpfm_tenant`、`hpfm_group` 三张表，是否都有一条ID=0的数据，由于表自增，需手动将 HZERO 这个租户的ID设置为0。  
    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546432910.jpg)

* 服务都部署成功后，可以查看注册中心上的服务是否都注册成功  

    *注册中心地址：`http://dev.hzero.org:8000`*

    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546430614.jpg)

* 访问 swagger 页面，测试API是否可用  

    *Swagger地址：`http://dev.hzero.org:8080/swagger/swagger-ui.html`*

    查看服务列表：  
    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546430697.jpg)

    例如找平台服务的API进行测试，首先需要授权，默认会跳转到 oauth 登录页面进行登录授权。之后便可访问API测试。
    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546433265.jpg)  
    ![](http://hzerodoc.saas.hand-china.com/img/docs/installation-configuration/install/1546433435.jpg)
