/*
 * Created on 2009-7-14
 */
package aurora.testcase.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;

public class TemplateTest {

    /**
     * Test template based view creation
     */
    public static void main(String[] args) 
        throws Exception
    {
        // APF初始化
        UncertainEngine engine = UncertainEngine.createInstance();
        PresentationManager pm = new PresentationManager(engine);
        
        // 获取aurora/testcase/ui的当前物理路径
        URL url = Thread.currentThread().getContextClassLoader().getResource("aurora/testcase/ui");
        if(url==null)
            throw new IOException("aurora/testcase/ui is not found in CLASSPATH");
        String path = url.getPath();
        // 根据获取的路径，装载组件package
        pm.loadViewComponentPackage(path);
        
        /* 以下和 Step 1 类似 */
        
        // 创建包含数据的model，设置greeting属性
        CompositeMap model = new CompositeMap("data");
        model.put("greeting", "world");
        // 创建hello组件的配置，设置color属性
        CompositeMap view = new CompositeMap("hello");
        view.put("color", "red");
        view.put("field", "greeting");

        // 创建一个Writer实例，用于输出界面内容
        PrintWriter out = new PrintWriter(System.out);
        
        // 通过PresentationManager创建BuildSession
        BuildSession session = pm.createSession( out );

        // 完成界面内容的创建
        session.buildView(model, view);
        out.flush();        
    }

}
