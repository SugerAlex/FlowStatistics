import org.bytedeco.javacpp.presets.opencv_core;
import sun.misc.BASE64Encoder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import org.json.JSONObject;


public class BodyTracking{
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //抽帧后的图片路径数组
        String imgarray[]= {"D:\\idea\\workspace\\FlowStatistics\\picture\\001.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\002.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\003.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\004.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\005.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\006.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\007.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\008.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\009.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0010.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0011.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0012.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0013.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0014.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0015.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0016.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0017.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0018.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0019.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0020.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0021.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0022.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0023.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0024.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0025.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0026.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0027.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0028.jpg",
                "D:\\idea\\workspace\\FlowStatistics\\picture\\0029.jpg"};
        //循环调用接口 获取渲染图
        for(int i=0;i<29;i++) {
            body_tracking(imgarray[i],1);
            //System.out.println("第"+i+"帧处理完成！");
        }
    }
    //人流量统计-动态版
    public static void body_tracking(String imgPath,int case_id){
        // 官网获取的 API Key 更新为你注册的
        String clientId = "45QA57nxGoP6fZ4GZ50rWoto";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "j95mwCEb7rHvSbc1lj8Xk0yCZInE5yxx";
        String access_token=getAuth(clientId, clientSecret);
        //调用人流量统计（动态版）
        //String access_token="24.70faad1dae2a3ea9a3a7c528c3e211aa.2592000.1625642267.282335-24325222";
        String request_url="https://aip.baidubce.com/rest/2.0/image-classify/v1/body_tracking";
        String url=request_url+"?access_token="+access_token;
        String img_str=convertFileToBase64(imgPath);
        //System.out.println(img_str);
        //对base64 utf-8转码
        String imgParam="" ;
        try {
            imgParam= URLEncoder.encode(img_str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String case_init="false";
        String show="false";
        String area="1,360,1,719,1279,719,1279,360";
        String params="case_id="+case_id+"&case_init="+case_init+"&image="+imgParam+"&show="+show+"&area="+area+"&dynamic=true";
        String res=sendPost(url,params);
        //com.alibaba.fastjson.JSONObject object= com.alibaba.fastjson.JSONObject.parseObject(res);
        //System.out.println("person_num："+object.get("person_num"));
        //System.out.println("person_count："+object.get("person_count"));
        System.out.println(res);
    }
    //post请求方法
    public static String sendPost(String url,String param) {
        String result="";
        try{
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection)httpurl.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine())!= null) {
                result += line;
            }
            in.close();
        }catch(Exception e){
            System.out.println("Helloword！"+e);
        }
        return result;
    }
    //图片转base64
    public static String convertFileToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            System.out.println("文件大小（字节）="+in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Str = encoder.encode(data);
        return base64Str;
    }
    //获取access_token
    public static String getAuth(String ak, String sk)
    {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
}