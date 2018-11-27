
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

public class test {
    public static void main(String[] args) {
        System.out.println(1);
        File imageFile = new File("F:/test/test/11.jpg");
//        File imageFile = new File("C://Users/admin/Desktop/微信搜狗验证码/yzmsg/5.jpg");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("C:/Users/admin/Desktop/Tess4J-3.4.8-src/Tess4J//tessdata");
        try {
            instance.setLanguage("eng"); //加载语言包
//            instance.setLanguage("osd"); //加载语言包
            String result = instance.doOCR(imageFile);
            String result2 = instance.doOCR(imageFile);
            System.err.println(imageFile.getName() +" result："+  result);
            System.err.println(imageFile.getName() +" result："+  result2);
            System.err.println(imageFile.getName() +" result："+  result2);
            System.err.println(imageFile.getName() +" result："+  result2);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    public String getResNum(File imageFile){
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("C:/Users/admin/Desktop/Tess4J-3.4.8-src/Tess4J//tessdata");
        try {
            instance.setLanguage("eng"); //加载语言包
            String result = instance.doOCR(imageFile);

            if (!result.equals(".")||!result.equals("")){
                return result;
            }
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }
}
