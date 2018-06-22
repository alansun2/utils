package com.ehu.util.sftp;

import com.ehu.util.DateUtils;
import com.ehu.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

@Component
@Slf4j
public class SftpUtils {

    @Autowired
    private FtpConfig ftpConfig;// ftp相关配置

    @Autowired
    private SftpUtil sftpUtil;// sftp工具类

    /**
     * @return String 返回文件在本地的路径
     * @throws
     * @Description: sftp 下载文件到本地缓存路径
     * @author 邓集海
     * @date 2014-11-11下午11:16:41
     */
    public String downlandFile(String fileName, HttpServletRequest request) throws Exception {
        String contextPath = request.getSession().getServletContext().getRealPath("/");
        String tmpPath = "";
        File file = new File(contextPath + tmpPath + fileName);
        if (!file.exists()) {
            if (!StringUtils.isEmpty(fileName)) {
                String baseDir = fileName.substring(0, fileName.indexOf("/") + 1);
                fileName = fileName.substring(fileName.indexOf("/") + 1, fileName.length());
                sftpUtil.changeDir(ftpConfig.getLocation() + baseDir);
                log.info("开始下载文件：" + fileName);
                try {
                    tmpPath += sftpUtil.downloadFile(fileName, baseDir, contextPath + tmpPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("sftp文件下载失败" + e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
                log.info("结束下载文件,返回目录为：" + tmpPath + fileName);
            } else {
                throw new RuntimeException("文件名为空");
            }
        } else {
            log.info("文件已经缓存在本地,无需下载,目录为：" + tmpPath + fileName);
        }
        return tmpPath + fileName;
    }

    /**
     * @param @daoParam fileName 文件名称
     * @param @daoParam input 文件流
     * @return String 返回文件在服务器上的路径
     * @throws
     * @Description: sftp 上传文件
     * @author 邓集海
     * @date 2014-11-11下午08:38:49
     */
    public String uploadFile(InputStream input, String fileFormat) {
        String fileName;
        try {
            StringBuffer sb = new StringBuffer();
            Date date = new Date();
            sb.append(StringUtils.getUUID()).append("_").append(DateUtils.formatDate(date, DateUtils.DATE_LONG_FORMAT)).append("." + fileFormat);
            fileName = sb.toString().replaceAll("-", "");
            String uploadPath = DateUtils.formatDate(date, DateUtils.DATE_DAY_FORMAT);
            String aaa = uploadPath.replace("-", "_");
            log.debug("开始改变远程服务器路径" + ftpConfig.getLocation() + aaa);
            sftpUtil.changeDir(ftpConfig.getLocation() + aaa);
            log.debug("改变成功:" + ftpConfig.getLocation() + aaa);
            log.debug("开始上传文件..");
            sftpUtil.upload(fileName, input);
            log.debug("文件上传成功");
            fileName = ftpConfig.getAddress() + aaa + "/" + fileName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            sftpUtil.closeConnection();
            return "FAIL";
        }
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public void deleteFile(String filePath) {
        try {
            sftpUtil.deleteFile(filePath);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            sftpUtil.closeConnection();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        sftpUtil.closeConnection();
    }
}
