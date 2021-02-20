[TOC]

# 虚拟定位

![](https://api.travis-ci.com/yu-xiaoyao/Virtuallocation.svg?branch=master)

## 下载
- [https://github.com/yu-xiaoyao/Virtuallocation/releases](https://github.com/yu-xiaoyao/Virtuallocation/releases)


## 使用

## 编译

### 高德控制台

- 在高德地图后台开发[https://console.amap.com/dev/key/app](https://console.amap.com/dev/key/app) 中创建两个应用,一个用于Android,一个用于web,名字都可以. 如:
  - **WebSearch** 用于WEB
  - **AndroidDev** 用于
- 添加绑定服务
  - **AndroidDev** 选择 Android平台
  - **PackageName** : 固定: **me.yuxiaoyao.virtuallocation**

- SHA1码的获取

  - Debug: **~/.android/debug.keystore**

  - Release: 自已创建,在开发时,可以跟Debug为一样的

  - ```
    keytool -v -list -keystore key.keystore
    ```

  - 找到SHA1后面的复制

### 添加文件

在项目目录下**app/** 下添加 **amap-config.properties** 文件

写上:

```properties
# 把 **AndroidDev** 的Key写到这里
service.type.android.key=177AAAA
# 把 **WebSearch** 的Key写在这里
service.type.web.key=3a0c7b79AAA
```

## travis-ci 发布
```
travis encrypt GH_TOKEN=XXXXXXX --add
```