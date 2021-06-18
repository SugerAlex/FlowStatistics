
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import java.net.URLEncoder;

/**
 * 人流量统计
 */
public class BodyNum {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String body_num() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_num";
        try {
            // 本地文件路径
            String filePath = "picture\\web5.jpg";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.70faad1dae2a3ea9a3a7c528c3e211aa.2592000.1625642267.282335-24325222";

            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            JSONObject object= JSONObject.parseObject(result);
            System.out.println(object.get("person_num"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        BodyNum.body_num();
    }
}