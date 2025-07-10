# spring事务测试

## 事务说明
[事务.md](../../note/数据库/事务.md)

## 基本测试
[ModelServerImpl.java](src/main/java/transaction/ModelServerImpl.java)

## 多线程事务
[MultiServiceImpl.java](src/main/java/transaction/MultiServiceImpl.java)

1. 获取线程资源，只有获取到任务数相同的线程才开始执行
2. 开启事务并执行，若失败则记录
3. 等待所有任务执行完毕
4. 判断是否存在失败，无则提交，有则回滚

FIXME:
1. 若未能获取到足够线程数则不会执行，导致阻塞等待
2. 任务执行时主线程只是等待