# 优惠券秒杀一键测试

## 只需要三步

1. 打开 `users.csv`，填写真实游客账号，每行格式为 `loginName,password`。
2. 修改 `test-config.properties` 中的 `activity_id`、`threads` 和 `user_mode`。
3. 双击 `run-test.bat`。

当前已经配置为使用你的 JMeter 安装目录：

```text
C:\apache-jmeter-5.6.3
```

如果以后移动 JMeter，只需修改 `test-config.properties` 中的 `jmeter_home`。

测试结束后会自动打开 HTML 报告，并在本目录的 `results` 下生成：

```text
summary.txt        简要结果：请求数、接受数、售罄数、P95、P99
verify-result.sql  用于检查库存和重复发券
result.jtl         JMeter 原始结果
html-report/       完整 JMeter 报告
```

为避免复制敏感信息，脚本生成的临时账号文件会在 JMeter 结束后立即删除；原始 `users.csv` 已加入 `.gitignore`。

## 两种常用模式

### 多游客争抢

```properties
threads=200
user_mode=unique
poll_final_result=false
```

`users.csv` 至少准备 200 个不同游客。适合测试防超卖、TPS、P95 和 P99。

### 同一游客重复点击

```properties
threads=50
user_mode=single
poll_final_result=false
```

脚本只读取 `users.csv` 第一行，并自动复制给全部线程。测试后执行生成的 `verify-result.sql`，该游客应当只获得一张券，库存只减少一份。

### 测试 Kafka 异步落库时间

```properties
threads=20
user_mode=unique
poll_final_result=true
```

此模式会在抢券成功后轮询最终结果。HTML 报告中的 `03-异步发券端到端` 表示从提交抢券到查询到数据库最终状态的耗时。

## 测试前提

- Spring Boot、MySQL、Redis 和 Kafka 已启动。
- 活动处于 `PUBLISHED`，当前时间位于领取时间窗口内。
- 活动已经自动预热，`cache_ready=1`，Redis `enabled=1`。
- `unique` 模式下，每一行必须是不同的 `TOURIST` 账号。
- 每轮测试使用新活动，避免旧领取记录影响结果。
