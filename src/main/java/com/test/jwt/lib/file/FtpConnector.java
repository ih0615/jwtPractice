package com.test.jwt.lib.file;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCommand;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class FtpConnector {
    public static class FileUpload {
        String server;
        String user;
        String passworrd;
        String path;


        public FileUpload(String server, String user, String password, String path) {
            this.server = server;
            this.user = user;
            this.passworrd = password;
            this.path = path;
        }

        public FTPClient getConnection() throws UnknownHostException {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient = new FTPClient();

                ftpClient.setControlEncoding("UTF-8");

                ftpClient.connect(server);

                int reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                } else {
                    System.out.println("Connect successful");
                    ftpClient.login(this.user, this.passworrd);
                    System.out.println("Login successful");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ftpClient;
        }

        public void uploadFile(FTPClient ftpClient, File file, String fileName) throws IOException {
            ftpClient.sendCommand(FTPCommand.MAKE_DIRECTORY, path);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftpClient.enterLocalPassiveMode();

            FileInputStream fis = new FileInputStream(file);

            ftpClient.storeFile(fileName, fis);

            fis.close();
        }

        public void deleteFile(FTPClient ftpClient, String fileName) throws Exception{
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean delflag = ftpClient.deleteFile(fileName);
            if(delflag==false){
                throw new Exception("파일 삭제에 실패했습니다.");
            }
        }

        public void disconnect(FTPClient ftpClient, FileInputStream fis) {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
