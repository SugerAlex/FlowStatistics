import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 视频抽帧请求人流量统计（动态版）接口
 */
public class BodyTrackingAPISample {
    private static String BODY_TRACKING_URL="https://aip.baidubce.com/rest/2.0/image-classify/v1/body_tracking";
    private static String access_token = "24.70faad1dae2a3ea9a3a7c528c3e211aa.2592000.1625642267.282335-24325222";
    private static String videoPath = "D:\\idea\\workspace\\FlowStatistics\\newtest.mp4";
    public static void main(String[] args) throws Exception {
        //抽帧为base64序列
        ArrayList<String> list = new ArrayList<String>();
        list = VideoFrame2Base64ImageUtil.getVideoFrame2Base64(videoPath);
        System.out.println("list的大小是"+list.size());
        int personNum=0;
        Vector<Integer> v1=new Vector<Integer>();
        //调接口
        for(int i=0 ; i< list.size();i++){
            String imgParam = URLEncoder.encode(list.get(i),"utf-8");
            String params = "image="+imgParam+"&dynamic=true&show=false&case_id=1&case_init=true&area=1,1,1,1079,960,1079,960,1";
            String result = HttpUtil.post(BODY_TRACKING_URL, access_token, params );
            com.alibaba.fastjson.JSONObject object= com.alibaba.fastjson.JSONObject.parseObject(result);
            System.out.println("第"+i+"帧调用结果是");
            System.out.println("person_count："+object.get("person_count"));
            personNum= (int) object.get("person_num");
            //System.out.println("person_number:"+personNum);
            v1.add(personNum);
        }
        //绘制折线图
        VideoFrame2Base64ImageUtil.draw2(v1);
    }
}