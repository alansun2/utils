package com.ehu.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件处理工具类
 */
@Slf4j
public class FileUtils {

    private static final Charset CHARSET = Charset.forName("GB18030");

    private static final CSVParser CSVPARSER = new CSVParser();

    /**
     * zip 解压
     *
     * @param zipFilePath
     * @param destDir
     * @return fileNames
     */
    public static List<String> unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        List<String> fileNames = new ArrayList<String>();
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis, CHARSET);
            ZipEntry ze = zis.getNextEntry();
            String fileName = "";
            File newFile = null;
            while (ze != null) {
                fileName = ze.getName();
                newFile = new File(destDir + File.separator + fileName);
                //                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
                fileNames.add(fileName);
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    /**
     * 解压Gzip
     * ungzip
     */
    public static boolean unGzip(String srcpath, String desPath) {

        File file = new File(srcpath);
        if (!file.exists()) {
            log.info(srcpath + "文件不存在");
            return false;
        }
        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(srcpath));

            org.apache.commons.io.FileUtils.copyInputStreamToFile(in, new File(desPath));
        } catch (FileNotFoundException e) {
            log.error("File not found. " + srcpath, e);
            return false;
        } catch (IOException e) {
            log.error("unGzip fail", e);
            return false;
        }
        return true;
    }

    /**
     * 获取csvreader
     *
     * @param desPath
     * @param charSet
     * @param skipLine
     * @param cSVParser
     * @return
     */
    public static CSVReader getCSVReader(String desPath, String charSet, int skipLine, CSVParser cSVParser) {
        try {
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(new File(desPath)), charSet)).withCSVParser(cSVParser).withSkipLines(skipLine);
            return csvReaderBuilder.build();
            //	        CsvToBean csvToBean = CsvToBeanBuilder(storeFinancials).withSkipLines().build();
        } catch (UnsupportedEncodingException e) {
            log.error("get csvreader fail", e);
        } catch (FileNotFoundException e) {
            log.error("get csvreader fail", e);
        }
        return null;
    }

    /**
     * 获取csvreader 默认，
     *
     * @param desPath
     * @param charSet
     * @param skipLine
     * @return
     */
    public static CSVReader getCSVReader(String desPath, String charSet, int skipLine) {
        return getCSVReader(desPath, charSet, skipLine, CSVPARSER);
    }

    /**
     * 获取数据表
     *
     * @param filePath
     * @return
     * @throws BiffException
     * @throws IOException
     */
    private Sheet[] getExcelWorkbook(String filePath) throws BiffException, IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook wk = Workbook.getWorkbook(fis);
        //获取第一张Sheet表
        return wk.getSheets();
    }

    /**
     * @param dirPath
     * @param fileName
     * @throws BiffException
     * @throws IOException
     */
    public <T> void excelHandler(String dirPath, String fileName, Class<T> clazz) throws BiffException, IOException {
        Sheet[] sheets = getExcelWorkbook("D:" + dirPath + "/alipay/" + fileName);
        int i = 0, j = 0, sheetSize = 0, cellSize = 0;
        for (Sheet sheet : sheets) {
            i = 0;
            j = 0;
            cellSize = 0;
            sheetSize = sheet.getRows();
            Cell[] cells = null;
            Cell cell = null;
            for (; i < sheetSize; i++) {
                j = 0;
                cells = sheet.getRow(i);
                cellSize = cells.length;
                for (; j < cellSize; j++) {
                    cell = cells[j];
                    if (cell != null) {
                    }

                }
            }
        }
    }

    /**
     * openOutputStream
     *
     * @param file
     * @param append
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 获取图片的后缀
     *
     * @param imageInputstream
     * @return
     * @throws IOException
     */
    public static Map<Integer, Object> getBufferedImageFormat(ImageInputStream imageInputstream) throws IOException {
        Map<Integer, Object> map = new HashMap<>();
        ImageIO.setUseCache(false);
        Iterator<ImageReader> it = ImageIO.getImageReaders(imageInputstream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        String suffix = null;
        while (it.hasNext()) {

            ImageReader imageReader = it.next();
            // 设置解码器的输入流
            imageReader.setInput(imageInputstream, true, true);

            // 图像文件格式后缀
            suffix = imageReader.getFormatName().trim().toLowerCase();
//                int height = imageReader.getHeight(0);
//                int width = imageReader.getWidth(0);
            //校验宽和高是否符合要求
            if (!"bmp、jpg、jpeg、png、gif".contains(suffix)) {
                throw new RuntimeException("不支持的图片格式");
            }
            // 解码成功返回BufferedImage对象
            // 0即为对第0张图像解码(gif格式会有多张图像),前面获取宽度高度的方法中的参数0也是同样的意思
            BufferedImage read = imageReader.read(i, imageReader.getDefaultReadParam());
            ImageIO.write(read, suffix, baos);
            i++;
        }

        map.put(1, baos);
        if (i == 1) {
            map.put(2, suffix);
            return map;
        } else if (i > 1) {
            map.put(2, "gif");
            return map;
        }
        return null;
    }

    // 压缩
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("ISO-8859-1");
    }

    // 解压缩
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes("ISO-8859-1"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
        return out.toString();
    }

    public static void main(String[] wer) throws IOException {
        String str = "eJxlz0FPgzAUwPE7n4JwnZECax0mO8DinJOpC8wZL6RCKdWslLbOMeN3F3GJTTz-f**9vE-Ltm0nS9JzXBTNO9e57gRx7EvbAc7ZXxSClTnWeSDLf5EcBJMkx5UmcogehNAHwDSsJFyzip3EscacKswNocq3fDjzu2LczyPvAvomYXSIq6v17GZBKGq9ZZrMXBmK5aGdq01Ru8-3k614yl5o9nrsVgG8rTajiMVr3UVb8BE-4qrJaE3c9Pouprv9Igj0XEGePITtKBEyRNF0apzUbEdOP3kITQIAxkbdE6lYwwfgg574Pfh53PqyvgGYjV4o";
        int s = str.length();
        String compress = compress(str);
        String uncompress = uncompress(compress);
        System.out.println(str.equals(uncompress));
    }
}
