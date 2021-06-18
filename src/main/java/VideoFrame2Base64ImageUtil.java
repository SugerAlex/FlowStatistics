import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 * 视频抽帧并存为base64图片序列
 */
public class VideoFrame2Base64ImageUtil {
    public static ArrayList<String> getVideoFrame2Base64(String videoPath) throws IOException {
        ArrayList<String> imagePathList = new ArrayList<String>();
        ArrayList<String> arrayList = new ArrayList<String>();
        Frame frame = null;
        FFmpegFrameGrabber ff;
        ff = new FFmpegFrameGrabber(videoPath);
        ff.start();
        int ftp = ff.getLengthInFrames();
        System.out.println("视频总帧数: " + ftp);
        double secondFrame = ff.getFrameRate();
        System.out.println("视频每秒的帧数: " + secondFrame);
        System.out.println("视频时长(秒): " + ftp / secondFrame);
        int len=(int)(8*ftp / secondFrame);  //需抽帧数
        System.out.println("需要抽帧数：" + len);
        for(int k=1;k<len;k++){
            System.out.println("每1s抽8帧，你正在抽取第"+k+"帧。");
            ff.setFrameNumber(k*(int)(secondFrame/8));
            frame = ff.grabImage();
            BufferedImage bufferedImage = FrameToBufferedImage(frame);
            File outPut = new File("D:\\idea\\workspace\\FlowStatistics\\picture\\"+"00"+k+".jpg");
            String temp = "D:\\idea\\workspace\\FlowStatistics\\picture\\"+"00"+k+".jpg";
            imagePathList.add(temp);
            System.out.println(temp);
            ImageIO.write(bufferedImage,"jpg", outPut);
            String base64 = BufferedImageToBase64(bufferedImage);
            System.out.println("抽取第"+k+"帧完成。");
            arrayList.add(base64);
        }
        System.out.println("图片路径个数："+imagePathList.size());
        return arrayList;
    }

    public static BufferedImage FrameToBufferedImage(Frame frame) {
        // 创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public static String BufferedImageToBase64(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String base64 = Base64Util.encode(baos.toByteArray());
        return base64;
    }

    public static void draw2(Vector<Integer> vec){
        CategoryDataset mDataset = GetDataset(vec);
        Font titleFont=new Font("隶书", Font.ITALIC, 18);
        Font font=new Font("宋体",Font.BOLD,12);
        Font legendFont=new Font("宋体", Font.BOLD, 15);


        JFreeChart mChart = ChartFactory.createLineChart(
                "实时人流量统计折线图",//图名字
                "时间",//横坐标
                "人数",//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接

        mChart.getTitle().setFont(titleFont);
        mChart.getLegend().setItemFont(legendFont);

        CategoryPlot plot=mChart.getCategoryPlot();
        plot.getDomainAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);

        CategoryPlot mPlot = (CategoryPlot)mChart.getPlot();
        mPlot.setBackgroundPaint(Color.LIGHT_GRAY);
        mPlot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
        mPlot.setOutlinePaint(Color.RED);//边界线

        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());//设置y轴显示整数数据

        ChartFrame mChartFrame = new ChartFrame("实时人流量统计折线图", mChart);
        mChartFrame.pack();
        mChartFrame.setVisible(true);

        FileOutputStream file_picture=null;
        try{
            file_picture=new FileOutputStream("line.jpg");
            ChartUtilities.writeChartAsJPEG(file_picture,1,mChart,400,300,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                file_picture.close();
            }catch(Exception e){}
        }

    };

    public static CategoryDataset GetDataset(Vector<Integer> vec) {
        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        mDataset.addValue(vec.get(0), "视频序列10", "0:20");
        mDataset.addValue(vec.get(1), "视频序列10", "0:40");
        mDataset.addValue(vec.get(2), "视频序列10", "1:00");
        mDataset.addValue(vec.get(3), "视频序列10", "1:20");
        mDataset.addValue(vec.get(4), "视频序列10", "1:40");
        mDataset.addValue(vec.get(5), "视频序列10", "2:00");
        return mDataset;
    }
}