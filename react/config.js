const alias = require('../alias').webpack;

const config = {
  master: './node_modules/@choerodon/master/lib/master.js',
  modules: ['.'],
  resourcesLevel: ['site', 'organization', 'project', 'user'],
  webpackConfig(configs) {
    configs.resolve.alias = alias;
    return configs;
  },
};

module.exports = config;

// const config = {
//   server: 'http://api.staging.saas.test.com',
//   fileServer: 'http://minio.staging.saas.test.com',
//   projectType: 'choerodon',
//   buildType: 'single',
//   master: './node_modules/@choerodon/master/lib/master.js',
//   theme: {
//     'primary-color': '#3f51b5',
//     'icon-font-size-base': '16px',
//   },
//   dashboard: {},
//   modules: [
//     '.',
//   ],
//   resourcesLevel: ['site', 'organization', 'project', 'user'],
// };

// module.exports = config;
