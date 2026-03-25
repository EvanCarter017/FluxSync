package com.esther.fluxsync.controller.security;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.UserDTO;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.TotpService;
import com.esther.fluxsync.service.UserRegister.UserRegister;
import com.esther.fluxsync.service.UserRegister.UserRegisterDrive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/security")
class RegisterController {

    private final DataBaseService db;
    private final UserRegister userRegister;
    private final TotpService totp;
    private final UserRegisterDrive userRegisterDrive;

    RegisterController(DataBaseService db,
                       UserRegister userRegister,
                       TotpService totp, UserRegisterDrive userRegisterDrive) {
        this.db = db;
        this.userRegister = userRegister;
        this.totp = totp;
        this.userRegisterDrive = userRegisterDrive;
    }

    /**
     * 用户注册接口，验证用户名是否已存在并注册新用户。
     * <p>
     * 该接口接收用户注册信息，首先验证用户名是否已存在，若已存在返回 409 冲突响应。
     * 若用户名不存在，则生成 TOTP 密钥并注册新用户。若注册过程出错，则返回 503 服务不可用的错误响应。
     * 成功注册后返回 200 OK 响应，表示注册成功。
     * </p>
     *
     * @param user 包含注册信息的 {@link UserDTO} 对象，包含用户名、密码等用户数据
     * @return {@link ResponseEntity} 包含 {@link ApiResponse} 对象
     *         - 注册成功：状态200 OK，消息“注册成功”
     *         - 用户已存在：状态409 Conflict，消息“用户已存在”
     *         - 注册失败：状态503 Service Unavailable，消息“注册失败”
     */
    @PostMapping("/register")
    @UseDB("fluxsync")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserDTO user) {

        List<Integer> checkUser = db.query(
                "SELECT COUNT(*) FROM fluxsync_user.users WHERE username = ?",
                rs -> rs.getInt(1),
                user.getUsername()
        );

        final int userExist = checkUser.get(0);

        if (userExist == 1) {
            return new ResponseEntity<>(ApiResponse.error(409,"用户已存在."), HttpStatus.CONFLICT);
        }

        final String secret = totp.createKey().getKey();

        try {
            userRegister.registerUser(user, secret);
            int uid = db.query(
                    "SELECT uid FROM fluxsync_user.users WHERE username = ?;",
                    rs -> rs.getInt("uid") ,
                    user.getUsername()
            ).get(0);
            userRegisterDrive.registerUser_Drive(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ApiResponse.error(503,"注册失败."), HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(ApiResponse.success("注册成功."), HttpStatus.OK);
    }
}
