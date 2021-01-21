# FfmpegUtils
用于通过在单机上主程序调度多个线程批量转换视频，转码、加水印、输出分片文件等
调用net.bramp.ffmpeg的方法进行处理

## 示例方法是 com.gdxsoft.ffmpegUtils.Samples.JobMainSampleImpl 

## maven
```
<dependency>
  <groupId>com.gdxsoft.ffmpeg</groupId>
  <artifactId>ffmpeg-utils</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Gradle 
```
implementation 'com.gdxsoft.ffmpeg:ffmpeg-utils:1.0.0'
```



#####  破解windows 下 nvdia 转码并发超过两个， OpenEncodeSessionEx failed: out of memory (10)

```
参考：http://www.smartplatform.top/index.php/archives/84/ 
特征码:84 C0 74 08 C6 43 38 01 33 C0
修改为:84 C0 90 90 C6 43 38 01 33 C0
如果在window下的安装git的化，“C:\Program Files\Git\usr\bin\sed.exe”
或在linux子系统下执行 
sed 's/\x84\xC0\x74\x08\xC6\x43\x38\x01\x33\xC0/\x84\xC0\x90\x90\xC6\x43\x38\x01\x33\xC0/g' nvcuvid.dll > nvcuvid-1.dll
```
