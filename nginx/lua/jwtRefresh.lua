    local jwtTool = require "jwtTool"
    local cjson = require "cjson.safe"

    local secret = "MB8wQbwmoz0O7T2igax//W3UxyQvAmEf6yU9JwK129YAemHXKPazo30g9GVCpots"

    -- 尝试从 Cookie 取 RT；取不到再看 body
    local cookies = ngx.var.http_cookie or ""
    local rt_from_cookie = nil
    for k, v in string.gmatch(cookies, "([^=]+)=([^;]+)") do
        if k == "rt" then
            rt_from_cookie = v
            break
        end
    end

    ngx.req.read_body()
    local body = ngx.req.get_body_data()
    local data = cjson.decode(body or "{}") or {}

    local rt = data.refresh_token or rt_from_cookie
    if not rt or rt == "" then
        ngx.status = 400
        ngx.say(cjson.encode({ error = "refresh_token required" }))
        return ngx.exit(400)
    end

    -- 验证必须是 RT
    local payload, err = jwtTool.verify(secret, rt, "rt")
    if not payload then
        ngx.status = ngx.HTTP_UNAUTHORIZED
        ngx.header["Content-Type"] = "application/json"
        ngx.say(cjson.encode({ error = "Unauthorized", detail = err }))
        return ngx.exit(ngx.HTTP_UNAUTHORIZED)
    end

    -- 颁发新的 AT；此处不做 RT 轮换（简化）
    local at_ttl = tonumber(data.at_ttl or 0) > 0 and tonumber(data.at_ttl) or (15 * 60)
    local new_at = jwtTool.sign_access(secret, payload.username or payload.sub, at_ttl)

    ngx.status = 200
    ngx.header["Content-Type"] = "application/json"
    ngx.say(cjson.encode({
        access_token = new_at,
        token_type = "Bearer",
        expires_in = at_ttl
    }))
    return ngx.exit(200)
