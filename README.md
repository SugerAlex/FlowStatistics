本项目调用百度AI人体分析接口进行静态人流量统计和动态人流量统计

调用准备工作  
1.新手接入指南 https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjgn3  
2.静态接口文档 https://ai.baidu.com/ai-doc/BODY/7k3cpyy1t   
3.动态接口文档 https://ai.baidu.com/ai-doc/BODY/wk3cpyyog   

工程文件说明  
#BodyNum 调用静态接口统计单幅图像的人数  
#JavavcCameraTest 实现摄像头实时人流量统计，参考了网上的教程  
#BodyTrackingAPISample和VideoFrame2Base64ImageUtil 完成抽帧和格式转化工作，调动态接口，展示折线图 
#BodyTracking 对抽帧得到的图片序列带调动态接口，其中access_token可动态生成  
#BodyTrackingBetter 优化了BodyTracking的逻辑  
#FileUtil、HttpUtil、Base64Util 是一些工具类  
