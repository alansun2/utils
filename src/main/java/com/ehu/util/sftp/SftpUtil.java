package com.ehu.util.sftp;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


/**
 * ClassName: SftpUtil
 *
 * @author 邓集海
 * @Description: sftp上传工具类
 * @date 2014-11-11下午12:56:11
 */
@Component
@Slf4j
public class SftpUtil {

    @Autowired
    private FtpConfig ftpConfig;

    /**
     * jcraft的ChannelSftp类实例，信息传输通道
     */
    private ChannelSftp channelSftp;

    /**
     * jcraft的session类实例，用来持久化连接
     */
    private Session session;

    /**
     * 当前操作路径
     */
    private String currentPath;

    /**
     * 当前目录下文件列表
     */
    private Vector<LsEntry> currentFiles;

    /**
     * 取得当前的ChannelSftp实例
     *
     * @return 当前的ChannelSftp实例
     */
    public ChannelSftp getChannelSftp() {
        return channelSftp;
    }

    /**
     * 根据指定config相关信息，连接远程sftp服务器
     *
     * @param ftpConfig ftp服务配置信息类
     */
    public void connectServer(FtpConfig ftpConfig) {
        String server = ftpConfig.getHost();
        int port = ftpConfig.getPort();
        String username = ftpConfig.getUsername();
        String password = ftpConfig.getPassword();
        String location = "/";
        if (null != ftpConfig.getLocation() && !"".equals(ftpConfig.getLocation())) {
            location = ftpConfig.getLocation();
        }
        String encode = ftpConfig.getEncoding();
        connectServer(server, port, username, password, location, encode);
    }

