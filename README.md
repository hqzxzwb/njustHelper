# njustHelper
南理工助手

编译须知：
开源工程缺少文件local.properties

local.properties为本地参数配置。除了默认的SDK路径之外，还需在其中配置以下几个参数：
    keyAlias 签名别名
    keyPassword 签名密码
    storeFilePath 签名文件路径
    storePassword 签名存储密码
    localUrl 局域网调试使用的地址，没有局域网环境可以使用线上地址http://njusthelper.duapp.com/njust0909/
    testStuid 测试用学号
    testJwcPwd 测试用教务处密码
    testLibPwd 测试用图书馆密码
    tencentBuglyId 腾讯Bugly的Id，没有的话也能跑
