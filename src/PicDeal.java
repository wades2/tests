import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PicDeal {
    //todo splitNums可以根据你给到的图片色差进行调整，在你自己使用时，可以针对splitNums做一个循环，每次加多少，得到不同的色差比的二值化后的图片，因为不同的图片可能干扰线、干扰点颜色原因，二值化后会有差异
    //todo splitWidthNum：把图片根据长度切分的分数，这个可以根据你图片中的数字个数进行切分
    public static int splitNums=2100000;

    public static int beginNum=100000;
    public static int endNum=4000000;
    public static final int splitWidthNum=6;
    public static void main(String[] args) {
        String path="C://Users/admin/Desktop/微信搜狗验证码/yzmsg/3.jpg";
        try{
            splitPic(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //todo 分割图片
    public static void splitPic(String picFile) throws Exception{
        //todo 分割图片
//        BufferedImage img = ImageIO.read(new File(picFile));
        Image image = ImageIO.read(new File(picFile));

        for (int i = 0; i < splitWidthNum; i++) {

//            BufferedImage img=toBufferedImage(image);
//            int xWith=(img.getWidth()/6)*(i);
//            BufferedImage newimg=img.getSubimage(xWith,0,img.getWidth()/6,img.getHeight());
            String filename="F://test/test3_"+i+".jpg";
//            ImageIO.write(newimg, "JPG", new File(filename));
            ImageIO.write(toBufferedImage(image), "JPG", new File(filename));


            //todo 分割后的图片处理
            for (int j=beginNum;j<=endNum;j+=25000){
                Image thisimg = ImageIO.read(new File(filename));
                BufferedImage imgDeal=removeBackgroud(thisimg,j,i);

                imgDeal=DeletePassPoint.deletepoints(imgDeal);

                //todo 分割后图片写出去
                String filename2="F://test/test3_"+i+"imgDeal_像素分割_"+j+".jpg";
                ImageIO.write(imgDeal, "JPG", new File(filename2));
            }


        }
    }



    //todo 图片处理算法
    public static BufferedImage removeBackgroud(Image img2,int j,int i)throws Exception {

        BufferedImage img=toBufferedImage(img2);

        String filename="F://test/test3_"+i+"_像素分割_"+j+".jpg";
        ImageIO.write(img, "JPG", new File(filename));


        splitNums=j;
        System.out.println("splitNums==========="+splitNums);

        img = img.getSubimage(1, 1, img.getWidth()-2, img.getHeight()-2);
        int width = img.getWidth();
        int height = img.getHeight();
        double subWidth = (double) width;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        //todo 以下是对图片进行二值化处理，在这里我的思路是规定，色差范围在splitNums到负splitNums之间的，算是同色，放入同一个色值，放入一个map中，
        //todo map中的Key放色值，value放这个色值得个数，后期就根据这个色值来对验证码进行二值化
        for (int x = (int) (1 + 0 * subWidth); x < (0 + 1) * subWidth && x < width - 1; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(img.getRGB(x, y)) == 1){
                    continue;
                }
                Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
                for (Integer color : map.keySet()) {
                    map2.put(color,map.get(color));
                }

                boolean hasnewColor=false;
                for (Integer color : map2.keySet()) {
//                        System.out.println(Math.abs(color)-Math.abs(img.getRGB(x, y)));
//                        Math.abs(color)-Math.abs(img.getRGB(x, y)))<splitNums&&Math.abs(color)-Math.abs(img.getRGB(x, y))>-splitNums
                    if (Math.abs(color)-Math.abs(img.getRGB(x, y))<splitNums&&Math.abs(color)-Math.abs(img.getRGB(x, y))>-splitNums){
                        map.put(color, map.get(color) + 1);
                        hasnewColor=true;
                    }
                }

                if (!hasnewColor){
                    map.put(img.getRGB(x, y), 1);
                }

                if (map.isEmpty()){
                    map.put(img.getRGB(x, y), 1);
                }

            }
        }

        System.out.println("==============================");

        int max = 0;
        int colorMax = 0;
        for (Integer color : map.keySet()) {
            if (max < map.get(color)) {
                max = map.get(color);
                colorMax = color;
            }
        }
        map.remove(colorMax);

        max = 0;
        int colorMax2 = 0;
        for (Integer color : map.keySet()) {
            if (max < map.get(color)) {
                max = map.get(color);
                colorMax2 = color;
            }
        }
        map.remove(colorMax2);


//        int colorMax3 = 0;
//        for (Integer color : map.keySet()) {
//            if (max < map.get(color)) {
//                max = map.get(color);
//                colorMax3 = color;
//            }
//        }
//        colorMax=colorMax2;
//        colorMax2=colorMax3;


        //todo 核心算法
        for (int x = (int) (1 + 0 * subWidth); x < (0 + 1) * subWidth&& x < width - 1; ++x) {
            for (int y = 0; y < height; ++y) {
                int ress=Math.abs(img.getRGB(x, y))-Math.abs(colorMax);
                int ress_2=Math.abs(Math.abs(colorMax)-Math.abs(img.getRGB(x, y)));

                int ress2=Math.abs(img.getRGB(x, y))-Math.abs(colorMax2);
//                int ress2_2=Math.abs(Math.abs(colorMax2)-Math.abs(img.getRGB(x, y)));
//                if (ress_2<ress2_2){
//                    if ((ress_2<splitNums&&ress_2>-splitNums)) {
//                        img.setRGB(x, y, Color.BLACK.getRGB());
//                    } else {
//                        img.setRGB(x, y, Color.WHITE.getRGB());
//                    }
//                }else{
//                    if ((ress_2>splitNums&&ress2_2<splitNums)||(-ress_2<-splitNums&&ress2_2>-splitNums)){
//                        img.setRGB(x, y, Color.BLACK.getRGB());
//                    }else{
//                        img.setRGB(x, y, Color.WHITE.getRGB());
//                    }
//                }

                if (ress>0&&ress2>0){
                    if (ress>ress2){
                        if ((ress2<splitNums&&ress2>-splitNums)) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }else{
//                            img.setRGB(x, y, Color.WHITE.getRGB());
                        if (ress2<splitNums&&ress2>ress) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }else if (ress<0&&ress2<0){
                    ress=Math.abs(ress);
                    ress2=Math.abs(ress2);
                    if (ress>ress2){
                        if ((ress2<splitNums&&ress2>-splitNums)) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }else{
                        if (ress2<splitNums&&ress2>ress) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }else if (ress>0&&ress2<0){
                    if (Math.abs(ress)>Math.abs(ress2)){
                        if ((Math.abs(ress2)>splitNums&&ress2<-splitNums)) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }else{
                        img.setRGB(x, y, Color.WHITE.getRGB());
//                        if (splitNums<)
                    }

                }else{
                    if (Math.abs(ress)>Math.abs(ress2)){
                        if ((Math.abs(ress2)>splitNums&&ress2<-splitNums)) {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }else{
                        img.setRGB(x, y, Color.WHITE.getRGB());
//                        if (splitNums<)
                    }
//                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }



    //todo 判断是否为白色的方法  爬虫 验证码处理
    public static int isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue()>600) {
            return 1;
        }
        return 0;
    }



    //todo image转bufferImage
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        // 加载所有像素
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            // 创建buffer图像
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // 复制
        Graphics g = bimage.createGraphics();
        // 赋值
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
}
