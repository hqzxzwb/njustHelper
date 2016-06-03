# njustHelper
南理工助手

编译须知：
开源工程缺少文件local.properties和app/google-services.json

local.properties为本地参数配置。除了默认的SDK路径之外，还需在其中配置以下几个参数：
    keyAlias 签名别名
    keyPassword 签名密码
    storeFilePath 签名文件路径
    storePassword 签名存储密码
    localUrl 局域网调试使用的地址，没有局域网环境可以使用线上地址http://njusthelper.duapp.com/njust0909/

app/google-services.json为google analytics配置文件。我现在也不知道怎么让工程在没有它的情况下跑起来。