    /**
     * 链接远程ftp服务器
     *
     * @param server   服务器地址
     * @param port     服务器端口
     * @param user     用户名
     * @param password 密码
     * @param path     登陆后的默认路径
     * @param encode   服务器文件系统编码
     */
    public void connectServer(String server, int port, String user, String password, String path,
                              String encode) {
        String errorMsg = "";
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, server, port);
            session.setPassword(password);
            // session.setUserInfo(new SftpUserInfo(ftpPassword));
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.setFilenameEncoding(encode);
            currentPath = path;
            channelSftp.cd(path);// 进入服务器指定的文件夹

        } catch (SftpException e) {
            errorMsg = "无法使用SFTP传输文件!";
            log.error("无法使用SFTP传输文件!", e);
            throw new RuntimeException(errorMsg);
        } catch (JSchException e) {
            errorMsg = "没有权限与SFTP服务器连接!";
            log.error("无法使用SFTP传输文件!", e);
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * 内部方法，关闭OutputStream
     *
     * @param os 希望关闭的OutputStream
     */
    private void closeOutputStream(OutputStream os) {
        try {
            if (null != os) {
                os.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("关闭OutputStream时出现异常：" + e.getMessage());
        }
    }

    /**
     * 内部方法，关闭InputStream
     *
     * @param is 希望关闭的InputStream
     */
    private void closeInputStream(InputStream is) {
        try {
            if (null != is) {
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("关闭OutputStream时出现异常：" + e.getMessage());
        }
    }

    /**
     * 内部方法，取得当前操作目录下的全部文件列表
     */
    private void getCurrentFileList() {
        try {
            @SuppressWarnings("unchecked")
            Vector<LsEntry> v = channelSftp.ls(currentPath);
            this.currentFiles = v;
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部方法，获得操作路径
     *
     * @param path 参数
     * @return String
     */
    private String getOperationPath(String path) {
        String operactePath;
        if (!"".equals(path.trim()) && '/' == path.charAt(0)) {
            operactePath = path;
        } else {
            operactePath = currentPath + path;
        }
        if (operactePath.lastIndexOf("/") != operactePath.length() - 1) {
            operactePath = operactePath + "/";
        }
        return operactePath;
    }

    /**
     * 内部方法，获得操作路径
     *
     * @param path 参数
     * @return String
     */
    private String getFileOperationPath(String path) {
        String operactePath;
        if ('/' == path.charAt(0)) {
            operactePath = path;
        } else {
            operactePath = currentPath + "/" + path;
        }
        return operactePath;
    }

    /**
     * 内部方法，验证路径和sftp连接
     *
     * @param path 路径
     * @return 验证结果
     */
    private boolean dirValidation(String path) {
        boolean result = true;
        if (channelSftp == null || !channelSftp.isConnected()) {
            log.error("channelSftp连接未创建,正在尝试重新登录..." + ftpConfig.getAddress());
            try {
                this.connectServer(ftpConfig);
                log.info("sftp服务器链接成功");
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
                throw new RuntimeException("操作出现异常：channelSftp重新链接失败" + e.getMessage());
            }
        }
        if (null != path && "".equals(path)) {
            result = false;
            throw new RuntimeException("操作出现异常：指定的path不能为空");
        }
        return result;
    }

    /**
     * 内部方法，判断一个字符串，是文件路径
     *
     * @param path 路径字符串
     * @return 判断结果，是返回ture，否则false
     */
    private boolean isFilePath(String path) {
        boolean result = false;
        if (null != path && !"".equals(path) && path.lastIndexOf("/") < path.length()) {
            result = true;
        }
        return result;
    }

    /**
     * 变换当前目录
     *
     * @param path 希望进入的目录
     * @return 成功为true，失败返回false
     */
    public boolean changeDir(String path) {
        boolean result = false;
        if (dirValidation(path)) {
            String testPath = getOperationPath(path);
            try {
                // 不存在其目录，创建目录,并改变当前目录
                if (!this.existFile(path)) {
                    log.info(path + "目录不存在,开始创建目录" + path);

                    this.makeDir(this.getFileName(path));
                    log.info("目录创建完成" + path);
                }
                channelSftp.cd(testPath);
                this.currentPath = testPath;

                result = true;
            } catch (SftpException e) {
                throw new RuntimeException("变换目录'" + path + "'时发生错误：" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 创建目录
     *
     * @param remotePath 远程目录
     * @return 操作结果，成功为true，失败为false
     */
    public boolean makeDir(String remotePath) {
        boolean result = false;
        if (dirValidation(remotePath)) {
            String testPath = getOperationPath(remotePath);
            try {
                channelSftp.mkdir(testPath);
                result = true;
            } catch (SftpException e) {
                throw new RuntimeException("创建目录'" + remotePath + "'时发生错误：" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 删除远程服务器上的目录（仅可删除非空目录）
     *
     * @param remotePath 远程目录
     * @return 操作结果，成功为true，失败为false
     */
    public boolean removeDir(String remotePath) {
        boolean result = false;
        if (dirValidation(remotePath)) {
            String testPath = getOperationPath(remotePath);
            try {
                channelSftp.rmdir(testPath);
                result = true;
            } catch (SftpException e) {
                throw new RuntimeException("删除目录'" + remotePath + "'时发生错误：" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 内部方法，取得一个文件他所属的目录的路径
     *
     * @param path 文件路径
     * @return 判断结果，是返回ture，否则false
     */
    private String getFileDir(String path) {
        String result = "";
        if (path.lastIndexOf("/") >= 0) {
            result = path.substring(0, path.lastIndexOf("/"));
        }
        return result;
    }

    /**
     * 内部方法，取得一个文件的文件名
     *
     * @param path 文件路径
     * @return 判断结果，是返回ture，否则false
     */
    public String getFileName(String path) {
        String result = path;
        if (path.lastIndexOf("/") > -1) {
            result = path.substring(path.lastIndexOf("/") + 1, path.length());
        }
        return result;
    }

    /**
     * 判断文件是否存在
     *
     * @param remoteFilePath 文件路径
     * @return 文件存在，则返回true，否则返回false
     */
    public boolean existFile(String remoteFilePath) {
        boolean result = false;
        if (dirValidation(remoteFilePath)) {
            if (!this.isFilePath(remoteFilePath)) {
                throw new RuntimeException("这不是一个文件路径：" + remoteFilePath);
            }
            String pathDir = this.getFileDir(remoteFilePath);
            String realPathDir = this.getOperationPath(pathDir);
            String fileName = this.getFileName(remoteFilePath);


            try {
                @SuppressWarnings("unchecked")
                Vector<LsEntry> v = channelSftp.ls(realPathDir);
                if (null != v && v.size() > 0) {
                    for (int i = 0; i < v.size(); ++i) {
                        LsEntry e = (LsEntry) v.get(i);
                        if (e.getFilename().equals(fileName)) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (SftpException e1) {
                log.warn("文件不存在,remoteFilePath=" + remoteFilePath);
            }
        }
        return result;
    }

    /**
     * 取得当前操作路径下的文件名列表(含文件夹)
     *
     * @return 文件名列表
     */
    public List<String> getFileList() {
        List<String> result = null;

        // 获取当前路径
        this.getCurrentFileList();
        if (null != currentFiles && currentFiles.size() > 0) {
            result = new ArrayList<String>();
            for (int i = 0; i < currentFiles.size(); ++i) {
                result.add(((LsEntry) currentFiles.get(i)).getFilename());
            }
        }

        return result;
    }

    /**
     * 从SFTP服务器下载文件至本地
     *
     * @param remoteFilePath 远程文件路径
     * @param localFilePath  本地文件路径
     * @return String  返回目录名称
     */
    public String downloadFile(String remoteFilePath, String baseDir, String localFilePath) {
        if (dirValidation(remoteFilePath)) {
            if (!existFile(remoteFilePath)) {
                throw new RuntimeException("下载文件" + remoteFilePath + "时发生异常，远程文件并不存在");
            }
            OutputStream os = null;
            try {
                //目录名称
                //文件名称
//        String fileName= remoteFilePath.substring(remoteFilePath.lastIndexOf("/")+1,remoteFilePath.length());
                String realPath = getFileOperationPath(remoteFilePath);
                File file = new File(localFilePath + "/" + baseDir + "/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                //文件存储到本地的路径
                remoteFilePath = localFilePath + "/" + baseDir + "/" + remoteFilePath;
                // 从服务器下载文件到本地
                channelSftp.get(realPath, remoteFilePath);
                this.getCurrentFileList();
                return baseDir;
            } catch (SftpException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生Sftp错误：" + e.getMessage());
            } finally {
                this.closeOutputStream(os);
            }
        } else {
            return "FAILED";
        }
    }

    /**
     * 删除服务器上的文件
     *
     * @param remoteFilePath 远程文件的完整路径
     * @return 操作结果(boolean型)
     */
    public boolean deleteFile(String remoteFilePath) {
        if (dirValidation(remoteFilePath)) {
            if (!existFile(remoteFilePath)) {
                throw new RuntimeException("删除文件" + remoteFilePath + "时发生异常，远程文件并不存在");
            }
            try {
                String realPath = getFileOperationPath(remoteFilePath);
                channelSftp.rm(realPath);
                return true;
            } catch (SftpException e) {
                throw new RuntimeException("删除文件：" + remoteFilePath + "时发生错误：" + e.getMessage());
            }
        } else {
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFilePath 远程文件名 remoteFilePath = 远程目录+文件名
     * @param localFilepath  本地文件名
     * @return -1 文件不存在 -2 文件内容为空 >0 成功上传，返回文件的大小
     */
    @SuppressWarnings("finally")
    public long upload(String remoteFilePath, String localFilepath) {
        if (!dirValidation(remoteFilePath)) {
            throw new RuntimeException("上传文件" + localFilepath + "时发生异常，channelSftp连接未创建");
        }
        InputStream is = null;
        long result = -1;
        try {
            File fileIn = new File(localFilepath);

            if (fileIn.exists()) {
                is = new FileInputStream(fileIn);
                result = fileIn.length();
                // 从本地上传到服务器
                channelSftp.put(is, remoteFilePath);

            } else {
                result = -1;
            }
        } catch (IOException e) {
            result = -1;
            throw new RuntimeException(e.getMessage() + ": 上传文件时发生错误！");
        } catch (SftpException e) {
            throw new RuntimeException(e.getMessage() + ": 上传文件时发生错误！");
        } finally {
            closeInputStream(is);
            return result;
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFilePath 远程文件名 这个remoteFilePath = 远程目录+文件名
     * @param is             本地文件名
     * @return -1 文件不存在 -2 文件内容为空 >0 成功上传，返回文件的大小
     */
    @SuppressWarnings("finally")
    public long upload(String remoteFilePath, InputStream is) {
        if (!dirValidation(remoteFilePath)) {
            throw new RuntimeException("上传文件时发生异常，channelSftp连接未创建");
        }
        long result = -1;
        try {
            // 从本地上传到服务器
            channelSftp.put(is, remoteFilePath);
        } catch (SftpException e) {
            throw new RuntimeException(e.getMessage() + ": 上传文件时发生错误！");
        } finally {
            closeInputStream(is);
            return result;
        }
    }


    /**
     * 上传文件
     *
     * @param filename 文件名
     * @return 操作结果
     */
    public long upload(String filename) {
        String newname = "";
        if (filename.lastIndexOf("/") > -1) {
            newname = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
        } else {
            newname = filename;
        }
        return upload(newname, filename);
    }


    /**
     * 关闭连接
     */
    public void closeConnection() {
        if (channelSftp != null) {
            if (channelSftp.isConnected()) {
                channelSftp.disconnect();
                session.disconnect();
            }
        }
    }

    /**
     * 下载文件，并写入HttpServletResponse（浏览器方式）
     *
     * @param remoteFilePath 远程文件路径
     * @param response       HttpServletResponse
     * @param fileName       通过浏览器，下载时看到的文件名
     * @return 操作结果，成功返回true，失败返回false
     */
    public boolean downloadFile(String remoteFilePath, HttpServletResponse response, String fileName) {
        if (dirValidation(remoteFilePath)) {
            if (!existFile(remoteFilePath)) {
                throw new RuntimeException("下载文件" + remoteFilePath + "时发生异常，远程文件并不存在");
            }
            OutputStream os = null;
            // String fileName = remoteFilePath.substring(remoteFilePath.lastIndexOf(File.separator) + 1);
            try {
                response.setHeader("Content-disposition", "attachment;filename="
                        + new String(fileName.getBytes("GBK"), "ISO8859-1"));
                os = response.getOutputStream();
                // 从服务器下载到本地
                channelSftp.get(remoteFilePath, os);
                return true;
            } catch (IOException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生文件读写错误：" + e.getMessage());
            } catch (SftpException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生Sftp错误：" + e.getMessage());
            } finally {
                this.closeOutputStream(os);
            }
        } else {
            return false;
        }
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Description:从sftp下载文件到指定路径。
     * @author 邓集海
     * @date 2014-11-11下午10:40:44
     */
    public boolean downlandRemoteFile(String remoteFilePath, HttpServletResponse response,
                                      String filePath) {
        if (dirValidation(remoteFilePath)) {
            if (!existFile(remoteFilePath)) {
                throw new RuntimeException("下载文件" + remoteFilePath + "时发生异常，远程文件并不存在");
            }
            try {
                // 从服务器下载到本地
                channelSftp.get(remoteFilePath, filePath);
                return true;
            } catch (SftpException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生Sftp错误：" + e.getMessage());
            } finally {
                this.closeConnection();
            }
        } else {
            return false;
        }
    }

    public String downloadFile2(String remoteFilePath, HttpServletResponse response, String fileName) {
        if (dirValidation(remoteFilePath)) {
            if (!existFile(remoteFilePath)) {
                throw new RuntimeException("下载文件" + remoteFilePath + "时发生异常，远程文件并不存在");
            }
            OutputStream os = null;
            // String fileName = remoteFilePath.substring(remoteFilePath.lastIndexOf(File.separator) + 1);
            try {
                // response.setHeader("Content-disposition", "attachment;filename="
                // + new String(fileName.getBytes("GBK"), "ISO8859-1"));
                os = response.getOutputStream();
                // 从服务器下载到本地
                channelSftp.get(remoteFilePath, os);
                return os.toString();
            } catch (IOException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生文件读写错误：" + e.getMessage());
            } catch (SftpException e) {
                throw new RuntimeException("下载文件：" + remoteFilePath + "时发生Sftp错误：" + e.getMessage());
            } finally {
                this.closeOutputStream(os);
            }
        } else {
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        SftpUtil sftpUtil = new SftpUtil();
        sftpUtil.connectServer("test.ehoo100.com", 22, "root", "s", "/data/tomcat/tomcat-8082-ehu/webapps/pic", "UTF-8");
        sftpUtil.deleteFile("/data/tomcat/tomcat-8082-ehu/webapps/pic/1.json");
        // sftpUtil.downloadFile("0d34ce21_20141111210757487.png", "d:\\test\\test2.jpg");
        sftpUtil.closeConnection();
    }
}
