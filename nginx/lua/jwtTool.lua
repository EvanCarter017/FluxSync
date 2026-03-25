local jwt = require "resty.jwt"

local _M = {}

-- 通用签发：传 token_type = "at" | "rt"
local function sign_with_type(secret, username, ttl_seconds, token_type)
    local iat = ngx.time()
    local exp = iat + ttl_seconds
    local token = jwt:sign(secret, {
        header = { typ = "JWT", alg = "HS256" },
        payload = {
            iss = "FluxSync Access Layer Service",
            sub = username,
            username = username,     -- 兼容旧字段
            iat = iat,
            exp = exp,
            typ = token_type         -- 关键：区分 AT/RT
        }
    })
    return token
end

-- 旧接口：默认 7 天过期
function _M.sign(secret, username, expire_seconds)
    return sign_with_type(secret, username, expire_seconds or (7 * 24 * 60 * 60), "at")
end

-- 新接口：分别签 AT/RT
function _M.sign_access(secret, username, ttl_seconds)
    return sign_with_type(secret, username, ttl_seconds or (15 * 60), "at")
end

function _M.sign_refresh(secret, username, ttl_seconds)
    return sign_with_type(secret, username, ttl_seconds or (30 * 24 * 60 * 60), "rt")
end

-- 通用校验：可要求期望的 typ
function _M.verify(secret, token, expected_type)
    local verified = jwt:verify(secret, token)
    if not verified.verified then
        return nil, verified.reason
    end
    if expected_type and verified.payload.typ ~= expected_type then
        return nil, "invalid_token_type"
    end
    return verified.payload, nil
end

return _M
