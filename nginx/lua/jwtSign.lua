local jwtTool = require "jwtTool"
local cjson = require "cjson.safe"

local secret = "MB8wQbwmoz0O7T2igax//W3UxyQvAmEf6yU9JwK129YAemHXKPazo30g9GVCpots"

ngx.req.read_body()
local body = ngx.req.get_body_data()
local data, err = cjson.decode(body)

if not data then
    ngx.status = 400
    ngx.say(cjson.encode({ error = "Invalid json", detail = err }))
    return ngx.exit(400)
end

local username = data.username
if not username or username == "" then
    ngx.status = 400
    ngx.say(cjson.encode({ error = "username required" }))
    return ngx.exit(400)
end

-- 可选：允许传自定义 ttl；否则用默认 15 分钟 / 30 天
local at_ttl = tonumber(data.at_ttl or 0) > 0 and tonumber(data.at_ttl) or (15 * 60)
local rt_ttl = tonumber(data.rt_ttl or 0) > 0 and tonumber(data.rt_ttl) or (30 * 24 * 60 * 60)

local at = jwtTool.sign_access(secret, username, at_ttl)
local rt = jwtTool.sign_refresh(secret, username, rt_ttl)

ngx.status = 200
ngx.header["Content-Type"] = "application/json"
ngx.say(cjson.encode({
    access_token = at,
    token_type = "Bearer",
    expires_in = at_ttl,
    refresh_token = rt,
    refresh_expires_in = rt_ttl
}))
return ngx.exit(200)