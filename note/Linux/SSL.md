## ssl
自签证书

1. 生成密钥 `openssl genrsa -out ssl.key 1024`
   - `-out {name}`: 输出文件
   - `1024`: 密钥长度
2. 生成证书申请文件CSR `openssl req -new -key ssl.key -out ssl.csr`
   - `-key {name}`: 密钥文件
   - `-out {name}`: 输出文件
   - 命令执行会要求输入信息:
     1. 国码: CN
     2. 省份: 随意填
     3. 城市: 随意填
     4. 公司: 随意填
     5. 部门: 随意填
     6. 域名: 需要和访问域名相同
     7. 管理员邮箱: 随意填
     8. 
3. 颁发证书 `openssl x509 -req -days 3650 -in ssl.csr -signkey ssl.key -out ssl.crt`
   - `-days 3650`: 有效期，单位: 日
   - `-in {name}`: 申请文件
   - `-signkey {name}`: 密钥文件
   - `-out {name}`: 输出文件