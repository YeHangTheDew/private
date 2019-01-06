package com.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Consumer;

/**该工具是需要获取事务执行结果的封装。
 * Created by yechh on 18/12/12.
 */
@Component
public class TransactionUtil {
    @Autowired
    private PlatformTransactionManager transactionManager;

    public boolean transact(Consumer consumer) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            consumer.accept(null);

            transactionManager.commit(status);
            return true;
        } catch (Exception e) {
            transactionManager.rollback(status);
            e.printStackTrace();
            return false;
        }


    }
}
/*Service举例：
@Service
public class TestService {
public void doSome(int i) {
    System.out.println("我是Service层" + i);
}
}

Controller中就可以使用
boolean result = transactionUtil.transact(s -> testService.doSome(1))来获取事务的执行结果了。

*
* */
