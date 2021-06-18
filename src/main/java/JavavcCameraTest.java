import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.alibaba.fastjson.JSONObject;
/**
 * 获取摄像头画面进行处理并回显图片在画面中
 * 人流量统计（动态版）JavaAPI示例代码
 * @author 小帅丶
 *
 */
public class JavavcCameraTest {

    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
    //人流量统计（动态版）接口地址
    private static String BODY_TRACKING_URL="https://aip.baidubce.com/rest/2.0/image-classify/v1/body_tracking";
    private static String ACCESS_TOKEN ="24.70faad1dae2a3ea9a3a7c528c3e211aa.2592000.1625642267.282335-24325222";//接口的token
    public static void main(String[] args) throws Exception, InterruptedException {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start(); // 开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("人流量实时统计");// 新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas.setVisible(true);
        canvas.setFocusable(true);
        //窗口置顶
        if(canvas.isAlwaysOnTopSupported()) {
            canvas.setAlwaysOnTop(true);
        }
        Frame frame =null;


        //canvas.setAlwaysOnTop(true);
        int ex = 0;
        while (true) {
            if (!canvas.isDisplayable()) {// 窗口是否关闭
                grabber.stop();// 停止抓取
                System.exit(2);// 退出
                grabber.close();
            }
            canvas.showImage(grabber.grab());//显示摄像头抓取的画面
            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            // 摄像头抓取的画面转BufferedImage
            BufferedImage bufferedImage = java2dFrameConverter.getBufferedImage(grabber.grabFrame());
            // bufferedImage 请求API接口 检测人流量
            String result = getBodyTrack(bufferedImage);
            BufferedImage bufferedImageAPI = getAPIResult(result);
            // 如果识别为空 则显示摄像头抓取的画面
            if (null == bufferedImageAPI) {
                canvas.showImage(grabber.grab());
            } else {
                // BufferedImage转IplImage
                IplImage iplImageAPI = BufImgToIplData(bufferedImageAPI);
                // 将IplImage转为Frame 并显示在窗口中
                Frame convertFrame = converter.convert(iplImageAPI);
                canvas.showImage(convertFrame);
            }
            ex++;
			Thread.sleep(100);// 100毫秒刷新一次图像.因为接口返回需要时间。所以看到的画面还是会有一定的延迟
        }
    }
    /**
     * BufferedImage转IplImage
     * @param bufferedImageAPI
     * @return
     */
    private static IplImage BufImgToIplData(BufferedImage bufferedImageAPI) {
        IplImage iplImage = null;
        ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        iplImage = iplConverter.convert(java2dConverter.convert(bufferedImageAPI));
        return iplImage;
    }
    /**
     * IplImage 转 BufferedImage
     * @param mat
     * @return BufferedImage
     */
    public static BufferedImage iplToBufImgData(IplImage mat) {
        if (mat.height() > 0 && mat.width() > 0) {
            //TYPE_3BYTE_BGR 表示一个具有 8 位 RGB 颜色分量的图像，对应于 Windows 风格的 BGR 颜色模型，具有用 3 字节存储的 Blue、Green 和 Red 三种颜色。
            BufferedImage image = new BufferedImage(mat.width(), mat.height(),BufferedImage.TYPE_3BYTE_BGR);
            WritableRaster raster = image.getRaster();
            DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
            byte[] data = dataBuffer.getData();
            BytePointer bytePointer = new BytePointer(data);
            mat.imageData(bytePointer);
            return image;
        }
        return null;
    }
    /**
     * 接口结果转bufferimage
     * @param result
     * @return BufferedImage
     * @throws Exception
     */
    private static BufferedImage getAPIResult(String result) throws Exception {
        JSONObject object = JSONObject.parseObject(result);
        BufferedImage bufferedImage = null;
        if(object.getInteger("person_num")>=1){
            Decoder decoder = Base64.getDecoder();
            byte [] b = decoder.decode(object.getString("image"));
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            bufferedImage = ImageIO.read(in);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg", baos);
            byte[] imageInByte = baos.toByteArray();
            // Base64解码
            for (int i = 0; i < imageInByte.length; ++i) {
                if (imageInByte[i] < 0) {// 调整异常数据
                    imageInByte[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream("D:\\idea\\workspace\\FlowStatistics\\xiaoshuairesult.jpg");//接口返回的渲染图
            out.write(imageInByte);
            out.flush();
            out.close();
            return bufferedImage;
        }else{
            return null;
        }
    }
    /**
     * 获取接口处理结果图
     * @param bufferedImage
     * @return String
     * @throws Exception
     */
    public static String getBodyTrack(BufferedImage bufferedImage) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,"jpg",baos);
        byte[] imageInByte = baos.toByteArray();
        Encoder base64 = Base64.getEncoder();
        String imageBase64 = base64.encodeToString(imageInByte);
        // Base64解码
        for (int i = 0; i < imageInByte.length; ++i) {
            if (imageInByte[i] < 0) {// 调整异常数据
                imageInByte[i] += 256;
            }
        }

        String access_token = ACCESS_TOKEN;
        String case_id = "2018";
        String case_init = "true";
        String area = "1,240,1,479,639,479,639,240";
        String params = "image=" + URLEncoder.encode(imageBase64, "utf-8")
                + "&dynamic=true&show=true&case_id=" + case_id
                + "&case_init="+case_init +"&area="+area;
        String result = HttpUtil.post(BODY_TRACKING_URL, access_token, params);
        System.out.println("接口内容==>"+result);
        return result;
    }
    /**
     * IplImage 转 BufferedImage
     * @param mat
     * @return BufferedImage
     */
    public static BufferedImage bufferimgToBase64(IplImage mat) {
        if (mat.height() > 0 && mat.width() > 0) {
            BufferedImage image = new BufferedImage(mat.width(), mat.height(),BufferedImage.TYPE_3BYTE_BGR);
            WritableRaster raster = image.getRaster();
            DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
            byte[] data = dataBuffer.getData();
            BytePointer bytePointer = new BytePointer(data);
            mat.imageData(bytePointer);
            return image;
        }
        return null;
    }
}