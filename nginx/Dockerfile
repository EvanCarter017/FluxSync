FROM openresty/openresty:alpine

COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf
COPY lua/ /usr/local/openresty/nginx/conf/lua/

RUN apk add --no-cache lua5.1 lua5.1-dev luarocks5.1 build-base && \
    luarocks-5.1 install lua-resty-jwt

EXPOSE 1007

CMD ["/usr/local/openresty/bin/openresty", "-g", "daemon off;", "-c", "/usr/local/openresty/nginx/conf/nginx.conf"]
