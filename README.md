简体中文 | [English](./README.en_US.md)
 
# hrds-code-repo

`hrds-code-repo` hrds-code-repo是Choerodon平台的代码库管理服务. 当前版本为: `0.23.0-alpha.1`

hrds-code-repo通过与Gitlab集成, 实现在Choerodon平台一站式管理代码库的功能。

## 特性
`hrds-code-repo` 含有以下功能:    

- `权限分配` ：对应用服务进行管理
- `权限分配`: 支持查看和分配团队成员的代码库权限
- `权限申请`: 支持向项目管理员申请应用服务的权限
- `权限审计`: 支持定时审计代码库与Gitlab权限不一致的数据, 并支持修复不一致权限
- `安全审计`: 支持查看团队成员的权限分布情况
- `保护分支/标记`: 支持查看和设置保护分支和保护标记, 用于对分支(branches)和标记(tags)的权限进行限制
- `操作日志`: 记录了权限分配的操作日志
- `总览`: 支持查看各应用服务的一些信息

## 前置要求
- [JAVA](https://www.java.com/en/)：`hrds-code-repo`基于Java8进行开发
- [GitLab](https://about.gitlab.com/)：`hrds-code-repo`使用`GitLab`进行代码的托管。同时，通过基于`GitLab Runner`实现持续集成以完成代码编译，单元测试执行，代码质量分析，docker镜像生成，helm chart打包，服务版本发布等自动化过程
- [Harbor](https://vmware.github.io/harbor/cn/)：企业级Docker registry 服务，用于存放服务版本所对应的docker镜像
- [Kubernetes](https://kubernetes.io/)：容器编排管理工具，用于部署服务版本所对应的helm chart包
- [ChartMuseum](https://chartmuseum.com/)：Helm Chart仓库，用于存放服务版本所对应的helm chart包
- [Sonarqube](https://www.sonarqube.org/)：管理代码质量的开放平台，用于管理服务的代码质量
- [MySQL](https://www.mysql.com)：主流数据库之一，用于`hrds-code-repo`的数据持久化
- [Redis](https://redis.io/)：内存数据库，用于数据缓存和部分非持久化数据存储

## 服务依赖

* `go-register-server`: 注册中心，在线上环境代替本地的`eureka-server`
* `iam-service`：用户服务，与用户有关的操作依赖与此服务
* `api-gateway`: 网关服务
* `oauth-server`: 授权服务
* `manager-service`: 管理服务
* `asgard-service` : 事务一致性服务
* `notify-service` : 通知服务
* `devops-service`: Devops服务

## 服务配置

* `bootstrap.yml`:

  ```yaml
  server:
    port: 7140
  management:
    server: 
      port: 7141
    endpoints:
      web:
        exposure:
          include: '*'
  
  spring:
    profiles:
      active: default
    cloud:
      config:
        fail-fast: false
        # 是否启用配置中心
        enabled: ${SPRING_CLOUD_CONFIG_ENABLED:false}
        # 配置中心地址
        uri: ${SPRING_CLOUD_CONFIG_URI:http://dev.hzero.org:8010}
        retry:
          # 最大重试次数
          maxAttempts: 6
          multiplier: 1.1
          # 重试间隔时间
          maxInterval: 2000
        # 标签
        label: ${SPRING_CLOUD_CONFIG_LABEL:}
  
  eureka:
    instance:
      # 以IP注册到注册中心
      preferIpAddress: ${EUREKA_INSTANCE_PREFER_IP_ADDRESS:true}
      leaseRenewalIntervalInSeconds: 10
      leaseExpirationDurationInSeconds: 30
      # 服务的一些元数据信息
      metadata-map:
        VERSION: 1.2.0.RELEASE
    client:
      serviceUrl:
        # 注册中心地址
        defaultZone: ${EUREKA_DEFAULT_ZONE:http://dev.hzero.org:8000/eureka}
      registryFetchIntervalSeconds: 10
      disable-delta: true
  ```

* `application.yml`:

  ```yaml
  spring:
    application:
      name: hrds-code-repo
    datasource:
      url: ${SPRING_DATASOURCE_URL:jdbc:mysql://db.hzero.org:3306/hzero_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false}
      username:  ${SPRING_DATASOURCE_USERNAME:hzero}
      password: ${SPRING_DATASOURCE_PASSWORD:hzero}
      hikari:
        # 连接池最小空闲连接数
        minimum-idle: ${SPRING_DATASOURCE_MINIMUM_IDLE:20}
        # 连接池允许的最大连接数
        maximum-pool-size: ${SPRING_DATASOURCE_MAXIMUM_POOL_SIZE:200}
        # 等待连接池分配连接的最大时长（毫秒）
        connection-timeout: ${SPRING_DATASOURCE_CONNECTION_TIMEOUT:30000}
    redis:
      host: redis.hzero.org
      port: 6379
      database: 1
      jedis:
        pool:
          # 资源池中最大连接数
          # 默认8，-1表示无限制；可根据服务并发redis情况及服务端的支持上限调整
          max-active: ${SPRING_REDIS_POOL_MAX_ACTIVE:50}
          # 资源池运行最大空闲的连接数
          # 默认8，-1表示无限制；可根据服务并发redis情况及服务端的支持上限调整，一般建议和max-active保持一致，避免资源伸缩带来的开销
          max-idle: ${SPRING_REDIS_POOL_MAX_IDLE:50}
          # 当资源池连接用尽后，调用者的最大等待时间(单位为毫秒)
          # 默认 -1 表示永不超时，设置5秒
          max-wait: ${SPRING_REDIS_POOL_MAX_WAIT:5000}
    resources:
      # 资源缓存时间，单位秒
      cache.period: 3600
      # 开启gzip压缩
      chain.gzipped: true
      # 启用缓存
      chain.cache: true
  
  server: 
    undertow:
      # 设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接
      # 默认值为8，建议设置每个CPU核心一个线程
      io-threads: ${SERVER_UNDERTOW_IO_THREADS:4}
      # 阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程
      # 默认等于 io-threads*8，它的值设置取决于系统的负载，可适当调大该值
      worker-threads: ${SERVER_UNDERTOW_WORKER_THREADS:128}
      # 每块buffer的空间大小，越小空间被利用越充分
      # 不要设置太大，以免影响其他应用，合适即可
      buffer-size: ${SERVER_UNDERTOW_BUFFER_SIZE:1024}
      # 是否分配的直接内存(NIO直接分配的堆外内存)
      # 默认false
      direct-buffers: true
      # HTTP POST请求最大的大小
      # 默认0，无限制，可设置10M
      max-http-post-size: 10485760
      allow-unescaped-characters-in-url: true
  
  feign:
    hystrix:
      enabled: true
      command:
        default:
          execution:
            isolation:
              thread:
                timeoutInMilliseconds: 30000
  
  hystrix:
    threadpool:
      default:
        # 执行命令线程池的核心线程数，也是命令执行的最大并发量
        # 默认10
        coreSize: 1000
        # 最大执行线程数
        maximumSize: 1000
    command:
      default:
        execution:
          isolation:
            thread:
              # HystrixCommand 执行的超时时间，超时后进入降级处理逻辑。一个接口，理论的最佳响应速度应该在200ms以内，或者慢点的接口就几百毫秒。
              # 默认 1000 毫秒，最高设置 2000足矣。如果超时，首先看能不能优化接口相关业务、SQL查询等，不要盲目加大超时时间，否则会导致线程堆积过多，hystrix 线程池卡死，最终服务不可用。
              timeoutInMilliseconds: ${HYSTRIX_COMMAND_TIMEOUT_IN_MILLISECONDS:40000}
  
  ribbon:
    # 客户端读取超时时间，超时时间要小于Hystrix的超时时间，否则重试机制就无意义了
    ReadTimeout: ${RIBBON_READ_TIMEOUT:30000}
    # 客户端连接超时时间
    ConnectTimeout: ${RIBBON_CONNECT_TIMEOUT:3000}
    # 访问实例失败(超时)，允许自动重试，设置重试次数，失败后会更换实例访问，请一定确保接口的幂等性，否则重试可能导致数据异常。
    OkToRetryOnAllOperations: true
    MaxAutoRetries: 1
    MaxAutoRetriesNextServer: 1
  
  mybatis:
    mapperLocations: classpath*:/mapper/*.xml
    configuration:
      mapUnderscoreToCamelCase: true
  
  
  logging:
    level:
      org.apache.ibatis: debug
      io.choerodon: debug
      org.hzero: debug
  
  choerodon:
    category:
      enabled: true # 是否开启项目/组织类型控制
    devops:
      message: true
    eureka:
      event:
        max-cache-size: 300
        retry-time: 5
        retry-interval: 3
        skip-services: config**, **register-server, **gateway**, zipkin**, hystrix**, oauth**
    saga:
      service: hzero-asgard
      consumer:
        enabled: false # 启动消费端
        thread-num: 2 # saga消息消费线程池大小
        max-poll-size: 200 # 每次拉取消息最大数量
        poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
    schedule:
      consumer:
        enabled: false # 启用任务调度消费端
        thread-num: 1 # 任务调度消费线程数
        poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
    cleanPermission: false
  
  
  gitlab:
    url: ${GITLAB_URL:http://git.example.com/}
    privateToken: ${GITLAB_PRIVATE_TOKEN:xxx} # 管理员token
  ```


## 安装和启动步骤

1. 创建数据库`hrds_code_repo`，创建用户`choerodon`，并为用户分配权限：

   ```sql
   CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
   CREATE DATABASE hrds_code_repo DEFAULT CHARACTER SET utf8;
   GRANT ALL PRIVILEGES ON hrds_code_repo.* TO choerodon@'%';
   FLUSH PRIVILEGES;
   ```

2. 拉取`hrds-code-repo`代码到本地：

   ```sh
   git clone https://github.com/choerodon/hrds-code-repo.git
   ```

3. 在项目根目录执行命令： `sh init-database.sh`

4. 使用下列命令运行或直接在集成环境中运行 `RducmApplication` 

   ```sh
   mvn clean spring-boot:run
   ```

## 链接

[更新日志](./CHANGELOG.zh-CN.md)

## 反馈途径

如果您发现任何缺陷或bug，请及时 [issue](https://github.com/choerodon/hrds-code-repo/issues/new)告知我们 。

## 如何参与

欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。 
