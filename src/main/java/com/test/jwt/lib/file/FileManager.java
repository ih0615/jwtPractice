package com.test.jwt.lib.file;

import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RequiredArgsConstructor
public class FileManager {

    private final FtpPropertiesHandler ftpPropertiesHandler;

    private FTPClient ftpClient;

    public int getConnection() throws Exception {
        this.ftpClient = new FTPClient();
        this.ftpClient.setControlEncoding("UTF-8");
        int isConnectionSuccess = 1;
        try {
            this.ftpClient.connect(ftpPropertiesHandler.getValue("FTP_URL"), Integer.parseInt(ftpPropertiesHandler.getValue("FTP_PORT")));
            int resultCode = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(resultCode)) {
                this.ftpClient.disconnect();
                isConnectionSuccess = 0;
                throw new Exception("FTP 서버에 연결 할 수 없습니다.");
            } else {
                this.ftpClient.setSoTimeout(5000);
                Boolean isSuccess = this.ftpClient.login(ftpPropertiesHandler.getValue("FTP_ID"), ftpPropertiesHandler.getValue("FTP_PWD"));
                if (!isSuccess) {
                    this.ftpClient.disconnect();
                    isConnectionSuccess = 0;
                    throw new Exception("FTP 서버에 로그인 할 수 없습니다.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnectionSuccess;
    }

    public int download(String filePath, String fileName, HttpServletRequest request) throws Exception {
        BufferedOutputStream bos = null;
        File fPath = null;
        File fDir = null;
        File f = null;

        String downloadPath = request.getSession().getServletContext().getRealPath("/") + ftpPropertiesHandler.getValue("FTP_PATH");//다운로드 경로

        int result = 1;

        //download 경로에 해당하는 디렉토리 생성
        downloadPath = downloadPath + filePath;
        fPath = new File(downloadPath);
        fDir = fPath;
        fDir.mkdir();

        f = new File(downloadPath, fileName);


        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.changeWorkingDirectory(filePath);

        bos = new BufferedOutputStream(new FileOutputStream(f));
        boolean isSuccess = this.ftpClient.retrieveFile(fileName, bos);

        if (isSuccess) {
            result = 1;
        } else {
            throw new Exception("파일 다운로드에 실패 했습니다.");
        }

        this.ftpClient.logout();


        if (bos != null) {
            bos.close();

        }

        return result;

    }


    public int upload(String localFilePath, String remoteFilePath, String fileName, HttpServletRequest request) throws
            Exception {
        FileInputStream fis = null;
        File uploadFile = new File(localFilePath);

        int result = 1;

        try {
            this.ftpClient.enterLocalPassiveMode();//패시브 모드 접속
            this.ftpClient.changeWorkingDirectory(remoteFilePath);//작업 디렉토리 변경
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//업로드 파일 타입 셋팅

            try {
                fis = new FileInputStream(uploadFile);//업로드 할 파일 생성
                boolean isSuccess = this.ftpClient.storeFile(fileName, fis);//파일 업로드
                if (isSuccess) {
                    result = 1;
                } else {
                    throw new Exception("파일 업로드에 실패 했습니다.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO Exception : " + e.getMessage());
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
            this.ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Exception : " + e.getMessage());
        }
        return result;
    }

    public int delete(String localFilePath, String remoteFilePath, String fileName, HttpServletRequest request) throws
            Exception {
        FileInputStream fis = null;

        int result = 1;

        try {
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.changeWorkingDirectory(remoteFilePath);
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try {
                boolean isSuccess = this.ftpClient.deleteFile(fileName);

                if (isSuccess) {
                    result = 1;
                } else {
                    throw new Exception("파일 삭제에 실패했습니다.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("IOException : " + e.getMessage());
                    }
                }
            }
            this.ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException : " + e.getMessage());
        }
        return result;
    }

    public void disconnect() throws Exception {
        if (this.ftpClient != null && this.ftpClient.isConnected()) {
            this.ftpClient.disconnect();
        }
    }
}
