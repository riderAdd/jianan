package com.example.demo;

import com.example.demo.dto.UserLoginDTO;
import com.example.demo.utils.TokenUtil;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Token工具类单元测试
 *
 * @author : ssk
 * @date : 2020/3/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class TokenUtilTest {

    private UserLoginDTO userLoginDTO;

    /**
     * Token生成者
     */
    private static final String ISS = "demo/login";

    /**
     * 秘钥
     */
    private static final String SECRET = "ad2fdfa225bca02c7fb1204eba7ba41c314a9cac";

    @Before
    public void init() {
        // 初始化用户登录DTO
        userLoginDTO = UserLoginDTO.builder().userId(2)
                .userName("archer")
                .password("1221").build();
    }

    /**
     * Token工具类：主流程测试1：生产Token
     */
    @Test
    public void testGenerateToken() {
        String token = TokenUtil.generateToken(userLoginDTO, 9000);
        System.out.println(token);
    }

    /**
     * Token工具类：主流程测试2：校验Token
     */
    @Test
    public void testValidateToken() {
        String token = TokenUtil.generateToken(userLoginDTO, 9000);
        String validResult = TokenUtil.validateToken(token);
        System.out.println(validResult);
    }

    /**
     * Token工具类：校验Token异常分支1：校验Token时效
     */
    @Test
    public void testValidateTokenForCheckTime() throws InterruptedException {
        // 有效时间1秒
        String token = TokenUtil.generateToken(userLoginDTO, 1000);

        // 暂停2秒
        Thread.sleep(2000);
        String validResult = TokenUtil.validateToken(token);
        System.out.println(validResult);
    }

    /**
     * Token工具类：校验Token异常分支2：校验Token其它错误
     */
    @Test
    public void testValidateTokenForCheckOther() throws InterruptedException {
        String validResult = TokenUtil.validateToken("checkValue");
        System.out.println(validResult);
    }

    /**
     * Token工具类：校验Token异常分支3：校验Token秘钥
     */
    @Test
    public void testValidateTokenForCheckValid() {
        // 设置jwt头
        final Map<String, Object> header = new HashMap<>(4, 1);
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Date nowDate = new Date();
        long nowMillis = nowDate.getTime();
        Date expireDate = null;

        // 默认15分钟
        expireDate = new Date(nowMillis + 15 * 60 * 1000);

        // 修改秘钥
        String SECRET2 = "12121212";
        Key SIGN_KEY = TokenUtil.generalSecretKey(SignatureAlgorithm.HS256, SECRET2);

        // 构建jwt
        JwtBuilder builder = Jwts.builder()
                .setHeader(header)
                .setIssuer(ISS)
                .setIssuedAt(nowDate)
                .setSubject(userLoginDTO.getUserId().toString())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY);

        // 压缩jwt
        String compactJws = builder.compact();

        String validResult = TokenUtil.validateToken(compactJws);
        System.out.println(validResult);
    }

}
