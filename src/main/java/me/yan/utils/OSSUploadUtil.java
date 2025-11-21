package me.yan.utils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云OSS上传工具类（Spring组件版）
 */
@Component
public class OSSUploadUtil {

    // ======================== OSS配置常量（直接修改为你的真实配置）========================
    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";  // OSS地域域名
    private static final String BUCKET_NAME = "page-voyage";                    // 你的Bucket名称
    private static final String REGION = "cn-beijing";                             // Bucket所在地域
    // =====================================================================================

    /**
     * 上传方法（非静态，Spring注入后调用）
     * @param content 要上传的Byte数组
     * @param originalFilename 原始文件名（用于提取后缀）
     * @return OSS公网访问路径
     * @throws Exception 上传异常（含详细错误信息）
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        OSS ossClient = null;
        try {
            // 简化版：直接创建OSS客户端，不使用ClientBuilderConfiguration
            // 新版本SDK推荐的构建方式
            ossClient = new OSSClientBuilder().build(
                    ENDPOINT,
                    new EnvironmentVariableCredentialsProvider());

            // 生成唯一存储路径（日期目录 + UUID唯一名）
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String fileSuffix = getFileSuffix(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + fileSuffix;
            String objectName = dateDir + "/" + uniqueFileName;

            // 执行上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    BUCKET_NAME,
                    objectName,
                    new ByteArrayInputStream(content)
            );
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            System.out.printf("上传成功：Path=%s, ETag=%s%n", objectName, result.getETag());

            // 拼接并返回访问URL
            return String.format("https://%s.%s/%s", BUCKET_NAME, ENDPOINT.replace("https://", ""), objectName);

        } catch (Exception e) {
            throw new Exception("OSS上传失败：" + e.getMessage(), e);
        } finally {
            // 强制关闭客户端，释放资源
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public void delete(String objectName) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    ENDPOINT,
                    new EnvironmentVariableCredentialsProvider());
            ossClient.deleteObject(BUCKET_NAME, objectName);
        } catch (Exception e) {
            throw new RuntimeException("OSS删除失败：" + e.getMessage(), e);
        } finally {
            // 强制关闭客户端，释放资源
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 内部工具：提取文件后缀（兼容无后缀文件）
     */
    private String getFileSuffix(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
