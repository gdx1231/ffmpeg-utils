# ffmpeg-utils

# windows 破解 nvdia 转码并发超过两个， OpenEncodeSessionEx failed: out of memory (10)
 ``` 
 参考：http://www.smartplatform.top/index.php/archives/84/ 
 在linux下
 特征码:84 C0 74 08 C6 43 38 01 33 C0
 修改为:84 C0 90 90 C6 43 38 01 33 C0
 sed 's/\x84\xC0\x74\x08\xC6\x43\x38\x01\x33\xC0/\x84\xC0\x90\x90\xC6\x43\x38\x01\x33\xC0/g' nvcuvid.dll > nvcuvid-1.dll
 ```
