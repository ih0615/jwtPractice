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


        //Todo fileclient 제거 하고 disconnect 메소드 구현
        public int upload (String localFilePath, String remoteFilePath, String fileName, HttpServletRequest request) throws
        Exception {
            FTPClient client = null;
            FileInputStream fis = null;
            File uploadFile = new File(localFilePath);

            String url = ftpPropertiesHandler.getValue("FTP_URL");//서버 ip
            String id = ftpPropertiesHandler.getValue("FTP_ID");//ftp id
            String pwd = ftpPropertiesHandler.getValue("FTP_PWD");//ftp pw
            String port = ftpPropertiesHandler.getValue("FTP_PORT");//ftp 포트

            int result = 1;

            try {
                client = new FTPClient();//클라이언트 객체 생성
                client.setControlEncoding("UTF-8");//문자코드 UTF-8
                client.connect(url, Integer.parseInt(port));//서버 접속

                client.login(id, pwd);//FTP 로그인
                client.enterLocalPassiveMode();//패시브 모드 접속
                client.changeWorkingDirectory(remoteFilePath);//작업 디렉토리 변경
                client.setFileType(FTP.BINARY_FILE_TYPE);//업로드 파일 타입 셋팅

                try {
                    fis = new FileInputStream(uploadFile);//업로드 할 파일 생성
                    boolean isSuccess = client.storeFile(fileName, fis);//파일 업로드

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
                client.logout();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO Exception : " + e.getMessage());
            } finally {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                    return result;
                }
            }
            return result;
        }

        public int delete (String localFilePath, String remoteFilePath, String fileName, HttpServletRequest request) throws
        Exception {
            FTPClient client = null;
            FileInputStream fis = null;

            String url = ftpPropertiesHandler.getValue("FTP_URL");//서버 ip
            String id = ftpPropertiesHandler.getValue("FTP_ID");//ftp id
            String pwd = ftpPropertiesHandler.getValue("FTP_PWD");//ftp pw
            String port = ftpPropertiesHandler.getValue("FTP_PORT");//ftp 포트

            int result = 1;

            try {
                client = new FTPClient();
                client.setControlEncoding("UTF-8");
                client.connect(url, Integer.parseInt(port));

                client.login(id, pwd);
                client.enterLocalPassiveMode();
                client.changeWorkingDirectory(remoteFilePath);
                client.setFileType(FTP.BINARY_FILE_TYPE);

                try {
                    boolean isSuccess = client.deleteFile(fileName);

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
                client.logout();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException : " + e.getMessage());
            } finally {
                if (client != null && client.isConnected()) {
                    try {
                        client.disconnect();
                        return result;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("IOException : " + e.getMessage());
                    }
                }
            }
            return result;
        }
    }
