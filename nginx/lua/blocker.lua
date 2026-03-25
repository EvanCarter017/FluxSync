local whitelist = {
    ["/api/security/login"] = true,
    ["/api/security/register"] = true,
    ["/api/security/totp/verify"] = true,
    ["/api/security/jwt/refresh"] = true
}
if whitelist[ngx.var.uri] then
    return
end

local uri = ngx.var.request_uri
if (uri:find("X%-Amz%-Signature") or uri:find("X%-Amz%-Algorithm"))
    and ngx.var.uri:find("^/storage/") then
    return
end


local jwtTool = require "jwtTool"
local cjson = require "cjson.safe"

local secret = "MB8wQbwmoz0O7T2igax//W3UxyQvAmEf6yU9JwK129YAemHXKPazo30g9GVCpots"

local headers = ngx.req.get_headers()
local args = ngx.req.get_uri_args()
local authorization = headers["Authorization"] or args["Authorization"]

if not authorization or not authorization:find("^Bearer ") then
    ngx.header["Content-Type"] = "application/json"
    ngx.status = ngx.HTTP_UNAUTHORIZED
    ngx.say(cjson.encode({ error = "Unauthorized" }))
    return ngx.exit(ngx.HTTP_UNAUTHORIZED)
end

local token = authorization:sub(8)

local payload, err = jwtTool.verify(secret, token, "at")

if not payload then
    ngx.header["Content-Type"] = "application/json"
    ngx.status = ngx.HTTP_UNAUTHORIZED
    ngx.say(cjson.encode({ error = "Unauthorized", detail = err }))
    return ngx.exit(ngx.HTTP_UNAUTHORIZED)
end

-- 透传用户
ngx.req.set_header("X-User", payload.username or payload.sub)
return