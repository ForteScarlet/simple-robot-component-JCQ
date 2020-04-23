package test;

import com.simplerobot.component.jcq.DebugJCQMain;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class TestRun extends DebugJCQMain {
    public static void main(String[] args) {
        TestRun testRun = new TestRun();

        testRun.startup();
        testRun.enable();

        testRun.privateMsg(1,1, 1, "msg!", 1);
        testRun.groupBan(1,1, 1, 1, 1, 1);

        testRun.disable();
        testRun.exit();


    }

}
